package org.marionette.server

import androidx.collection.SparseArrayCompat
import com.google.protobuf.ByteString
import kotlinx.coroutines.launch
import org.marionette.proto.*
import org.marionette.proto.rpc.IWebSocketSession

internal class RemoteUpdater(private val session: IWebSocketSession) {

    private class MutableNodeUpdate {
        val values = SparseArrayCompat<ByteString>()
        val actions = ArrayList<NodeAction>()
    }

    private val nodeUpdates = SparseArrayCompat<MutableNodeUpdate>()

    fun clear() {
        nodeUpdates.clear()
        session.launch {
            session.send(message {
                update = NodeUpdateList.getDefaultInstance()
            })
        }
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
                        nodeField {
                            id = i
                            value = byteString
                        }
                    })
                    childAction.addAll(mutableNodeUpdate.actions)
                }
            })
        }
        session.launch {
            session.send(message {
                update = command
            })
        }
        nodeUpdates.clear()
    }

    fun move(nodeId: Int, from: Int, to: Int, count: Int) {
        nodeUpdate(nodeId).actions.add(
            nodeAction {
                move = moveAction {
                    this.from = from
                    this.to = to
                    this.count = count
                }
            }
        )
    }

    fun remove(nodeId: Int, index: Int, count: Int) {
        nodeUpdate(nodeId).actions.add(
            nodeAction {
                remove = removeAction {
                    this.index = index
                    this.count = count
                }
            }
        )
    }

    fun insert(nodeId: Int, index: Int, newNodeId: Int, name: String) {
        nodeUpdate(nodeId).actions.add(
            nodeAction {
                insert = insertAction {
                    this.index = index
                    this.nodeId = newNodeId
                    this.type = name
                }
            }
        )
    }
}

