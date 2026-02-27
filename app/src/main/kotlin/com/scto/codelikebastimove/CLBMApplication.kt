package com.scto.codelikebastimove

import android.app.Application
import android.content.res.Configuration

import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.logger.BuildConfig
import com.scto.codelikebastimove.core.logger.CLBMLogger
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CLBMApplication : Application() {

  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private lateinit var userPreferencesRepository: UserPreferencesRepository

  override fun onCreate() {
    super.onCreate()

    CLBMLogger.initialize(BuildConfig.LOGGING_DEFAULT_ENABLED)

    userPreferencesRepository = UserPreferencesRepository(this)
    applicationScope.launch {
      userPreferencesRepository.initializeLoggingIfNeeded(BuildConfig.LOGGING_DEFAULT_ENABLED)
    }

    applicationScope.launch {
      userPreferencesRepository.loggingEnabled.collect { enabled -> CLBMLogger.setEnabled(enabled) }
    }

    observeThemeModeChanges()

    applicationScope.launch {
      syncEditorThemeForCurrentMode()
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)

    applicationScope.launch {
      syncEditorThemeForCurrentMode()
    }
  }

  private fun observeThemeModeChanges() {
    applicationScope.launch {
      userPreferencesRepository.userPreferences
        .map { it.themeMode }
        .distinctUntilChanged()
        .collect { themeMode ->
          syncEditorThemeForMode(themeMode)
        }
    }
  }

  private suspend fun syncEditorThemeForCurrentMode() {
    val preferences = userPreferencesRepository.getUserPreferencesOnce()
    syncEditorThemeForMode(preferences.themeMode)
  }

  private suspend fun syncEditorThemeForMode(themeMode: ThemeMode) {
    val editorSettings = userPreferencesRepository.getEditorSettingsOnce()
    val currentThemeName = editorSettings.editorTheme
    val isDark = resolveIsDark(themeMode)

    val currentTheme = EditorThemes.getTheme(currentThemeName)
    if (currentTheme.isDark == isDark) return

    val newTheme = EditorThemes.getThemeForMode(currentThemeName, isDark)
    if (newTheme.name != currentThemeName) {
      userPreferencesRepository.setEditorTheme(newTheme.name)
    }
  }

  private fun resolveIsDark(themeMode: ThemeMode): Boolean {
    return when (themeMode) {
      ThemeMode.DARK -> true
      ThemeMode.LIGHT -> false
      ThemeMode.FOLLOW_SYSTEM -> {
        val nightMask = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        nightMask == Configuration.UI_MODE_NIGHT_YES
      }
    }
  }
}
