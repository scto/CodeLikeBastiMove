package com.scto.codelikebastimove.feature.submodulemaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class BuildVariant(
  val moduleName: String,
  val activeVariant: String,
  val availableVariants: List<String>,
)

data class BuildVariantsUiState(
  val variants: List<BuildVariant> = emptyList(),
  val isLoading: Boolean = false,
  val error: String? = null,
)

class BuildVariantsViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(BuildVariantsUiState())
  val uiState: StateFlow<BuildVariantsUiState> = _uiState.asStateFlow()

  private var currentProjectPath: String? = null
  private val PREFS_DIR_NAME = ".androidide"
  private val PREFS_FILE_NAME = "build_variants.json"

  fun loadBuildVariants(projectPath: String) {
    if (projectPath.isBlank()) {
      _uiState.update { it.copy(error = "Kein Projekt geöffnet") }
      return
    }
    currentProjectPath = projectPath

    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true, error = null) }
      try {
        val variants =
          withContext(Dispatchers.IO) {
            val modules = scanProjectForModules(File(projectPath))
            applySavedVariants(File(projectPath), modules)
          }
        _uiState.update { it.copy(variants = variants, isLoading = false) }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(isLoading = false, error = "Fehler beim Laden der Module: ${e.message}")
        }
      }
    }
  }

  fun updateVariant(moduleName: String, newVariant: String) {
    _uiState.update { state ->
      val updatedList =
        state.variants.map { variant ->
          if (variant.moduleName == moduleName) {
            variant.copy(activeVariant = newVariant)
          } else {
            variant
          }
        }
      saveVariants(updatedList)
      state.copy(variants = updatedList)
    }
  }

  private fun saveVariants(variants: List<BuildVariant>) {
    val path = currentProjectPath ?: return
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val projectRoot = File(path)
        val prefsFile = File(projectRoot, "$PREFS_DIR_NAME/$PREFS_FILE_NAME")

        if (!prefsFile.parentFile.exists()) {
          prefsFile.parentFile.mkdirs()
        }

        val json = JSONObject()
        variants.forEach { variant -> json.put(variant.moduleName, variant.activeVariant) }

        prefsFile.writeText(json.toString(2))
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  private fun applySavedVariants(
    projectRoot: File,
    modules: List<BuildVariant>,
  ): List<BuildVariant> {
    val prefsFile = File(projectRoot, "$PREFS_DIR_NAME/$PREFS_FILE_NAME")
    if (!prefsFile.exists()) return modules

    return try {
      val content = prefsFile.readText()
      if (content.isBlank()) return modules

      val json = JSONObject(content)
      modules.map { variant ->
        val savedVariant = json.optString(variant.moduleName)
        if (savedVariant.isNotEmpty() && variant.availableVariants.contains(savedVariant)) {
          variant.copy(activeVariant = savedVariant)
        } else {
          variant
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      modules
    }
  }

  private fun scanProjectForModules(projectRoot: File): List<BuildVariant> {
    val modules = mutableListOf<BuildVariant>()

    val settingsKts = File(projectRoot, "settings.gradle.kts")
    val settingsGroovy = File(projectRoot, "settings.gradle")

    val settingsFile =
      when {
        settingsKts.exists() -> settingsKts
        settingsGroovy.exists() -> settingsGroovy
        else -> null
      }

    if (settingsFile != null) {
      val content = settingsFile.readText()
      val regex = Regex("include\\s*\\(?\\s*[\"'](:[^\"']+)[\"']\\s*\\)?")
      val matches = regex.findAll(content)

      for (match in matches) {
        val gradlePath = match.groupValues[1]
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
      val appDir = File(projectRoot, "app")
      if (appDir.exists()) {
        parseModuleBuildFile(":app", appDir)?.let { modules.add(it) }
      }
      parseModuleBuildFile(":", projectRoot)?.let { modules.add(it) }
    }

    return modules.sortedBy { it.moduleName }
  }

  private fun parseModuleBuildFile(moduleName: String, moduleDir: File): BuildVariant? {
    val buildKts = File(moduleDir, "build.gradle.kts")
    val buildGroovy = File(moduleDir, "build.gradle")

    val buildFile =
      when {
        buildKts.exists() -> buildKts
        buildGroovy.exists() -> buildGroovy
        else -> return null
      }

    val content = buildFile.readText()

    val isAndroid =
      content.contains("com.android.application") ||
        content.contains("com.android.library") ||
        content.contains("id(\"com.android.application\")") ||
        content.contains("id(\"com.android.library\")")

    if (!isAndroid) {
      return BuildVariant(moduleName, "main", listOf("main"))
    }

    val buildTypes = extractBuildTypes(content)
    val productFlavors = extractProductFlavors(content)

    val variants =
      if (productFlavors.isEmpty()) {
        buildTypes.toList()
      } else {
        productFlavors.flatMap { flavor ->
          buildTypes.map { type ->
            val capitalizedType =
              type.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
              }
            "$flavor$capitalizedType"
          }
        }
      }

    val finalVariants = if (variants.isEmpty()) listOf("debug", "release") else variants.sorted()

    val defaultVariant =
      finalVariants.find { it.contains("debug", ignoreCase = true) }
        ?: finalVariants.firstOrNull()
        ?: "debug"

    return BuildVariant(
      moduleName = moduleName,
      activeVariant = defaultVariant,
      availableVariants = finalVariants,
    )
  }

  private fun extractBuildTypes(content: String): Set<String> {
    val foundTypes = mutableSetOf("debug", "release")

    val buildTypesMatch = Regex("buildTypes\\s*\\{([\\s\\S]*?)\\}").find(content)
    if (buildTypesMatch != null) {
      val blockContent = buildTypesMatch.groupValues[1]

      Regex("(?:create|register)\\s*\\(\\s*[\"']([^\"']+)[\"']").findAll(blockContent).forEach {
        foundTypes.add(it.groupValues[1])
      }

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

    val flavorsMatch = Regex("productFlavors\\s*\\{([\\s\\S]*?)\\}").find(content)
    if (flavorsMatch != null) {
      val blockContent = flavorsMatch.groupValues[1]

      Regex("(?:create|register)\\s*\\(\\s*[\"']([^\"']+)[\"']").findAll(blockContent).forEach {
        foundFlavors.add(it.groupValues[1])
      }

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
