package open.source.androp.client

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.MainThread
import androidx.collection.SparseArrayCompat
import okhttp3.*
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import open.source.androp.proto.*
import java.io.Closeable

class SocketSession(
    private val socket: WebSocket,
) : Closeable {

    fun sendEvent(
        nodeId: Int,
        type: EventType,
        values: SparseArrayCompat<String>,
    ) {
        val builder = ClientEvent.newBuilder()
        builder.targetId = nodeId
        builder.type = type
        (0 until values.size()).forEach {
            val key = values.keyAt(it)
            val value = values.valueAt(it)
            val valueBuilder = TypedValue.newBuilder()
            valueBuilder.type = key
            valueBuilder.data = value
            builder.addValue(valueBuilder)
        }
        socket.send(builder.build().toByteArray().toByteString())
    }

    override fun close() {
        socket.close(0, null)
    }

    @MainThread
    interface Callbacks {
        fun onInsert(id: Int, start: Int, items: List<InsertItem>)

        fun onMove(id: Int, from: Int, to: Int, count: Int)

        fun onRemove(id: Int, start: Int, count: Int)

        fun onUpdate(id: Int, values: List<TypedValue>)

        fun onCleanup()
    }

    private class MessageEmitter(
        private val handler: Handler,
    ) : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            handler.sendEmptyMessage(MessageType.Cleanup.ordinal)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            val buffer = Buffer()
            buffer.write(bytes)
            val command = RenderCommand.parseFrom(buffer.inputStream())
            val valueUpdateList = command.valueUpdateList
            val nodeUpdateList = command.nodeUpdateList
            nodeUpdateList.forEach { nodeUpdate ->
                handler.obtainMessage(MessageType.NodeUpdate.ordinal, nodeUpdate).sendToTarget()
            }
            valueUpdateList.forEach { valueUpdate ->
                handler.obtainMessage(MessageType.ValueUpdate.ordinal, valueUpdate).sendToTarget()
            }
            buffer.clear()
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            handler.sendEmptyMessage(MessageType.Cleanup.ordinal)
        }
    }

    private enum class MessageType {
        NodeUpdate,
        ValueUpdate,
        Cleanup
    }

    private class CallbacksWrapper(
        private val callbacks: Callbacks,
    ) : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            val commands = MessageType.values()
            if (msg.what in commands.indices) {
                when (commands[msg.what]) {
                    MessageType.NodeUpdate -> {
                        val nodeUpdate = msg.obj as NodeUpdate
                        val nodeId = nodeUpdate.nodeId
                        when (nodeUpdate.commandCase) {
                            NodeUpdate.CommandCase.INSERT -> {
                                val insert = nodeUpdate.insert
                                val start = insert.start
                                val items = insert.itemList
                                callbacks.onInsert(nodeId, start, items)
                            }
                            NodeUpdate.CommandCase.REMOVE -> {
                                val remove = nodeUpdate.remove
                                val start = remove.start
                                val count = remove.count
                                callbacks.onRemove(nodeId, start, count)
                            }
                            NodeUpdate.CommandCase.MOVE -> {
                                val move = nodeUpdate.move
                                val from = move.from
                                val to = move.to
                                val count = move.count
                                callbacks.onMove(nodeId, from, to, count)
                            }
                            else -> throw UnsupportedOperationException()
                        }
                    }
                    MessageType.ValueUpdate -> {
                        val valueUpdate = msg.obj as ValueUpdate
                        val nodeId = valueUpdate.nodeId
                        callbacks.onUpdate(nodeId, valueUpdate.attrList)
                    }
                    MessageType.Cleanup -> {
                        callbacks.onCleanup()
                    }
                }
                return true
            } else {
                return false
            }
        }
    }

    companion object {

        fun open(
            client: OkHttpClient,
            url: String,
            callbacks: Callbacks,
        ): SocketSession {
            return SocketSession(client.newWebSocket(
                Request.Builder()
                    .url(url)
                    .build(),
                MessageEmitter(Handler(
                    Looper.getMainLooper(),
                    CallbacksWrapper(callbacks)
                ))
            ))
        }
    }
}