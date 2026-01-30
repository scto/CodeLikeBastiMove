package com.scto.codelikebastimove.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.auth.AuthRepository
import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

  private val userPreferencesRepository = UserPreferencesRepository(application)
  private val authRepository = AuthRepository.getInstance()

  private val _signOutTriggered = MutableStateFlow(false)
  val signOutTriggered: StateFlow<Boolean> = _signOutTriggered.asStateFlow()

  val userPreferences: StateFlow<UserPreferences> =
    userPreferencesRepository.userPreferences.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UserPreferences(),
    )

  val currentUserEmail: String?
    get() = authRepository.currentUser?.email

  fun setThemeMode(themeMode: ThemeMode) {
    viewModelScope.launch { userPreferencesRepository.setThemeMode(themeMode) }
  }

  fun setDynamicColorsEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setDynamicColorsEnabled(enabled) }
  }

  fun signOut() {
    authRepository.signOut()
    _signOutTriggered.value = true
  }

  fun resetSignOutTrigger() {
    _signOutTriggered.value = false
  }
}
