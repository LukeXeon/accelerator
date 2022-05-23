package org.marionette.server.vnode

import com.google.protobuf.*
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSuperclassOf

internal class VEventHandler(
    private val function: KFunction<*>,
    private val types: List<KClass<*>>
) : (List<ByteString>) -> Unit {

    override fun invoke(input: List<ByteString>) {
        if (input.size != types.size) {
            throw IllegalArgumentException()
        }
        function.call((input.indices).asSequence().map {
            types[it] to input[it]
        }.map { (type, bytes) ->
            if (bytes == ByteString.EMPTY) {
                return@map null
            }
            return@map when {
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
        private val sParseFromTable = HashMap<Class<*>, Method>()

        private fun getParseFrom(type: Class<*>): Method {
            return synchronized(sParseFromTable) {
                sParseFromTable.getOrPut(type) {
                    type.getMethod(
                        "parseFrom",
                        ByteString::class.java
                    )
                }
            }
        }
    }
}