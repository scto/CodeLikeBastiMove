package com.scto.codelikebastimove

import android.os.Build
import android.os.Bundle
import android.os.Environment

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

import com.google.accompanist.systemuicontroller.rememberSystemUiController

import com.scto.codelikebastimove.R
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.ui.theme.AppTheme
import com.scto.codelikebastimove.core.ui.theme.ThemeMode
import com.scto.codelikebastimove.core.auth.AuthRepository
import com.scto.codelikebastimove.core.auth.AuthState
import com.scto.codelikebastimove.feature.auth.navigation.AuthNavHost
import com.scto.codelikebastimove.feature.main.MainScreen
import com.scto.codelikebastimove.feature.onboarding.OnboardingScreen
import com.scto.codelikebastimove.core.datastore.ThemeMode as DataStoreThemeMode

import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private fun checkFileAccessPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val userPreferencesRepository = remember {
                UserPreferencesRepository(context)
            }
            val userPreferences by userPreferencesRepository.userPreferences.collectAsState(
                initial = UserPreferences()
            )

            val lifecycleOwner = LocalLifecycleOwner.current
            var hasRealPermission by remember {
                mutableStateOf(checkFileAccessPermission())
            }

            LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    val currentPermission = checkFileAccessPermission()
                    hasRealPermission = currentPermission
                    userPreferencesRepository.setFileAccessPermissionGranted(currentPermission)
                }
            }

            val themeMode = when (userPreferences.themeMode) {
                DataStoreThemeMode.LIGHT -> ThemeMode.LIGHT
                DataStoreThemeMode.DARK -> ThemeMode.DARK
                DataStoreThemeMode.FOLLOW_SYSTEM -> ThemeMode.FOLLOW_SYSTEM
            }

            AppTheme(
                themeMode = themeMode,
                dynamicColor = userPreferences.dynamicColorsEnabled
            ) {
                // Konfiguriere die System Bars (Statusbar etc.) passend zum Theme
                //ConfigureSystemBars()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isOnboardingComplete = userPreferences.onboardingConfig.onboardingCompleted

                    val authRepository = remember {
                        AuthRepository.getInstance()
                    }
                    val authState by authRepository.authStateFlow.collectAsState(initial = AuthState.Loading)

                    val isAuthenticated = when (authState) {
                        is AuthState.Authenticated -> true
                        is AuthState.NotAuthenticated -> false
                        is AuthState.Loading -> authRepository.isLoggedIn
                        is AuthState.Error -> false
                    }

                    when {
                        !isOnboardingComplete || !hasRealPermission -> {
                            OnboardingScreen(
                                onOnboardingComplete = {}
                            )
                        }
                        !isAuthenticated -> {
                            AuthNavHost(
                                onAuthSuccess = {}
                            )
                        } else -> {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}

/*
@Composable
fun ConfigureSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
}
*/