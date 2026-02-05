package com.scto.codelikebastimove.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.DirectoryItem
import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.StoredProject
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.logger.CLBMLogger
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import com.scto.codelikebastimove.core.templates.impl.ProjectManagerImpl
import com.scto.codelikebastimove.feature.home.navigation.HomeDestination
import com.scto.codelikebastimove.feature.submodulemaker.generator.ModuleGenerator
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)
    private val projectManager: ProjectManager = ProjectManagerImpl(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val currentProjectPath: StateFlow<String?> = _uiState
        .map { state -> state.projectPath.ifBlank { null } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val navigationStack = mutableListOf<HomeDestination>()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            var rootDir = repository.getRootDirectoryOnce()
            
            if (rootDir.isBlank()) {
                @Suppress("DEPRECATION")
                val externalDir = android.os.Environment.getExternalStorageDirectory()
                val defaultDir = File(externalDir, "CLBMProjects")
                try {
                    if (!defaultDir.exists()) {
                        defaultDir.mkdirs()
                    }
                    rootDir = defaultDir.absolutePath
                    repository.setRootDirectory(rootDir)
                    CLBMLogger.d(TAG, "Initialized default CLBMProjects directory: $rootDir")
                } catch (e: Exception) {
                    CLBMLogger.e(TAG, "Failed to create CLBMProjects directory", e)
                }
            }
            
            if (rootDir.isNotBlank()) {
                val dir = File(rootDir)
                if (!dir.exists()) {
                    try {
                        dir.mkdirs()
                    } catch (e: Exception) {
                        CLBMLogger.e(TAG, "Failed to create root directory", e)
                    }
                }
                _uiState.update { it.copy(rootDirectory = rootDir) }
                refreshDirectoryContents()
                scanAndSyncProjects(rootDir)
            }

            launch {
                repository.projects.collect { projects ->
                    _uiState.update { it.copy(projects = projects) }
                }
            }

            try {
                val prefs = repository.userPreferences.first()
                val lastPath = prefs.currentProjectPath

                if (!lastPath.isNullOrBlank()) {
                    val projectFile = File(lastPath)
                    if (projectFile.exists()) {
                        CLBMLogger.d(TAG, "Restoring last opened project: $lastPath")

                        _uiState.update {
                            it.copy(
                                projectName = projectFile.name,
                                projectPath = lastPath,
                                isProjectOpen = true,
                                currentDestination = HomeDestination.IDE,
                            )
                        }
                    } else {
                        repository.setCurrentProjectPath("")
                    }
                }
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Failed to restore last opened project", e)
            }
        }
    }

    private suspend fun scanAndSyncProjects(rootDir: String) {
        val dir = File(rootDir)
        if (!dir.exists() || !dir.isDirectory) return

        val existingProjects = repository.getProjectsOnce()
        val existingPaths = existingProjects.map { it.path }.toSet()

        val projectsOnDisk = dir.listFiles()
            ?.filter { it.isDirectory && isProjectDirectory(it) }
            ?: emptyList()

        for (projectDir in projectsOnDisk) {
            if (projectDir.absolutePath !in existingPaths) {
                CLBMLogger.d(TAG, "Found new project on disk: ${projectDir.absolutePath}")
                val storedProject = StoredProject(
                    name = projectDir.name,
                    path = projectDir.absolutePath,
                    packageName = detectPackageName(projectDir),
                    templateType = ProjectTemplateType.EMPTY_COMPOSE,
                    createdAt = projectDir.lastModified(),
                    lastOpenedAt = projectDir.lastModified(),
                )
                repository.addProject(storedProject)
            }
        }

        val diskPaths = projectsOnDisk.map { it.absolutePath }.toSet()
        for (project in existingProjects) {
            if (project.path.startsWith(rootDir) && project.path !in diskPaths) {
                CLBMLogger.d(TAG, "Removing missing project from datastore: ${project.path}")
                repository.removeProject(project.path)
            }
        }
    }

    fun refreshDirectoryContents() {
        val rootDir = _uiState.value.rootDirectory
        if (rootDir.isBlank()) return

        val dir = File(rootDir)
        if (!dir.exists() || !dir.isDirectory) {
            _uiState.update { it.copy(directoryContents = emptyList()) }
            return
        }

        val items = dir.listFiles()
            ?.map { file ->
                DirectoryItem(
                    name = file.name,
                    path = file.absolutePath,
                    isDirectory = file.isDirectory,
                    isProject = isProjectDirectory(file),
                    lastModified = file.lastModified(),
                    size = if (file.isFile) file.length() else 0,
                )
            }
            ?.sortedWith(
                compareBy<DirectoryItem> { !it.isDirectory }
                    .thenBy { !it.isProject }
                    .thenBy { it.name.lowercase() }
            ) ?: emptyList()

        _uiState.update { it.copy(directoryContents = items) }
    }

    fun onNavigate(destination: HomeDestination) {
        val currentDestination = _uiState.value.currentDestination
        if (currentDestination != destination) {
            navigationStack.add(currentDestination)
        }
        _uiState.update { it.copy(currentDestination = destination) }
    }

    fun onBackPressed(): Boolean {
        return if (navigationStack.isNotEmpty()) {
            val previousDestination = navigationStack.removeAt(navigationStack.size - 1)
            _uiState.update { it.copy(currentDestination = previousDestination) }
            true
        } else {
            if (_uiState.value.currentDestination != HomeDestination.Home) {
                _uiState.update { it.copy(currentDestination = HomeDestination.Home) }
                true
            } else {
                false
            }
        }
    }

    fun onOpenProject(projectPath: String, projectName: String) {
        viewModelScope.launch {
            repository.updateProjectLastOpened(projectPath, System.currentTimeMillis())
            repository.setCurrentProjectPath(projectPath)
        }
        navigationStack.add(_uiState.value.currentDestination)
        _uiState.update {
            it.copy(
                projectName = projectName,
                projectPath = projectPath,
                isProjectOpen = true,
                currentDestination = HomeDestination.IDE,
            )
        }
    }

    fun onCloseProject() {
        viewModelScope.launch { repository.setCurrentProjectPath("") }
        navigationStack.clear()
        _uiState.update {
            it.copy(
                isProjectOpen = false,
                projectPath = "",
                currentDestination = HomeDestination.Home,
            )
        }
    }

    fun createProject(
        name: String,
        packageName: String,
        templateType: ProjectTemplateType,
        minSdk: Int = 24,
        useKotlin: Boolean = true,
        useKotlinDsl: Boolean = true,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            CLBMLogger.d(TAG, "Creating project: $name with template: $templateType")

            val rootDir = _uiState.value.rootDirectory

            if (rootDir.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Root directory not set. Please complete onboarding first.",
                    )
                }
                return@launch
            }

            val rootDirFile = File(rootDir)
            if (!rootDirFile.exists()) {
                rootDirFile.mkdirs()
            }

            if (!rootDirFile.canWrite()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Cannot write to project directory. Check storage permissions.",
                    )
                }
                return@launch
            }

            val templates = projectManager.getAvailableTemplates()
            val template = templates.find {
                when (templateType) {
                    ProjectTemplateType.EMPTY_ACTIVITY -> it.name == "Empty Activity"
                    ProjectTemplateType.EMPTY_COMPOSE -> it.name == "Empty Compose Activity"
                    ProjectTemplateType.BOTTOM_NAVIGATION -> it.name == "Bottom Navigation Activity"
                    ProjectTemplateType.NAVIGATION_DRAWER -> it.name == "Navigation Drawer Activity"
                    ProjectTemplateType.TABBED -> it.name == "Tabbed Activity"
                    ProjectTemplateType.MULTI_MODULE -> it.name == "Empty Compose Activity"
                    ProjectTemplateType.MVVM_CLEAN -> it.name == "MVVM Clean Architecture"
                    ProjectTemplateType.WEAR_OS -> it.name == "Empty Compose Activity"
                    ProjectTemplateType.RESPONSIVE_FOLDABLE -> it.name == "Empty Compose Activity"
                }
            }

            if (template == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Template not found") }
                return@launch
            }

            val config = ProjectConfig(
                projectName = name,
                packageName = packageName,
                minSdk = minSdk,
                language = if (useKotlin) ProjectLanguage.KOTLIN else ProjectLanguage.JAVA,
                gradleLanguage = if (useKotlinDsl) GradleLanguage.KOTLIN_DSL else GradleLanguage.GROOVY,
            )

            val result = projectManager.createProject(template, config, rootDir)

            result
                .onSuccess { project ->
                    val storedProject = StoredProject(
                        name = name,
                        path = project.path,
                        packageName = packageName,
                        templateType = templateType,
                        createdAt = System.currentTimeMillis(),
                        lastOpenedAt = System.currentTimeMillis(),
                    )
                    repository.addProject(storedProject)
                    refreshDirectoryContents()

                    repository.setCurrentProjectPath(project.path)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            projectName = name,
                            projectPath = project.path,
                            isProjectOpen = true,
                            currentDestination = HomeDestination.IDE,
                        )
                    }
                }
                .onFailure { error ->
                    CLBMLogger.e(TAG, "Failed to create project: ${error.message}", error)
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun createSubModule(config: ModuleConfig) {
        val currentProjectPath = _uiState.value.projectPath
        if (currentProjectPath.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Kein Projekt geöffnet") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val projectDir = File(currentProjectPath)
            val moduleDir = File(projectDir, config.directoryPath)

            if (moduleDir.exists()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Modul existiert bereits: ${config.gradlePath}"
                    )
                }
                return@launch
            }

            ModuleGenerator().generateModule(projectDir, config)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    notifyFileSystemChanged()
                }
                .onFailure { error: Throwable ->
                    CLBMLogger.e(TAG, "Error creating submodule", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Fehler beim Erstellen des Moduls: ${error.message}",
                        )
                    }
                }
        }
    }

    fun deleteProject(projectPath: String) {
        viewModelScope.launch {
            repository.removeProject(projectPath)
            try {
                val deleted = File(projectPath).deleteRecursively()
                if (deleted) {
                    CLBMLogger.d(TAG, "Project deleted successfully: $projectPath")
                }
                refreshDirectoryContents()
                notifyFileSystemChanged()
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Failed to delete project: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = "Failed to delete project: ${e.message}") }
            }
        }
    }

    fun cloneRepository(
        url: String,
        branch: String,
        shallowClone: Boolean,
        singleBranch: Boolean,
        recursive: Boolean = true,
        depth: Int? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, cloneProgress = "Cloning repository...") }

            val rootDir = _uiState.value.rootDirectory
            if (rootDir.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cloneProgress = "",
                        errorMessage = "Root directory not set. Please complete onboarding first."
                    )
                }
                return@launch
            }

            val repoName = extractRepoName(url)
            val targetDir = File(rootDir, repoName)

            if (targetDir.exists()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cloneProgress = "",
                        errorMessage = "A project with name '$repoName' already exists"
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = false, cloneProgress = "") }
            notifyFileSystemChanged()
        }
    }

    fun getCloneTargetDirectory(url: String): String {
        val rootDir = _uiState.value.rootDirectory
        if (rootDir.isBlank()) return ""
        val repoName = extractRepoName(url)
        return File(rootDir, repoName).absolutePath
    }

    private fun extractRepoName(url: String): String {
        return url
            .removeSuffix(".git")
            .removeSuffix("/")
            .substringAfterLast("/")
            .ifBlank { "cloned_repo" }
    }

    fun importProject(sourcePath: String, copyToWorkspace: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, importProgress = "Importing project...") }
            CLBMLogger.d(TAG, "Importing project from: $sourcePath, copyToWorkspace: $copyToWorkspace")

            val sourceDir = File(sourcePath)
            if (!sourceDir.exists() || !sourceDir.isDirectory) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        importProgress = "",
                        errorMessage = "Invalid project path"
                    )
                }
                return@launch
            }

            if (!isProjectDirectory(sourceDir)) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        importProgress = "",
                        errorMessage = "The selected folder is not a valid Android project"
                    )
                }
                return@launch
            }

            val projectName = sourceDir.name
            val rootDir = _uiState.value.rootDirectory

            if (rootDir.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        importProgress = "",
                        errorMessage = "Root directory not set. Please complete onboarding first."
                    )
                }
                return@launch
            }

            try {
                val targetPath = if (copyToWorkspace) {
                    val targetDir = File(rootDir, projectName)
                    if (targetDir.exists()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                importProgress = "",
                                errorMessage = "A project with this name already exists"
                            )
                        }
                        return@launch
                    }

                    _uiState.update { it.copy(importProgress = "Copying files...") }
                    sourceDir.copyRecursively(targetDir, overwrite = false)
                    targetDir.absolutePath
                } else {
                    sourcePath
                }

                val storedProject = StoredProject(
                    name = projectName,
                    path = targetPath,
                    packageName = detectPackageName(File(targetPath)),
                    templateType = ProjectTemplateType.EMPTY_COMPOSE,
                    createdAt = System.currentTimeMillis(),
                    lastOpenedAt = System.currentTimeMillis(),
                )
                repository.addProject(storedProject)
                refreshDirectoryContents()

                repository.setCurrentProjectPath(targetPath)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        importProgress = "",
                        projectName = projectName,
                        projectPath = targetPath,
                        isProjectOpen = true,
                        currentDestination = HomeDestination.IDE,
                    )
                }
                CLBMLogger.d(TAG, "Project imported successfully: $targetPath")
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Failed to import project", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        importProgress = "",
                        errorMessage = "Failed to import project: ${e.message}"
                    )
                }
            }
        }
    }

    private fun detectPackageName(projectDir: File): String {
        val manifestFile = File(projectDir, "app/src/main/AndroidManifest.xml")
        if (manifestFile.exists()) {
            try {
                val content = manifestFile.readText()
                val packageRegex = """package\s*=\s*["']([^"']+)["']""".toRegex()
                val match = packageRegex.find(content)
                if (match != null) {
                    return match.groupValues[1]
                }
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Failed to detect package name", e)
            }
        }
        return "com.example.${projectDir.name.lowercase().replace("-", "").replace(" ", "")}"
    }

    fun validateProjectPath(path: String): Boolean {
        val dir = File(path)
        return dir.exists() && dir.isDirectory && isProjectDirectory(dir)
    }

    fun createFolder(folderName: String) {
        viewModelScope.launch {
            val rootDir = _uiState.value.rootDirectory
            if (rootDir.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Root directory not set") }
                return@launch
            }

            try {
                val newFolder = File(rootDir, folderName)
                if (!newFolder.exists()) {
                    newFolder.mkdirs()
                }
                refreshDirectoryContents()
                notifyFileSystemChanged()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to create folder: ${e.message}") }
            }
        }
    }

    fun isProjectDirectory(file: File): Boolean {
        if (!file.isDirectory) return false
        return File(file, "build.gradle.kts").exists() ||
            File(file, "build.gradle").exists() ||
            File(file, "settings.gradle.kts").exists() ||
            File(file, "settings.gradle").exists()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun notifyFileSystemChanged() {
        _uiState.update { it.copy(lastFileSystemUpdate = System.currentTimeMillis()) }
    }

    fun refreshFileSystem() {
        notifyFileSystemChanged()
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
