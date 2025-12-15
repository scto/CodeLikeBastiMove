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
    // Speichert den Ansichtsmodus des Projekt-Explorers
    val projectViewType: ProjectViewMode = ProjectViewMode.ANDROID,
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    
    // Editor State
    val openFiles: List<EditorFile> = emptyList(),
    val activeFileIndex: Int = -1,
    
    // Trigger für Dateisystem-Aktualisierungen (Timestamp)
    val lastFileSystemUpdate: Long = 0L
)

data class EditorFile(
    val name: String,
    val path: String,
    val content: String,
    val isModified: Boolean = false
)

enum class BottomSheetContentType(val title: String) {
    TERMINAL("Terminal"),
    BUILD_OUTPUT("Build"),
    LOGCAT("Logcat"),
    PROBLEMS("Problems"),
    TODO_LIST("TODO")
}

enum class ProjectViewMode(val displayName: String) {
    ANDROID("Android"),
    PROJECT("Projekt"),
    PACKAGES("Pakete")
}

typealias BottomSheetContent = BottomSheetContentType