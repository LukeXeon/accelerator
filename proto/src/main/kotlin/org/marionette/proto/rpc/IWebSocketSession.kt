package org.marionette.proto.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import org.marionette.proto.Message

interface IWebSocketSession : CoroutineScope {
    val incoming: SharedFlow<Message>
    suspend fun send(message: Message)
}