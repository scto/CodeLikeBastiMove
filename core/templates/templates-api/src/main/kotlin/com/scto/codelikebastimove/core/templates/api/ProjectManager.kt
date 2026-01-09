package com.scto.codelikebastimove.core.templates.api

interface ProjectManager {
    fun getAvailableTemplates(): List<ProjectTemplate>
    
    suspend fun createProject(
        template: ProjectTemplate,
        config: ProjectConfig,
        outputPath: String
    ): Result<Project>
}
