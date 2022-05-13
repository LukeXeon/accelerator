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
import org.marionette.proto.types.*
import org.marionette.server.Modifier
import org.marionette.server.MarionetteNode
import org.marionette.server.vnode.VNode

/** Scope defining modifiers only available on rows. */
interface RowScope {
    /**
     * Size the element's width to split the available space with other weighted sibling elements
     * in the [Row]. The parent will divide the horizontal space remaining after measuring
     * unweighted child elements and distribute it according to the weights, the default weight
     * being 1.
     */
    fun Modifier.defaultWeight(): Modifier
}

private object RowScopeImplInstance : RowScope {
    override fun Modifier.defaultWeight(): Modifier {
        return this.then(
            WidthModifier(
                Dimension.newBuilder()
                    .setType(Dimension.Kind.Dimension_Expand).build()
            )
        )
    }
}

/**
 * A layout composable with [content], which lays its children out in a Row.
 *
 * By default, the [Row] will size itself to fit the content, unless a [Dimension] constraint has
 * been provided. When children are smaller than the size of the [Row], they will be placed
 * within the available space subject to [verticalAlignment] and [horizontalAlignment].
 *
 * @param modifier The modifier to be applied to the layout.
 * @param horizontalAlignment The horizontal alignment to apply to the set of children, when they do
 *   not consume the full width of the [Row] (i.e. whether to push the children towards the start,
 *   center or end of the [Row]).
 * @param verticalAlignment The horizontal alignment to apply to children when they are smaller
 *  than the height of the [Row]
 * @param content The content inside the [Row]
 */
@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalAlignment: Horizontal = Horizontal.Horizontal_Start,
    verticalAlignment: Vertical = Vertical.Vertical_Top,
    content: @Composable RowScope.() -> Unit,
) {
    MarionetteNode(
        factory = { VNode.create<IBox>("row", true) },
        modifier = modifier,
        update = {
            this.set(verticalAlignment) { this.verticalAlignment = it }
            this.set(horizontalAlignment) { this.horizontalAlignment = it }
        },
        content = { RowScopeImplInstance.content() }
    )
}
