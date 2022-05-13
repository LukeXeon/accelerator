package org.marionette.proto.rpc

import io.grpc.ManagedChannelBuilder
import io.grpc.internal.AbstractManagedChannelImplBuilder

class MarionetteChannelBuilder : AbstractManagedChannelImplBuilder<MarionetteChannelBuilder>() {
    override fun delegate(): ManagedChannelBuilder<*> {
        TODO("Not yet implemented")
    }
}