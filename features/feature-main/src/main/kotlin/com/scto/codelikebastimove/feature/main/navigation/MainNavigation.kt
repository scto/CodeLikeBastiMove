package com.scto.codelikebastimove.feature.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
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
    data object CreateProject : MainDestination("create_project", "Projekt erstellen")
    data object OpenProject : MainDestination("open_project", "Projekt öffnen")
    data object CloneRepository : MainDestination("clone_repository", "Repository klonen")
    data object IDE : MainDestination("ide", "IDE")
    data object Settings : MainDestination("settings", "IDE-Einstellungen")
    data object AssetStudio : MainDestination("asset_studio", "Asset Studio")
    data object AIAgent : MainDestination("ai_agent", "AI Agent")
    data object BuildVariants : MainDestination("build_variants", "Build Varianten")
    data object SubModuleMaker : MainDestination("sub_module_maker", "Sub-Module Maker")
    data object Console : MainDestination("console", "Konsole")
    data object Documentation : MainDestination("documentation", "Dokumentation")
    data object LayoutDesigner : MainDestination("layout_designer", "Layout Designer")
    data object VectorAssetStudio : MainDestination("vector_asset_studio", "Vector Asset Studio")
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
        destination = MainDestination.CreateProject
    ),
    HomeAction(
        title = "Vorhandenes Projekt öffnen",
        icon = Icons.Default.Folder,
        destination = MainDestination.OpenProject
    ),
    HomeAction(
        title = "Repository klonen",
        icon = Icons.Outlined.CloudDownload,
        destination = MainDestination.CloneRepository
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
    ),
    HomeAction(
        title = "Layout Designer",
        icon = Icons.Default.DesignServices,
        destination = MainDestination.LayoutDesigner
    ),
    HomeAction(
        title = "Asset Studio",
        icon = Icons.Default.Brush,
        destination = MainDestination.AssetStudio
    ),
    HomeAction(
        title = "Vector Asset Studio",
        icon = Icons.Default.Image,
        destination = MainDestination.VectorAssetStudio
    )
)
