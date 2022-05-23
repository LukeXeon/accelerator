package org.marionette.server.vnode

import org.marionette.proto.NodeEvent
import org.marionette.proto.node.INode
import org.marionette.server.RemoteUpdater
import org.marionette.server.vnode.VNode.Companion.vNode

internal class VRoot(val rootUpdater: RemoteUpdater) : VParent {
    override val children = ArrayList<INode>()
    fun commit() {
        rootUpdater.commit()
    }

    fun clear() {
        rootUpdater.clear()
    }

    override fun move(from: Int, to: Int, count: Int) {
        super.move(from, to, count)
        rootUpdater.move(0, from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        super.remove(index, count)
        rootUpdater.remove(0, index, count)
    }

    override fun insert(index: Int, instance: INode) {
        super.insert(index, instance)
        val vNode = instance.vNode
        rootUpdater.insert(0, index, vNode.nodeId, vNode.name)
    }

    fun dispatch(event: NodeEvent) {
        for (node in children) {
            if (node.vNode.dispatch(event)) {
                return
            }
        }
    }
}