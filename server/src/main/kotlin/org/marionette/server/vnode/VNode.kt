package org.marionette.server.vnode

import androidx.collection.SparseArrayCompat
import com.google.protobuf.*
import org.marionette.proto.NodeEvent
import org.marionette.proto.node.INode
import org.marionette.server.RemoteUpdater
import org.marionette.server.set
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

sealed class VNode private constructor(val name: String) {

    val nodeId = generateViewId()

    var parent: VParent? = null

    abstract fun dispatch(event: NodeEvent): Boolean

    open class VLeaf(name: String) : VNode(name), InvocationHandler {
        protected val rootUpdater: RemoteUpdater
            get() {
                val instance = updater
                if (instance != null) {
                    return instance
                }
                var current: Any? = this
                loop@ while (current != null) {
                    when (current) {
                        is VRoot -> {
                            return current.rootUpdater
                        }
                        is VGroup -> {
                            current = current.parent
                        }
                        else -> {
                            break@loop
                        }
                    }
                }
                throw AssertionError()
            }

        private var updater: RemoteUpdater? = null

        private val events = SparseArrayCompat<((List<ByteString>) -> Unit)?>()

        override fun dispatch(event: NodeEvent): Boolean {
            return if (event.nodeId == nodeId) {
                events[0]?.invoke(event.argList.map {
                    if (it.kindCase == NodeEvent.EventArg.KindCase.DATA) {
                        it.data
                    } else {
                        ByteString.EMPTY
                    }
                })
                true
            } else {
                false
            }
        }

        @OptIn(ExperimentalReflectionOnLambdas::class)
        override fun invoke(obj: Any, method: Method, args: Array<out Any?>): Any {
            if (method.isDefault) {
                return method.invoke(this, args)
            }
            val index = VIndexer[obj.javaClass, method]
            if (index != null) {
                val arg = args[0]
                val bytes = when {
                    arg == null -> {
                        if (index is VIndexer.EventIndex) {
                            events.remove(index.value)
                        }
                        ByteString.EMPTY
                    }
                    index is VIndexer.EventIndex -> {
                        val types = index.types
                        if (arg !is Function<*>) {
                            throw UnsupportedOperationException()
                        }
                        val function = arg.reflect() ?: throw UnsupportedOperationException()
                        events[index.value] = VEventHandler(function, types)
                        boolValue {
                            value = true
                        }.toByteString()
                    }
                    arg is Enum<*> -> {
                        int32Value {
                            value = arg.ordinal
                        }.toByteString()
                    }
                    arg is MessageLite -> {
                        arg.toByteString()
                    }
                    arg is Boolean -> {
                        boolValue {
                            value = arg
                        }.toByteString()
                    }
                    arg is Byte -> {
                        ByteString.copyFrom(byteArrayOf(arg))
                    }
                    arg is Char -> {
                        ByteString.copyFromUtf8(String(charArrayOf(arg)))
                    }
                    arg is Short -> {
                        int32Value {
                            value = arg.toInt()
                        }.toByteString()
                    }
                    arg is Int -> {
                        int32Value {
                            value = arg
                        }.toByteString()
                    }
                    arg is Long -> {
                        int64Value {
                            value = arg
                        }.toByteString()
                    }
                    arg is Float -> {
                        floatValue {
                            value = arg
                        }.toByteString()
                    }
                    arg is Double -> {
                        doubleValue {
                            value = arg
                        }.toByteString()
                    }
                    arg is ByteArray -> {
                        ByteString.copyFrom(arg)
                    }
                    arg is String -> {
                        ByteString.copyFromUtf8(arg)
                    }
                    else -> throw UnsupportedOperationException()
                }
                rootUpdater.set(nodeId, index.value, bytes)
            }
            throw UnsupportedOperationException()
        }
    }

    class VGroup(name: String) : VLeaf(name), VParent {
        override val children = ArrayList<INode>()

        override fun dispatch(event: NodeEvent): Boolean {
            return if (super.dispatch(event)) {
                true
            } else {
                children.any { it.vNode.dispatch(event) }
            }
        }

        override fun move(from: Int, to: Int, count: Int) {
            super.move(from, to, count)
            rootUpdater.move(nodeId, from, to, count)
        }

        override fun remove(index: Int, count: Int) {
            super.remove(index, count)
            rootUpdater.remove(nodeId, index, count)
        }

        override fun insert(index: Int, instance: INode) {
            super.insert(index, instance)
            val vNode = instance.vNode
            rootUpdater.insert(nodeId, index, vNode.nodeId, vNode.name)
        }
    }

    companion object {

        private val sNextGeneratedId = AtomicInteger(1)

        private fun generateViewId(): Int {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF)
                    newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }

        val INode.vNode: VNode
            get() {
                return Proxy.getInvocationHandler(this) as VNode
            }

        inline fun <reified N : INode> create(
            name: String,
            isGroup: Boolean = false
        ): N {
            return Proxy.newProxyInstance(
                N::class.java.classLoader,
                arrayOf(N::class.java),
                if (isGroup) VGroup(name) else VLeaf(name)
            ).apply { VIndexer.indexing(javaClass) } as N
        }
    }
}