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
            // Einfacher Regex um include(":app") oder include ':app' zu finden
            // Matches: include(":app"), include ':app', include ":app"
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
        
        // Prüfen ob es ein Android Modul ist
        val isAndroid = content.contains("com.android.application") || 
                       content.contains("com.android.library") ||
                       content.contains("id(\"com.android.application\")") ||
                       content.contains("id(\"com.android.library\")")

        if (!isAndroid) {
            // Java/Kotlin Library - hat normalerweise keine Build Types wie Android
            // Wir können es ignorieren oder als "Standard" anzeigen
            return BuildVariant(moduleName, "main", listOf("main"))
        }

        // Standard Build Types
        val buildParams = mutableListOf("debug", "release")
        
        // Versuche benutzerdefinierte Build Types zu finden (sehr rudimentär)
        if (content.contains("buildTypes {")) {
            // Hier könnte man komplexer parsen, aber für jetzt reichen die Standards + Check
            if (content.contains("create(\"staging\")") || content.contains("staging {")) {
                buildParams.add("staging")
            }
        }

        return BuildVariant(
            moduleName = moduleName,
            activeVariant = "debug",
            availableVariants = buildParams
        )
    }
}