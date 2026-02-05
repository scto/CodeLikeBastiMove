package com.scto.codelikebastimove.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MergeType
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewQuilt
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainContentType(
  val title: String,
  val icon: ImageVector,
  val selectedIcon: ImageVector,
  val description: String,
) {
  EDITOR(
    title = "Editor",
    icon = Icons.Outlined.Code,
    selectedIcon = Icons.Filled.Code,
    description = "Code editor with syntax highlighting",
  ),
  PROJECT(
    title = "Project",
    icon = Icons.Outlined.FolderOpen,
    selectedIcon = Icons.Filled.FolderOpen,
    description = "Project file explorer",
  ),
  GIT(
    title = "Git",
    icon = Icons.Outlined.MergeType,
    selectedIcon = Icons.Outlined.MergeType,
    description = "Git version control",
  ),
  ASSETS_STUDIO(
    title = "Assets",
    icon = Icons.Outlined.Image,
    selectedIcon = Icons.Filled.Image,
    description = "Asset Studio for icons and images",
  ),
  THEME_BUILDER(
    title = "Theme",
    icon = Icons.Outlined.Palette,
    selectedIcon = Icons.Filled.Palette,
    description = "Material Theme Builder",
  ),
  LAYOUT_DESIGNER(
    title = "Layout",
    icon = Icons.Outlined.ViewQuilt,
    selectedIcon = Icons.Filled.ViewQuilt,
    description = "Layout Designer for XML",
  ),
  SETTINGS(
    title = "Settings",
    icon = Icons.Outlined.Settings,
    selectedIcon = Icons.Filled.Settings,
    description = "Application settings",
  ),
}
