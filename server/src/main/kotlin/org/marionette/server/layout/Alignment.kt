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

import org.marionette.proto.types.Horizontal
import org.marionette.proto.types.Vertical

/**
 * A class used to specify the position of a sized box inside an available space. This is often used
 * to specify how a parent layout should place its children.
 */

data class Alignment(val horizontal: Horizontal, val vertical: Vertical) {
    /** Common [Alignment] options used in layouts. */
    companion object {
        val TopStart: Alignment = Alignment(Horizontal.Horizontal_Start, Vertical.Vertical_Top)
        val TopCenter: Alignment =
            Alignment(Horizontal.Horizontal_CenterHorizontally, Vertical.Vertical_Top)
        val TopEnd: Alignment = Alignment(Horizontal.Horizontal_End, Vertical.Vertical_Top)

        val CenterStart: Alignment =
            Alignment(Horizontal.Horizontal_Start, Vertical.Vertical_CenterVertically)
        val Center: Alignment =
            Alignment(Horizontal.Horizontal_CenterHorizontally, Vertical.Vertical_CenterVertically)
        val CenterEnd: Alignment =
            Alignment(Horizontal.Horizontal_End, Vertical.Vertical_CenterVertically)

        val BottomStart: Alignment =
            Alignment(Horizontal.Horizontal_Start, Vertical.Vertical_Bottom)
        val BottomCenter: Alignment =
            Alignment(Horizontal.Horizontal_CenterHorizontally, Vertical.Vertical_Bottom)
        val BottomEnd: Alignment = Alignment(Horizontal.Horizontal_End, Vertical.Vertical_Bottom)

        val Top: Vertical = Vertical.Vertical_Top
        val CenterVertically: Vertical = Vertical.Vertical_CenterVertically
        val Bottom: Vertical = Vertical.Vertical_Bottom

        val Start: Horizontal = Horizontal.Horizontal_Start
        val CenterHorizontally: Horizontal = Horizontal.Horizontal_CenterHorizontally
        val End: Horizontal = Horizontal.Horizontal_End
    }
}
