/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.marionette.server

import androidx.annotation.RestrictTo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.marionette.proto.types.Background
import org.marionette.proto.types.BackgroundKt
import org.marionette.proto.types.ContentScale
import org.marionette.proto.types.background

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BackgroundModifier private constructor(
    color: Color?,
    image: String?,
    contentScale: ContentScale = ContentScale.ContentScale_FillBounds,
) : Modifier.Element {
    val background: Background

    init {
        require((color != null) xor (image != null)) {
            "Exactly one of color and image must be non-null"
        }
        background = background {
            if (color != null) {
                this.color = color.toArgb()
            } else if (!image.isNullOrEmpty()) {
                this.image = BackgroundKt.image {
                    this.url = url
                    this.contentScale = contentScale
                }
            }
        }
    }

    constructor(color: Color) :
            this(color = color, image = null)

    constructor(image: String, contentScale: ContentScale) :
            this(color = null, image = image, contentScale = contentScale)
}

/**
 * Apply a background color to the element this modifier is attached to. This will cause the
 * element to paint the specified [ColorProvider] as its background, which will fill the bounds of
 * the element.
 */
fun Modifier.background(color: Color): Modifier =
    this.then(BackgroundModifier(color))

/**
 * Apply a background image to the element this modifier is attached to.
 */
fun Modifier.background(
    image: String,
    contentScale: ContentScale = ContentScale.ContentScale_FillBounds,
): Modifier =
    this.then(BackgroundModifier(image, contentScale))



