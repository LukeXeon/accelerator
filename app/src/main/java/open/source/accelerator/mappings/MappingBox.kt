package open.source.accelerator.mappings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import open.source.accelerator.proto.PbBox

private fun Modifier.apply(scope: BoxScope, child: PbBox.Child): Modifier {
    var m = this
    with(scope) {
        if (child.hasMatchParentSize()) {
            m = m.matchParentSize()
        } else if (child.hasAlign()) {
            m = m.align(child.align.toAlignment())
        }
    }
    return m
}

@Composable
fun MappingBox(descriptor: PbBox, modifier: Modifier) {
    Box(modifier = modifier) {
        RenderChildren(children = descriptor.childrenMap) { child ->
            if (child.hasContent()) {
                MappingNode(
                    descriptor = child.content,
                    modifier = Modifier.apply(this, child)
                )
            }
        }
    }
}