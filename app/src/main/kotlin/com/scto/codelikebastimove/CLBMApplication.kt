package com.scto.codelikebastimove

import android.app.Application
import android.content.res.Configuration
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.logger.BuildConfig
import com.scto.codelikebastimove.core.logger.CLBMLogger
import com.scto.codelikebastimove.core.utils.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Haupt-Application-Klasse. Dient als zentraler Hub für globale Instanzen.
 */
class CLBMApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Initialisierte Manager-Instanzen
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var wakelockManager: WakelockManager
    lateinit var fileUtils: FileUtils

    companion object {
        private lateinit var instance: CLBMApplication
        fun getInstance(): CLBMApplication = instance

        // Globale Zugriffspunkte (Shortcuts)
        val logger get() = CLBMLogger
        val wakelock get() = getInstance().wakelockManager
        val files get() = getInstance().fileUtils
        val permissions get() = PermissionUtils
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 1. Logging sofort bereitstellen
        CLBMLogger.initialize(this, BuildConfig.LOGGING_DEFAULT_ENABLED)
        CLBMLogger.i("App", "CodeLikeBastiMove wird initialisiert...")

        // 2. Singleton-Komponenten instanziieren
        userPreferencesRepository = UserPreferencesRepository(this)
        wakelockManager = WakelockManager(this)
        fileUtils = FileUtils

        // 3. Auslagerung der Initialisierungslogik
        CLBMApplicationInitializer.initialize(this, applicationScope)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applicationScope.launch {
            CLBMApplicationInitializer.syncEditorThemeForCurrentMode(this@CLBMApplication)
        }
    }
}