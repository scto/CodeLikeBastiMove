package com.scto.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.analytics.FirebaseAnalytics
import com.scto.analytics.ImplAnalytics
import com.scto.app.di.mainModule
import com.scto.app.ui.MainApp
import com.scto.di.koinScope
import com.scto.main.view.state.MainUIState
import com.scto.main.view.vm.MainViewModel
import com.scto.navigation.Screen
import com.scto.sync.service.SyncDataService
import com.scto.ui.compositionlocal.LocalAnalytics
import com.scto.ui.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules

class MainActivity : ComponentActivity(), KoinComponent {

    private val viewModel: MainViewModel by koinScope<MainActivity>().inject()
    private val firebaseAnalytics by getKoin().inject<FirebaseAnalytics>()
    private val analyticsImpl by lazy { ImplAnalytics(firebaseAnalytics) }

    init {
        loadKoinModules(mainModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        //False - allows to drawing the content "edge-to-edge"
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Keep the splash screen visible until we have the settings loaded
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value !is MainUIState.Success
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Default settings
            var darkMode by remember { mutableStateOf(true) }
            var dynamicColors by remember { mutableStateOf(true) }
            var isFirstRun by remember { mutableStateOf(true) }

            if (uiState is MainUIState.Success) {
                (uiState as MainUIState.Success).settings.let {
                    darkMode = it.darkMode
                    dynamicColors = it.dynamicColors
                    isFirstRun = it.isFirstRun
                    
                    // Update analytics state based on settings
                    LaunchedEffect(it.isAnalyticsEnabled) {
                        analyticsImpl.setCollectionEnabled(it.isAnalyticsEnabled)
                    }
                }
            }

            AppTheme(
                darkTheme = darkMode,
                dynamicColor = dynamicColors
            ) {
                KoinContext {
                    CompositionLocalProvider(LocalAnalytics provides analyticsImpl) {
                        val startDestination = if (isFirstRun) Screen.OnBoarding else Screen.Home
                        MainApp(startDestination = startDestination)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SyncDataService::class.java)
            .also(this::startForegroundService)
    }

    override fun onStop() {
        super.onStop()
        Intent(this, SyncDataService::class.java)
            .also(this::stopService)
    }
}