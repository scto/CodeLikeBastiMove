package com.scto.codelikebastimove.core.common.extensions

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun Long.formatSize(): String {
    val kb = 1024
    val mb = kb * 1024

    return when {
        this >= mb -> String.format("%.2f MB", toDouble() / mb)
        this >= kb -> String.format("%.2f KB", toDouble() / kb)
        else -> "$this Bytes"
    }
}
