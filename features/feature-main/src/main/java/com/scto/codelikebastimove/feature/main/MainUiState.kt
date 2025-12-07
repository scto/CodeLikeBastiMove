package com.scto.codelikebastimove.feature.main

data class MainUiState(
    val currentContent: MainContentType = MainContentType.EDITOR,
    val isNavigationSheetOpen: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val projectName: String = "Untitled Project",
    val hasUnsavedChanges: Boolean = false,
    val isLoading: Boolean = false,
    val bottomSheetContent: BottomSheetContentType = BottomSheetContentType.TERMINAL
)

enum class BottomSheetContentType(val title: String) {
    TERMINAL("Terminal"),
    BUILD_OUTPUT("Build"),
    LOGCAT("Logcat"),
    PROBLEMS("Problems"),
    TODO_LIST("TODO")
}
