package open.source.accelerator.mappings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.protobuf.Int64Value
import open.source.accelerator.proto.PbPadding
import open.source.accelerator.proto.PbUiUnit

@Composable
fun PbUiUnit.toUnit(): Dp {
    val density = AmbientDensity.current
    return with(density) {
        when (type) {
            PbUiUnit.Type.Dp -> value.dp
            PbUiUnit.Type.Sp -> value.sp.toDp()
            else -> value.toDp()
        }
    }
}

@Composable
fun PbPadding.toPaddingValues(): PaddingValues {
    return PaddingValues(
        start.toUnit(),
        top.toUnit(),
        right.toUnit(),
        end.toUnit()
    )
}

fun Int64Value.toColor(): Color {
    return Color(value)
}