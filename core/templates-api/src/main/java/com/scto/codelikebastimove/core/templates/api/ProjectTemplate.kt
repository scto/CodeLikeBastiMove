package com.scto.codelikebastimove.core.templates.api

interface ProjectTemplate {
    val name: String
    val description: String
    
    fun generateProject(config: ProjectConfig): List<ProjectFile>
}
