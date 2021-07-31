package open.source.accelerator.mappings

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import open.source.accelerator.proto.PbBox


@Composable
fun MappingBox(descriptor: PbBox) {
    Box {
        descriptor.childrenList.forEach {
            MappingChild(descriptor = it)
        }
    }
}