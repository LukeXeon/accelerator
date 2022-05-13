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

import androidx.compose.runtime.Composable
import org.marionette.proto.node.IBox
import org.marionette.server.Modifier
import org.marionette.server.MarionetteNode
import org.marionette.server.vnode.VNode

/**
 * A layout composable with [content].
 *
 * By default, the [Box] will size itself to fit the content, unless a [Dimension] constraint has
 * been provided. When the children are smaller than the [Box], they will be placed within the box
 * subject to the [contentAlignment]. When the [content] has more than one layout child, all of
 * the children will be stacked on top of each other in the composition order.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The alignment of children within the [Box].
 * @param content The content inside the [Box].
 */
@Composable
fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    val (horizontalAlignment, verticalAlignment) = contentAlignment
    MarionetteNode(
        factory = { VNode.create<IBox>("box", true) },
        modifier = modifier,
        update = {
            this.set(horizontalAlignment) { this.horizontalAlignment = it }
            this.set(verticalAlignment) { this.verticalAlignment = it }
        },
        content = content
    )
}
