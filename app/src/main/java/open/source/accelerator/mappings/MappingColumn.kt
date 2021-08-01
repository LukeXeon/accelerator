package open.source.accelerator.mappings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import open.source.accelerator.proto.PbAlignment
import open.source.accelerator.proto.PbColumn

private fun Modifier.apply(scope: ColumnScope, attr: PbColumn.Child): Modifier {
    var m = this
    with(scope) {
        if (attr.hasWeight()) {
            m = m.weight(attr.weight.value)
        }
        if (attr.align != PbAlignment.Horizontal.UNRECOGNIZED) {
            m = m.align(attr.align.toHorizontal())
        }
    }
    return m
}

@Composable
fun MappingColumn(descriptor: PbColumn, modifier: Modifier) {
    Column(modifier = modifier) {
        descriptor.childrenMap.toChildren().forEach { child ->
            if (child.hasContent()) {
                MappingNode(
                    descriptor = child.content,
                    modifier = Modifier.apply(this, child)
                )
            }
        }
    }
}