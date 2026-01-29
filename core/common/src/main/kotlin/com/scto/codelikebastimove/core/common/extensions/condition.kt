package com.scto.codelikebastimove.core.common.extensions

fun doIf(condition: Boolean, action: () -> Unit) {
    if (condition) action()
}

fun doIfNotNull(value: Any?, action: () -> Unit) {
    if (value != null) action()
}

fun doIfNull(value: Any?, action: () -> Unit) {
    if (value == null) action()
}

infix fun String.makePluralIf(condition: Boolean): String {
    return "$this${if (condition) "s" else ""}"
}