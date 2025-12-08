package com.scto.codelikebastimove.feature.main

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.StoredProject
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import com.scto.codelikebastimove.feature.main.navigation.MainDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = UserPreferencesRepository(application)
    private var projectManager: ProjectManager? = null
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val navigationStack = mutableListOf<MainDestination>()
    
    init {
        initializeApp()
    }
    
    fun setProjectManager(manager: ProjectManager) {
        projectManager = manager
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            val existingRootDir = repository.getRootDirectoryOnce()
            if (existingRootDir.isBlank()) {
                val clbmDir = createClbmDirectory()
                if (clbmDir != null) {
                    repository.setRootDirectory(clbmDir)
                    _uiState.update { it.copy(rootDirectory = clbmDir) }
                }
            } else {
                val dir = File(existingRootDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                _uiState.update { it.copy(rootDirectory = existingRootDir) }
            }
            
            repository.projects.collect { projects ->
                _uiState.update { it.copy(projects = projects) }
            }
        }
    }
    
    private fun createClbmDirectory(): String? {
        return try {
            val externalStorage = Environment.getExternalStorageDirectory()
            val clbmDir = File(externalStorage, "CLBM")
            if (!clbmDir.exists()) {
                clbmDir.mkdirs()
            }
            clbmDir.absolutePath
        } catch (e: Exception) {
            val context = getApplication<Application>()
            val filesDir = context.filesDir
            val clbmDir = File(filesDir, "CLBM")
            if (!clbmDir.exists()) {
                clbmDir.mkdirs()
            }
            clbmDir.absolutePath
        }
    }
    
    fun onNavigate(destination: MainDestination) {
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
            if (_uiState.value.currentDestination != MainDestination.Home) {
                _uiState.update { it.copy(currentDestination = MainDestination.Home) }
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
                currentDestination = MainDestination.IDE
            )
        }
    }
    
    fun onCloseProject() {
        viewModelScope.launch {
            repository.setCurrentProjectPath("")
        }
        navigationStack.clear()
        _uiState.update {
            it.copy(
                isProjectOpen = false,
                projectPath = "",
                currentDestination = MainDestination.Home
            )
        }
    }
    
    fun createProject(
        name: String,
        packageName: String,
        templateType: ProjectTemplateType,
        minSdk: Int = 24,
        useKotlin: Boolean = true,
        useKotlinDsl: Boolean = true
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val rootDir = _uiState.value.rootDirectory
            if (rootDir.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Root directory not set") }
                return@launch
            }
            
            val template = projectManager?.getAvailableTemplates()?.find {
                when (templateType) {
                    ProjectTemplateType.EMPTY_ACTIVITY -> it.name == "Empty Activity"
                    ProjectTemplateType.EMPTY_COMPOSE -> it.name == "Empty Compose Activity"
                    ProjectTemplateType.BOTTOM_NAVIGATION -> it.name == "Bottom Navigation"
                    ProjectTemplateType.NAVIGATION_DRAWER -> it.name == "Navigation Drawer"
                    ProjectTemplateType.TABBED -> it.name == "Tabbed Activity"
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
                gradleLanguage = if (useKotlinDsl) GradleLanguage.KOTLIN_DSL else GradleLanguage.GROOVY
            )
            
            val result = projectManager?.createProject(template, config, rootDir)
            
            result?.onSuccess { project ->
                val storedProject = StoredProject(
                    name = name,
                    path = project.path,
                    packageName = packageName,
                    templateType = templateType,
                    createdAt = System.currentTimeMillis(),
                    lastOpenedAt = System.currentTimeMillis()
                )
                repository.addProject(storedProject)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        projectName = name,
                        projectPath = project.path,
                        isProjectOpen = true,
                        currentDestination = MainDestination.IDE
                    )
                }
            }?.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
    
    fun deleteProject(projectPath: String) {
        viewModelScope.launch {
            repository.removeProject(projectPath)
            try {
                File(projectPath).deleteRecursively()
            } catch (e: Exception) {
            }
        }
    }
    
    fun cloneRepository(url: String, branch: String, shallowClone: Boolean, singleBranch: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, cloneProgress = "Cloning repository...") }
            
            val rootDir = _uiState.value.rootDirectory
            if (rootDir.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Root directory not set") }
                return@launch
            }
            
            val repoName = url.substringAfterLast("/").removeSuffix(".git")
            val targetDir = File(rootDir, repoName)
            
            _uiState.update { it.copy(isLoading = false, cloneProgress = "") }
        }
    }
    
    fun onContentTypeChanged(contentType: MainContentType) {
        _uiState.update { it.copy(currentContent = contentType) }
    }
    
    fun onNavigationSheetToggle() {
        _uiState.update { it.copy(isNavigationSheetOpen = !it.isNavigationSheetOpen) }
    }
    
    fun onNavigationSheetDismiss() {
        _uiState.update { it.copy(isNavigationSheetOpen = false) }
    }
    
    fun onBottomSheetToggle() {
        _uiState.update { it.copy(isBottomSheetExpanded = !it.isBottomSheetExpanded) }
    }
    
    fun onBottomSheetContentChanged(content: BottomSheetContentType) {
        _uiState.update { 
            it.copy(
                bottomSheetContent = content,
                isBottomSheetExpanded = true
            )
        }
    }
    
    fun onProjectNameChanged(name: String) {
        _uiState.update { it.copy(projectName = name) }
    }
    
    fun onUnsavedChangesChanged(hasChanges: Boolean) {
        _uiState.update { it.copy(hasUnsavedChanges = hasChanges) }
    }
    
    fun onLoadingChanged(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
