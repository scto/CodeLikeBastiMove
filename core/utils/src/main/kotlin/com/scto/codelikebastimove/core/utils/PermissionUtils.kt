package com.scto.codelikebastimove.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment

import androidx.core.content.ContextCompat

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted

import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Hilfsklasse für Berechtigungen. Unterstützt Accompanist-Zustände 
 * und persistente Speicherung im DataStore.
 */
@OptIn(ExperimentalPermissionsApi::class)
object PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        } else true
    }

    /**
     * Speichert den gewählten Berechtigungsstatus im Repository.
     */
    fun setPermissionState(repository: UserPreferencesRepository, type: PermissionType, granted: Boolean) {
        runBlocking {
            when (type) {
                PermissionType.FILE_ACCESS -> repository.setFileAccessPermissionGranted(granted)
                PermissionType.NOTIFICATIONS -> repository.setNotificationPermissionGranted(granted)
                PermissionType.INSTALL_PACKAGES -> repository.setInstallPackagesPermissionGranted(granted)
                PermissionType.USAGE_STATS -> repository.setUsageAnalyticsPermissionGranted(granted)
                PermissionType.BATTERY_OPTIMIZATION -> repository.setBatteryOptimizationDisabled(granted)
            }
        }
    }

    /**
     * Liest den Status einer Berechtigung aus dem DataStore.
     */
    fun getPermissionState(repository: UserPreferencesRepository, type: PermissionType): Boolean {
        return runBlocking {
            val config = repository.onboardingConfig.first()
            when (type) {
                PermissionType.FILE_ACCESS -> config.fileAccessPermissionGranted
                PermissionType.NOTIFICATIONS -> config.notificationPermissionGranted
                PermissionType.INSTALL_PACKAGES -> config.installPackagesPermissionGranted
                PermissionType.USAGE_STATS -> config.usageAnalyticsPermissionGranted
                PermissionType.BATTERY_OPTIMIZATION -> config.batteryOptimizationDisabled
            }
        }
    }

    enum class PermissionType { 
        FILE_ACCESS, NOTIFICATIONS, INSTALL_PACKAGES, USAGE_STATS, BATTERY_OPTIMIZATION 
    }
}