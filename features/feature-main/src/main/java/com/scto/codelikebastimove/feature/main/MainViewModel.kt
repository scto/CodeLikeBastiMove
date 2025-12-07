package com.scto.codelikebastimove.feature.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    fun onContentTypeChanged(contentType: MainContentType) {
        _uiState.update { it.copy(currentContent = contentType) }
    }
    
    fun onNavigationSheetToggle() {
        _uiState.update { it.copy(isNavigationSheetOpen = !it.isNavigationSheetOpen) }
    }
    
    fun onNavigationSheetDismiss() {
        _uiState.update { it.copy(isNavigationSheetOpen = false) }
    }
    
    fun onBottomSheetToggle() {
        _uiState.update { it.copy(isBottomSheetExpanded = !it.isBottomSheetExpanded) }
    }
    
    fun onBottomSheetContentChanged(content: BottomSheetContentType) {
        _uiState.update { 
            it.copy(
                bottomSheetContent = content,
                isBottomSheetExpanded = true
            )
        }
    }
    
    fun onProjectNameChanged(name: String) {
        _uiState.update { it.copy(projectName = name) }
    }
    
    fun onUnsavedChangesChanged(hasChanges: Boolean) {
        _uiState.update { it.copy(hasUnsavedChanges = hasChanges) }
    }
    
    fun onLoadingChanged(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
