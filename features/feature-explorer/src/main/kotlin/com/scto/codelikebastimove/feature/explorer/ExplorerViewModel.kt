package com.scto.codelikebastimove.feature.explorer

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class ExplorerUiState(
    val currentPath: String = "",
    val files: List<FileItem> = emptyList(),
    val selectedFiles: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val fileFilter: FileFilter = FileFilter.ALL,
    val showHiddenFiles: Boolean = false,
    val navigationHistory: List<String> = emptyList(),
    val historyIndex: Int = -1
)

class ExplorerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = UserPreferencesRepository(application)
    
    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState: StateFlow<ExplorerUiState> = _uiState.asStateFlow()
    
    init {
        initializeExplorer()
    }
    
    private fun initializeExplorer() {
        viewModelScope.launch {
            val rootDir = repository.getRootDirectoryOnce()
            val startPath = if (rootDir.isNotBlank() && File(rootDir).exists()) {
                rootDir
            } else {
                Environment.getExternalStorageDirectory().absolutePath
            }
            navigateTo(startPath)
        }
    }
    
    fun navigateTo(path: String) {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) {
            _uiState.update { it.copy(errorMessage = "Directory does not exist: $path") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val files = loadFiles(dir)
                val newHistory = _uiState.value.navigationHistory.take(_uiState.value.historyIndex + 1) + path
                
                _uiState.update { 
                    it.copy(
                        currentPath = path,
                        files = files,
                        selectedFiles = emptySet(),
                        isLoading = false,
                        navigationHistory = newHistory,
                        historyIndex = newHistory.size - 1,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load directory: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun loadFiles(directory: File): List<FileItem> {
        val state = _uiState.value
        val allFiles = directory.listFiles()?.toList() ?: emptyList()
        
        val filteredFiles = allFiles
            .filter { file ->
                if (!state.showHiddenFiles && file.isHidden) return@filter false
                
                when (state.fileFilter) {
                    FileFilter.ALL -> true
                    FileFilter.DIRECTORIES_ONLY -> file.isDirectory
                    FileFilter.FILES_ONLY -> file.isFile
                    FileFilter.PROJECTS_ONLY -> {
                        file.isDirectory && (
                            File(file, "build.gradle.kts").exists() ||
                            File(file, "build.gradle").exists() ||
                            File(file, "settings.gradle.kts").exists() ||
                            File(file, "settings.gradle").exists()
                        )
                    }
                }
            }
            .map { FileItem(it) }
        
        return when (state.sortOrder) {
            SortOrder.NAME_ASC -> filteredFiles.sortedWith(
                compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() }
            )
            SortOrder.NAME_DESC -> filteredFiles.sortedWith(
                compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.name.lowercase() }
            )
            SortOrder.DATE_ASC -> filteredFiles.sortedWith(
                compareBy<FileItem> { !it.isDirectory }.thenBy { it.lastModified }
            )
            SortOrder.DATE_DESC -> filteredFiles.sortedWith(
                compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.lastModified }
            )
            SortOrder.SIZE_ASC -> filteredFiles.sortedWith(
                compareBy<FileItem> { !it.isDirectory }.thenBy { it.size }
            )
            SortOrder.SIZE_DESC -> filteredFiles.sortedWith(
                compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.size }
            )
        }
    }
    
    fun navigateUp() {
        val currentPath = _uiState.value.currentPath
        val parentPath = File(currentPath).parent
        if (parentPath != null) {
            navigateTo(parentPath)
        }
    }
    
    fun navigateBack(): Boolean {
        val state = _uiState.value
        if (state.historyIndex > 0) {
            val previousPath = state.navigationHistory[state.historyIndex - 1]
            _uiState.update { it.copy(historyIndex = it.historyIndex - 1) }
            navigateToWithoutHistory(previousPath)
            return true
        }
        return false
    }
    
    fun navigateForward(): Boolean {
        val state = _uiState.value
        if (state.historyIndex < state.navigationHistory.size - 1) {
            val nextPath = state.navigationHistory[state.historyIndex + 1]
            _uiState.update { it.copy(historyIndex = it.historyIndex + 1) }
            navigateToWithoutHistory(nextPath)
            return true
        }
        return false
    }
    
    private fun navigateToWithoutHistory(path: String) {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return
        
        viewModelScope.launch {
            val files = loadFiles(dir)
            _uiState.update { 
                it.copy(
                    currentPath = path,
                    files = files,
                    selectedFiles = emptySet(),
                    errorMessage = null
                )
            }
        }
    }
    
    fun toggleFileSelection(path: String) {
        _uiState.update { state ->
            val newSelection = if (path in state.selectedFiles) {
                state.selectedFiles - path
            } else {
                state.selectedFiles + path
            }
            state.copy(selectedFiles = newSelection)
        }
    }
    
    fun selectAll() {
        _uiState.update { state ->
            state.copy(selectedFiles = state.files.map { it.path }.toSet())
        }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedFiles = emptySet()) }
    }
    
    fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        refreshCurrentDirectory()
    }
    
    fun setFileFilter(filter: FileFilter) {
        _uiState.update { it.copy(fileFilter = filter) }
        refreshCurrentDirectory()
    }
    
    fun toggleHiddenFiles() {
        _uiState.update { it.copy(showHiddenFiles = !it.showHiddenFiles) }
        refreshCurrentDirectory()
    }
    
    fun refreshCurrentDirectory() {
        navigateToWithoutHistory(_uiState.value.currentPath)
    }
    
    fun navigateToProjectsRoot() {
        viewModelScope.launch {
            val rootDir = repository.getRootDirectoryOnce()
            if (rootDir.isNotBlank()) {
                navigateTo(rootDir)
            }
        }
    }
    
    fun navigateToExternalStorage() {
        navigateTo(Environment.getExternalStorageDirectory().absolutePath)
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
