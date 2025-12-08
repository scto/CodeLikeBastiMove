package com.scto.codelikebastimove.core.templates.impl

import android.content.Context
import android.util.Log
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
        EmptyComposeActivityTemplate(),
        BottomNavigationTemplate(),
        NavigationDrawerTemplate(),
        TabbedActivityTemplate()
    )

    override fun getAvailableTemplates(): List<ProjectTemplate> = templates

    override suspend fun createProject(
        template: ProjectTemplate,
        config: ProjectConfig,
        outputPath: String
    ): Result<Project> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating project: ${config.projectName} at $outputPath")
            
            val outputDir = File(outputPath)
            if (!outputDir.exists()) {
                val created = outputDir.mkdirs()
                Log.d(TAG, "Output directory created: $created")
            }
            
            if (!outputDir.canWrite()) {
                val error = "Cannot write to output directory: $outputPath"
                Log.e(TAG, error)
                return@withContext Result.failure(Exception(error))
            }
            
            val projectDir = File(outputPath, config.projectName)
            Log.d(TAG, "Project directory: ${projectDir.absolutePath}")
            
            if (projectDir.exists()) {
                Log.d(TAG, "Deleting existing project directory")
                projectDir.deleteRecursively()
            }
            
            val dirCreated = projectDir.mkdirs()
            Log.d(TAG, "Project directory created: $dirCreated")
            
            if (!dirCreated && !projectDir.exists()) {
                val error = "Failed to create project directory: ${projectDir.absolutePath}"
                Log.e(TAG, error)
                return@withContext Result.failure(Exception(error))
            }

            val files = template.generateProject(config)
            Log.d(TAG, "Generated ${files.size} files from template")
            
            var filesCreated = 0
            for (file in files) {
                val targetFile = File(projectDir, file.relativePath)
                if (file.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    targetFile.parentFile?.mkdirs()
                    targetFile.writeText(file.content)
                    filesCreated++
                }
            }
            Log.d(TAG, "Created $filesCreated files")

            val project = Project(
                name = config.projectName,
                path = projectDir.absolutePath,
                config = config,
                files = files
            )

            Log.d(TAG, "Project created successfully: ${project.path}")
            Result.success(project)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create project: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    companion object {
        private const val TAG = "ProjectManagerImpl"
    }
}
