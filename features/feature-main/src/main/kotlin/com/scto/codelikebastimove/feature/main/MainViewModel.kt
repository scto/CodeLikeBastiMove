package com.scto.codelikebastimove.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.io.File

class MainViewModel(
    private val projectManager: ProjectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // SubModuleMaker State
    private val _moduleNameInput = MutableStateFlow("")
    val moduleNameInput = _moduleNameInput.asStateFlow()

    private val _selectedLanguage = MutableStateFlow<ProjectLanguage>(ProjectLanguage.Kotlin)
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _selectedTemplate = MutableStateFlow<ProjectTemplate?>(null)
    val selectedTemplate = _selectedTemplate.asStateFlow()

    val availableTemplates: StateFlow<List<ProjectTemplate>> = projectManager.getAvailableTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentProject: StateFlow<Project?> = projectManager.currentProject
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Set default template if available when templates are loaded
        viewModelScope.launch {
            availableTemplates.collect { templates ->
                if (templates.isNotEmpty() && _selectedTemplate.value == null) {
                    _selectedTemplate.value = templates.first()
                }
            }
        }
    }

    fun setModuleNameInput(input: String) {
        _moduleNameInput.value = input
    }

    fun setSelectedLanguage(language: ProjectLanguage) {
        _selectedLanguage.value = language
    }

    fun setSelectedTemplate(template: ProjectTemplate) {
        _selectedTemplate.value = template
    }

    fun createModuleWithGradleNotation(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val input = _moduleNameInput.value.trim()
        val template = _selectedTemplate.value
        val language = _selectedLanguage.value
        val project = currentProject.value

        if (input.isEmpty()) {
            onError("Module name cannot be empty")
            return
        }

        if (template == null) {
            onError("Please select a template")
            return
        }

        if (project == null) {
            onError("No project currently opened")
            return
        }

        viewModelScope.launch {
            try {
                // Parse Gradle notation (e.g., ":features:home" or "features:home" or just "home")
                // 1. Remove leading colon if present
                val cleanPath = if (input.startsWith(":")) input.substring(1) else input
                
                // 2. Split into segments
                val segments = cleanPath.split(":")
                
                // 3. Last segment is the name, previous segments are the path
                val moduleName = segments.last()
                val parentPathSegments = segments.dropLast(1)
                
                // 4. Construct parent directory
                // Start from project root
                var parentDir = project.rootDirectory
                
                // Traverse/Create path directories
                if (parentPathSegments.isNotEmpty()) {
                    val relativePath = parentPathSegments.joinToString(File.separator)
                    parentDir = File(project.rootDirectory, relativePath)
                    
                    // Create parent directories if they don't exist
                    if (!parentDir.exists()) {
                        if (!parentDir.mkdirs()) {
                            onError("Failed to create parent directories: $relativePath")
                            return@launch
                        }
                    }
                }

                projectManager.createModule(
                    parent = parentDir,
                    name = moduleName,
                    template = template,
                    language = language,
                    gradleLanguage = GradleLanguage.KotlinDsl // Assuming KTS default for now
                )
                
                // Reset input
                _moduleNameInput.value = ""
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error creating module")
            }
        }
    }

    // Existing methods...
    fun loadProject(path: String) {
        viewModelScope.launch {
            projectManager.openProject(File(path))
        }
    }

    fun createProject(
        parent: File,
        name: String,
        template: ProjectTemplate,
        language: ProjectLanguage,
        gradleLanguage: GradleLanguage
    ) {
        viewModelScope.launch {
            projectManager.createProject(parent, name, template, language, gradleLanguage)
        }
    }
}