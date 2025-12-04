package com.scto.codelikebastimove.feature.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.BuildToolsVersion
import com.scto.codelikebastimove.core.datastore.OnboardingConfig
import com.scto.codelikebastimove.core.datastore.OpenJdkVersion
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    val onboardingConfig: StateFlow<OnboardingConfig> = userPreferencesRepository.onboardingConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OnboardingConfig()
        )

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun nextPage() {
        if (_currentPage.value < 3) {
            _currentPage.value++
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun setFileAccessPermissionGranted(granted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setFileAccessPermissionGranted(granted)
        }
    }

    fun setUsageAnalyticsPermissionGranted(granted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUsageAnalyticsPermissionGranted(granted)
        }
    }

    fun setBatteryOptimizationDisabled(disabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setBatteryOptimizationDisabled(disabled)
        }
    }

    fun setSelectedOpenJdkVersion(version: OpenJdkVersion) {
        viewModelScope.launch {
            userPreferencesRepository.setSelectedOpenJdkVersion(version)
        }
    }

    fun setSelectedBuildToolsVersion(version: BuildToolsVersion) {
        viewModelScope.launch {
            userPreferencesRepository.setSelectedBuildToolsVersion(version)
        }
    }

    fun setGitEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setGitEnabled(enabled)
        }
    }

    fun setGitLfsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setGitLfsEnabled(enabled)
        }
    }

    fun setSshEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setSshEnabled(enabled)
        }
    }

    fun startInstallation() {
        viewModelScope.launch {
            userPreferencesRepository.setInstallationStarted(true)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
            userPreferencesRepository.setInstallationCompleted(true)
        }
    }
}
