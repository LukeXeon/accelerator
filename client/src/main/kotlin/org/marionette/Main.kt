package org.marionette

import io.grpc.ServerBuilder
import io.grpc.ServerProvider
import io.grpc.ServerRegistry

fun main() {
    ServerRegistry.getDefaultRegistry()
        .register(object : ServerProvider() {
            override fun isAvailable(): Boolean = true

            override fun priority(): Int = Int.MAX_VALUE

            override fun builderForPort(port: Int): ServerBuilder<*> {



            }

        })
    ServerBuilder.forPort(0)
}