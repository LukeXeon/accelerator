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
import androidx.compose.runtime.AbstractApplier
import org.marionette.proto.node.INode
import org.marionette.server.vnode.VParent
import org.marionette.server.vnode.VRoot

/**
 * Applier for the Glance composition.
 * @suppress
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class Applier(root: VRoot) : AbstractApplier<Any>(root) {

    override fun onClear() {
        (root as VRoot).clear()
    }

    override fun onEndChanges() {
        (root as VRoot).commit()
    }

    override fun move(from: Int, to: Int, count: Int) {
        (current as VParent).move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        (current as VParent).remove(index, count)
    }

    override fun insertTopDown(index: Int, instance: Any) {
        (current as VParent).insert(index, (instance as INode))
    }

    override fun insertBottomUp(index: Int, instance: Any) {

    }
}
