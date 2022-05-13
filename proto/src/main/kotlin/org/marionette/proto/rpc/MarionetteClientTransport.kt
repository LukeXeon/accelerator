package org.marionette.proto.rpc

import com.google.common.util.concurrent.ListenableFuture
import io.grpc.*
import io.grpc.internal.*
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledExecutorService

class MarionetteClientTransport : ServerTransport, ConnectionClientTransport {

    override fun getLogId(): InternalLogId {
        TODO("Not yet implemented")
    }

    override fun getStats(): ListenableFuture<InternalChannelz.SocketStats> {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun newStream(
        method: MethodDescriptor<*, *>?,
        headers: Metadata?,
        callOptions: CallOptions?,
        tracers: Array<out ClientStreamTracer>?,
    ): ClientStream {
        TODO("Not yet implemented")
    }

    override fun ping(callback: ClientTransport.PingCallback?, executor: Executor?) {
        TODO("Not yet implemented")
    }

    override fun start(listener: ManagedClientTransport.Listener?): Runnable? {
        TODO("Not yet implemented")
    }

    override fun shutdown(reason: Status?) {
        TODO("Not yet implemented")
    }

    override fun shutdownNow(reason: Status?) {
        TODO("Not yet implemented")
    }

    override fun getScheduledExecutorService(): ScheduledExecutorService {
        TODO("Not yet implemented")
    }

    override fun getAttributes(): Attributes {
        TODO("Not yet implemented")
    }
}