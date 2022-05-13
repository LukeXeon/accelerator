package org.marionette.server

import androidx.annotation.RestrictTo
import androidx.collection.SparseArrayCompat

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

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : Modifier.Element> Modifier.findModifier(): T? =
    this.foldIn<T?>(null) { acc, cur ->
        if (cur is T) {
            cur
        } else {
            acc
        }
    }

/**
 * Find the last modifier of the given type, and create a new [Modifier] which is equivalent
 * with the previous one, but without any modifiers of specified type.
 * @suppress
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : Modifier.Element> Modifier.extractModifier(): Pair<T?, Modifier> =
    if (any { it is T }) {
        foldIn<Pair<T?, Modifier>>(null to Modifier) { acc, cur ->
            if (cur is T) {
                cur to acc.second
            } else {
                acc.first to acc.second.then(cur)
            }
        }
    } else {
        null to this
    }


inline fun <T, R> SparseArrayCompat<T>.map(transform: (Int, T) -> R): List<R> {
    return (0 until size()).map {
        transform(keyAt(it), valueAt(it))
    }
}

inline fun <T : Any> SparseArrayCompat<T>.getOrPut(
    key: Int,
    factory: () -> T,
): T {
    var value = get(key)
    if (value == null) {
        value = factory()
        put(key, value)
    }
    return value
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> SparseArrayCompat<T>.set(key: Int, value: T) {
    put(key, value)
}

inline fun <T> MutableList<T>.remove(
    index: Int,
    count: Int,
    onRemove: (T.() -> Unit) = {},
) {
    if (count == 1) {
        removeAt(index).onRemove()
    } else {
        subList(index, index + count).onEach {
            forEach(onRemove)
        }.clear()
    }
}

fun <T> MutableList<T>.move(
    from: Int,
    to: Int,
    count: Int,
) {
    val dest = if (from > to) to else to - count
    if (count == 1) {
        if (from == to + 1 || from == to - 1) {
            // Adjacent elements, perform swap to avoid backing array manipulations.
            val fromEl = get(from)
            val toEl = set(to, fromEl)
            set(from, toEl)
        } else {
            val fromEl = removeAt(from)
            add(dest, fromEl)
        }
    } else {
        val subView = subList(from, from + count)
        val subCopy = subView.toMutableList()
        subView.clear()
        addAll(dest, subCopy)
    }
}


