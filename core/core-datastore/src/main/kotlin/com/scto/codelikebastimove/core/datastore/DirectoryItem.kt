package com.scto.codelikebastimove.core.datastore

data class DirectoryItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val isProject: Boolean,
    val lastModified: Long,
    val size: Long = 0
)
