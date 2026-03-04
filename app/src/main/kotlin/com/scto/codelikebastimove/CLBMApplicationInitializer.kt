package com.scto.codelikebastimove

import android.content.res.Configuration

import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.logger.BuildConfig
import com.scto.codelikebastimove.core.logger.CLBMLogger
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Übernimmt komplexe Startup-Aufgaben wie Sora Editor Init, 
 * Textmate-Konfiguration und Theme-Synchronisation.
 */
object CLBMApplicationInitializer {

    private const val TAG = "AppInitializer"

    fun initialize(app: CLBMApplication, scope: CoroutineScope) {
        val repo = app.userPreferencesRepository

        // 1. Logging-Status mit DataStore synchronisieren
        scope.launch {
            repo.initializeLoggingIfNeeded(BuildConfig.LOGGING_DEFAULT_ENABLED)
            repo.loggingEnabled.collect { enabled -> CLBMLogger.setEnabled(enabled) }
        }

        // 2. App-Theme Überwachung
        scope.launch {
            repo.userPreferences
                .map { it.themeMode }
                .distinctUntilChanged()
                .collect { themeMode ->
                    syncEditorThemeForMode(app, themeMode)
                }
        }

        // 3. Standort-Setup (Editor-Umgebung)
        scope.launch {
            setupEditorEnvironment(app)
            syncEditorThemeForCurrentMode(app)
        }
        
        CLBMLogger.d(TAG, "Standort-Setup für Editor und Themes abgeschlossen.")
    }

    private suspend fun setupEditorEnvironment(app: CLBMApplication) {
        // Platzhalter für Textmate, Sora-Editor spezifische Initialisierungen
        CLBMLogger.i(TAG, "Entwicklungsumgebung vorbereitet.")
    }

    suspend fun syncEditorThemeForCurrentMode(app: CLBMApplication) {
        val preferences = app.userPreferencesRepository.getUserPreferencesOnce()
        syncEditorThemeForMode(app, preferences.themeMode)
    }

    private suspend fun syncEditorThemeForMode(app: CLBMApplication, themeMode: ThemeMode) {
        val repo = app.userPreferencesRepository
        val editorSettings = repo.getEditorSettingsOnce()
        val currentThemeName = editorSettings.editorTheme
        val isDark = resolveIsDark(app, themeMode)

        val currentTheme = EditorThemes.getTheme(currentThemeName)
        if (currentTheme.isDark == isDark) return

        val newTheme = EditorThemes.getThemeForMode(currentThemeName, isDark)
        if (newTheme.name != currentThemeName) {
            repo.setEditorTheme(newTheme.name)
            CLBMLogger.d(TAG, "Editor-Theme an aktuellen Modus angepasst: ${newTheme.name}")
        }
    }

    private fun resolveIsDark(app: CLBMApplication, themeMode: ThemeMode): Boolean {
        return when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.FOLLOW_SYSTEM -> {
                val nightMask = app.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightMask == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
}