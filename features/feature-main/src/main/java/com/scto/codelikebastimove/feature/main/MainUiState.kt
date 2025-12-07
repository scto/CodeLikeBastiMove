package com.scto.codelikebastimove.feature.main

import com.scto.codelikebastimove.feature.main.navigation.MainDestination

data class MainUiState(
    val currentDestination: MainDestination = MainDestination.Home,
    val currentContent: MainContentType = MainContentType.EDITOR,
    val isNavigationSheetOpen: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val projectName: String = "Untitled Project",
    val hasUnsavedChanges: Boolean = false,
    val isLoading: Boolean = false,
    val bottomSheetContent: BottomSheetContentType = BottomSheetContentType.TERMINAL,
    val isProjectOpen: Boolean = false
)

enum class BottomSheetContentType(val title: String) {
    TERMINAL("Terminal"),
    BUILD_OUTPUT("Build"),
    LOGCAT("Logcat"),
    PROBLEMS("Problems"),
    TODO_LIST("TODO")
}

typealias BottomSheetContent = BottomSheetContentType
