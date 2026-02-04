package com.scto.codelikebastimove.core.updater

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpdateRepository(
    private val context: Context
) {
    private val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    
    private val updateChecker = UpdateChecker(
        currentVersionCode = packageInfo.versionCode,
        currentVersionName = packageInfo.versionName ?: "0.0.1"
    )

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: Flow<UpdateState> = _updateState.asStateFlow()

    private val _lastCheckTime = MutableStateFlow(0L)
    val lastCheckTime: Flow<Long> = _lastCheckTime.asStateFlow()

    suspend fun checkForUpdates(): UpdateInfo? {
        _updateState.value = UpdateState.Checking
        
        return updateChecker.checkForUpdates().fold(
            onSuccess = { updateInfo ->
                _updateState.value = if (updateInfo.isUpdateAvailable) {
                    UpdateState.UpdateAvailable(updateInfo)
                } else {
                    UpdateState.UpToDate(updateInfo)
                }
                _lastCheckTime.value = System.currentTimeMillis()
                updateInfo
            },
            onFailure = { error ->
                _updateState.value = UpdateState.Error(error.message ?: "Unknown error")
                null
            }
        )
    }

    suspend fun getAllReleases(): List<GitHubRelease> {
        return updateChecker.getAllReleases().getOrElse { emptyList() }
    }

    fun getVersionName(): String = packageInfo.versionName ?: "0.0.1"
    fun getVersionCode(): Int = packageInfo.versionCode

    fun getGitHubProjectUrl(): String = UpdateChecker.GITHUB_PROJECT_URL
    fun getGitHubReleasesUrl(): String = UpdateChecker.GITHUB_RELEASES_URL
}

sealed class UpdateState {
    data object Idle : UpdateState()
    data object Checking : UpdateState()
    data class UpToDate(val info: UpdateInfo) : UpdateState()
    data class UpdateAvailable(val info: UpdateInfo) : UpdateState()
    data class Error(val message: String) : UpdateState()
}
