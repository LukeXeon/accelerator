package open.source.accelerator.mappings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import open.source.accelerator.proto.PbNode

@Composable
fun MappingNode(descriptor: PbNode, modifier: Modifier) {
    when (descriptor.valueCase) {
        PbNode.ValueCase.ROW -> MappingRow(descriptor = descriptor.row, modifier)
        PbNode.ValueCase.COLUMN -> MappingColumn(descriptor = descriptor.column, modifier)
        PbNode.ValueCase.TEXT -> MappingText(descriptor = descriptor.text, modifier)
        PbNode.ValueCase.IMAGE -> MappingImage(descriptor = descriptor.image, modifier)
        PbNode.ValueCase.BOX -> MappingBox(descriptor = descriptor.box, modifier)
        else -> Unit
    }
}







