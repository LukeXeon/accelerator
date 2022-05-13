package org.marionette.proto.rpc

import io.grpc.ServerBuilder
import io.grpc.ServerStreamTracer
import io.grpc.internal.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MarionetteServerBuilder(
    val address: MarionetteSocketAddress,
) : AbstractServerImplBuilder<MarionetteServerBuilder>() {

    private val serverImplBuilder: ServerImplBuilder

    val schedulerPool: ObjectPool<ScheduledExecutorService> =
        SharedResourcePool.forResource(GrpcUtil.TIMER_SERVICE)

    init {
        serverImplBuilder = ServerImplBuilder(
            ServerImplBuilder.ClientTransportServersBuilder { TODO() }
        )

        // In-process transport should not record its traffic to the stats module.
        // https://github.com/grpc/grpc-java/issues/2284

        // In-process transport should not record its traffic to the stats module.
        // https://github.com/grpc/grpc-java/issues/2284
        serverImplBuilder.setStatsRecordStartedRpcs(false)
        serverImplBuilder.setStatsRecordFinishedRpcs(false)
        // Disable handshake timeout because it is unnecessary, and can trigger Thread creation that can
        // break some environments (like tests).
        // Disable handshake timeout because it is unnecessary, and can trigger Thread creation that can
        // break some environments (like tests).
        handshakeTimeout(Long.MAX_VALUE, TimeUnit.SECONDS)
    }

    override fun delegate(): ServerBuilder<*> {
        return serverImplBuilder
    }

    companion object {
        fun forChannels(
            incoming: ReceiveChannel<ByteArray>,
            outgoing: SendChannel<ByteArray>,
        ): MarionetteServerBuilder {
            return MarionetteServerBuilder(MarionetteSocketAddress(incoming, outgoing))
        }
    }
}