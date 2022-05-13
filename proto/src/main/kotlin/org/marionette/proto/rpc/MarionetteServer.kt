package org.marionette.proto.rpc

import io.grpc.InternalChannelz
import io.grpc.InternalInstrumented
import io.grpc.internal.InternalServer
import io.grpc.internal.ServerListener
import java.net.SocketAddress

class MarionetteServer(
    builder: MarionetteServerBuilder,
) : InternalServer {
    private val listenAddress = builder.address
    private val schedulerPool = builder.schedulerPool
    private var listener: ServerListener? = null
    private val shutdown = false

    override fun start(listener: ServerListener?) {
        this.listener = listener

        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun getListenSocketAddress(): SocketAddress {
        return listenAddress
    }

    override fun getListenSocketStats(): InternalInstrumented<InternalChannelz.SocketStats>? {
        TODO("Not yet implemented")
    }

    override fun getListenSocketAddresses(): List<SocketAddress> {
        return listOf(listenAddress)
    }

    override fun getListenSocketStatsList(): MutableList<InternalInstrumented<InternalChannelz.SocketStats>>? {
        TODO("Not yet implemented")
    }
}