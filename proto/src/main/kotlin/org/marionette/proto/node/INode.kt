package org.marionette.proto.node

import org.marionette.proto.types.Background
import org.marionette.proto.types.Dimension
import org.marionette.proto.types.Padding
import org.marionette.proto.types.Visibility

interface INode {
    var onClick: (() -> Unit)?
    var visibility: Visibility?
    var width: Dimension?
    var height: Dimension?
    var padding: Padding?
    var background: Background?
}