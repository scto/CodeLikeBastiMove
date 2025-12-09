package com.scto.codelikebastimove

import android.app.Application
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.logger.BuildConfig
import com.scto.codelikebastimove.core.logger.CLBMLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CLBMApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        CLBMLogger.initialize(BuildConfig.LOGGING_DEFAULT_ENABLED)
        
        val userPreferencesRepository = UserPreferencesRepository(this)
        applicationScope.launch {
            userPreferencesRepository.initializeLoggingIfNeeded(BuildConfig.LOGGING_DEFAULT_ENABLED)
            
            userPreferencesRepository.loggingEnabled.collect { enabled ->
                CLBMLogger.setEnabled(enabled)
            }
        }
    }
}
