package com.scto.codelikebastimove.feature.submodulemaker

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.feature.submodulemaker.generator.ModuleGenerator
import com.scto.codelikebastimove.feature.submodulemaker.model.GradleNotation
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleTemplate
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleType
import com.scto.codelikebastimove.feature.submodulemaker.model.ProgrammingLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SubModuleMakerUiState(
    val gradleNotation: String = "",
    val parsedNotation: GradleNotation? = null,
    val notationError: String? = null,
    val selectedLanguage: ProgrammingLanguage = ProgrammingLanguage.KOTLIN,
    val selectedModuleType: ModuleType = ModuleType.LIBRARY,
    val selectedTemplate: ModuleTemplate = ModuleTemplate.EMPTY,
    val useCompose: Boolean = true,
    val customPackageName: String = "",
    val basePackageName: String = "com.scto.codelikebastimove",
    val minSdk: Int = 24,
    val targetSdk: Int = 35,
    val projectUri: Uri? = null,
    val isCreating: Boolean = false,
    val createResult: CreateModuleResult? = null,
    val recentNotations: List<String> = emptyList(),
    val suggestedNotations: List<String> = emptyList(),
)

sealed class CreateModuleResult {
    data class Success(val gradlePath: String, val createdFiles: List<String>) : CreateModuleResult()
    data class Error(val message: String) : CreateModuleResult()
}

class SubModuleMakerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SubModuleMakerUiState())
    val uiState: StateFlow<SubModuleMakerUiState> = _uiState.asStateFlow()

    init {
        updateSuggestions()
    }

    fun setGradleNotation(notation: String) {
        val parsed = GradleNotation.parse(notation)
        _uiState.update {
            it.copy(
                gradleNotation = notation,
                parsedNotation = parsed,
                notationError = if (notation.isNotBlank() && parsed == null) {
                    getNotationError(notation)
                } else null,
            )
        }
        updateSuggestions()
    }

    private fun getNotationError(notation: String): String {
        return when {
            !notation.startsWith(":") && notation.isNotBlank() -> "Gradle notation must start with ':'"
            notation.contains("::") -> "Double colons are not allowed"
            notation.endsWith(":") -> "Notation cannot end with ':'"
            notation.contains(" ") -> "Spaces are not allowed in module names"
            notation.any { it.isUpperCase() } -> "Module names should be lowercase"
            else -> "Invalid Gradle notation format"
        }
    }

    fun setLanguage(language: ProgrammingLanguage) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun setModuleType(type: ModuleType) {
        _uiState.update { it.copy(selectedModuleType = type) }
    }

    fun setTemplate(template: ModuleTemplate) {
        _uiState.update { it.copy(selectedTemplate = template) }
    }

    fun setUseCompose(useCompose: Boolean) {
        _uiState.update { it.copy(useCompose = useCompose) }
    }

    fun setCustomPackageName(packageName: String) {
        _uiState.update { it.copy(customPackageName = packageName) }
    }

    fun setBasePackageName(packageName: String) {
        _uiState.update { it.copy(basePackageName = packageName) }
    }

    fun setMinSdk(sdk: Int) {
        _uiState.update { it.copy(minSdk = sdk.coerceIn(21, 35)) }
    }

    fun setTargetSdk(sdk: Int) {
        _uiState.update { it.copy(targetSdk = sdk.coerceIn(24, 36)) }
    }

    fun setProjectUri(uri: Uri) {
        _uiState.update { it.copy(projectUri = uri) }
    }

    fun selectSuggestion(notation: String) {
        setGradleNotation(notation)
    }

    fun createModule(context: Context) {
        val state = _uiState.value
        val parsed = state.parsedNotation ?: return
        val projectUri = state.projectUri

        if (projectUri == null) {
            _uiState.update {
                it.copy(createResult = CreateModuleResult.Error("Please select a project directory"))
            }
            return
        }

        val config = ModuleConfig(
            gradlePath = parsed.fullPath,
            packageName = state.customPackageName.ifBlank { "" },
            language = state.selectedLanguage,
            moduleType = state.selectedModuleType,
            useCompose = state.useCompose,
            minSdk = state.minSdk,
            targetSdk = state.targetSdk,
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }

            val result = withContext(Dispatchers.IO) {
                try {
                    val projectDir = DocumentFile.fromTreeUri(context, projectUri)
                    if (projectDir == null) {
                        CreateModuleResult.Error("Cannot access project directory")
                    } else {
                        val generator = ModuleGenerator(context)
                        val generatorResult = generator.generateModuleWithDocumentFile(
                            projectDir = projectDir,
                            config = config,
                            basePackage = state.basePackageName,
                        )
                        generatorResult.fold(
                            onSuccess = { createdFiles ->
                                CreateModuleResult.Success(config.gradlePath, createdFiles)
                            },
                            onFailure = { error ->
                                CreateModuleResult.Error(error.message ?: "Unknown error")
                            }
                        )
                    }
                } catch (e: Exception) {
                    CreateModuleResult.Error(e.message ?: "Failed to create module")
                }
            }

            _uiState.update {
                it.copy(
                    isCreating = false,
                    createResult = result,
                    recentNotations = if (result is CreateModuleResult.Success) {
                        (listOf(parsed.fullPath) + it.recentNotations).distinct().take(10)
                    } else it.recentNotations,
                )
            }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(createResult = null) }
    }

    private fun updateSuggestions() {
        val current = _uiState.value.gradleNotation
        val suggestions = mutableListOf<String>()

        if (current.isBlank()) {
            suggestions.addAll(listOf(
                ":core:",
                ":feature:",
                ":data:",
                ":domain:",
                ":ui:",
            ))
        } else if (current.startsWith(":core:") && !current.substringAfter(":core:").contains(":")) {
            suggestions.addAll(listOf(
                ":core:common",
                ":core:network",
                ":core:database",
                ":core:ui",
                ":core:utils",
                ":core:testing",
            ).filter { it.startsWith(current) && it != current })
        } else if (current.startsWith(":feature:") && !current.substringAfter(":feature:").contains(":")) {
            suggestions.addAll(listOf(
                ":feature:home",
                ":feature:auth",
                ":feature:settings",
                ":feature:profile",
                ":feature:onboarding",
            ).filter { it.startsWith(current) && it != current })
        } else if (current.startsWith(":data:") && !current.substringAfter(":data:").contains(":")) {
            suggestions.addAll(listOf(
                ":data:repository",
                ":data:local",
                ":data:remote",
                ":data:model",
            ).filter { it.startsWith(current) && it != current })
        } else if (current.startsWith(":domain:") && !current.substringAfter(":domain:").contains(":")) {
            suggestions.addAll(listOf(
                ":domain:usecase",
                ":domain:model",
                ":domain:repository",
            ).filter { it.startsWith(current) && it != current })
        }

        _uiState.update { it.copy(suggestedNotations = suggestions) }
    }

    fun getGeneratedPackageName(): String {
        val state = _uiState.value
        if (state.customPackageName.isNotBlank()) return state.customPackageName
        val parsed = state.parsedNotation ?: return ""
        return parsed.toPackageName(state.basePackageName)
    }

    fun getPreviewConfig(): ModuleConfig? {
        val state = _uiState.value
        val parsed = state.parsedNotation ?: return null
        return ModuleConfig(
            gradlePath = parsed.fullPath,
            packageName = state.customPackageName.ifBlank { "" },
            language = state.selectedLanguage,
            moduleType = state.selectedModuleType,
            useCompose = state.useCompose,
            minSdk = state.minSdk,
            targetSdk = state.targetSdk,
        )
    }
}
