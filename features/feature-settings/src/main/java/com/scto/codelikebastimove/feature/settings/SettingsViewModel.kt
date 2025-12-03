package com.scto.codelikebastimove.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _text = MutableStateFlow("This is Settings Screen")
    val text: StateFlow<String> = _text.asStateFlow()
}
