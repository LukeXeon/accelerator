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
package org.marionette.server.layout

import androidx.annotation.RestrictTo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.marionette.proto.types.*
import org.marionette.server.Modifier


/**
 * Apply additional space along each edge of the content in [Dp]: [start], [top], [end] and
 * [bottom]. The start and end edges will be determined by layout direction of the current locale.
 * Padding is applied before content measurement and takes precedence; content may only be as large
 * as the remaining space.
 *
 * If any value is not defined, it will be [0.dp] or whatever value was defined by an earlier
 * modifier.
 */
fun Modifier.padding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): Modifier = this.then(
    PaddingModifier(
        start = start,
        top = top,
        end = end,
        bottom = bottom,
    )
)

/**
 * Apply [horizontal] dp space along the left and right edges of the content, and [vertical] dp
 * space along the top and bottom edges.
 *
 * If any value is not defined, it will be [0.dp] or whatever value was defined by an earlier
 * modifier.
 */
fun Modifier.padding(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
): Modifier = this.then(
    PaddingModifier(
        start = horizontal,
        top = vertical,
        end = horizontal,
        bottom = vertical,
    )
)

/**
 * Apply [all] dp of additional space along each edge of the content, left, top, right and bottom.
 */
fun Modifier.padding(all: Dp): Modifier {
    return this.then(
        PaddingModifier(
            start = all,
            top = all,
            end = all,
            bottom = all,
        )
    )
}

/**
 *  Apply additional space along each edge of the content in [Dp]: [left], [top], [right] and
 * [bottom], ignoring the current locale's layout direction.
 */
fun Modifier.absolutePadding(
    left: Dp = 0.dp,
    top: Dp = 0.dp,
    right: Dp = 0.dp,
    bottom: Dp = 0.dp,
): Modifier = this.then(
    PaddingModifier(
        left = left,
        top = top,
        right = right,
        bottom = bottom,
    )
)

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Modifier.collectPadding(): PaddingModifier? =
    foldIn<PaddingModifier?>(null) { acc, modifier ->
        if (modifier is PaddingModifier) {
            (acc ?: PaddingModifier()) + modifier
        } else {
            acc
        }
    }

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PaddingModifier(
    left: Dp = Dp.Hairline,
    start: Dp = Dp.Hairline,
    top: Dp = Dp.Hairline,
    right: Dp = Dp.Hairline,
    end: Dp = Dp.Hairline,
    bottom: Dp = Dp.Hairline,
) : Modifier.Element {
    val padding = padding {
        this.left = left.value
        this.start = start.value
        this.top = top.value
        this.right = right.value
        this.end = end.value
        this.bottom = bottom.value
    }

    operator fun plus(other: PaddingModifier) =
        PaddingModifier(
            left = (padding.left + other.padding.left).dp,
            start = (padding.start + other.padding.start).dp,
            top = (padding.top + other.padding.top).dp,
            right = (padding.right + other.padding.right).dp,
            end = (padding.end + other.padding.end).dp,
            bottom = (padding.bottom + other.padding.bottom).dp,
        )
}



