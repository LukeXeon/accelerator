package open.source.accelerator.mappings

import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import open.source.accelerator.proto.PbColumn

@Composable
fun MappingColumn(descriptor: PbColumn) {
    LazyColumnFor(
        contentPadding = descriptor.contentPadding.toPaddingValues(),
        items = descriptor.childrenList
    ) {
        MappingChild(it)
    }
}