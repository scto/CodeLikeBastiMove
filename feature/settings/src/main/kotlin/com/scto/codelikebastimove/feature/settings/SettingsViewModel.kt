package com.scto.codelikebastimove.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.BuildSettings
import com.scto.codelikebastimove.core.datastore.EditorSettings
import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.updater.UpdateCheckInterval
import com.scto.codelikebastimove.core.updater.UpdateRepository
import com.scto.codelikebastimove.core.updater.UpdateState
import com.scto.codelikebastimove.core.updater.UpdateWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

  private val userPreferencesRepository = UserPreferencesRepository(application)
  val updateRepository = UpdateRepository(application)

  val userPreferences: StateFlow<UserPreferences> =
    userPreferencesRepository.userPreferences.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UserPreferences(),
    )

  val updateState: StateFlow<UpdateState> =
    updateRepository.updateState.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UpdateState.Idle
    )

  fun setThemeMode(themeMode: ThemeMode) {
    viewModelScope.launch { userPreferencesRepository.setThemeMode(themeMode) }
  }

  fun setDynamicColorsEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setDynamicColorsEnabled(enabled) }
  }

  fun setLoggingEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setLoggingEnabled(enabled) }
  }

  fun setOnboardingCompleted(completed: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setOnboardingCompleted(completed) }
  }

  fun setFileAccessPermissionGranted(granted: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setFileAccessPermissionGranted(granted) }
  }

  fun setInstallationStarted(started: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setInstallationStarted(started) }
  }

  fun setInstallationCompleted(completed: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setInstallationCompleted(completed) }
  }

  fun resetOnboarding() {
    viewModelScope.launch {
      userPreferencesRepository.setOnboardingCompleted(false)
      userPreferencesRepository.setFileAccessPermissionGranted(false)
      userPreferencesRepository.setInstallationStarted(false)
      userPreferencesRepository.setInstallationCompleted(false)
    }
  }

  fun setEditorFontSize(fontSize: Float) {
    viewModelScope.launch { userPreferencesRepository.setEditorFontSize(fontSize) }
  }

  fun setEditorFontFamily(fontFamily: String) {
    viewModelScope.launch { userPreferencesRepository.setEditorFontFamily(fontFamily) }
  }

  fun setEditorTabSize(tabSize: Int) {
    viewModelScope.launch { userPreferencesRepository.setEditorTabSize(tabSize) }
  }

  fun setEditorUseSoftTabs(useSoftTabs: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorUseSoftTabs(useSoftTabs) }
  }

  fun setEditorShowLineNumbers(showLineNumbers: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorShowLineNumbers(showLineNumbers) }
  }

  fun setEditorWordWrap(wordWrap: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorWordWrap(wordWrap) }
  }

  fun setEditorHighlightCurrentLine(highlightCurrentLine: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorHighlightCurrentLine(highlightCurrentLine) }
  }

  fun setEditorAutoIndent(autoIndent: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorAutoIndent(autoIndent) }
  }

  fun setEditorShowWhitespace(showWhitespace: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorShowWhitespace(showWhitespace) }
  }

  fun setEditorBracketMatching(bracketMatching: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorBracketMatching(bracketMatching) }
  }

  fun setEditorAutoCloseBrackets(autoCloseBrackets: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorAutoCloseBrackets(autoCloseBrackets) }
  }

  fun setEditorAutoCloseQuotes(autoCloseQuotes: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorAutoCloseQuotes(autoCloseQuotes) }
  }

  fun setEditorTheme(editorTheme: String) {
    viewModelScope.launch { userPreferencesRepository.setEditorTheme(editorTheme) }
  }

  fun setEditorMinimapEnabled(minimapEnabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorMinimapEnabled(minimapEnabled) }
  }

  fun setEditorStickyScroll(stickyScroll: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorStickyScroll(stickyScroll) }
  }

  fun setEditorSmoothScrolling(smoothScrolling: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setEditorSmoothScrolling(smoothScrolling) }
  }

  fun updateEditorSettings(settings: EditorSettings) {
    viewModelScope.launch { userPreferencesRepository.updateEditorSettings(settings) }
  }

  fun setParallelBuildEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setParallelBuildEnabled(enabled) }
  }

  fun setCleanBeforeBuildEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setCleanBeforeBuildEnabled(enabled) }
  }

  fun setOfflineModeEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setOfflineModeEnabled(enabled) }
  }

  fun setAutoRunEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setAutoRunEnabled(enabled) }
  }

  fun updateBuildSettings(settings: BuildSettings) {
    viewModelScope.launch { userPreferencesRepository.updateBuildSettings(settings) }
  }

  fun setUpdateCheckInterval(interval: UpdateCheckInterval) {
    viewModelScope.launch {
      userPreferencesRepository.setUpdateCheckIntervalHours(interval.hours)
      if (interval.hours <= 0) {
        UpdateWorker.cancelPeriodicCheck(getApplication())
      } else {
        UpdateWorker.schedulePeriodicCheck(getApplication(), interval.hours)
      }
    }
  }

  fun checkForUpdates() {
    viewModelScope.launch {
      updateRepository.checkForUpdates()
    }
  }

  fun getUpdateCheckInterval(): UpdateCheckInterval {
    return UpdateCheckInterval.fromHours(userPreferences.value.updateCheckIntervalHours)
  }
}
