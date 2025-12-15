/*
package com.scto.codelikebastimove.core.templates.api

interface ProjectManager {
    fun getAvailableTemplates(): List<ProjectTemplate>
    
    suspend fun createProject(
        template: ProjectTemplate,
        config: ProjectConfig,
        outputPath: String
    ): Result<Project>
}
*/
package com.scto.codelikebastimove.core.templates.api

import kotlinx.coroutines.flow.StateFlow

interface ProjectManager {
    val currentProject: StateFlow<Project?>
    
    suspend fun createProject(config: ProjectConfig): Project
    suspend fun openProject(path: String): Project
    fun closeProject()
    
    // Weitere Methoden, falls vorhanden, hier belassen
}