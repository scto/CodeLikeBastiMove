package com.scto.codelikebastimove.core.templates.api

data class Project(
    val name: String,
    val path: String,
    val config: ProjectConfig,
    val files: List<ProjectFile>
)
