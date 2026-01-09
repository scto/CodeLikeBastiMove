package com.scto.codelikebastimove.feature.soraeditor.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.FrameLayout
import com.scto.codelikebastimove.feature.soraeditor.language.LanguageRegistry
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import com.scto.codelikebastimove.feature.soraeditor.model.HighlightingMode
import com.scto.codelikebastimove.feature.soraeditor.theme.EditorThemeProvider
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.Magnifier

class SoraEditorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    private val codeEditor: CodeEditor = CodeEditor(context)
    private val languageRegistry = LanguageRegistry(context)
    private val themeProvider = EditorThemeProvider()
    
    private var currentLanguageType: EditorLanguageType = EditorLanguageType.PLAIN_TEXT
    private var currentConfig: EditorConfig = EditorConfig()
    private var currentTheme: EditorTheme = EditorThemes.DarkModern
    
    private var onTextChangeListener: ((String) -> Unit)? = null
    private var onCursorChangeListener: ((Int, Int) -> Unit)? = null
    
    init {
        addView(codeEditor, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setupEditor()
        setupEventListeners()
    }
    
    private fun setupEditor() {
        codeEditor.apply {
            typefaceText = Typeface.MONOSPACE
            typefaceLineNumber = Typeface.MONOSPACE
            
            isLineNumberEnabled = currentConfig.showLineNumbers
            isWordwrap = currentConfig.wordWrap
            tabWidth = currentConfig.tabWidth
            
            getComponent(EditorAutoCompletion::class.java)?.isEnabled = currentConfig.autoComplete
            getComponent(Magnifier::class.java)?.isEnabled = true
        }
        
        applyTheme(currentTheme)
    }
    
    private fun setupEventListeners() {
        codeEditor.subscribeEvent(ContentChangeEvent::class.java) { event, _ ->
            onTextChangeListener?.invoke(codeEditor.text.toString())
        }
        
        codeEditor.subscribeEvent(SelectionChangeEvent::class.java) { event, _ ->
            val cursor = codeEditor.cursor
            onCursorChangeListener?.invoke(cursor.leftLine, cursor.leftColumn)
        }
    }
    
    fun setText(text: String) {
        codeEditor.setText(text)
    }
    
    fun getText(): String {
        return codeEditor.text.toString()
    }
    
    fun setLanguage(languageType: EditorLanguageType) {
        currentLanguageType = languageType
        val language = languageRegistry.getLanguage(languageType, currentConfig.highlightingMode)
        codeEditor.setEditorLanguage(language)
    }
    
    fun setLanguageFromFileName(fileName: String) {
        val languageType = EditorLanguageType.fromFileName(fileName)
        setLanguage(languageType)
    }
    
    fun applyConfig(config: EditorConfig) {
        currentConfig = config
        
        codeEditor.apply {
            setTextSize(config.textSize)
            isLineNumberEnabled = config.showLineNumbers
            isWordwrap = config.wordWrap
            tabWidth = config.tabWidth
            isHighlightCurrentLine = config.highlightCurrentLine
            isHighlightBracketPair = config.highlightBrackets
            
            getComponent(EditorAutoCompletion::class.java)?.isEnabled = config.autoComplete
        }
        
        if (currentConfig.highlightingMode != config.highlightingMode) {
            setLanguage(currentLanguageType)
        }
    }
    
    fun applyTheme(theme: EditorTheme) {
        currentTheme = theme
        themeProvider.applyTheme(codeEditor, theme)
    }
    
    fun setHighlightingMode(mode: HighlightingMode) {
        currentConfig = currentConfig.copy(highlightingMode = mode)
        setLanguage(currentLanguageType)
    }
    
    fun undo() {
        codeEditor.undo()
    }
    
    fun redo() {
        codeEditor.redo()
    }
    
    fun canUndo(): Boolean = codeEditor.canUndo()
    
    fun canRedo(): Boolean = codeEditor.canRedo()
    
    fun setOnTextChangeListener(listener: (String) -> Unit) {
        onTextChangeListener = listener
    }
    
    fun setOnCursorChangeListener(listener: (line: Int, column: Int) -> Unit) {
        onCursorChangeListener = listener
    }
    
    fun getCursorPosition(): Pair<Int, Int> {
        val cursor = codeEditor.cursor
        return Pair(cursor.leftLine, cursor.leftColumn)
    }
    
    fun setCursorPosition(line: Int, column: Int) {
        codeEditor.cursor.set(line, column)
    }
    
    fun selectAll() {
        codeEditor.selectAll()
    }
    
    fun copy() {
        codeEditor.copyText()
    }
    
    fun cut() {
        codeEditor.cutText()
    }
    
    fun paste() {
        codeEditor.pasteText()
    }
    
    fun formatCode() {
        codeEditor.formatCodeAsync()
    }
    
    fun goToLine(line: Int) {
        val lineCount = codeEditor.lineCount
        val targetLine = line.coerceIn(0, lineCount - 1)
        codeEditor.cursor.set(targetLine, 0)
        codeEditor.ensurePositionVisible(targetLine, 0)
    }
    
    fun findText(query: String, options: SearchOptions = SearchOptions()): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val text = codeEditor.text.toString()
        val searchText = if (options.caseSensitive) query else query.lowercase()
        val searchIn = if (options.caseSensitive) text else text.lowercase()
        
        var index = 0
        while (true) {
            val foundIndex = searchIn.indexOf(searchText, index)
            if (foundIndex == -1) break
            
            val line = codeEditor.text.getIndexer().getCharLine(foundIndex)
            val column = codeEditor.text.getIndexer().getCharColumn(foundIndex)
            
            results.add(SearchResult(foundIndex, foundIndex + query.length, line, column))
            index = foundIndex + 1
        }
        
        return results
    }
    
    fun replaceText(search: String, replacement: String, all: Boolean = false) {
        val text = getText()
        val newText = if (all) {
            text.replace(search, replacement)
        } else {
            text.replaceFirst(search, replacement)
        }
        setText(newText)
    }
    
    fun insertText(text: String) {
        codeEditor.insertText(text, text.length)
    }
    
    fun getCodeEditor(): CodeEditor = codeEditor
    
    fun release() {
        codeEditor.release()
    }
    
    data class SearchOptions(
        val caseSensitive: Boolean = false,
        val wholeWord: Boolean = false,
        val regex: Boolean = false
    )
    
    data class SearchResult(
        val startIndex: Int,
        val endIndex: Int,
        val line: Int,
        val column: Int
    )
}
