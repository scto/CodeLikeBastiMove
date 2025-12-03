package com.scto.codelikebastimove.core.templates.impl

import android.content.Context

import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File

class ProjectManagerImpl(private val context: Context) : ProjectManager {

    private val templates = listOf(
        EmptyActivityTemplate(),
        EmptyComposeActivityTemplate()
    )

    override fun getAvailableTemplates(): List<ProjectTemplate> = templates

    override suspend fun createProject(
        template: ProjectTemplate,
        config: ProjectConfig,
        outputPath: String
    ): Result<Project> = withContext(Dispatchers.IO) {
        try {
            val projectDir = File(outputPath, config.projectName)
            if (projectDir.exists()) {
                projectDir.deleteRecursively()
            }
            projectDir.mkdirs()

            val files = template.generateProject(config)
            
            for (file in files) {
                val targetFile = File(projectDir, file.relativePath)
                if (file.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    targetFile.parentFile?.mkdirs()
                    targetFile.writeText(file.content)
                }
            }

            val project = Project(
                name = config.projectName,
                path = projectDir.absolutePath,
                config = config,
                files = files
            )

            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}