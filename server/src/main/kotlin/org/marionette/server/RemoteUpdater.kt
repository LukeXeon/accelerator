package org.marionette.server

import androidx.collection.SparseArrayCompat
import com.google.protobuf.ByteString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import org.marionette.proto.*
import kotlin.coroutines.CoroutineContext

class RemoteUpdater private constructor(
    override val coroutineContext: CoroutineContext,
    private val channel: Channel<NodeUpdateList>,
) : CoroutineScope, ReceiveChannel<NodeUpdateList> by channel {

    constructor(coroutineContext: CoroutineContext) : this(coroutineContext, Channel())

    private class MutableNodeUpdate {
        val values = SparseArrayCompat<ByteString>()
        val actions = ArrayList<NodeUpdate.ChildAction>()
    }

    private val nodeUpdates = SparseArrayCompat<MutableNodeUpdate>()

    fun clear() {
        nodeUpdates.clear()
        launch { channel.send(NodeUpdateList.getDefaultInstance()) }
    }

    private fun nodeUpdate(nodeId: Int): MutableNodeUpdate {
        return nodeUpdates.getOrPut(nodeId) { MutableNodeUpdate() }
    }

    fun set(nodeId: Int, id: Int, value: ByteString) {
        nodeUpdate(nodeId).values[id] = value
    }

    fun commit() {
        val command = nodeUpdateList {
            update.addAll(nodeUpdates.map { i, mutableNodeUpdate ->
                nodeUpdate {
                    nodeId = i
                    data.addAll(mutableNodeUpdate.values.map { i, byteString ->
                        NodeUpdateKt.field {
                            id = i
                            data = byteString
                        }
                    })
                    childAction.addAll(mutableNodeUpdate.actions)
                }
            })
        }
        launch { channel.send(command) }
        nodeUpdates.clear()
    }

    fun move(nodeId: Int, from: Int, to: Int, count: Int) {
        nodeUpdate(nodeId).actions.add(
            NodeUpdateKt.childAction {
                move = NodeUpdateKt.ChildActionKt.move {
                    this.from = from
                    this.to = to
                    this.count = count
                }
            }
        )
    }

    fun remove(nodeId: Int, index: Int, count: Int) {
        nodeUpdate(nodeId).actions.add(
            NodeUpdateKt.childAction {
                remove = NodeUpdateKt.ChildActionKt.remove {
                    this.index = index
                    this.count = count
                }
            }
        )
    }

    fun insert(nodeId: Int, index: Int, newNodeId: Int, name: String) {
        nodeUpdate(nodeId).actions.add(
            NodeUpdateKt.childAction {
                insert = NodeUpdateKt.ChildActionKt.insert {
                    this.index = index
                    this.nodeId = newNodeId
                    this.type = name
                }
            }
        )
    }
}

