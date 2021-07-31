package open.source.accelerator.services

import android.graphics.drawable.Drawable
import androidx.compose.runtime.staticAmbientOf
import open.source.accelerator.proto.PbImage

typealias ImageDispose = () -> Unit

typealias ImageCallback = (Drawable?) -> Unit

typealias ImageLoader = (PbImage, ImageCallback) -> ImageDispose

val AmbientRouter = staticAmbientOf<(String) -> Unit>()

val AmbientImageLoader = staticAmbientOf<ImageLoader>()