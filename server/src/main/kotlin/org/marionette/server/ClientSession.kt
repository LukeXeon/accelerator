package org.marionette.server

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.marionette.proto.Message
import org.marionette.proto.rpc.IWebSocketSession
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@OptIn(DelicateCoroutinesApi::class)
internal class ClientSession(private val socket: WebSocketSession) : IWebSocketSession {
    private val background = Executors
        .newSingleThreadScheduledExecutor()
        .asCoroutineDispatcher()
    override val incoming = socket.incoming.consumeAsFlow()
        .mapNotNull {
            if (it is Frame.Binary)
                runCatching {
                    Message.parseFrom(it.data)
                }.getOrThrow()
            else
                null
        }
        .shareIn(socket, SharingStarted.WhileSubscribed())

    override suspend fun send(message: Message) {
        withContext(background) {
            socket.send(message.toByteArray())
        }
    }

    override val coroutineContext: CoroutineContext
        get() = socket.coroutineContext

    init {
        GlobalScope.launch(background) {
            background.use {
                socket.coroutineContext.job.join()
            }
        }
    }
}