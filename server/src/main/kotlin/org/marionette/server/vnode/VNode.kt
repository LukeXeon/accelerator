package org.marionette.server.vnode

import androidx.collection.ArrayMap
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
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.javaSetter
import kotlin.reflect.jvm.reflect
import kotlin.reflect.typeOf

internal sealed class VNode private constructor(val name: String) {

    val nodeId = generateViewId()

    var parent: VParent? = null

    abstract fun dispatch(event: NodeEvent): Boolean

    private open class VLeaf(name: String) : VNode(name), InvocationHandler {
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
                events[event.eventId]?.invoke(event.argList.map {
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
            val index = Companion[obj.javaClass, method]
            if (index != null) {
                val arg = args[0]
                val bytes = when {
                    arg == null -> {
                        if (index.eventTypes != null) {
                            events.remove(index.value)
                        }
                        ByteString.EMPTY
                    }
                    arg is Function<*> && index.eventTypes != null -> {
                        val types = index.eventTypes
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

    private class VGroup(name: String) : VLeaf(name), VParent {
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

    private class VIndex(
        val value: Int,
        val eventTypes: List<KClass<*>>? = null
    )

    companion object {

        private val sNextGeneratedId = AtomicInteger(1)

        private val sSupportTypes = listOf(
            ByteArray::class,
            Boolean::class,
            Byte::class,
            Char::class,
            Short::class,
            Int::class,
            Long::class,
            Float::class,
            Double::class,
            Enum::class,
            MessageLite::class
        )

        private val sIndexTable = ArrayMap<Class<*>, Map<Method, VIndex>>()

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

        private fun indexing(type: Class<*>) {
            synchronized(sIndexTable) {
                if (!sIndexTable.containsKey(type)) {
                    val map = ArrayMap<Method, VIndex>()
                    type.kotlin.memberProperties.asSequence()
                        .mapNotNull {
                            it as? KMutableProperty<*>
                        }.sortedBy {
                            it.name
                        }.forEachIndexed { index, kMutableProperty ->
                            val method = kMutableProperty.javaSetter
                            if (method != null) {
                                val propertyType = kMutableProperty.returnType
                                if (propertyType.isSupertypeOf(typeOf<Function<Unit>>())) {
                                    var arguments = propertyType.arguments
                                    arguments = arguments.subList(0, arguments.size - 1)
                                    if (arguments.asSequence().map { it.type }
                                            .all {
                                                if (it != null && it.arguments.isEmpty()) {
                                                    val classifier = it.classifier
                                                    if (classifier is KClass<*> && classifier.isSupportType) {
                                                        return@all true
                                                    }
                                                }
                                                return@all false
                                            }
                                    ) {
                                        map[method] = VIndex(
                                            index,
                                            arguments.mapNotNull { it.type?.classifier as? KClass<*> }
                                        )
                                        return@forEachIndexed
                                    }
                                } else if (propertyType.arguments.isEmpty()) {
                                    val classifier = propertyType.classifier
                                    if (classifier is KClass<*> && classifier.isSupportType) {
                                        map[method] = VIndex(index)
                                        return@forEachIndexed
                                    }
                                }
                            }
                            throw UnsupportedOperationException()
                        }
                }
            }
        }

        private operator fun get(type: Class<*>, method: Method): VIndex? {
            val table = synchronized(sIndexTable) {
                sIndexTable.getValue(type)
            }
            return synchronized(table) {
                table[method]
            }
        }

        private val KClass<*>.isSupportType: Boolean
            get() {
                return sSupportTypes.any { it.isSuperclassOf(this) }
            }

        val INode.vNode: VNode
            get() {
                return Proxy.getInvocationHandler(this) as VNode
            }

        fun create(
            type: KClass<*>,
            name: String,
            isGroup: Boolean
        ): Any {
            return Proxy.newProxyInstance(
                type.java.classLoader,
                arrayOf(type.java),
                if (isGroup) VGroup(name) else VLeaf(name)
            ).apply { indexing(javaClass) }
        }

        inline fun <reified N : INode> create(
            name: String,
            isGroup: Boolean = false
        ): N {
            return create(N::class, name, isGroup) as N
        }
    }
}