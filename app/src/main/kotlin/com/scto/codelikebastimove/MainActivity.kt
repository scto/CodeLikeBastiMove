package com.scto.codelikebastimove

import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.scto.codelikebastimove.core.datastore.ThemeMode as DataStoreThemeMode
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.ui.theme.AppTheme
import com.scto.codelikebastimove.core.ui.theme.ThemeMode
import com.scto.codelikebastimove.feature.main.MainScreen
import com.scto.codelikebastimove.feature.onboarding.OnboardingScreen
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import androidx.compose.foundation.isSystemInDarkTheme

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
      val userPreferencesRepository = remember { UserPreferencesRepository(context) }
      val userPreferences by
        userPreferencesRepository.userPreferences.collectAsState(initial = UserPreferences())

      val lifecycleOwner = LocalLifecycleOwner.current
      var hasRealPermission by remember { mutableStateOf(checkFileAccessPermission()) }

      LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
          val currentPermission = checkFileAccessPermission()
          hasRealPermission = currentPermission
          userPreferencesRepository.setFileAccessPermissionGranted(currentPermission)
        }
      }

      val themeMode =
        when (userPreferences.themeMode) {
          DataStoreThemeMode.LIGHT -> ThemeMode.LIGHT
          DataStoreThemeMode.DARK -> ThemeMode.DARK
          DataStoreThemeMode.FOLLOW_SYSTEM -> ThemeMode.FOLLOW_SYSTEM
        }

      val isSystemDark = isSystemInDarkTheme()
      LaunchedEffect(isSystemDark, userPreferences.themeMode) {
        if (userPreferences.themeMode == DataStoreThemeMode.FOLLOW_SYSTEM) {
          val editorSettings = userPreferencesRepository.getEditorSettingsOnce()
          val currentTheme = EditorThemes.getTheme(editorSettings.editorTheme)
          if (currentTheme.isDark != isSystemDark) {
            val newTheme = EditorThemes.getThemeForMode(editorSettings.editorTheme, isSystemDark)
            if (newTheme.name != editorSettings.editorTheme) {
              userPreferencesRepository.setEditorTheme(newTheme.name)
            }
          }
        }
      }

      AppTheme(themeMode = themeMode, dynamicColor = userPreferences.dynamicColorsEnabled) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val isOnboardingComplete = userPreferences.onboardingConfig.onboardingCompleted

          when {
            !isOnboardingComplete || !hasRealPermission -> {
              OnboardingScreen(onOnboardingComplete = {})
            }
            else -> {
              MainScreen()
            }
          }
        }
      }
    }
  }
}
