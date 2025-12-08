package com.scto.codelikebastimove.feature.slidingpanel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.ViewQuilt
import androidx.compose.ui.graphics.vector.ImageVector

enum class SlidingPanelNavigationType(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val opensInPanel: Boolean
) {
    FILE_TREE(
        title = "Dateien",
        icon = Icons.Outlined.FolderOpen,
        selectedIcon = Icons.Filled.FolderOpen,
        opensInPanel = true
    ),
    BUILD_VARIANTS(
        title = "Build",
        icon = Icons.Outlined.Build,
        selectedIcon = Icons.Filled.Build,
        opensInPanel = true
    ),
    ASSET_STUDIO(
        title = "Assets",
        icon = Icons.Outlined.Image,
        selectedIcon = Icons.Filled.Image,
        opensInPanel = true
    ),
    THEME_BUILDER(
        title = "Theme",
        icon = Icons.Outlined.Palette,
        selectedIcon = Icons.Filled.Palette,
        opensInPanel = true
    ),
    LAYOUT_DESIGNER(
        title = "Layout",
        icon = Icons.Outlined.ViewQuilt,
        selectedIcon = Icons.Filled.ViewQuilt,
        opensInPanel = false
    ),
    TERMINAL(
        title = "Terminal",
        icon = Icons.Outlined.Terminal,
        selectedIcon = Icons.Filled.Terminal,
        opensInPanel = false
    ),
    SETTINGS(
        title = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        opensInPanel = false
    );
    
    companion object {
        val panelItems = entries.filter { it.opensInPanel }
        val externalItems = entries.filter { !it.opensInPanel }
    }
}
