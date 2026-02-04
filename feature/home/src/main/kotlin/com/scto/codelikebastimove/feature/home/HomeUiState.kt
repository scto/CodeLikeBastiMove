package com.scto.codelikebastimove.feature.home

import com.scto.codelikebastimove.core.datastore.DirectoryItem
import com.scto.codelikebastimove.core.datastore.StoredProject
import com.scto.codelikebastimove.feature.home.navigation.HomeDestination

data class HomeUiState(
    val currentDestination: HomeDestination = HomeDestination.Home,
    val projectName: String = "Untitled Project",
    val projectPath: String = "",
    val isProjectOpen: Boolean = false,
    val rootDirectory: String = "",
    val projects: List<StoredProject> = emptyList(),
    val directoryContents: List<DirectoryItem> = emptyList(),
    val errorMessage: String? = null,
    val cloneProgress: String = "",
    val isLoading: Boolean = false,
    val lastFileSystemUpdate: Long = 0L,
)
