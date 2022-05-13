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

package org.marionette.server.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.Updater
import org.marionette.server.Modifier
import org.marionette.server.MarionetteNode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnitType
import org.marionette.proto.node.IText
import org.marionette.proto.types.*
import org.marionette.server.vnode.VNode

/**
 * Adds a text view to the glance view.
 *
 * @param text The text to be displayed.
 * @param modifier [Modifier] to apply to this layout node.
 * @param style [TextStyle]] configuration for the text such as color, font, text align etc.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if
 * necessary. If the text exceeds the given number of lines, it will be truncated.
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    AbstractText(
        factory = { VNode.create("text") },
        modifier = modifier,
        text = text,
        style = style,
        maxLines = maxLines
    )
}

@Composable
inline fun <T : IText> AbstractText(
    noinline factory: () -> T,
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
    update: @DisallowComposableCalls Updater<T>.() -> Unit = {},
) {
    MarionetteNode(
        factory = factory,
        modifier = modifier,
        update = {
            this.set(text) { this.text = it }
            this.set(style?.color) { this.color = it?.toArgb() }
            this.set(style?.fontSize) { this.fontSize = it?.toProtobuf() }
            this.set(style?.fontWeight) { this.fontWeight = it }
            this.set(style?.fontStyle) { this.fontStyle = it }
            this.set(style?.textAlign) { this.textAlign = it }
            this.set(style?.textDecoration) { this.textDecoration = it?.mask }
            this.set(maxLines) { this.maxLines = it }
            update()
        }
    )
}

fun androidx.compose.ui.unit.TextUnit.toProtobuf(): TextUnit {
    val outer = this
    return textUnit {
        value = outer.value
        type = if (outer.type == TextUnitType.Em)
            TextUnit.Type.TextUnit_Type_Em
        else
            TextUnit.Type.TextUnit_Type_Sp
    }
}
