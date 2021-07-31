package open.source.accelerator.mappings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import open.source.accelerator.proto.PbChildContent

@Composable
fun MappingChild(descriptor: PbChildContent, modifier: Modifier) {
    when (descriptor.valueCase) {
        PbChildContent.ValueCase.ROW -> MappingRow(descriptor = descriptor.row, modifier)
        PbChildContent.ValueCase.COLUMN -> MappingColumn(descriptor = descriptor.column, modifier)
        PbChildContent.ValueCase.TEXT -> MappingText(descriptor = descriptor.text, modifier)
        PbChildContent.ValueCase.IMAGE -> MappingImage(descriptor = descriptor.image, modifier)
        PbChildContent.ValueCase.BOX -> MappingBox(descriptor = descriptor.box, modifier)
        else -> Unit
    }
}







