package org.marionette.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.kotlin.AbstractCoroutineStub
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.typeOf


val LocalRpcChannel = staticCompositionLocalOf<Channel> {
    error("No default channel")
}


@Composable
fun <T : AbstractCoroutineStub<T>> CoroutineStub(
    create: (Channel, CallOptions) -> T,
    options: CallOptions = CallOptions.DEFAULT,
): T {
    val channel = LocalRpcChannel.current
    return remember(channel, create, options) { create(channel, options) }
}

fun <T : AbstractCoroutineStub<T>> findConstructor(type: KClass<T>): KFunction<T> {
    return type.constructors.find {
        it.parameters.size == 2 && it.parameters[0].type == typeOf<Channel>()
                && it.parameters[1].type == typeOf<CallOptions>()
    } ?: throw AssertionError()
}

@Composable
inline fun <reified T : AbstractCoroutineStub<T>> CoroutineStub(
    options: CallOptions = CallOptions.DEFAULT,
): T {
    val constructor = findConstructor(T::class)
    return CoroutineStub(remember(constructor) {
        { channel, callOptions ->
            constructor.call(channel, callOptions)
        }
    }, options)
}

