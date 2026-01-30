package com.scto.codelikebastimove.feature.onboarding

import android.app.Application
import android.os.Build
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.BuildToolsVersion
import com.scto.codelikebastimove.core.datastore.OnboardingConfig
import com.scto.codelikebastimove.core.datastore.OpenJdkVersion
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.logger.CLBMLogger
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

  private val userPreferencesRepository = UserPreferencesRepository(application)

  val onboardingConfig: StateFlow<OnboardingConfig> =
    userPreferencesRepository.onboardingConfig.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = OnboardingConfig(),
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
    viewModelScope.launch { userPreferencesRepository.setFileAccessPermissionGranted(granted) }
  }

  fun setUsageAnalyticsPermissionGranted(granted: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setUsageAnalyticsPermissionGranted(granted) }
  }

  fun setBatteryOptimizationDisabled(disabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setBatteryOptimizationDisabled(disabled) }
  }

  fun setSelectedOpenJdkVersion(version: OpenJdkVersion) {
    viewModelScope.launch { userPreferencesRepository.setSelectedOpenJdkVersion(version) }
  }

  fun setSelectedBuildToolsVersion(version: BuildToolsVersion) {
    viewModelScope.launch { userPreferencesRepository.setSelectedBuildToolsVersion(version) }
  }

  fun setGitEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setGitEnabled(enabled) }
  }

  fun setGitLfsEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setGitLfsEnabled(enabled) }
  }

  fun setSshEnabled(enabled: Boolean) {
    viewModelScope.launch { userPreferencesRepository.setSshEnabled(enabled) }
  }

  fun startInstallation() {
    viewModelScope.launch { userPreferencesRepository.setInstallationStarted(true) }
  }

  fun completeOnboarding() {
    viewModelScope.launch {
      createClbmProjectsDirectory()
      userPreferencesRepository.setOnboardingCompleted(true)
      userPreferencesRepository.setInstallationCompleted(true)
    }
  }

  private suspend fun createClbmProjectsDirectory() {
    val existingDir = userPreferencesRepository.getRootDirectoryOnce()
    if (existingDir.isNotBlank() && File(existingDir).exists()) {
      CLBMLogger.d(TAG, "CLBMProjects directory already exists at: $existingDir")
      return
    }

    val clbmDir =
      if (hasExternalStoragePermission()) {
        try {
          val externalStorage = Environment.getExternalStorageDirectory()
          val clbmProjectsDir = File(externalStorage, "CLBMProjects")
          CLBMLogger.d(TAG, "Attempting to create CLBMProjects at: ${clbmProjectsDir.absolutePath}")

          if (!clbmProjectsDir.exists()) {
            val created = clbmProjectsDir.mkdirs()
            CLBMLogger.d(TAG, "Directory creation result: $created")
            if (!created) {
              throw Exception("Failed to create directory")
            }
          }

          if (clbmProjectsDir.canWrite()) {
            CLBMLogger.d(TAG, "CLBMProjects created successfully at external storage")
            clbmProjectsDir.absolutePath
          } else {
            CLBMLogger.w(TAG, "Cannot write to external storage, falling back to internal")
            createInternalDirectory()
          }
        } catch (e: Exception) {
          CLBMLogger.e(TAG, "Failed to create external directory: ${e.message}", e)
          createInternalDirectory()
        }
      } else {
        CLBMLogger.d(TAG, "No external storage permission, using internal storage")
        createInternalDirectory()
      }

    CLBMLogger.d(TAG, "Final CLBMProjects directory: $clbmDir")
    userPreferencesRepository.setRootDirectory(clbmDir)
  }

  private fun hasExternalStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Environment.isExternalStorageManager()
    } else {
      true
    }
  }

  private fun createInternalDirectory(): String {
    val filesDir = getApplication<Application>().filesDir
    val clbmProjectsDir = File(filesDir, "CLBMProjects")
    if (!clbmProjectsDir.exists()) {
      clbmProjectsDir.mkdirs()
    }
    CLBMLogger.d(TAG, "Created CLBMProjects at internal storage: ${clbmProjectsDir.absolutePath}")
    return clbmProjectsDir.absolutePath
  }

  companion object {
    private const val TAG = "OnboardingViewModel"
  }
}
