package org.marionette.server.vnode

import org.marionette.proto.node.INode
import org.marionette.server.move
import org.marionette.server.remove
import org.marionette.server.vnode.VNode.Companion.vNode

interface VParent {
    val children: MutableList<INode>

    fun move(from: Int, to: Int, count: Int) {
        children.move(from, to, count)
    }

    fun remove(index: Int, count: Int) {
        children.remove(index, count) { this.vNode.parent = null }
    }

    fun insert(index: Int, instance: INode) {
        val vNode = instance.vNode
        vNode.parent = this
        children.add(instance)
    }
}