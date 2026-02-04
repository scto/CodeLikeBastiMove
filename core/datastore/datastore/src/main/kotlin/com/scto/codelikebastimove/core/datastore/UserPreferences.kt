package com.scto.codelikebastimove.core.datastore

data class GitConfig(val userName: String = "", val userEmail: String = "") {
  fun isConfigured(): Boolean = userName.isNotBlank() && userEmail.isNotBlank()
}

data class ClonedRepository(
  val path: String,
  val url: String,
  val branch: String,
  val clonedAt: Long,
)

enum class OpenJdkVersion(val displayName: String) {
  OPENJDK_17("OpenJDK 17"),
  OPENJDK_22("OpenJDK 22"),
}

enum class BuildToolsVersion(val displayName: String) {
  BUILD_TOOLS_35_0_1("35.0.1"),
  BUILD_TOOLS_34_0_2("34.0.2"),
  BUILD_TOOLS_33_0_1("33.0.1"),
}

enum class ProjectTemplateType(val displayName: String) {
  EMPTY_ACTIVITY("Empty Activity"),
  EMPTY_COMPOSE("Empty Compose Activity"),
  BOTTOM_NAVIGATION("Bottom Navigation"),
  NAVIGATION_DRAWER("Navigation Drawer"),
  TABBED("Tabbed Activity"),
  MULTI_MODULE("Multi-Module Project"),
  MVVM_CLEAN("MVVM Clean Architecture"),
  WEAR_OS("Wear OS App"),
  RESPONSIVE_FOLDABLE("Responsive/Foldable"),
}

data class VersionCatalogEntry(val name: String, val version: String)

data class VersionCatalogLibrary(
  val alias: String,
  val group: String,
  val name: String,
  val versionRef: String,
)

data class VersionCatalogPlugin(val alias: String, val id: String, val versionRef: String)

data class VersionCatalogBundle(val alias: String, val libraries: List<String>)

data class VersionCatalog(
  val versions: List<VersionCatalogEntry> = emptyList(),
  val libraries: List<VersionCatalogLibrary> = emptyList(),
  val plugins: List<VersionCatalogPlugin> = emptyList(),
  val bundles: List<VersionCatalogBundle> = emptyList(),
) {
  fun toTomlContent(): String {
    val sb = StringBuilder()

    if (versions.isNotEmpty()) {
      sb.appendLine("[versions]")
      versions.forEach { sb.appendLine("${it.name} = \"${it.version}\"") }
      sb.appendLine()
    }

    if (libraries.isNotEmpty()) {
      sb.appendLine("[libraries]")
      libraries.forEach {
        sb.appendLine(
          "${it.alias} = { group = \"${it.group}\", name = \"${it.name}\", version.ref = \"${it.versionRef}\" }"
        )
      }
      sb.appendLine()
    }

    if (plugins.isNotEmpty()) {
      sb.appendLine("[plugins]")
      plugins.forEach {
        sb.appendLine("${it.alias} = { id = \"${it.id}\", version.ref = \"${it.versionRef}\" }")
      }
      sb.appendLine()
    }

    if (bundles.isNotEmpty()) {
      sb.appendLine("[bundles]")
      bundles.forEach {
        val libs = it.libraries.joinToString(", ") { lib -> "\"$lib\"" }
        sb.appendLine("${it.alias} = [$libs]")
      }
    }

    return sb.toString().trimEnd()
  }
}

data class GradleInfo(
  val gradleVersion: String = "8.10.2",
  val distributionUrl: String = "https://services.gradle.org/distributions/gradle-8.10.2-bin.zip",
  val agpVersion: String = "8.7.3",
  val kotlinVersion: String = "2.1.0",
  val composeBomVersion: String = "2024.12.01",
  val usesVersionCatalog: Boolean = true,
)

data class TemplateInfo(
  val id: String,
  val name: String,
  val description: String,
  val version: String,
  val lastUpdated: Long,
  val templateType: ProjectTemplateType,
  val gradleInfo: GradleInfo = GradleInfo(),
  val versionCatalog: VersionCatalog = VersionCatalog(),
  val supportedLanguages: List<String> = listOf("Kotlin"),
  val features: List<String> = emptyList(),
  val minSdk: Int = 24,
  val targetSdk: Int = 35,
  val compileSdk: Int = 35,
)

data class TemplateRegistry(
  val totalTemplateCount: Int = 0,
  val registryLastUpdated: Long = 0L,
  val registryVersion: String = "1.0.0",
  val templates: List<TemplateInfo> = emptyList(),
)

data class StoredProject(
  val name: String,
  val path: String,
  val packageName: String,
  val templateType: ProjectTemplateType,
  val createdAt: Long,
  val lastOpenedAt: Long,
)

data class OnboardingConfig(
  val onboardingCompleted: Boolean = false,
  val fileAccessPermissionGranted: Boolean = false,
  val usageAnalyticsPermissionGranted: Boolean = false,
  val batteryOptimizationDisabled: Boolean = false,
  val selectedOpenJdkVersion: OpenJdkVersion = OpenJdkVersion.OPENJDK_17,
  val selectedBuildToolsVersion: BuildToolsVersion = BuildToolsVersion.BUILD_TOOLS_35_0_1,
  val gitEnabled: Boolean = false,
  val gitLfsEnabled: Boolean = false,
  val sshEnabled: Boolean = false,
  val installationStarted: Boolean = false,
  val installationCompleted: Boolean = false,
)

enum class CursorAnimationType {
  NONE,
  FADE,
  BLINK,
  SCALE,
}

enum class RenderWhitespaceMode {
  NONE,
  SELECTION,
  BOUNDARY,
  TRAILING,
  ALL,
}

enum class LineEndingType {
  LF,
  CRLF,
  CR,
}

data class EditorSettings(
  val fontSize: Float = 14f,
  val fontFamily: String = "JetBrains Mono",
  val tabSize: Int = 4,
  val useSoftTabs: Boolean = true,
  val showLineNumbers: Boolean = true,
  val pinLineNumber: Boolean = false,
  val wordWrap: Boolean = false,
  val highlightCurrentLine: Boolean = true,
  val autoIndent: Boolean = true,
  val showWhitespace: Boolean = false,
  val bracketMatching: Boolean = true,
  val autoCloseBrackets: Boolean = true,
  val autoCloseQuotes: Boolean = true,
  val editorTheme: String = "Darcula",
  val minimapEnabled: Boolean = true,
  val stickyScroll: Boolean = false,
  val fastDelete: Boolean = false,
  val cursorAnimation: CursorAnimationType = CursorAnimationType.FADE,
  val keyboardSuggestion: Boolean = true,
  val lineSpacing: Float = 1.2f,
  val renderWhitespace: RenderWhitespaceMode = RenderWhitespaceMode.NONE,
  val hideSoftKbd: Boolean = false,
  val lineEndingSetting: LineEndingType = LineEndingType.LF,
  val finalNewline: Boolean = true,
  val cursorBlinkRate: Int = 530,
  val smoothScrolling: Boolean = true,
)

data class BuildSettings(
  val parallelBuildEnabled: Boolean = true,
  val cleanBeforeBuildEnabled: Boolean = false,
  val offlineModeEnabled: Boolean = false,
  val autoRunEnabled: Boolean = true,
)

data class UserPreferences(
  val themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
  val dynamicColorsEnabled: Boolean = true,
  val gitConfig: GitConfig = GitConfig(),
  val clonedRepositories: List<ClonedRepository> = emptyList(),
  val onboardingConfig: OnboardingConfig = OnboardingConfig(),
  val rootDirectory: String = "",
  val projects: List<StoredProject> = emptyList(),
  val currentProjectPath: String = "",
  val loggingEnabled: Boolean = true,
  val loggingInitialized: Boolean = false,
  val editorSettings: EditorSettings = EditorSettings(),
  val buildSettings: BuildSettings = BuildSettings(),
  val updateCheckIntervalHours: Long = 24,
)
