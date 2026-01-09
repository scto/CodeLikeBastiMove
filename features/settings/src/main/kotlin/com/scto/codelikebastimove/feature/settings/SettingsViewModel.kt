package com.scto.codelikebastimove.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userPreferencesRepository = UserPreferencesRepository(application)

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(themeMode)
        }
    }

    fun setDynamicColorsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDynamicColorsEnabled(enabled)
        }
    }
}
