package com.scto.codelikebastimove.ui.gallery

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GalleryViewModel : ViewModel() {
    private val _text = MutableStateFlow("This is Gallery Screen")
    val text: StateFlow<String> = _text.asStateFlow()
}
