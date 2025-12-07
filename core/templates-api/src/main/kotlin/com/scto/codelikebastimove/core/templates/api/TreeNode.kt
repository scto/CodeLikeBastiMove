package com.scto.codelikebastimove.core.templates.api

data class TreeNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val isExpanded: Boolean = false,
    val children: List<TreeNode> = emptyList(),
    val level: Int = 0
)
