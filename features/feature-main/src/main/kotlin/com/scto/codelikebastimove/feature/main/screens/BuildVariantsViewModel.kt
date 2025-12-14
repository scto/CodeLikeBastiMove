package com.scto.codelikebastimove.feature.main.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.util.Locale

data class BuildVariant(
    val moduleName: String,
    val activeVariant: String,
    val availableVariants: List<String>
)

data class BuildVariantsUiState(
    val variants: List<BuildVariant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class BuildVariantsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BuildVariantsUiState())
    val uiState: StateFlow<BuildVariantsUiState> = _uiState.asStateFlow()

    fun loadBuildVariants(projectPath: String) {
        if (projectPath.isBlank()) {
            _uiState.update { it.copy(error = "Kein Projekt geöffnet") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val variants = withContext(Dispatchers.IO) {
                    scanProjectForModules(File(projectPath))
                }
                _uiState.update { 
                    it.copy(
                        variants = variants,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Fehler beim Laden der Module: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun updateVariant(moduleName: String, newVariant: String) {
        _uiState.update { state ->
            val updatedList = state.variants.map { variant ->
                if (variant.moduleName == moduleName) {
                    variant.copy(activeVariant = newVariant)
                } else {
                    variant
                }
            }
            state.copy(variants = updatedList)
        }
    }

    private fun scanProjectForModules(projectRoot: File): List<BuildVariant> {
        val modules = mutableListOf<BuildVariant>()
        
        // Versuche settings.gradle.kts oder settings.gradle zu lesen
        val settingsKts = File(projectRoot, "settings.gradle.kts")
        val settingsGroovy = File(projectRoot, "settings.gradle")
        
        val settingsFile = when {
            settingsKts.exists() -> settingsKts
            settingsGroovy.exists() -> settingsGroovy
            else -> null
        }

        if (settingsFile != null) {
            val content = settingsFile.readText()
            // Regex um include(":app") oder include ':app' zu finden
            val regex = Regex("include\\s*\\(?\\s*[\"'](:[^\"']+)[\"']\\s*\\)?")
            val matches = regex.findAll(content)
            
            for (match in matches) {
                val gradlePath = match.groupValues[1] // z.B. :features:login
                val relativePath = gradlePath.trimStart(':').replace(':', File.separatorChar)
                val moduleDir = File(projectRoot, relativePath)
                
                if (moduleDir.exists()) {
                    val variant = parseModuleBuildFile(gradlePath, moduleDir)
                    if (variant != null) {
                        modules.add(variant)
                    }
                }
            }
        } else {
            // Fallback: Wenn keine settings.gradle da ist, schaue einfach in root und 'app'
            val appDir = File(projectRoot, "app")
            if (appDir.exists()) {
                parseModuleBuildFile(":app", appDir)?.let { modules.add(it) }
            }
            // Check root als Modul
            parseModuleBuildFile(":", projectRoot)?.let { modules.add(it) }
        }

        return modules.sortedBy { it.moduleName }
    }

    private fun parseModuleBuildFile(moduleName: String, moduleDir: File): BuildVariant? {
        val buildKts = File(moduleDir, "build.gradle.kts")
        val buildGroovy = File(moduleDir, "build.gradle")
        
        val buildFile = when {
            buildKts.exists() -> buildKts
            buildGroovy.exists() -> buildGroovy
            else -> return null
        }

        val content = buildFile.readText()
        
        // Prüfen ob es ein Android Modul ist (Application oder Library)
        val isAndroid = content.contains("com.android.application") || 
                       content.contains("com.android.library") ||
                       content.contains("id(\"com.android.application\")") ||
                       content.contains("id(\"com.android.library\")")

        if (!isAndroid) {
            // Für reine Kotlin/Java Module gibt es keine Build Varianten im Android-Sinne
            return BuildVariant(moduleName, "main", listOf("main"))
        }

        // 1. Build Types extrahieren (Standard: debug, release)
        val buildTypes = extractBuildTypes(content)
        
        // 2. Product Flavors extrahieren
        val productFlavors = extractProductFlavors(content)

        // 3. Varianten kombinieren (Flavor + BuildType)
        // Android naming convention: <flavor><BuildType> (CamelCase)
        val variants = if (productFlavors.isEmpty()) {
            buildTypes.toList()
        } else {
            productFlavors.flatMap { flavor ->
                buildTypes.map { type ->
                    // flavor="demo", type="debug" -> "demoDebug"
                    val capitalizedType = type.replaceFirstChar { 
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                    }
                    "$flavor$capitalizedType"
                }
            }
        }

        val finalVariants = if (variants.isEmpty()) listOf("debug", "release") else variants.sorted()
        
        // Standardmäßig 'debug' auswählen, wenn vorhanden
        val defaultVariant = finalVariants.find { it.contains("debug", ignoreCase = true) } 
            ?: finalVariants.firstOrNull() 
            ?: "debug"

        return BuildVariant(
            moduleName = moduleName,
            activeVariant = defaultVariant,
            availableVariants = finalVariants
        )
    }

    private fun extractBuildTypes(content: String): Set<String> {
        val foundTypes = mutableSetOf("debug", "release")
        
        // Sucht nach dem Block buildTypes { ... }
        val buildTypesMatch = Regex("buildTypes\\s*\\{([\\s\\S]*?)\\}").find(content)
        if (buildTypesMatch != null) {
            val blockContent = buildTypesMatch.groupValues[1]
            
            // Matcht: create("staging") oder register("staging")
            Regex("(?:create|register)\\s*\\(\\s*[\"']([^\"']+)[\"']").findAll(blockContent).forEach {
                foundTypes.add(it.groupValues[1])
            }
            
            // Matcht Groovy Style: staging { ... }
            // Wir filtern Standard-Methoden aus
            Regex("\\b(\\w+)\\s*\\{").findAll(blockContent).forEach {
                val name = it.groupValues[1]
                if (name !in listOf("getByName", "create", "register", "named", "release", "debug")) {
                    foundTypes.add(name)
                }
            }
        }
        return foundTypes
    }

    private fun extractProductFlavors(content: String): Set<String> {
        val foundFlavors = mutableSetOf<String>()
        
        // Sucht nach dem Block productFlavors { ... }
        val flavorsMatch = Regex("productFlavors\\s*\\{([\\s\\S]*?)\\}").find(content)
        if (flavorsMatch != null) {
            val blockContent = flavorsMatch.groupValues[1]
            
            // Matcht: create("demo") oder register("demo")
            Regex("(?:create|register)\\s*\\(\\s*[\"']([^\"']+)[\"']").findAll(blockContent).forEach {
                foundFlavors.add(it.groupValues[1])
            }
            
            // Matcht Groovy Style: demo { ... }
            Regex("\\b(\\w+)\\s*\\{").findAll(blockContent).forEach {
                val name = it.groupValues[1]
                if (name !in listOf("getByName", "create", "register", "named")) {
                    foundFlavors.add(name)
                }
            }
        }
        return foundFlavors
    }
}