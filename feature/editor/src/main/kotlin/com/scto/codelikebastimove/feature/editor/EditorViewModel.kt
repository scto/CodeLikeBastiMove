package com.scto.codelikebastimove.feature.editor

import androidx.lifecycle.ViewModel
import com.scto.codelikebastimove.feature.editor.highlighting.EditorLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditorViewModel : ViewModel() {

    private val _editorContent = MutableStateFlow("")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _currentLanguage = MutableStateFlow<EditorLanguage>(EditorLanguage.PlainText)
    val currentLanguage: StateFlow<EditorLanguage> = _currentLanguage.asStateFlow()

    private val _currentColorScheme = MutableStateFlow<EditorColorScheme?>(null)
    val currentColorScheme: StateFlow<EditorColorScheme?> = _currentColorScheme.asStateFlow()

    private var editor: CodeEditor? = null

    fun setEditor(codeEditor: CodeEditor) {
        editor = codeEditor
        // Set initial content if available
        if (_editorContent.value.isNotEmpty()) {
            editor?.setText(_editorContent.value)
        }
        // Apply initial language and theme if available
        _currentLanguage.value.setup(editor)
        _currentColorScheme.value?.let { editor?.colorScheme = it }
    }

    fun updateContent(newContent: String) {
        if (_editorContent.value != newContent) {
            _editorContent.value = newContent
            editor?.setText(newContent)
        }
    }

    fun undo() {
        editor?.undo()
        _editorContent.value = editor?.text?.toString() ?: ""
    }

    fun redo() {
        editor?.redo()
        _editorContent.value = editor?.text?.toString() ?: ""
    }

    fun search(query: String) {
        // This is a basic stub. Rosemoe editor has its own search implementation.
        // For a full implementation, you'd likely interact with editor.findReplacePanel.
        // For now, we'll just log or show a simple message.
        println("Searching for: $query")
    }

    fun replaceAll(replacement: String) {
        // This is a basic stub. Rosemoe editor has its own replace implementation.
        // For now, we'll just log.
        println("Replacing all with: $replacement")
    }

    fun setTheme(themeName: String) {
        val newScheme = EditorUtils.ThemeRegistry.getTheme(themeName)
        if (newScheme != null) {
            _currentColorScheme.value = newScheme
            editor?.colorScheme = newScheme
        } else {
            // Handle theme not found
            println("Theme '$themeName' not found.")
        }
    }

    fun setLanguage(language: EditorLanguage) {
        _currentLanguage.value = language
        language.setup(editor)
    }

    override fun onCleared() {
        super.onCleared()
        editor = null // Clean up reference
    }
}
