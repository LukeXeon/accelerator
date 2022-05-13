package org.marionette.server.vnode

import com.google.protobuf.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSuperclassOf

class VEventHandler(
    private val function: KFunction<*>,
    private val types: List<KClass<*>>
) : (List<ByteString>) -> Unit {

    override fun invoke(input: List<ByteString>) {
        function.call(types.mapIndexed { index, type ->
            val bytes = input[index]
            if (bytes == ByteString.EMPTY) {
                return@mapIndexed null
            }
            return@mapIndexed when {
                type == ByteArray::class -> {
                    bytes.toByteArray()
                }
                type == Boolean::class -> {
                    BoolValue.parseFrom(bytes).value
                }
                type == Byte::class -> {
                    bytes.byteAt(0)
                }
                type == Char::class -> {
                    StringValue.parseFrom(bytes).value[0]
                }
                type == Short::class -> {
                    Int32Value.parseFrom(bytes).value.toShort()
                }
                type == Int::class -> {
                    Int32Value.parseFrom(bytes).value
                }
                type == Long::class -> {
                    Int64Value.parseFrom(bytes).value
                }
                type == Float::class -> {
                    FloatValue.parseFrom(bytes).value
                }
                type == Double::class -> {
                    DoubleValue.parseFrom(bytes).value
                }
                type == String::class -> {
                    StringValue.parseFrom(bytes).value
                }
                Enum::class.isSuperclassOf(type) -> {
                    type.java.enumConstants[Int32Value.parseFrom(bytes).value]
                }
                MessageLite::class.isSuperclassOf(type) -> {
                    getParseFrom(type.java).invoke(bytes)
                }
                else -> {
                    throw UnsupportedOperationException()
                }
            }
        })
    }

    companion object {
        private val sParseFromTable = HashMap<Class<*>, (ByteString) -> MessageLite>()

        private fun getParseFrom(type: Class<*>): (ByteString) -> MessageLite {
            return synchronized(sParseFromTable) {
                sParseFromTable.getOrPut(type) {
                    val m = type.getMethod(
                        "parseFrom",
                        ByteString::class.java
                    )
                    return@getOrPut { data ->
                        m.invoke(null, data) as MessageLite
                    }
                }
            }
        }
    }
}