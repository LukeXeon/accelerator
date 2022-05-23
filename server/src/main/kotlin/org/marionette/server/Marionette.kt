package org.marionette.server

import androidx.compose.runtime.Composable
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.marionette.proto.Message
import org.marionette.server.vnode.VRoot

fun Route.compose(path: String, content: @Composable () -> Unit) {
    webSocket(path) {
        val session = ClientSession(this)
        val updater = RemoteUpdater(session)
        LocalGrpcChannel provides MarionetteChannel(session)
        val root = VRoot(updater)
        launch {
            session.incoming.mapNotNull {
                if (it.channelCase == Message.ChannelCase.EVENT) it.event else null
            }.collect {
                root.dispatch(it)
            }
        }
    }
}


