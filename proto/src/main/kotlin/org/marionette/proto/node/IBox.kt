package org.marionette.proto.node

import org.marionette.proto.types.Horizontal
import org.marionette.proto.types.Vertical

interface IBox : INode {
    var horizontalAlignment: Horizontal?
    var verticalAlignment: Vertical?
}