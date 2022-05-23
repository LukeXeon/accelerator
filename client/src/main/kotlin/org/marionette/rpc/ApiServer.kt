package org.marionette.rpc

import com.google.protobuf.ByteString
import com.google.protobuf.MessageLite
import io.grpc.*
import io.grpc.util.MutableHandlerRegistry
import kotlinx.coroutines.launch
import org.marionette.ServerSession
import org.marionette.proto.message
import org.marionette.proto.rpcMessage
import java.util.concurrent.TimeUnit

class ApiServer(private val session: ServerSession) : Server() {

    private val registry = MutableHandlerRegistry()

    override fun start(): Server {
        registry.lookupMethod("")
        TODO("Not yet implemented")
    }

    override fun shutdown(): Server {
        TODO("Not yet implemented")
    }

    override fun shutdownNow(): Server {
        TODO("Not yet implemented")
    }

    override fun isShutdown(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTerminated(): Boolean {
        TODO("Not yet implemented")
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit?): Boolean {
        TODO("Not yet implemented")
    }

    override fun awaitTermination() {
        TODO("Not yet implemented")
    }

    private inner class Call<ReqT, RespT>(
        private val id: Int,
        private val methodDescriptor: MethodDescriptor<ReqT, RespT>
    ) : ServerCall<ReqT, RespT>() {

        override fun request(numMessages: Int) {
        }

        override fun sendHeaders(headers: Metadata?) {

        }

        override fun sendMessage(message: RespT) {
            session.launch {
                session.send(message {
                    rpc = rpcMessage {
                        data = if (message is MessageLite) {
                            message.toByteString()
                        } else {
                            ByteString.readFrom(methodDescriptor.streamResponse(message))
                        }
                    }
                })
            }
        }

        override fun close(status: Status?, trailers: Metadata?) {
            TODO("Not yet implemented")
        }

        override fun isCancelled(): Boolean {
            TODO("Not yet implemented")
        }

        override fun getMethodDescriptor(): MethodDescriptor<ReqT, RespT> = methodDescriptor
    }
}