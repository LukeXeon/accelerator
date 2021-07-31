package open.source.accelerator.mappings

import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.runtime.Composable
import open.source.accelerator.proto.PbRow

@Composable
fun MappingRow(descriptor: PbRow) {
    LazyRowFor(
        contentPadding = descriptor.contentPadding.toPaddingValues(),
        items = descriptor.childrenList
    ) {
        MappingChild(it)
    }
}
