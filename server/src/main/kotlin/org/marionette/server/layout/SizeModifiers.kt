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
import org.marionette.proto.types.Dimension
import org.marionette.proto.types.dimension
import org.marionette.server.Modifier

/**
 * Modifier to represent the width of an element.
 *
 * @suppress
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class WidthModifier(val width: Dimension) : Modifier.Element

/** Sets the absolute width of an element, in [Dp]. */
fun Modifier.width(width: Dp): Modifier =
    this.then(WidthModifier(dimension { dp = width.value }))

/** Specifies that the width of the element should wrap its contents. */
fun Modifier.wrapContentWidth(): Modifier =
    this.then(WidthModifier(dimension { type = Dimension.Kind.Dimension_Wrap }))

/**
 * Specifies that the width of the element should expand to the size of its parent. Note that if
 * multiple elements within a linear container (e.g. Row or Column) have their width as
 * [fillMaxWidth], then they will all share the remaining space.
 */
fun Modifier.fillMaxWidth(): Modifier =
    this.then(WidthModifier(dimension { type = Dimension.Kind.Dimension_Fill }))

/**
 * Modifier to represent the height of an element.
 *
 * @suppress
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class HeightModifier(val height: Dimension) : Modifier.Element

/** Sets the absolute height of an element, in [Dp]. */
fun Modifier.height(height: Dp): Modifier =
    this.then(HeightModifier(dimension { dp = height.value }))

/** Specifies that the height of the element should wrap its contents. */
fun Modifier.wrapContentHeight(): Modifier =
    this.then(HeightModifier(dimension { type = Dimension.Kind.Dimension_Wrap }))

/**
 * Specifies that the height of the element should expand to the size of its parent. Note that if
 * multiple elements within a linear container (e.g. Row or Column) have their height as
 * expandHeight, then they will all share the remaining space.
 */
fun Modifier.fillMaxHeight(): Modifier =
    this.then(HeightModifier(dimension { type = Dimension.Kind.Dimension_Fill }))

/** Sets both the width and height of an element, in [Dp]. */
fun Modifier.size(size: Dp): Modifier = this.width(size).height(size)

/** Sets both the width and height of an element, in [Dp]. */
fun Modifier.size(width: Dp, height: Dp): Modifier =
    this.width(width).height(height)

/** Wrap both the width and height's content. */
fun Modifier.wrapContentSize(): Modifier =
    this.wrapContentHeight().wrapContentWidth()

/** Set both the width and height to the maximum available space. */
fun Modifier.fillMaxSize(): Modifier = this.fillMaxWidth().fillMaxHeight()