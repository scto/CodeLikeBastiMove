package com.scto.codelikebastimove.core.plugin.api.descriptor

data class PluginDescriptor(
    val id: String,
    val name: String,
    val version: String,
    val description: String = "",
    val author: String = "",
    val vendor: PluginVendor? = null,
    val minHostVersion: String = "1.0.0",
    val maxHostVersion: String? = null,
    val dependencies: List<PluginDependency> = emptyList(),
    val extensionPoints: List<String> = emptyList(),
    val permissions: List<PluginPermission> = emptyList(),
    val category: PluginCategory = PluginCategory.GENERAL,
    val iconPath: String? = null,
    val changeNotes: String = "",
    val metadata: Map<String, String> = emptyMap()
)

data class PluginVendor(
    val name: String,
    val url: String = "",
    val email: String = ""
)

data class PluginDependency(
    val pluginId: String,
    val version: String,
    val optional: Boolean = false
)

enum class PluginPermission(val displayName: String, val description: String) {
    FILE_ACCESS("File Access", "Read and write files in the project"),
    EDITOR_ACCESS("Editor Access", "Modify editor content and behavior"),
    PROJECT_ACCESS("Project Access", "Access project structure and configuration"),
    NETWORK_ACCESS("Network Access", "Make network requests"),
    SYSTEM_ACCESS("System Access", "Access system information"),
    UI_ACCESS("UI Access", "Display custom UI elements"),
    BUILD_ACCESS("Build Access", "Access build system and outputs"),
    GIT_ACCESS("Git Access", "Access Git repository operations"),
    SETTINGS_ACCESS("Settings Access", "Read and modify settings"),
    THEME_ACCESS("Theme Access", "Modify application theme")
}

enum class PluginCategory(val displayName: String) {
    GENERAL("General"),
    EDITOR("Editor"),
    LANGUAGE_SUPPORT("Language Support"),
    CODE_ANALYSIS("Code Analysis"),
    BUILD_TOOLS("Build Tools"),
    VERSION_CONTROL("Version Control"),
    DEBUGGING("Debugging"),
    TESTING("Testing"),
    UI_THEME("UI/Theme"),
    PRODUCTIVITY("Productivity"),
    FRAMEWORKS("Frameworks"),
    OTHER("Other")
}

enum class PluginState {
    UNINSTALLED,
    INSTALLED,
    RESOLVED,
    STARTING,
    ACTIVE,
    STOPPING,
    DISABLED,
    ERROR
}
