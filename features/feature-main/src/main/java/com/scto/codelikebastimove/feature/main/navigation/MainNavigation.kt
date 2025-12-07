package com.scto.codelikebastimove.feature.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainDestination(
    val route: String,
    val title: String
) {
    data object Home : MainDestination("home", "Home")
    data object IDE : MainDestination("ide", "IDE")
    data object Settings : MainDestination("settings", "IDE-Einstellungen")
    data object AssetStudio : MainDestination("asset_studio", "Asset Studio")
    data object AIAgent : MainDestination("ai_agent", "AI Agent")
    data object BuildVariants : MainDestination("build_variants", "Build Varianten")
    data object SubModuleMaker : MainDestination("sub_module_maker", "Sub-Module Maker")
    data object Console : MainDestination("console", "Konsole")
    data object Documentation : MainDestination("documentation", "Dokumentation")
}

data class HomeAction(
    val title: String,
    val icon: ImageVector,
    val destination: MainDestination? = null,
    val onClick: (() -> Unit)? = null
)

val homeActions = listOf(
    HomeAction(
        title = "Projekt erstellen",
        icon = Icons.Default.Add,
        onClick = { }
    ),
    HomeAction(
        title = "Vorhandenes Projekt öffnen",
        icon = Icons.Default.Folder,
        destination = MainDestination.IDE
    ),
    HomeAction(
        title = "Repository klonen",
        icon = Icons.Outlined.CloudDownload,
        onClick = { }
    ),
    HomeAction(
        title = "Konsole",
        icon = Icons.Default.Terminal,
        destination = MainDestination.Console
    ),
    HomeAction(
        title = "Einstellungen",
        icon = Icons.Default.Settings,
        destination = MainDestination.Settings
    ),
    HomeAction(
        title = "IDE Configurations",
        icon = Icons.Default.Tune,
        destination = MainDestination.Settings
    ),
    HomeAction(
        title = "Dokumentation",
        icon = Icons.Default.Book,
        destination = MainDestination.Documentation
    )
)
