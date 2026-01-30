package com.scto.codelikebastimove.core.plugin.api.extension

import kotlin.reflect.KClass

interface ExtensionRegistry {
  fun <T : Extension> registerExtensionPoint(descriptor: ExtensionPointDescriptor<T>)

  fun <T : Extension> getExtensionPoint(id: String): ExtensionPointDescriptor<T>?

  fun getExtensionPoints(): List<ExtensionPointDescriptor<*>>

  fun <T : Extension> registerExtension(
    extensionPointId: String,
    pluginId: String,
    extension: T,
  ): Boolean

  fun <T : Extension> unregisterExtension(
    extensionPointId: String,
    pluginId: String,
    extensionId: String,
  ): Boolean

  fun unregisterAllExtensions(pluginId: String)

  fun <T : Extension> getExtensions(extensionPointId: String): List<T>

  fun <T : Extension> getExtensions(extensionClass: KClass<T>): List<T>

  fun <T : Extension> getExtension(extensionPointId: String, extensionId: String): T?

  fun getExtensionsByPlugin(pluginId: String): Map<String, List<Extension>>
}

object ExtensionPoints {
  const val EDITOR_ACTIONS = "com.scto.codelikebastimove.extension.editorActions"
  const val TOOL_WINDOWS = "com.scto.codelikebastimove.extension.toolWindows"
  const val THEME_PROVIDERS = "com.scto.codelikebastimove.extension.themeProviders"
  const val PROJECT_WIZARDS = "com.scto.codelikebastimove.extension.projectWizards"
  const val CODE_ANALYZERS = "com.scto.codelikebastimove.extension.codeAnalyzers"
  const val FILE_TYPE_HANDLERS = "com.scto.codelikebastimove.extension.fileTypeHandlers"
  const val COMMANDS = "com.scto.codelikebastimove.extension.commands"
  const val BACKGROUND_TASKS = "com.scto.codelikebastimove.extension.backgroundTasks"
}

fun createDefaultExtensionPoints(): List<ExtensionPointDescriptor<*>> =
  listOf(
    ExtensionPointDescriptor(
      id = ExtensionPoints.EDITOR_ACTIONS,
      name = "Editor Actions",
      extensionClass = EditorAction::class,
      description = "Actions available in the editor context menu and toolbar",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.TOOL_WINDOWS,
      name = "Tool Windows",
      extensionClass = ToolWindowProvider::class,
      description = "Custom tool windows and panels",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.THEME_PROVIDERS,
      name = "Theme Providers",
      extensionClass = ThemeProvider::class,
      description = "Custom themes and color schemes",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.PROJECT_WIZARDS,
      name = "Project Wizards",
      extensionClass = ProjectWizardExtension::class,
      description = "Project creation templates and wizards",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.CODE_ANALYZERS,
      name = "Code Analyzers",
      extensionClass = CodeAnalyzer::class,
      description = "Code analysis and linting tools",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.FILE_TYPE_HANDLERS,
      name = "File Type Handlers",
      extensionClass = FileTypeHandler::class,
      description = "Handlers for custom file types",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.COMMANDS,
      name = "Commands",
      extensionClass = CommandContribution::class,
      description = "Command palette entries and actions",
    ),
    ExtensionPointDescriptor(
      id = ExtensionPoints.BACKGROUND_TASKS,
      name = "Background Tasks",
      extensionClass = BackgroundTask::class,
      description = "Background services and scheduled tasks",
    ),
  )
