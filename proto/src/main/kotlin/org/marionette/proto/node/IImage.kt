package org.marionette.proto.node

import org.marionette.proto.types.ContentScale

interface IImage : INode {
    var url: String?
    var contentDescription: String?
    var contentScale: ContentScale?
}