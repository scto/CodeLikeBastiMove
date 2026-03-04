package com.scto.codelikebastimove.feature.onboarding

import android.app.Application
import android.os.Build
import android.os.Environment

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.scto.codelikebastimove.core.datastore.*
import com.scto.codelikebastimove.core.logger.CLBMLogger
import com.scto.codelikebastimove.core.utils.PermissionUtils
import com.scto.codelikebastimove.core.utils.ProjectUtils

import java.io.File
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel für den Onboarding-Prozess.
 * Verwaltet Berechtigungen, Installationseinstellungen und die Ersteinrichtung des Projektordners.
 */
class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val TAG = "OnboardingVM"

    // Onboarding-Konfiguration aus dem DataStore
    val onboardingConfig: StateFlow<OnboardingConfig> =
        userPreferencesRepository.onboardingConfig.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OnboardingConfig(),
        )

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun setCurrentPage(page: Int) { _currentPage.value = page }
    fun nextPage() { if (_currentPage.value < 3) _currentPage.value++ }
    fun previousPage() { if (_currentPage.value > 0) _currentPage.value-- }

    // --- Berechtigungen & Telemetrie ---

    fun setPermission(type: PermissionUtils.PermissionType, granted: Boolean) {
        viewModelScope.launch { PermissionUtils.setPermissionState(userPreferencesRepository, type, granted) }
    }

    // Abwärtskompatible Setter für UI-Events
    fun setFileAccessPermissionGranted(granted: Boolean) = setPermission(PermissionUtils.PermissionType.FILE_ACCESS, granted)
    fun setUsageAnalyticsPermissionGranted(granted: Boolean) = setPermission(PermissionUtils.PermissionType.USAGE_STATS, granted)
    fun setBatteryOptimizationDisabled(disabled: Boolean) = setPermission(PermissionUtils.PermissionType.BATTERY_OPTIMIZATION, disabled)

    // --- Installations-Optionen ---
    fun setSelectedOpenJdkVersion(version: OpenJdkVersion) = viewModelScope.launch { 
        userPreferencesRepository.setSelectedOpenJdkVersion(version) 
    }
    
    fun setSelectedBuildToolsVersion(version: BuildToolsVersion) = viewModelScope.launch { 
        userPreferencesRepository.setSelectedBuildToolsVersion(version) 
    }

    fun setGitEnabled(enabled: Boolean) = viewModelScope.launch { userPreferencesRepository.setGitEnabled(enabled) }
    fun setGitLfsEnabled(enabled: Boolean) = viewModelScope.launch { userPreferencesRepository.setGitLfsEnabled(enabled) }
    fun setSshEnabled(enabled: Boolean) = viewModelScope.launch { userPreferencesRepository.setSshEnabled(enabled) }

    // --- Abschluss & Verzeichniserstellung ---
    fun startInstallation() {
        viewModelScope.launch { userPreferencesRepository.setInstallationStarted(true) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val rootPath = createClbmProjectsDirectory()
            userPreferencesRepository.setOnboardingCompleted(true)
            userPreferencesRepository.setInstallationCompleted(true)
            CLBMLogger.i(TAG, "Onboarding erfolgreich abgeschlossen. Root: $rootPath")
        }
    }

    /**
     * Erstellt das Projektverzeichnis auf dem externen Speicher oder nutzt den internen Fallback.
     * Scannt den Ordner zudem nach bereits vorhandenen Projekten.
     */
    private suspend fun createClbmProjectsDirectory(): String {
        val existingDir = userPreferencesRepository.getRootDirectoryOnce()
        if (existingDir.isNotBlank() && File(existingDir).exists()) {
            CLBMLogger.d(TAG, "Projektverzeichnis bereits vorhanden: $existingDir")
            scanAndStoreExistingProjects(File(existingDir))
            return existingDir
        }

        val clbmDir: String = if (hasExternalStoragePermission()) {
            try {
                val externalRoot = Environment.getExternalStorageDirectory()
                val targetDir = File(externalRoot, "CLBMProjects")
                
                if (!targetDir.exists()) {
                    if (!targetDir.mkdirs()) throw Exception("Konnte Verzeichnis auf SD-Karte nicht erstellen")
                }

                if (targetDir.canWrite()) {
                    CLBMLogger.d(TAG, "CLBMProjects auf externem Speicher erstellt.")
                    scanAndStoreExistingProjects(targetDir)
                    targetDir.absolutePath
                } else {
                    CLBMLogger.w(TAG, "Keine Schreibrechte auf SD-Karte, nutze internen Speicher.")
                    createInternalDirectory()
                }
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Fehler beim Zugriff auf externen Speicher: ${e.message}", e)
                createInternalDirectory()
            }
        } else {
            CLBMLogger.d(TAG, "Keine MANAGE_EXTERNAL_STORAGE Rechte, nutze App-Datenverzeichnis.")
            createInternalDirectory()
        }

        userPreferencesRepository.setRootDirectory(clbmDir)
        return clbmDir
    }

    /**
     * Sucht im angegebenen Verzeichnis nach validen Gradle-Projekten.
     */
    private fun scanAndStoreExistingProjects(directory: File) {
        val folders = directory.listFiles { file -> file.isDirectory } ?: return
        var count = 0
        for (folder in folders) {
            if (ProjectUtils.isProjectDirectory(folder)) {
                CLBMLogger.i(TAG, "Bestehendes Projekt erkannt: ${folder.name}")
                // Hier könnten Projekte in eine Liste für den ProjectManager geladen werden
                count++
            }
        }
        CLBMLogger.d(TAG, "$count bestehende Projekte im Verzeichnis gefunden.")
    }

    private fun hasExternalStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else true
    }

    private fun createInternalDirectory(): String {
        val filesDir = getApplication<Application>().filesDir
        val clbmProjectsDir = File(filesDir, "CLBMProjects")
        if (!clbmProjectsDir.exists()) clbmProjectsDir.mkdirs()
        return clbmProjectsDir.absolutePath
    }
}