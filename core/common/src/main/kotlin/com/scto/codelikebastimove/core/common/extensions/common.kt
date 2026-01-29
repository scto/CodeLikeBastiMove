@file:Suppress("NOTHING_TO_INLINE")

package com.scto.codelikebastimove.core.common.extensions

import java.util.Locale

inline fun Any?.isNotNull() = this != null

inline fun Any?.isNull() = this == null

fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}