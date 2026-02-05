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

enum class ModuleType {
  APPLICATION,
  LIBRARY,
  JAVA_LIBRARY,
  KOTLIN_LIBRARY,
  UNKNOWN
}

data class BuildVariant(
  val moduleName: String,
  val modulePath: String,
  val activeVariant: String,
  val availableVariants: List<String>,
  val moduleType: ModuleType = ModuleType.UNKNOWN,
  val parentModule: String? = null,
  val isSubmodule: Boolean = false,
  val depth: Int = 0,
  val children: MutableList<BuildVariant> = mutableListOf(),
)

data class ModuleGroup(
  val name: String,
  val modules: List<BuildVariant>,
  val isExpanded: Boolean = true,
)

data class BuildVariantsUiState(
  val variants: List<BuildVariant> = emptyList(),
  val moduleGroups: List<ModuleGroup> = emptyList(),
  val isLoading: Boolean = false,
  val error: String? = null,
  val projectName: String = "",
  val totalModules: Int = 0,
  val androidModules: Int = 0,
)

class GradleParser(private val content: String) {
  private var pos = 0
  private val len = content.length
  
  fun extractAllIncludeModules(): List<String> {
    val modules = mutableListOf<String>()
    pos = 0
    
    while (pos < len) {
      skipWhitespaceAndComments()
      if (pos >= len) break
      
      if (matchKeyword("include")) {
        skipWhitespaceAndComments()
        
        if (pos < len && content[pos] == '(') {
          pos++
          extractModulesUntilCloseParen(modules)
        } else {
          extractModulesUntilNewlineOrBlock(modules)
        }
      } else if (matchKeyword("includeBuild")) {
        skipUntilNewlineOrBlock()
      } else {
        pos++
      }
    }
    
    return modules.distinct()
  }
  
  private fun matchKeyword(keyword: String): Boolean {
    if (pos + keyword.length > len) return false
    
    if (pos > 0) {
      val prevChar = content[pos - 1]
      if (prevChar.isLetterOrDigit() || prevChar == '_') return false
    }
    
    for (i in keyword.indices) {
      if (content[pos + i] != keyword[i]) return false
    }
    
    val afterPos = pos + keyword.length
    if (afterPos < len) {
      val afterChar = content[afterPos]
      if (afterChar.isLetterOrDigit() || afterChar == '_') return false
    }
    
    pos += keyword.length
    return true
  }
  
  private fun skipWhitespaceAndComments() {
    while (pos < len) {
      when {
        content[pos].isWhitespace() -> pos++
        
        pos + 1 < len && content[pos] == '/' && content[pos + 1] == '/' -> {
          while (pos < len && content[pos] != '\n') pos++
          if (pos < len) pos++
        }
        
        pos + 1 < len && content[pos] == '/' && content[pos + 1] == '*' -> {
          pos += 2
          while (pos + 1 < len && !(content[pos] == '*' && content[pos + 1] == '/')) pos++
          if (pos + 1 < len) pos += 2
        }
        
        else -> break
      }
    }
  }
  
  private fun extractModulesUntilCloseParen(modules: MutableList<String>) {
    var parenCount = 1
    
    while (pos < len && parenCount > 0) {
      skipWhitespaceAndComments()
      if (pos >= len) break
      
      when (content[pos]) {
        '(' -> {
          parenCount++
          pos++
        }
        ')' -> {
          parenCount--
          pos++
        }
        '"', '\'' -> {
          val str = extractString()
          if (str != null && (str.startsWith(":") || str.contains(":"))) {
            val modulePath = if (str.startsWith(":")) str else ":$str"
            modules.add(modulePath)
          }
        }
        ',' -> pos++
        else -> pos++
      }
    }
  }
  
  private fun extractModulesUntilNewlineOrBlock(modules: MutableList<String>) {
    while (pos < len) {
      skipInlineWhitespaceAndComments()
      if (pos >= len) break
      
      val c = content[pos]
      
      if (c == '\n' || c == '\r') {
        pos++
        break
      }
      
      if (c == '{') break
      
      if (c == '"' || c == '\'') {
        val str = extractString()
        if (str != null && (str.startsWith(":") || str.contains(":"))) {
          val modulePath = if (str.startsWith(":")) str else ":$str"
          modules.add(modulePath)
        }
      } else if (c == ',') {
        pos++
      } else {
        pos++
      }
    }
  }
  
  private fun skipInlineWhitespaceAndComments() {
    while (pos < len) {
      val c = content[pos]
      when {
        c == ' ' || c == '\t' -> pos++
        
        pos + 1 < len && c == '/' && content[pos + 1] == '/' -> {
          while (pos < len && content[pos] != '\n') pos++
          break
        }
        
        pos + 1 < len && c == '/' && content[pos + 1] == '*' -> {
          pos += 2
          while (pos + 1 < len && !(content[pos] == '*' && content[pos + 1] == '/')) pos++
          if (pos + 1 < len) pos += 2
        }
        
        else -> break
      }
    }
  }
  
  private fun skipUntilNewlineOrBlock() {
    while (pos < len) {
      val c = content[pos]
      if (c == '\n' || c == '\r' || c == '{') break
      if (c == '"' || c == '\'') {
        extractString()
      } else {
        pos++
      }
    }
  }
  
  private fun extractString(): String? {
    if (pos >= len) return null
    val quote = content[pos]
    if (quote != '"' && quote != '\'') return null
    
    pos++
    val start = pos
    
    while (pos < len && content[pos] != quote) {
      if (content[pos] == '\\' && pos + 1 < len) {
        pos += 2
      } else {
        pos++
      }
    }
    
    val result = content.substring(start, pos)
    if (pos < len) pos++
    
    return result
  }
  
  fun stripCommentsOnly(): String {
    val result = StringBuilder()
    pos = 0
    
    while (pos < len) {
      when {
        pos + 1 < len && content[pos] == '/' && content[pos + 1] == '/' -> {
          while (pos < len && content[pos] != '\n') pos++
          if (pos < len) {
            result.append('\n')
            pos++
          }
        }
        pos + 1 < len && content[pos] == '/' && content[pos + 1] == '*' -> {
          pos += 2
          while (pos + 1 < len && !(content[pos] == '*' && content[pos + 1] == '/')) pos++
          if (pos + 1 < len) pos += 2
          result.append(' ')
        }
        content[pos] == '"' || content[pos] == '\'' -> {
          val quote = content[pos]
          result.append(content[pos])
          pos++
          while (pos < len && content[pos] != quote) {
            if (content[pos] == '\\' && pos + 1 < len) {
              result.append(content[pos])
              pos++
            }
            if (pos < len) {
              result.append(content[pos])
              pos++
            }
          }
          if (pos < len) {
            result.append(content[pos])
            pos++
          }
        }
        else -> {
          result.append(content[pos])
          pos++
        }
      }
    }
    
    return result.toString()
  }
  
  companion object {
    fun extractBlockWithBraces(content: String, blockName: String): String? {
      val parser = GradleParser(content)
      val cleaned = parser.stripCommentsOnly()
      
      val pattern = Regex("""\b$blockName\s*\{""")
      val match = pattern.find(cleaned) ?: return null
      
      val startBrace = match.range.last
      var braceCount = 1
      var i = startBrace + 1
      var inString = false
      var stringChar = ' '
      
      while (i < cleaned.length && braceCount > 0) {
        val c = cleaned[i]
        
        if (!inString && (c == '"' || c == '\'')) {
          inString = true
          stringChar = c
        } else if (inString && c == stringChar && (i == 0 || cleaned[i - 1] != '\\')) {
          inString = false
        } else if (!inString) {
          when (c) {
            '{' -> braceCount++
            '}' -> braceCount--
          }
        }
        i++
      }
      
      return if (braceCount == 0) {
        cleaned.substring(startBrace + 1, i - 1)
      } else null
    }
    
    fun extractAndroidBlock(content: String): String? {
      return extractBlockWithBraces(content, "android")
    }
  }
}

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
        val result = withContext(Dispatchers.IO) {
          val projectRoot = File(projectPath)
          val projectName = parseProjectName(projectRoot)
          val modules = scanProjectForModules(projectRoot)
          val modulesWithSaved = applySavedVariants(projectRoot, modules)
          val groups = organizeModulesIntoGroups(modulesWithSaved)
          val androidCount = modulesWithSaved.count { 
            it.moduleType == ModuleType.APPLICATION || it.moduleType == ModuleType.LIBRARY 
          }
          
          Triple(projectName, modulesWithSaved to groups, androidCount)
        }
        
        val (projectName, modulesAndGroups, androidCount) = result
        val (modules, groups) = modulesAndGroups
        
        _uiState.update { 
          it.copy(
            variants = modules, 
            moduleGroups = groups,
            isLoading = false,
            projectName = projectName,
            totalModules = modules.size,
            androidModules = androidCount,
          ) 
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(isLoading = false, error = "Fehler beim Laden der Module: ${e.message}")
        }
      }
    }
  }

  fun toggleGroupExpanded(groupName: String) {
    _uiState.update { state ->
      val updatedGroups = state.moduleGroups.map { group ->
        if (group.name == groupName) {
          group.copy(isExpanded = !group.isExpanded)
        } else {
          group
        }
      }
      state.copy(moduleGroups = updatedGroups)
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
      
      val updatedGroups = state.moduleGroups.map { group ->
        val updatedModules = group.modules.map { variant ->
          if (variant.moduleName == moduleName) {
            variant.copy(activeVariant = newVariant)
          } else {
            variant
          }
        }
        group.copy(modules = updatedModules)
      }
      
      saveVariants(updatedList)
      state.copy(variants = updatedList, moduleGroups = updatedGroups)
    }
  }

  private fun parseProjectName(projectRoot: File): String {
    val settingsKts = File(projectRoot, "settings.gradle.kts")
    val settingsGroovy = File(projectRoot, "settings.gradle")

    val settingsFile = when {
      settingsKts.exists() -> settingsKts
      settingsGroovy.exists() -> settingsGroovy
      else -> return projectRoot.name
    }

    val content = settingsFile.readText()
    
    val nameRegex = Regex("""rootProject\.name\s*=\s*["']([^"']+)["']""")
    nameRegex.find(content)?.let { return it.groupValues[1] }
    
    return projectRoot.name
  }

  private fun organizeModulesIntoGroups(modules: List<BuildVariant>): List<ModuleGroup> {
    val groupMap = mutableMapOf<String, MutableList<BuildVariant>>()
    
    for (module in modules) {
      val parts = module.moduleName.trimStart(':').split(':')
      val groupName = when {
        parts.size == 1 && parts[0] == "app" -> "App"
        parts.size == 1 -> "Root"
        else -> parts[0].replaceFirstChar { 
          if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
      }
      
      groupMap.getOrPut(groupName) { mutableListOf() }.add(module)
    }
    
    val sortOrder = listOf("App", "Root", "Feature", "Core")
    
    return groupMap.entries
      .sortedWith(compareBy { 
        val index = sortOrder.indexOf(it.key)
        if (index >= 0) index else sortOrder.size + it.key.hashCode()
      })
      .map { (name, moduleList) ->
        val sortedModules = moduleList.sortedWith(compareBy({ it.depth }, { it.moduleName }))
        ModuleGroup(
          name = name,
          modules = sortedModules,
          isExpanded = true,
        )
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
    val modulePathSet = mutableSetOf<String>()

    val settingsKts = File(projectRoot, "settings.gradle.kts")
    val settingsGroovy = File(projectRoot, "settings.gradle")

    val settingsFile = when {
      settingsKts.exists() -> settingsKts
      settingsGroovy.exists() -> settingsGroovy
      else -> null
    }

    if (settingsFile != null) {
      val content = settingsFile.readText()
      val parser = GradleParser(content)
      val modulePaths = parser.extractAllIncludeModules()
      
      for (gradlePath in modulePaths) {
        if (modulePathSet.contains(gradlePath)) continue
        modulePathSet.add(gradlePath)
        
        val relativePath = gradlePath.trimStart(':').replace(':', File.separatorChar)
        val moduleDir = File(projectRoot, relativePath)

        if (moduleDir.exists()) {
          val parts = gradlePath.trimStart(':').split(':')
          val parentModule = if (parts.size > 1) {
            ":" + parts.dropLast(1).joinToString(":")
          } else null
          
          val variant = parseModuleBuildFile(
            moduleName = gradlePath, 
            moduleDir = moduleDir,
            parentModule = parentModule,
            depth = parts.size - 1,
          )
          if (variant != null) {
            modules.add(variant)
          }
        }
      }
    } else {
      val appDir = File(projectRoot, "app")
      if (appDir.exists()) {
        parseModuleBuildFile(":app", appDir, null, 0)?.let { modules.add(it) }
      }
    }

    return modules.sortedWith(compareBy({ it.depth }, { it.moduleName }))
  }

  private fun parseModuleBuildFile(
    moduleName: String, 
    moduleDir: File,
    parentModule: String?,
    depth: Int,
  ): BuildVariant? {
    val buildKts = File(moduleDir, "build.gradle.kts")
    val buildGroovy = File(moduleDir, "build.gradle")

    val buildFile = when {
      buildKts.exists() -> buildKts
      buildGroovy.exists() -> buildGroovy
      else -> return null
    }

    val content = buildFile.readText()

    val moduleType = detectModuleType(content)
    
    val isAndroidModule = moduleType == ModuleType.APPLICATION || moduleType == ModuleType.LIBRARY

    if (!isAndroidModule) {
      return BuildVariant(
        moduleName = moduleName,
        modulePath = moduleDir.absolutePath,
        activeVariant = "main",
        availableVariants = listOf("main"),
        moduleType = moduleType,
        parentModule = parentModule,
        isSubmodule = parentModule != null,
        depth = depth,
      )
    }

    val androidBlock = GradleParser.extractAndroidBlock(content)
    val buildTypes = if (androidBlock != null) {
      extractBuildTypesFromAndroidBlock(androidBlock)
    } else {
      setOf("debug", "release")
    }
    
    val productFlavors = if (androidBlock != null) {
      extractProductFlavorsFromAndroidBlock(androidBlock)
    } else {
      emptySet()
    }

    val variants = if (productFlavors.isEmpty()) {
      buildTypes.toList()
    } else {
      productFlavors.flatMap { flavor ->
        buildTypes.map { type ->
          val capitalizedType = type.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
          }
          "$flavor$capitalizedType"
        }
      }
    }

    val finalVariants = if (variants.isEmpty()) listOf("debug", "release") else variants.sorted()

    val defaultVariant = finalVariants.find { it.contains("debug", ignoreCase = true) }
      ?: finalVariants.firstOrNull()
      ?: "debug"

    return BuildVariant(
      moduleName = moduleName,
      modulePath = moduleDir.absolutePath,
      activeVariant = defaultVariant,
      availableVariants = finalVariants,
      moduleType = moduleType,
      parentModule = parentModule,
      isSubmodule = parentModule != null,
      depth = depth,
    )
  }

  private fun detectModuleType(content: String): ModuleType {
    val parser = GradleParser(content)
    val cleaned = parser.stripCommentsOnly()
    
    return when {
      cleaned.contains("com.android.application") || 
        cleaned.contains("""id("com.android.application")""") ||
        cleaned.contains("""id('com.android.application')""") ||
        cleaned.contains("alias(libs.plugins.android.application)") -> ModuleType.APPLICATION
        
      cleaned.contains("com.android.library") || 
        cleaned.contains("""id("com.android.library")""") ||
        cleaned.contains("""id('com.android.library')""") ||
        cleaned.contains("alias(libs.plugins.android.library)") -> ModuleType.LIBRARY
        
      cleaned.contains("java-library") || 
        cleaned.contains("""id("java-library")""") ||
        cleaned.contains("""id('java-library')""") -> ModuleType.JAVA_LIBRARY
        
      cleaned.contains("org.jetbrains.kotlin.jvm") || 
        cleaned.contains("""kotlin("jvm")""") ||
        cleaned.contains("""id("org.jetbrains.kotlin.jvm")""") -> ModuleType.KOTLIN_LIBRARY
        
      else -> ModuleType.UNKNOWN
    }
  }

  private fun extractBuildTypesFromAndroidBlock(androidBlock: String): Set<String> {
    val foundTypes = mutableSetOf<String>()
    
    val buildTypesBlock = GradleParser.extractBlockWithBraces(androidBlock, "buildTypes")
    
    if (buildTypesBlock != null) {
      Regex("""(?:create|register)\s*\(\s*["']([^"']+)["']""").findAll(buildTypesBlock).forEach {
        foundTypes.add(it.groupValues[1])
      }
      
      Regex("""getByName\s*\(\s*["']([^"']+)["']""").findAll(buildTypesBlock).forEach {
        foundTypes.add(it.groupValues[1])
      }
      
      Regex("""named\s*\(\s*["']([^"']+)["']""").findAll(buildTypesBlock).forEach {
        foundTypes.add(it.groupValues[1])
      }
      
      val knownBuildTypes = listOf("debug", "release", "benchmark", "staging", "qa", "beta", "alpha", "canary")
      for (buildType in knownBuildTypes) {
        if (buildTypesBlock.contains(Regex("""\b$buildType\s*\{"""))) {
          foundTypes.add(buildType)
        }
      }
    }
    
    if (!foundTypes.contains("debug")) foundTypes.add("debug")
    if (!foundTypes.contains("release")) foundTypes.add("release")
    
    return foundTypes
  }

  private fun extractProductFlavorsFromAndroidBlock(androidBlock: String): Set<String> {
    val foundFlavors = mutableSetOf<String>()

    val flavorsBlock = GradleParser.extractBlockWithBraces(androidBlock, "productFlavors")
    
    if (flavorsBlock != null) {
      Regex("""(?:create|register)\s*\(\s*["']([^"']+)["']""").findAll(flavorsBlock).forEach {
        foundFlavors.add(it.groupValues[1])
      }
      
      Regex("""getByName\s*\(\s*["']([^"']+)["']""").findAll(flavorsBlock).forEach {
        foundFlavors.add(it.groupValues[1])
      }

      val excludedNames = setOf(
        "getByName", "create", "register", "named", "all", 
        "configureEach", "dimension", "matching", "withType",
        "maybeCreate", "findByName", "forEach", "filter", "map",
        "flavorDimensions", "setDimension"
      )
      
      Regex("""^\s*(\w+)\s*\{""", RegexOption.MULTILINE).findAll(flavorsBlock).forEach {
        val name = it.groupValues[1]
        if (name !in excludedNames && name.first().isLowerCase()) {
          foundFlavors.add(name)
        }
      }
    }
    
    return foundFlavors
  }
}
