package com.scto.settings.core.model

/**
 * App Settings
 *
 * Created on Jan 03, 2025.
 *
 */
data class Settings(
    val darkMode: Boolean = true,
    val dynamicColors: Boolean = true,
    val isFirstRun: Boolean = true,
    val isAnalyticsEnabled: Boolean = false
)