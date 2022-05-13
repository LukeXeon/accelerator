package org.marionette.proto.node

interface ISwitch : IText {
    var checked: Boolean?
    var onCheckedChanged: ((Boolean) -> Unit)?
}