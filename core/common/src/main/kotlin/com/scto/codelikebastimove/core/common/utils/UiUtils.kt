package com.scto.codelikebastimove.core.common.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Composable-Helper, um festzustellen, ob die UI gerade im Dark Mode gerendert werden soll.
 * Wrapper um [isSystemInDarkTheme].
 *
 * Nutzung in Compose:
 * if (isAppInDarkTheme()) { ... }
 */
@Composable
@ReadOnlyComposable
fun isAppInDarkTheme(): Boolean {
    return isSystemInDarkTheme()
}

/**
 * Prüft anhand des [Context], ob die App gerade im Dark Mode läuft.
 * Nützlich für Logik außerhalb von @Composable-Funktionen (z.B. Analytics, Services).
 *
 * @return True, wenn der Nachtmodus in der aktuellen Konfiguration aktiv ist.
 */
fun Context.isDarkMode(): Boolean {
    // Wir prüfen direkt die Konfiguration des Contexts (Resources).
    // Das funktioniert auch, wenn AppCompatDelegate.setDefaultNightMode() genutzt wurde,
    // da dies die Konfiguration der Activity aktualisiert.
    val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return uiMode == Configuration.UI_MODE_NIGHT_YES
}