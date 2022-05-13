package org.marionette.proto.node

import org.marionette.proto.types.FontStyle
import org.marionette.proto.types.FontWeight
import org.marionette.proto.types.TextAlign
import org.marionette.proto.types.TextUnit

interface IText : INode {
    var text: String?
    var maxLines: Int?
    var color: Int?
    var fontSize: TextUnit?
    var fontWeight: FontWeight?
    var fontStyle: FontStyle?
    var textAlign: TextAlign?
    var textDecoration: Int?
}