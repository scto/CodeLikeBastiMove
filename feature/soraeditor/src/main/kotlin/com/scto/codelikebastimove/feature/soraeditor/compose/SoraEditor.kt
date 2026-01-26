package com.scto.codelikebastimove.feature.soraeditor.compose

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

@Composable
fun SoraEditor(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    languageType: EditorLanguageType = EditorLanguageType.PLAIN_TEXT,
    config: EditorConfig = EditorConfig(),
    theme: EditorTheme = EditorThemes.DarkModern,
    onCursorChange: ((line: Int, column: Int) -> Unit)? = null,
    editorViewRef: ((SoraEditorView) -> Unit)? = null
) {
    val context = LocalContext.current
    
    val editorView = remember {
        SoraEditorView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    
    LaunchedEffect(text) {
        if (editorView.getText() != text) {
            editorView.setText(text)
        }
    }
    
    LaunchedEffect(languageType) {
        editorView.setLanguage(languageType)
    }
    
    LaunchedEffect(config) {
        editorView.applyConfig(config)
    }
    
    LaunchedEffect(theme) {
        editorView.applyTheme(theme)
    }
    
    LaunchedEffect(Unit) {
        editorView.setOnTextChangeListener { newText ->
            onTextChange(newText)
        }
        
        onCursorChange?.let { callback ->
            editorView.setOnCursorChangeListener { line, column ->
                callback(line, column)
            }
        }
        
        editorViewRef?.invoke(editorView)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            editorView.release()
        }
    }
    
    AndroidView(
        factory = { editorView },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun rememberSoraEditorState(
    initialText: String = "",
    initialLanguage: EditorLanguageType = EditorLanguageType.PLAIN_TEXT,
    initialConfig: EditorConfig = EditorConfig(),
    initialTheme: EditorTheme = EditorThemes.DarkModern
): SoraEditorState {
    return remember {
        SoraEditorState(
            text = initialText,
            languageType = initialLanguage,
            config = initialConfig,
            theme = initialTheme
        )
    }
}

class SoraEditorState(
    text: String,
    languageType: EditorLanguageType,
    config: EditorConfig,
    theme: EditorTheme
) {
    var text: String = text
        private set
    
    var languageType: EditorLanguageType = languageType
        private set
    
    var config: EditorConfig = config
        private set
    
    var theme: EditorTheme = theme
        private set
    
    var cursorLine: Int = 0
        private set
    
    var cursorColumn: Int = 0
        private set
    
    var isModified: Boolean = false
        private set
    
    private var editorView: SoraEditorView? = null
    
    fun bindEditorView(view: SoraEditorView) {
        editorView = view
    }
    
    fun setText(newText: String) {
        text = newText
        editorView?.setText(newText)
    }
    
    fun setLanguage(language: EditorLanguageType) {
        languageType = language
        editorView?.setLanguage(language)
    }
    
    fun setConfig(newConfig: EditorConfig) {
        config = newConfig
        editorView?.applyConfig(newConfig)
    }
    
    fun setTheme(newTheme: EditorTheme) {
        theme = newTheme
        editorView?.applyTheme(newTheme)
    }
    
    fun updateCursor(line: Int, column: Int) {
        cursorLine = line
        cursorColumn = column
    }
    
    fun markModified() {
        isModified = true
    }
    
    fun markSaved() {
        isModified = false
    }
    
    fun undo() = editorView?.undo()
    fun redo() = editorView?.redo()
    fun canUndo() = editorView?.canUndo() ?: false
    fun canRedo() = editorView?.canRedo() ?: false
    fun selectAll() = editorView?.selectAll()
    fun copy() = editorView?.copy()
    fun cut() = editorView?.cut()
    fun paste() = editorView?.paste()
    fun formatCode() = editorView?.formatCode()
    fun goToLine(line: Int) = editorView?.goToLine(line)
}
