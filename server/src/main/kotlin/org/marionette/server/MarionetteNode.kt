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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.Updater
import org.marionette.proto.node.INode
import org.marionette.server.layout.HeightModifier
import org.marionette.server.layout.PaddingModifier
import org.marionette.server.layout.WidthModifier

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Composable
inline fun <T : INode> MarionetteNode(
    noinline factory: () -> T,
    modifier: Modifier,
    update: @DisallowComposableCalls Updater<T>.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val onClick = modifier.findModifier<ClickableModifier>()?.onClick
    val visibility = modifier.findModifier<VisibilityModifier>()?.visibility
    val width = modifier.findModifier<WidthModifier>()?.width
    val height = modifier.findModifier<HeightModifier>()?.height
    val background = modifier.findModifier<BackgroundModifier>()?.background
    val padding = modifier.findModifier<PaddingModifier>()?.padding
    ComposeNode<T, Applier>(factory, {
        update()
        set(onClick) { this.onClick = it }
        set(background) { this.background = it }
        set(visibility) { this.visibility = it }
        set(width) { this.width = it }
        set(height) { this.height = it }
        set(padding) { this.padding = it }
    }, content)
}


