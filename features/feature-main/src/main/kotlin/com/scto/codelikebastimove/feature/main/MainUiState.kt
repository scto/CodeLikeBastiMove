package com.scto.codelikebastimove.feature.main

import com.scto.codelikebastimove.core.datastore.DirectoryItem
import com.scto.codelikebastimove.core.datastore.StoredProject
import com.scto.codelikebastimove.feature.main.navigation.MainDestination

data class MainUiState(
    val currentDestination: MainDestination = MainDestination.Home,
    val currentContent: MainContentType = MainContentType.EDITOR,
    val isNavigationSheetOpen: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val projectName: String = "Untitled Project",
    // Dieser Pfad ist die "Source of Truth" für die UI
    val projectPath: String = "",
    val isProjectOpen: Boolean = false,
    val rootDirectory: String = "",
    val projects: List<StoredProject> = emptyList(),
    val directoryContents: List<DirectoryItem> = emptyList(),
    val errorMessage: String? = null,
    val cloneProgress: String = "",
    val bottomSheetContent: BottomSheetContentType = BottomSheetContentType.TERMINAL,
    // NEU: Speichert den Ansichtsmodus des Projekt-Explorers (z.B. Android vs Projekt)
    val projectViewType: ProjectViewMode = ProjectViewMode.ANDROID
)

enum class BottomSheetContentType(val title: String) {
    TERMINAL("Terminal"),
    BUILD_OUTPUT("Build"),
    LOGCAT("Logcat"),
    PROBLEMS("Problems"),
    TODO_LIST("TODO")
}

// NEU: Enum für die Projekt-Ansicht in den State verschoben, damit es überall verfügbar ist
enum class ProjectViewMode(val displayName: String) {
    ANDROID("Android"),
    PROJECT("Projekt"),
    PACKAGES("Pakete")
}

typealias BottomSheetContent = BottomSheetContentType
