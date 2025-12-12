package com.scto.codelikebastimove.feature.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.scto.codelikebastimove.core.logger.CLBMLogger
import com.scto.codelikebastimove.core.datastore.DirectoryItem
import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.datastore.StoredProject
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import com.scto.codelikebastimove.core.templates.impl.ProjectManagerImpl
import com.scto.codelikebastimove.feature.main.navigation.MainDestination
import com.scto.codelikebastimove.feature.main.screens.ModuleType
import com.scto.codelikebastimove.feature.main.screens.ProgrammingLanguage

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
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = UserPreferencesRepository(application)
    private val projectManager: ProjectManager = ProjectManagerImpl(application)
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
 
    val currentProjectPath: StateFlow<String?> = _uiState
        .map { state -> state.projectPath.ifBlank { null } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun updateContentType(contentType: MainContentType) {
        _uiState.update { it.copy(currentContent = contentType) }
    }
    
    fun updateProjectViewMode(mode: ProjectViewMode) {
        _uiState.update { it.copy(projectViewType = mode) }
    }
    
    private val navigationStack = mutableListOf<MainDestination>()
    
    init {
        initializeApp()
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            // 1. Root Directory laden
            val existingRootDir = repository.getRootDirectoryOnce()
            if (existingRootDir.isNotBlank()) {
                val dir = File(existingRootDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                _uiState.update { it.copy(rootDirectory = existingRootDir) }
                refreshDirectoryContents()
            }
            
            // 2. Projekte laden
            launch {
                repository.projects.collect { projects ->
                    _uiState.update { it.copy(projects = projects) }
                }
            }

            // 3. Zuletzt geöffnetes Projekt wiederherstellen
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
                                currentDestination = MainDestination.IDE
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
    
    // --- Editor File Management ---

    fun openFile(path: String) {
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            CLBMLogger.e(TAG, "File not found or is directory: $path")
            return
        }

        // Prüfen, ob Datei bereits geöffnet ist
        val existingIndex = _uiState.value.openFiles.indexOfFirst { it.path == path }
        if (existingIndex != -1) {
            _uiState.update { 
                it.copy(
                    activeFileIndex = existingIndex,
                    currentContent = MainContentType.EDITOR 
                ) 
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val content = withContext(Dispatchers.IO) {
                    file.readText()
                }
                
                val newFile = EditorFile(
                    name = file.name,
                    path = file.path,
                    content = content
                )

                _uiState.update { state ->
                    val newOpenFiles = state.openFiles + newFile
                    state.copy(
                        openFiles = newOpenFiles,
                        activeFileIndex = newOpenFiles.lastIndex,
                        currentContent = MainContentType.EDITOR,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Error reading file: $path", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Fehler beim Öffnen der Datei: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun closeFile(index: Int) {
        _uiState.update { state ->
            val newOpenFiles = state.openFiles.toMutableList()
            newOpenFiles.removeAt(index)
            
            var newActiveIndex = state.activeFileIndex
            if (index <= state.activeFileIndex) {
                newActiveIndex = (state.activeFileIndex - 1).coerceAtLeast(0)
            }
            if (newOpenFiles.isEmpty()) {
                newActiveIndex = -1
            } else if (newActiveIndex >= newOpenFiles.size) {
                newActiveIndex = newOpenFiles.size - 1
            }

            state.copy(
                openFiles = newOpenFiles,
                activeFileIndex = newActiveIndex
            )
        }
    }

    fun selectFile(index: Int) {
        if (index in _uiState.value.openFiles.indices) {
            _uiState.update { it.copy(activeFileIndex = index) }
        }
    }

    fun updateFileContent(content: String) {
        val activeIndex = _uiState.value.activeFileIndex
        if (activeIndex != -1) {
            _uiState.update { state ->
                val files = state.openFiles.toMutableList()
                val currentFile = files[activeIndex]
                files[activeIndex] = currentFile.copy(
                    content = content,
                    isModified = true
                )
                state.copy(
                    openFiles = files,
                    hasUnsavedChanges = true
                )
            }
        }
    }

    // --- End Editor File Management ---
    
    fun refreshDirectoryContents() {
        val rootDir = _uiState.value.rootDirectory
        if (rootDir.isBlank()) return
        
        val dir = File(rootDir)
        if (!dir.exists() || !dir.isDirectory) {
            _uiState.update { it.copy(directoryContents = emptyList()) }
            return
        }
        
        val items = dir.listFiles()?.map { file ->
            DirectoryItem(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                isProject = isProjectDirectory(file),
                lastModified = file.lastModified(),
                size = if (file.isFile) file.length() else 0
            )
        }?.sortedWith(
            compareBy<DirectoryItem> { !it.isDirectory }
                .thenBy { !it.isProject }
                .thenBy { it.name.lowercase() }
        ) ?: emptyList()
        
        _uiState.update { it.copy(directoryContents = items) }
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
                currentDestination = MainDestination.Home,
                openFiles = emptyList(),
                activeFileIndex = -1
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            CLBMLogger.d(TAG, "Creating project: $name with template: $templateType")
            
            val rootDir = _uiState.value.rootDirectory
            
            if (rootDir.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Root directory not set. Please complete onboarding first.") }
                return@launch
            }
            
            val rootDirFile = File(rootDir)
            if (!rootDirFile.exists()) {
                rootDirFile.mkdirs()
            }
            
            if (!rootDirFile.canWrite()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Cannot write to project directory. Check storage permissions.") }
                return@launch
            }
            
            val templates = projectManager.getAvailableTemplates()
            val template = templates.find {
                when (templateType) {
                    ProjectTemplateType.EMPTY_ACTIVITY -> it.name == "Empty Activity"
                    ProjectTemplateType.EMPTY_COMPOSE -> it.name == "Empty Compose Activity"
                    ProjectTemplateType.BOTTOM_NAVIGATION -> it.name == "Bottom Navigation"
                    ProjectTemplateType.NAVIGATION_DRAWER -> it.name == "Navigation Drawer"
                    ProjectTemplateType.TABBED -> it.name == "Tabbed Activity"
                    ProjectTemplateType.MULTI_MODULE -> it.name == "Multi-Module Project"
                    ProjectTemplateType.MVVM_CLEAN -> it.name == "MVVM Clean Architecture"
                    ProjectTemplateType.WEAR_OS -> it.name == "Wear OS Activity"
                    ProjectTemplateType.RESPONSIVE_FOLDABLE -> it.name == "Responsive Activity"
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
            
            val result = projectManager.createProject(template, config, rootDir)
            
            result.onSuccess { project ->
                val storedProject = StoredProject(
                    name = name,
                    path = project.path,
                    packageName = packageName,
                    templateType = templateType,
                    createdAt = System.currentTimeMillis(),
                    lastOpenedAt = System.currentTimeMillis()
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
                        currentDestination = MainDestination.IDE
                    )
                }
            }.onFailure { error ->
                CLBMLogger.e(TAG, "Failed to create project: ${error.message}", error)
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    /**
     * Erstellt ein Sub-Modul basierend auf der Gradle-Notation (z.B. :core:ui).
     * @param modulePath Der Pfad im Gradle-Format (z.B. :features:login)
     * @param packageName Der Paketname für das neue Modul (optional)
     * @param language Die Programmiersprache (Kotlin/Java)
     * @param type Der Modultyp (Library, Feature, Core) für die build.gradle Konfiguration
     */
    fun createSubModule(
        modulePath: String,
        packageName: String,
        language: ProgrammingLanguage,
        type: ModuleType
    ) {
        val currentProjectPath = _uiState.value.projectPath
        if (currentProjectPath.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Kein Projekt geöffnet") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 1. Gradle Pfad normalisieren und Ordnerstruktur ableiten
                // :features:login -> features/login
                val gradlePath = if (modulePath.startsWith(":")) modulePath else ":$modulePath"
                val relativePath = gradlePath.trimStart(':').replace(':', File.separatorChar)
                
                val projectDir = File(currentProjectPath)
                val moduleDir = File(projectDir, relativePath)
                
                if (moduleDir.exists()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Modul existiert bereits: $gradlePath") }
                    return@launch
                }
                
                if (!moduleDir.mkdirs()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Konnte Ordner nicht erstellen: ${moduleDir.path}") }
                    return@launch
                }

                // 2. build.gradle.kts erstellen
                val finalPackageName = if (packageName.isNotBlank()) {
                    packageName
                } else {
                    // Fallback: Versuche einen Paketnamen aus dem Pfad zu generieren
                    "com.example.${relativePath.replace(File.separatorChar, '.')}"
                }

                val buildFile = File(moduleDir, "build.gradle.kts")
                buildFile.writeText(generateModuleBuildGradle(finalPackageName, type))
                
                // .gitignore erstellen
                File(moduleDir, ".gitignore").writeText("/build\n")
                
                // proguard-rules.pro erstellen
                File(moduleDir, "consumer-rules.pro").createNewFile()
                File(moduleDir, "proguard-rules.pro").createNewFile()

                // 3. Ordnerstruktur für Source Code erstellen
                val srcDir = if (language == ProgrammingLanguage.KOTLIN) "src/main/kotlin" else "src/main/java"
                val packagePath = finalPackageName.replace('.', File.separatorChar)
                val codeDir = File(moduleDir, "$srcDir/$packagePath")
                codeDir.mkdirs()
                
                // AndroidManifest.xml erstellen (notwendig für Android Libraries)
                val manifestDir = File(moduleDir, "src/main")
                manifestDir.mkdirs()
                val manifestFile = File(manifestDir, "AndroidManifest.xml")
                manifestFile.writeText("""
                    <?xml version="1.0" encoding="utf-8"?>
                    <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                    </manifest>
                """.trimIndent())

                // 4. Modul in settings.gradle.kts (oder settings.gradle) registrieren
                val settingsKts = File(projectDir, "settings.gradle.kts")
                val settingsGroovy = File(projectDir, "settings.gradle")
                
                if (settingsKts.exists()) {
                    val content = settingsKts.readText()
                    if (!content.contains("include(\"$gradlePath\")")) {
                        settingsKts.appendText("\ninclude(\"$gradlePath\")\n")
                    }
                } else if (settingsGroovy.exists()) {
                    val content = settingsGroovy.readText()
                    if (!content.contains("include '$gradlePath'") && !content.contains("include \"$gradlePath\"")) {
                        settingsGroovy.appendText("\ninclude '$gradlePath'\n")
                    }
                }

                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                // Optional: Force refresh of file tree
                
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Error creating submodule", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Fehler beim Erstellen des Moduls: ${e.message}") }
            }
        }
    }

    private fun generateModuleBuildGradle(packageName: String, type: ModuleType): String {
        return """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }

            android {
                namespace = "$packageName"
                compileSdk = 34

                defaultConfig {
                    minSdk = 24
                    
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                    }
                }
                
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }
                
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }

            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.appcompat:appcompat:1.6.1")
                implementation("com.google.android.material:material:1.11.0")
                
                testImplementation("junit:junit:4.13.2")
                androidTestImplementation("androidx.test.ext:junit:1.1.5")
                androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
            }
        """.trimIndent()
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
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Failed to delete project: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = "Failed to delete project: ${e.message}") }
            }
        }
    }
    
    fun cloneRepository(url: String, branch: String, shallowClone: Boolean, singleBranch: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, cloneProgress = "Cloning repository...") }
            // Dummy implementation
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
    
    companion object {
        private const val TAG = "MainViewModel"
    }
}