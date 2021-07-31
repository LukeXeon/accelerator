package open.source.accelerator.mappings

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.AmbientView
import open.source.accelerator.services.AmbientImageLoader
import open.source.accelerator.services.AmbientRouter
import open.source.accelerator.proto.PbImage


private fun BlendMode.toPorterDuffMode(): PorterDuff.Mode = when (this) {
    BlendMode.Clear -> PorterDuff.Mode.CLEAR
    BlendMode.Src -> PorterDuff.Mode.SRC
    BlendMode.Dst -> PorterDuff.Mode.DST
    BlendMode.SrcOver -> PorterDuff.Mode.SRC_OVER
    BlendMode.DstOver -> PorterDuff.Mode.DST_OVER
    BlendMode.SrcIn -> PorterDuff.Mode.SRC_IN
    BlendMode.DstIn -> PorterDuff.Mode.DST_IN
    BlendMode.SrcOut -> PorterDuff.Mode.SRC_OUT
    BlendMode.DstOut -> PorterDuff.Mode.DST_OUT
    BlendMode.SrcAtop -> PorterDuff.Mode.SRC_ATOP
    BlendMode.DstAtop -> PorterDuff.Mode.DST_ATOP
    BlendMode.Xor -> PorterDuff.Mode.XOR
    BlendMode.Plus -> PorterDuff.Mode.ADD
    BlendMode.Screen -> PorterDuff.Mode.SCREEN
    BlendMode.Overlay -> PorterDuff.Mode.OVERLAY
    BlendMode.Darken -> PorterDuff.Mode.DARKEN
    BlendMode.Lighten -> PorterDuff.Mode.LIGHTEN
    BlendMode.Modulate -> {
        // b/73224934 Android PorterDuff Multiply maps to Skia Modulate
        PorterDuff.Mode.MULTIPLY
    }
    // Always return SRC_OVER as the default if there is no valid alternative
    else -> PorterDuff.Mode.SRC_OVER
}

@Composable
fun MappingImage(descriptor: PbImage) {
    val view = AmbientView.current
    val loader = AmbientImageLoader.current
    val router = AmbientRouter.current
    val onClick = remember<(String) -> Unit>(router) { { router(it) } }
    var isDirty by remember { mutableStateOf(true) }
    var drawable by remember { mutableStateOf<Drawable?>(null) }
    val callback = remember(view) {
        object : Drawable.Callback {
            override fun invalidateDrawable(who: Drawable) {
                isDirty = true
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                view.scheduleDrawable(who, what, `when`)
            }

            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                view.unscheduleDrawable(who, what)
            }
        }
    }
    DisposableEffect(descriptor, loader, drawable) {
        onDispose(loader(descriptor) {
            if (it != drawable) {
                isDirty = true
                drawable?.callback = null
                drawable = it
                it?.callback = callback
            }
        })
    }
    Image(
        painter = remember(drawable) {
            val it = drawable
            if (it == null) DrawablePainter.Unspecified else DrawablePainter(it)
        },
        modifier = Modifier.drawBehind {
            if (isDirty) {
                isDirty = false
            }
        }.apply {
            if (descriptor.hasClickUrl()) {
                clickable {
                    onClick(descriptor.clickUrl.value)
                }
            }
        }
    )
}

private data class DrawablePainter(
    private val drawable: Drawable?
) : Painter() {
    override val intrinsicSize: Size
        get() {
            return if (drawable == null) {
                Size.Unspecified
            } else {
                Size(
                    drawable.intrinsicWidth.toFloat(),
                    drawable.intrinsicWidth.toFloat()
                )
            }
        }

    override fun DrawScope.onDraw() {
        val drawable = drawable
        drawIntoCanvas { drawable?.draw(it.nativeCanvas) }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        val drawable = drawable ?: return false
        drawable.alpha = (alpha * 255).toInt()
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        drawable ?: return false
        if (colorFilter == null) {
            drawable.colorFilter = null
        } else {
            drawable.colorFilter = PorterDuffColorFilter(
                colorFilter.color.toArgb(),
                colorFilter.blendMode.toPorterDuffMode()
            )
        }
        return true
    }

    companion object {
        val Unspecified = DrawablePainter(null)
    }

}