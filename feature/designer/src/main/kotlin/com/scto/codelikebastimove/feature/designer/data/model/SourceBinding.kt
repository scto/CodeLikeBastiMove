package com.scto.codelikebastimove.feature.designer.data.model

data class SourceBinding(
  val filePath: String,
  val functionName: String? = null,
  val insertionAnchor: InsertionAnchor = InsertionAnchor.FUNCTION_BODY,
  val lineNumber: Int? = null,
  val packageName: String = "",
  val imports: List<String> = emptyList(),
)

enum class InsertionAnchor {
  FUNCTION_BODY,
  COMPOSABLE_CONTENT,
  FILE_END,
  AFTER_LINE,
  REPLACE_BLOCK,
}

data class DesignerProject(
  val id: String,
  val name: String,
  val sourceBinding: SourceBinding?,
  val blockTree: BlockTree,
  val themeDescriptor: ThemeDescriptor?,
  val customComponents: List<String> = emptyList(),
  val createdAt: Long = System.currentTimeMillis(),
  val modifiedAt: Long = System.currentTimeMillis(),
)

data class ThemeDescriptor(
  val name: String,
  val description: String = "",
  val origin: ThemeOrigin = ThemeOrigin.DEFAULT,
  val target: ThemeTarget = ThemeTarget.PROJECT,
  val isDynamic: Boolean = false,
  val seedColor: String? = null,
  val schemaStyle: String = "TonalSpot",
  val customColors: Map<String, String> = emptyMap(),
)

enum class ThemeOrigin {
  DEFAULT,
  THEME_BUILDER,
  CUSTOM,
  REPOSITORY,
}

enum class ThemeTarget {
  PROJECT,
  REPOSITORY,
}

data class ExportConfig(
  val exportPath: String,
  val themePath: String = "ui/theme",
  val componentsPath: String = "ui/components",
  val componentPrefix: String = "",
  val themeName: String = "AppTheme",
  val includeImports: Boolean = true,
  val formatCode: Boolean = true,
  val validateSyntax: Boolean = true,
  val exportThemeToRepo: Boolean = false,
  val themeRepoName: String = "",
  val themeRepoDescription: String = "",
)

data class ExportResult(
  val success: Boolean,
  val generatedCode: String,
  val filePath: String?,
  val errors: List<String> = emptyList(),
  val warnings: List<String> = emptyList(),
)

data class ValidationResult(
  val isValid: Boolean,
  val errors: List<ValidationError> = emptyList(),
  val warnings: List<ValidationWarning> = emptyList(),
)

data class ValidationError(
  val line: Int?,
  val column: Int?,
  val message: String,
  val severity: ErrorSeverity = ErrorSeverity.ERROR,
)

data class ValidationWarning(val line: Int?, val message: String)

enum class ErrorSeverity {
  ERROR,
  WARNING,
  INFO,
}
