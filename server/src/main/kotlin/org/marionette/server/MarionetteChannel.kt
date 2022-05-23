package org.marionette.server

import com.google.protobuf.ByteString
import com.google.protobuf.Empty
import com.google.protobuf.MessageLite
import io.grpc.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.mapNotNull
import org.marionette.proto.*
import org.marionette.proto.rpc.IWebSocketSession
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.channels.Channel as Queue

internal class MarionetteChannel(private val session: IWebSocketSession) : Channel() {
    private val nextRequestId = AtomicInteger()

    override fun <RequestT : Any, ResponseT : Any> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: CallOptions?
    ): ClientCall<RequestT, ResponseT> {
        return Call(nextRequestId.incrementAndGet(), methodDescriptor)
    }

    override fun authority(): String = InetAddress.getLocalHost().hostName

    private inner class Call<ReqT, RespT>(
        private val id: Int,
        private val methodDescriptor: MethodDescriptor<ReqT, RespT>
    ) : ClientCall<ReqT, RespT>() {

        private val queue = Queue<ReqT>(8)

        private var task: Pair<Job, Listener<RespT>>? = null

        override fun start(responseListener: Listener<RespT>, headers: Metadata) {
            task = session.launch {
                session.send(message {
                    rpc = rpcMessage {
                        requestId = id
                        startCall = methodCall {
                            this.name = methodDescriptor.fullMethodName
                            this.headers = headers {
                                header.addAll(headers.toByteStringList())
                            }
                        }
                    }
                })
                responseListener.onReady()
                session.incoming.mapNotNull {
                    if (it.channelCase == Message.ChannelCase.RPC && it.rpc.requestId == id)
                        it.rpc
                    else
                        null
                }.collect {
                    when (it.bodyCase) {
                        RpcMessage.BodyCase.HEADERS -> {
                            responseListener.onHeaders(it.headers.headerList.toMetadata())
                        }
                        RpcMessage.BodyCase.DATA -> {
                            responseListener.onMessage(methodDescriptor.parseResponse(it.data.newInput()))
                        }
                        RpcMessage.BodyCase.FETCH -> {
                            repeat(it.fetch) {
                                val message = queue.receive()
                                session.send(message {
                                    rpc = rpcMessage {
                                        requestId = id
                                        data = if (message is MessageLite)
                                            message.toByteString()
                                        else
                                            ByteString.readFrom(
                                                methodDescriptor.streamRequest(
                                                    message
                                                )
                                            )
                                    }
                                })
                            }
                        }
                        RpcMessage.BodyCase.CLOSE -> {
                            currentCoroutineContext().job.cancel()
                            responseListener.onClose(
                                Status.fromCodeValue(it.close.code),
                                it.close.headers.headerList.toMetadata()
                            )
                        }
                        else -> throw AssertionError()
                    }
                }
            } to responseListener
        }

        override fun request(numMessages: Int) {
            session.launch {
                session.send(message {
                    rpc = rpcMessage {
                        requestId = id
                        fetch = numMessages
                    }
                })
            }
        }

        override fun cancel(message: String?, cause: Throwable?) {
            val t = task ?: return
            session.launch {
                val (job, listener) = t
                job.cancelAndJoin()
                listener.onClose(
                    Status.CANCELLED.withDescription(message).withCause(cause),
                    Metadata()
                )
            }
        }

        override fun halfClose() {
            session.launch {
                session.send(message {
                    rpc = rpcMessage {
                        requestId = id
                        halfClose = Empty.getDefaultInstance()
                    }
                })
            }
        }

        override fun sendMessage(message: ReqT) {
            session.launch {
                queue.send(message)
            }
        }
    }
}