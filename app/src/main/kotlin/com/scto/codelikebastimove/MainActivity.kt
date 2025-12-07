package com.scto.codelikebastimove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.ui.theme.AppIcons
import com.scto.codelikebastimove.core.ui.theme.CodeLikeBastiMoveTheme
import com.scto.codelikebastimove.core.ui.theme.ThemeMode
import com.scto.codelikebastimove.feature.onboarding.OnboardingScreen
import com.scto.codelikebastimove.navigation.AppDrawer
import com.scto.codelikebastimove.navigation.AppNavigation
import com.scto.codelikebastimove.navigation.screens
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = screens.find { it.route == currentRoute } ?: screens.first()

    AppDrawer(
        drawerState = drawerState,
        navController = navController,
        scope = scope
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = AppIcons.Menu,
                                contentDescription = stringResource(com.scto.codelikebastimove.core.resources.R.string.navigation_drawer_open)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
