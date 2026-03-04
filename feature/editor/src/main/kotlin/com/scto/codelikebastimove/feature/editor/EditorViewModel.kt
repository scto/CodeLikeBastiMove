package com.scto.codelikebastimove.feature.editor

import androidx.lifecycle.ViewModel
import com.scto.codelikebastimove.feature.editor.highlighting.EditorLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import io.github.rosemoe.sora.widget.EditorTextAction
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorAutoPair
import io.github.rosemoe.sora.widget.SymbolInputView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditorViewModel : ViewModel() {

    private val _editorContent = MutableStateFlow("")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _currentLanguage = MutableStateFlow<EditorLanguageType>(EditorLanguageType.PLAIN_TEXT)
    val currentLanguage: StateFlow<EditorLanguageType> = _currentLanguage.asStateFlow()

    private val _currentColorScheme = MutableStateFlow<EditorColorScheme?>(null)
    val currentColorScheme: StateFlow<EditorColorScheme?> = _currentColorScheme.asStateFlow()

    private val _hasSelection = MutableStateFlow(false)
    val hasSelection: StateFlow<Boolean> = _hasSelection.asStateFlow()

    private val _cursorLine = MutableStateFlow(0)
    val cursorLine: StateFlow<Int> = _cursorLine.asStateFlow()

    private val _cursorColumn = MutableStateFlow(0)
    val cursorColumn: StateFlow<Int> = _cursorColumn.asStateFlow()

    private val _isWordWrapEnabled = MutableStateFlow(false)
    val isWordWrapEnabled: StateFlow<Boolean> = _isWordWrapEnabled.asStateFlow()

    private var editor: CodeEditor? = null

    fun setEditor(codeEditor: CodeEditor) {
        editor = codeEditor
        // Set initial content if available
        if (_editorContent.value.isNotEmpty()) {
            editor?.setText(_editorContent.value)
        }
        // Apply initial language, theme, and word wrap if available
        _currentLanguage.value.setup(editor)
        _currentColorScheme.value?.let { editor?.colorScheme = it }
        editor?.setWordWrap(_isWordWrapEnabled.value)
    }

    fun updateContent(newContent: String) {
        if (_editorContent.value != newContent) {
            _editorContent.value = newContent
            // Do not call setText here, as it will cause an infinite loop if
            // this is called from the editor's setTextChangeListener.
            // The AndroidView update block will handle setting the text.
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

    fun cut() {
        editor?.cut()
        _editorContent.value = editor?.text?.toString() ?: ""
    }

    fun copy() {
        editor?.copy()
    }

    fun paste() {
        editor?.paste()
        _editorContent.value = editor?.text?.toString() ?: ""
    }

    fun selectAll() {
        editor?.selectAll()
        _hasSelection.value = editor?.has          Selection() ?: false
    }

    fun goToLine(line: Int) {
        editor?.setCursor(line.coerceAtLeast(0), 0)
        editor?.moveSelectionToScreenCenter()
    }

    fun indent() {
        editor?.run {
            val start = region.start
            val end = region.end
            performEditorAction(EditorTextAction.ACTION_INSERT_CHAR, "\t")
            if (start != end) {
                // If there was a selection, apply indent to all selected lines
                for (i in start.line..end.line) {
                    insertText(i, 0, "\t", false)
                }
            }
        }
        _editorContent.value = editor?.text?.toString() ?: ""
    }

    fun outdent() {
        editor?.run {
            val start = region.start
            val end = region.end
            if (start != end) {
                // If there was a selection, apply outdent to all selected lines
                for (i in start.line..end.line) {
                    val lineText = getText().get       LineString(i)
                    if (lineText.startsWith("\t")) {
                        deleteText(i, 0, i, 1)
                    } else if (lineText.startsWith("    ")) { // Assuming 4 spaces for tab
                        deleteText(i, 0, i, 4)
                    }
                }
            } else {
                val lineText = getText().getLineString(start.line)
                if (lineText.startsWith("\t")) {
                    deleteText(start.line, 0, start.line, 1)
                } else if (lineText.startsWith("    ")) {
                    deleteText(start.line, 0, start.line, 4)
                }
            }
        }
        _editorContent.value = editor?.text?.toString() ?: ""
    }

    fun toggleWordWrap() {
        _isWordWrapEnabled.value = !_isWordWrapEnabled.value
        editor?.setWordWrap(_isWordWrapEnabled.value)
    }

    fun search(query: String) {
        editor?.findReplacePanel?.find(query)
    }

    fun replaceAll(replacement: String) {
        editor?.findReplacePanel?.replaceAll(editor?.findReplacePanel?.findQuery, replacement)
        _editorContent.value = editor?.text?.toString() ?: ""
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

    fun setLanguage(language: EditorLanguageType) {
        _currentLanguage.value = language
        language.setup(editor)
    }

    fun onSelectionChanged(hasSelection: Boolean) {
        _hasSelection.value = hasSelection
    }

    fun onCursorPositionChanged(line: Int, column: Int) {
        _cursorLine.value = line
        _cursorColumn.value = column
    }

    override fun onCleared() {
        super.onCleared()
        editor = null // Clean up reference
    }
}
