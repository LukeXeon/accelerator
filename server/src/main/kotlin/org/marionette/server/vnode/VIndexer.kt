package org.marionette.server.vnode

import androidx.collection.ArrayMap
import com.google.protobuf.MessageLite
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaSetter
import kotlin.reflect.typeOf

object VIndexer {
    open class PropertyIndex(val value: Int)

    class EventIndex(index: Int, val types: List<KClass<*>>) : PropertyIndex(index)

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

    private val sIndexTable = ArrayMap<Class<*>, Map<Method, PropertyIndex>>()

    fun indexing(type: Class<*>) {
        synchronized(sIndexTable) {
            if (!sIndexTable.containsKey(type)) {
                val map = ArrayMap<Method, PropertyIndex>()
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
                                    map[method] = EventIndex(
                                        index,
                                        arguments.mapNotNull { it.type?.classifier as? KClass<*> }
                                    )
                                    return@forEachIndexed
                                }
                            } else if (propertyType.arguments.isEmpty()) {
                                val classifier = propertyType.classifier
                                if (classifier is KClass<*> && classifier.isSupportType) {
                                    map[method] = PropertyIndex(index)
                                    return@forEachIndexed
                                }
                            }
                        }
                        throw UnsupportedOperationException()
                    }
            }
        }
    }

    operator fun get(type: Class<*>, method: Method): PropertyIndex? {
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
}