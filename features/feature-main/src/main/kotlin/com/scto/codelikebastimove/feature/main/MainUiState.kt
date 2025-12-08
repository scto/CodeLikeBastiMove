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
    val projectPath: String = "",
    val hasUnsavedChanges: Boolean = false,
    val isLoading: Boolean = false,
    val bottomSheetContent: BottomSheetContentType = BottomSheetContentType.TERMINAL,
    val isProjectOpen: Boolean = false,
    val rootDirectory: String = "",
    val projects: List<StoredProject> = emptyList(),
    val directoryContents: List<DirectoryItem> = emptyList(),
    val errorMessage: String? = null,
    val cloneProgress: String = ""
)

enum class BottomSheetContentType(val title: String) {
    TERMINAL("Terminal"),
    BUILD_OUTPUT("Build"),
    LOGCAT("Logcat"),
    PROBLEMS("Problems"),
    TODO_LIST("TODO")
}

typealias BottomSheetContent = BottomSheetContentType
