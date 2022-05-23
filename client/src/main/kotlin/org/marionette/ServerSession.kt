package org.marionette

import kotlinx.coroutines.flow.SharedFlow
import org.marionette.proto.Message
import org.marionette.proto.rpc.IWebSocketSession
import kotlin.coroutines.CoroutineContext

class ServerSession : IWebSocketSession {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
    override val incoming: SharedFlow<Message> = TODO()

    override suspend fun send(message: Message) {

    }
}