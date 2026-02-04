package com.scto.codelikebastimove.feature.treeview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector

enum class TreeViewMode(
  val displayName: String,
  val icon: ImageVector,
  val description: String,
) {
  FILE_VIEW(
    displayName = "Files",
    icon = Icons.Default.Folder,
    description = "Standard file system view",
  ),
  PACKAGE_VIEW(
    displayName = "Packages",
    icon = Icons.Default.Inventory2,
    description = "Group files by package structure",
  ),
  MODULE_VIEW(
    displayName = "Modules",
    icon = Icons.Default.Extension,
    description = "View by Gradle modules",
  ),
  PROJECT_VIEW(
    displayName = "Project",
    icon = Icons.Default.AccountTree,
    description = "Android Studio-style project view",
  ),
}

data class TreeViewState(
  val currentMode: TreeViewMode = TreeViewMode.FILE_VIEW,
  val showHiddenFiles: Boolean = false,
  val selectedPath: String? = null,
  val expandedPaths: Set<String> = emptySet(),
  val searchQuery: String = "",
  val isSearchActive: Boolean = false,
)
