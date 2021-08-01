package open.source.accelerator.mappings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.protobuf.Int64Value
import com.google.protobuf.MessageLite
import open.source.accelerator.proto.PbAlignment
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

fun PbAlignment.toAlignment(): Alignment {
    val align = this
    return when {
        align.horizontal == PbAlignment.Horizontal.Start && align.vertical == PbAlignment.Vertical.Top -> Alignment.TopStart
        align.horizontal == PbAlignment.Horizontal.Start && align.vertical == PbAlignment.Vertical.CenterVertically -> Alignment.CenterStart
        align.horizontal == PbAlignment.Horizontal.Start && align.vertical == PbAlignment.Vertical.Bottom -> Alignment.BottomStart
        align.horizontal == PbAlignment.Horizontal.CenterHorizontally && align.vertical == PbAlignment.Vertical.Top -> Alignment.TopCenter
        align.horizontal == PbAlignment.Horizontal.CenterHorizontally && align.vertical == PbAlignment.Vertical.CenterVertically -> Alignment.Center
        align.horizontal == PbAlignment.Horizontal.CenterHorizontally && align.vertical == PbAlignment.Vertical.Bottom -> Alignment.BottomCenter
        align.horizontal == PbAlignment.Horizontal.End && align.vertical == PbAlignment.Vertical.Top -> Alignment.TopEnd
        align.horizontal == PbAlignment.Horizontal.End && align.vertical == PbAlignment.Vertical.CenterVertically -> Alignment.CenterEnd
        align.horizontal == PbAlignment.Horizontal.End && align.vertical == PbAlignment.Vertical.Bottom -> Alignment.BottomEnd
        else -> Alignment.TopStart
    }
}

fun PbAlignment.Horizontal.toHorizontal(): Alignment.Horizontal {
    return when (this) {
        PbAlignment.Horizontal.Start -> Alignment.Start
        PbAlignment.Horizontal.CenterHorizontally -> Alignment.CenterHorizontally
        PbAlignment.Horizontal.End -> Alignment.Start
        else -> Alignment.Start
    }
}

fun PbAlignment.Vertical.toVertical(): Alignment.Vertical {
    return when (this) {
        PbAlignment.Vertical.Top -> Alignment.Top
        PbAlignment.Vertical.CenterVertically -> Alignment.CenterVertically
        PbAlignment.Vertical.Bottom -> Alignment.Bottom
        else -> Alignment.Top
    }
}

@Composable
inline fun <T : MessageLite> RenderChildren(
    children: Map<Int, T>,
    content: @Composable (T) -> Unit
) {
    @Suppress("ReplaceManualRangeWithIndicesCalls")
    for (i in 0 until children.size) {
        content(children.getValue(i))
    }
}

