package com.scto.codelikebastimove.core.datastore

data class GitConfig(
    val userName: String = "",
    val userEmail: String = ""
) {
    fun isConfigured(): Boolean = userName.isNotBlank() && userEmail.isNotBlank()
}

data class ClonedRepository(
    val path: String,
    val url: String,
    val branch: String,
    val clonedAt: Long
)

enum class OpenJdkVersion(val displayName: String) {
    OPENJDK_17("OpenJDK 17"),
    OPENJDK_22("OpenJDK 22")
}

enum class BuildToolsVersion(val displayName: String) {
    BUILD_TOOLS_35_0_1("35.0.1"),
    BUILD_TOOLS_34_0_2("34.0.2"),
    BUILD_TOOLS_33_0_1("33.0.1")
}

enum class ProjectTemplateType(val displayName: String) {
    EMPTY_ACTIVITY("Empty Activity"),
    EMPTY_COMPOSE("Empty Compose Activity"),
    BOTTOM_NAVIGATION("Bottom Navigation"),
    NAVIGATION_DRAWER("Navigation Drawer"),
    TABBED("Tabbed Activity")
}

data class StoredProject(
    val name: String,
    val path: String,
    val packageName: String,
    val templateType: ProjectTemplateType,
    val createdAt: Long,
    val lastOpenedAt: Long
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
    val installationCompleted: Boolean = false
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
    val loggingInitialized: Boolean = false
)