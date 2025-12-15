/*
package com.scto.codelikebastimove.core.templates.impl

import android.content.Context
import com.scto.codelikebastimove.core.logger.CLBMLogger
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
            CLBMLogger.d(TAG, "Creating project: ${config.projectName} at $outputPath")
            
            val outputDir = File(outputPath)
            if (!outputDir.exists()) {
                val created = outputDir.mkdirs()
                CLBMLogger.d(TAG, "Output directory created: $created")
            }
            
            if (!outputDir.canWrite()) {
                val error = "Cannot write to output directory: $outputPath"
                CLBMLogger.e(TAG, error)
                return@withContext Result.failure(Exception(error))
            }
            
            val projectDir = File(outputPath, config.projectName)
            CLBMLogger.d(TAG, "Project directory: ${projectDir.absolutePath}")
            
            if (projectDir.exists()) {
                CLBMLogger.d(TAG, "Deleting existing project directory")
                projectDir.deleteRecursively()
            }
            
            val dirCreated = projectDir.mkdirs()
            CLBMLogger.d(TAG, "Project directory created: $dirCreated")
            
            if (!dirCreated && !projectDir.exists()) {
                val error = "Failed to create project directory: ${projectDir.absolutePath}"
                CLBMLogger.e(TAG, error)
                return@withContext Result.failure(Exception(error))
            }

            val files = template.generateProject(config)
            CLBMLogger.d(TAG, "Generated ${files.size} files from template")
            
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
            CLBMLogger.d(TAG, "Created $filesCreated files")

            val project = Project(
                name = config.projectName,
                path = projectDir.absolutePath,
                config = config,
                files = files
            )

            CLBMLogger.d(TAG, "Project created successfully: ${project.path}")
            Result.success(project)
        } catch (e: Exception) {
            CLBMLogger.e(TAG, "Failed to create project: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    companion object {
        private const val TAG = "ProjectManagerImpl"
    }
}

*/
package com.scto.codelikebastimove.core.templates.impl

import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File

class ProjectManagerImpl(
    private val userPreferencesRepository: UserPreferencesRepository
) : ProjectManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    override val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    init {
        scope.launch {
            loadProjects()
        }
    }

    private suspend fun loadProjects() {
        try {
            val recentItems = userPreferencesRepository.recentProjects.first()
            val loadedProjects = recentItems.mapNotNull { item ->
                val file = File(item.path)
                if (file.exists()) {
                    Project(
                        name = file.name,
                        location = item.path,
                        language = ProjectLanguage.KOTLIN, // Default or inferred
                        config = createDefaultConfig(file.name),
                        files = emptyList() // Not loading all files for the list view
                    )
                } else {
                    null
                }
            }
            _projects.value = loadedProjects
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun createProject(
        name: String,
        location: String,
        language: ProjectLanguage,
        template: ProjectTemplate
    ): Result<Project> {
        return withContext(Dispatchers.IO) {
            try {
                val projectDir = File(location, name)
                if (projectDir.exists()) {
                    return@withContext Result.failure(Exception("Project directory already exists"))
                }
                if (!projectDir.mkdirs()) {
                    return@withContext Result.failure(Exception("Could not create project directory"))
                }

                val config = createDefaultConfig(name)
                val files = template.generate(config)

                files.forEach { projectFile ->
                    val file = File(projectDir, projectFile.path)
                    file.parentFile?.mkdirs()
                    file.writeText(projectFile.content)
                }

                val project = Project(
                    name = name,
                    location = projectDir.absolutePath,
                    language = language,
                    config = config,
                    files = files
                )

                userPreferencesRepository.addRecentProject(projectDir.absolutePath)
                loadProjects()

                Result.success(project)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun openProject(path: String): Result<Project> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (!file.exists()) {
                    return@withContext Result.failure(Exception("Project path not found: $path"))
                }
                
                // Add to recent
                userPreferencesRepository.addRecentProject(path)
                
                val project = Project(
                    name = file.name,
                    location = file.absolutePath,
                    language = ProjectLanguage.KOTLIN,
                    config = createDefaultConfig(file.name),
                    files = emptyList() 
                )
                
                loadProjects()
                
                Result.success(project)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun createDefaultConfig(name: String): ProjectConfig {
        return ProjectConfig(
            applicationId = "com.example.${name.lowercase().replace("\\s".toRegex(), "")}",
            minSdk = 29,
            targetSdk = 35,
            compileSdk = 36,
            versionCode = 1,
            versionName = "1.0"
        )
    }
}