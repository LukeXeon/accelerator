package open.source.accelerator.mappings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import open.source.accelerator.proto.PbAlignment
import open.source.accelerator.proto.PbRow

private fun Modifier.apply(scope: RowScope, attr: PbRow.Child): Modifier {
    var m = this
    with(scope) {
        if (attr.hasWeight()) {
            m = m.weight(attr.weight.value)
        }
        if (attr.align != PbAlignment.Vertical.UNRECOGNIZED) {
            m = m.align(attr.align.toVertical())
        }
    }
    return m
}

@Composable
fun MappingRow(descriptor: PbRow, modifier: Modifier) {
    Row(modifier = modifier) {
        descriptor.childrenList.forEach {
            if (it.hasContent()) {
                MappingNode(
                    descriptor = it.content,
                    modifier = Modifier.apply(this, it)
                )
            }
        }
    }
}
