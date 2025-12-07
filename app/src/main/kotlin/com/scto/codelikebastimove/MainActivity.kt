package com.scto.codelikebastimove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.ui.theme.CodeLikeBastiMoveTheme
import com.scto.codelikebastimove.core.ui.theme.ThemeMode
import com.scto.codelikebastimove.feature.main.MainScreen
import com.scto.codelikebastimove.feature.onboarding.OnboardingScreen
import com.scto.codelikebastimove.core.datastore.ThemeMode as DataStoreThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val userPreferencesRepository = remember { UserPreferencesRepository(context) }
            val userPreferences by userPreferencesRepository.userPreferences.collectAsState(
                initial = UserPreferences()
            )
            
            val themeMode = when (userPreferences.themeMode) {
                DataStoreThemeMode.LIGHT -> ThemeMode.LIGHT
                DataStoreThemeMode.DARK -> ThemeMode.DARK
                DataStoreThemeMode.FOLLOW_SYSTEM -> ThemeMode.FOLLOW_SYSTEM
            }
            
            CodeLikeBastiMoveTheme(
                themeMode = themeMode,
                dynamicColor = userPreferences.dynamicColorsEnabled
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!userPreferences.onboardingConfig.onboardingCompleted) {
                        OnboardingScreen(
                            onOnboardingComplete = { }
                        )
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }
}
