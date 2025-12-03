package com.scto.codelikebastimove.core.datastore

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    val dynamicColorsEnabled: Boolean = true
)