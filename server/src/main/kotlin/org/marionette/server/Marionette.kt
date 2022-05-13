package org.marionette.server

import androidx.compose.runtime.Composable
import com.google.protobuf.ByteString
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.utils.io.pool.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.marionette.proto.Message
import org.marionette.proto.message
import org.marionette.server.vnode.VRoot
import java.io.ByteArrayOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.coroutines.CoroutineContext


class Marionette : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    companion object : Plugin<Application, WebSockets.WebSocketOptions, Marionette> {

        override val key: AttributeKey<Marionette> = AttributeKey("compose-render")

        override fun install(
            pipeline: Application,
            configure: WebSockets.WebSocketOptions.() -> Unit,
        ): Marionette {
            val feature = Marionette()
            pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                feature.job.cancel()
            }
            with(pipeline) {
                install(WebSockets, configure)
            }
            return feature
        }
    }
}


fun Route.compose(path: String, content: @Composable () -> Unit) {
    webSocket(path) {
        val feature = application.plugin(Marionette)
        val updater = RemoteUpdater(feature.coroutineContext)
        feature.launch {
            val output = ByteArrayOutputStream()
            updater.consumeEach {
                message {
                    updates = it
                }.writeTo(GZIPOutputStream(output))
                send(output.toByteArray())
                output.reset()
            }
        }
        feature.launch {
            val sink = PipedInputStream()
            val buffer = ByteArrayPool.borrow()
            try {
                @Suppress("BlockingMethodInNonBlockingContext")
                while (sink.available() != -1) {
                    val count = sink.read(buffer)
                    send(message {
                        stream = ByteString.copyFrom(buffer, 0, count)
                    }.toByteArray())
                }
            } finally {
                ByteArrayPool.recycle(buffer)
            }
        }
        val root = VRoot(updater)
        val source = PipedOutputStream()
        incoming.consumeEach {
            if (it is Frame.Binary) {
                val response = Message.parseFrom(GZIPInputStream(it.data.inputStream()))
                if (response.channelCase == Message.ChannelCase.EVENT) {
                    root.dispatch(response.event)
                } else if (response.channelCase == Message.ChannelCase.STREAM) {
                    feature.launch {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        response.stream.writeTo(source)
                    }
                }
            }
        }
    }
}


