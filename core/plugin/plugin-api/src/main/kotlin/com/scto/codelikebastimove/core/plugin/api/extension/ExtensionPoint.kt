package com.scto.codelikebastimove.core.plugin.api.extension

import kotlin.reflect.KClass

data class ExtensionPointDescriptor<T : Extension>(
  val id: String,
  val name: String,
  val extensionClass: KClass<T>,
  val description: String = "",
  val allowMultiple: Boolean = true,
)

interface Extension {
  val id: String
  val name: String
  val description: String
    get() = ""

  val priority: Int
    get() = 0
}

interface EditorAction : Extension {
  val icon: String?
    get() = null

  val shortcut: String?
    get() = null

  fun isEnabled(context: EditorActionContext): Boolean = true

  suspend fun execute(context: EditorActionContext)
}

data class EditorActionContext(
  val filePath: String,
  val fileName: String,
  val fileExtension: String,
  val selectedText: String?,
  val cursorLine: Int,
  val cursorColumn: Int,
  val content: String,
)

interface ToolWindowProvider : Extension {
  val icon: String?
    get() = null

  val position: ToolWindowPosition
    get() = ToolWindowPosition.BOTTOM

  val isCloseable: Boolean
    get() = true

  val isVisible: Boolean
    get() = true

  fun createContent(): Any

  fun onActivate() {}

  fun onDeactivate() {}
}

enum class ToolWindowPosition {
  LEFT,
  RIGHT,
  BOTTOM,
  TOP,
}

interface ThemeProvider : Extension {
  val isDark: Boolean
  val colors: Map<String, String>
  val typography: Map<String, Any>

  fun apply()
}

interface ProjectWizardExtension : Extension {
  val templateName: String
  val templateDescription: String
  val templateIcon: String?
    get() = null

  val category: String
    get() = "General"

  fun getConfigurationSteps(): List<WizardStep>

  suspend fun createProject(config: Map<String, Any>, targetPath: String): ProjectCreationResult
}

data class WizardStep(
  val id: String,
  val title: String,
  val description: String = "",
  val fields: List<WizardField>,
)

data class WizardField(
  val id: String,
  val label: String,
  val type: WizardFieldType,
  val defaultValue: Any? = null,
  val required: Boolean = false,
  val options: List<String> = emptyList(),
  val validation: ((Any?) -> Boolean)? = null,
)

enum class WizardFieldType {
  TEXT,
  NUMBER,
  BOOLEAN,
  SELECT,
  MULTI_SELECT,
  FILE_PATH,
  DIRECTORY_PATH,
}

data class ProjectCreationResult(
  val success: Boolean,
  val projectPath: String? = null,
  val errorMessage: String? = null,
)

interface CodeAnalyzer : Extension {
  val supportedFileExtensions: List<String>

  suspend fun analyze(filePath: String, content: String): List<AnalysisResult>
}

data class AnalysisResult(
  val severity: AnalysisSeverity,
  val message: String,
  val line: Int,
  val column: Int,
  val endLine: Int? = null,
  val endColumn: Int? = null,
  val quickFixes: List<QuickFix> = emptyList(),
)

enum class AnalysisSeverity {
  ERROR,
  WARNING,
  INFO,
  HINT,
}

data class QuickFix(val id: String, val description: String, val action: suspend () -> Unit)

interface FileTypeHandler : Extension {
  val fileExtensions: List<String>
  val mimeTypes: List<String>
    get() = emptyList()

  val icon: String?
    get() = null

  fun canHandle(filePath: String): Boolean

  fun getHighlightingRules(): Map<String, String> = emptyMap()
}

interface CommandContribution : Extension {
  val commandId: String
  val title: String
  val category: String
    get() = "General"

  val icon: String?
    get() = null

  val shortcut: String?
    get() = null

  fun isEnabled(): Boolean = true

  suspend fun execute(args: Map<String, Any> = emptyMap())
}

interface BackgroundTask : Extension {
  val intervalMs: Long
    get() = 60000L

  val runOnStartup: Boolean
    get() = false

  suspend fun execute()
}
