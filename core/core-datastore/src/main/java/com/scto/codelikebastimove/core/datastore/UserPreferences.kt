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

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    val dynamicColorsEnabled: Boolean = true,
    val gitConfig: GitConfig = GitConfig(),
    val clonedRepositories: List<ClonedRepository> = emptyList()
)