package org.marionette.server

import androidx.compose.runtime.Composable
import org.marionette.proto.node.ISwitch
import org.marionette.server.text.AbstractText
import org.marionette.server.text.TextStyle
import org.marionette.server.vnode.VNode


@Composable
fun Switch(
    checked: Boolean,
    onCheckedChanged: ((Boolean) -> Unit)? = null,
    text: String = "",
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    AbstractText(
        factory = { VNode.create<ISwitch>("switch") },
        text = text,
        modifier = modifier,
        style = style,
        maxLines = maxLines,
        update = {
            set(onCheckedChanged) { this.onCheckedChanged = it }
            set(checked) { this.checked = it }
        }
    )
}