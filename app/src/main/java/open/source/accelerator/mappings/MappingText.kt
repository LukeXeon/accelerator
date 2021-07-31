package open.source.accelerator.mappings

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.protobuf.Int32Value
import open.source.accelerator.services.AmbientRouter
import open.source.accelerator.proto.PbText
import open.source.accelerator.proto.PbUiUnit
import kotlin.math.max
import kotlin.math.min

@Composable
private fun PbUiUnit.toTextUnit(): TextUnit {
    val density = AmbientDensity.current
    val value = value
    return with(density) {
        when (type) {
            PbUiUnit.Type.Dp -> TextUnit.Sp(value.dp.toSp().value)
            PbUiUnit.Type.Sp -> TextUnit.Sp(value)
            else -> TextUnit.Sp(value.toSp().value)
        }
    }
}

private fun PbText.FontStyle.toFontStyle(): FontStyle? {
    return when (this) {
        PbText.FontStyle.Italic -> FontStyle.Italic
        PbText.FontStyle.Normal -> FontStyle.Normal
        PbText.FontStyle.UNRECOGNIZED -> null
    }
}

private fun Int32Value.toFontWeight(): FontWeight {
    return FontWeight(min(1000, max(1, value)))
}

private fun PbText.TextOverflow.toTextOverflow(): TextOverflow {
    return when (this) {
        PbText.TextOverflow.Ellipsis -> TextOverflow.Ellipsis
        PbText.TextOverflow.Clip -> TextOverflow.Clip
        PbText.TextOverflow.UNRECOGNIZED -> TextOverflow.Clip
    }
}

private fun PbText.TextAlign.toTextAlign(): TextAlign? {
    return when (this) {
        PbText.TextAlign.Left -> TextAlign.Left
        PbText.TextAlign.Right -> TextAlign.Right
        PbText.TextAlign.Center -> TextAlign.Center
        PbText.TextAlign.Justify -> TextAlign.Justify
        PbText.TextAlign.Start -> TextAlign.Start
        PbText.TextAlign.End -> TextAlign.End
        PbText.TextAlign.UNRECOGNIZED -> null
    }
}

private fun List<PbText.TextDecoration>.toTextDecoration(): TextDecoration? {
    if (isEmpty()) {
        return null
    }
    TextDecoration.combine(this.map {
        when (it) {
            PbText.TextDecoration.None -> TextDecoration.None
            PbText.TextDecoration.Underline -> TextDecoration.Underline
            PbText.TextDecoration.LineThrough -> TextDecoration.LineThrough
            PbText.TextDecoration.UNRECOGNIZED -> TextDecoration.None
        }
    })
    return null
}

@Composable
fun MappingText(descriptor: PbText) {
    val router = AmbientRouter.current
    val onClick = remember<(String) -> Unit>(router) { { router(it) } }
    Text(
        text = descriptor.text,
        fontSize = if (descriptor.hasTextSize())
            descriptor.textSize.toTextUnit()
        else
            TextUnit.Unspecified,
        color = if (descriptor.hasColor())
            descriptor.color.toColor()
        else
            Color.Unspecified,
        fontStyle = descriptor.fontStyle.toFontStyle(),
        fontWeight = if (descriptor.hasFontWeight())
            descriptor.fontWeight.toFontWeight()
        else
            null,
        letterSpacing = if (descriptor.hasLetterSpacing())
            descriptor.letterSpacing.toTextUnit()
        else
            TextUnit.Unspecified,
        textDecoration = descriptor.textDecorationList.toTextDecoration(),
        textAlign = descriptor.textAlign.toTextAlign(),
        lineHeight = if (descriptor.hasLineHeight())
            descriptor.lineHeight.toTextUnit()
        else
            TextUnit.Unspecified,
        overflow = descriptor.overflow.toTextOverflow(),
        softWrap = if (descriptor.hasSoftWrap())
            descriptor.softWrap.value
        else
            true,
        maxLines = if (descriptor.hasMaxLines())
            descriptor.maxLines.value
        else
            Int.MAX_VALUE,
        modifier = Modifier.apply {
            if (descriptor.hasClickUrl()) {
                clickable {
                    onClick(descriptor.clickUrl.value)
                }
            }
        }
    )
}
