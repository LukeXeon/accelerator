package open.source.accelerator.mappings

import androidx.compose.runtime.Composable
import open.source.accelerator.proto.PbChild

@Composable
fun MappingChild(descriptor: PbChild) {
    when (descriptor.valueCase) {
        PbChild.ValueCase.ROW -> MappingRow(descriptor = descriptor.row)
        PbChild.ValueCase.COLUMN -> MappingColumn(descriptor = descriptor.column)
        PbChild.ValueCase.TEXT -> MappingText(descriptor = descriptor.text)
        PbChild.ValueCase.IMAGE -> MappingImage(descriptor = descriptor.image)
        PbChild.ValueCase.BOX -> MappingBox(descriptor = descriptor.box)
        else -> Unit
    }
}







