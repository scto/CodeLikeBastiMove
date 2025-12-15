package com.scto.codelikebastimove.feature.main.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.scto.codelikebastimove.core.templates.api.ProjectManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import java.io.File

class BuildVariantsViewModel(
    private val projectManager: ProjectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<BuildVariantsUiState>(BuildVariantsUiState.Loading)
    val uiState: StateFlow<BuildVariantsUiState> = _uiState.asStateFlow()

    init {
        observeCurrentProject()
    }

    private fun observeCurrentProject() {
        viewModelScope.launch {
            projectManager.currentProject.collectLatest { project ->
                if (project != null) {
                    loadBuildVariants(project.path)
                } else {
                    _uiState.value = BuildVariantsUiState.Empty("Kein Projekt geöffnet")
                }
            }
        }
    }

    private fun loadBuildVariants(projectPath: String) {
        viewModelScope.launch {
            _uiState.value = BuildVariantsUiState.Loading
            try {
                val variants = parseBuildVariants(projectPath)
                if (variants.isEmpty()) {
                    _uiState.value = BuildVariantsUiState.Empty("Keine Build-Varianten gefunden (oder keine Android App).")
                } else {
                    _uiState.value = BuildVariantsUiState.Success(variants)
                }
            } catch (e: Exception) {
                _uiState.value = BuildVariantsUiState.Error(e.message ?: "Unbekannter Fehler beim Laden der Varianten")
            }
        }
    }

    private fun parseBuildVariants(projectPath: String): List<String> {
        val buildFile = File(projectPath, "app/build.gradle.kts")
        val variants = mutableListOf<String>()

        if (buildFile.exists()) {
            val content = buildFile.readText()
            
            // Sehr einfacher Parser für buildTypes in Kotlin DSL
            // Sucht nach 'create("release")' oder 'getByName("debug")' innerhalb von buildTypes
            
            // Standard Varianten hinzufügen, da sie fast immer da sind
            variants.add("debug")
            variants.add("release")

            // Versuchen, benutzerdefinierte Typen zu finden
            // Dies ist ein naiver Ansatz und könnte verbessert werden
            val lines = content.lines()
            var insideBuildTypes = false
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("buildTypes {")) {
                    insideBuildTypes = true
                    continue
                }
                if (insideBuildTypes && trimmed == "}") {
                    insideBuildTypes = false
                }
                
                if (insideBuildTypes) {
                    // Suche nach create("name") oder getByName("name")
                    if (trimmed.contains("create(") || trimmed.contains("register(")) {
                        val name = trimmed.substringAfter("\"").substringBefore("\"")
                        if (name.isNotEmpty() && name != "debug" && name != "release") {
                            variants.add(name)
                        }
                    }
                }
            }
        } else {
             // Fallback für Projekte ohne app modul im root (oder andere Struktur)
             // Hier könnte man rekursiv suchen
             return emptyList()
        }
        
        return variants.distinct()
    }
}

sealed interface BuildVariantsUiState {
    data object Loading : BuildVariantsUiState
    data class Success(val variants: List<String>) : BuildVariantsUiState
    data class Error(val message: String) : BuildVariantsUiState
    data class Empty(val message: String) : BuildVariantsUiState
}