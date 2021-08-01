package open.source.accelerator.mappings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import open.source.accelerator.proto.PbRenderNode

@Composable
fun MappingRenderNode(descriptor: PbRenderNode, modifier: Modifier) {
    when (descriptor.valueCase) {
        PbRenderNode.ValueCase.ROW -> MappingRow(descriptor = descriptor.row, modifier)
        PbRenderNode.ValueCase.COLUMN -> MappingColumn(descriptor = descriptor.column, modifier)
        PbRenderNode.ValueCase.TEXT -> MappingText(descriptor = descriptor.text, modifier)
        PbRenderNode.ValueCase.IMAGE -> MappingImage(descriptor = descriptor.image, modifier)
        PbRenderNode.ValueCase.BOX -> MappingBox(descriptor = descriptor.box, modifier)
        else -> Unit
    }
}







