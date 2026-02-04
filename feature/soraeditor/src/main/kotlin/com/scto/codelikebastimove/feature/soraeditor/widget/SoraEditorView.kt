package com.scto.codelikebastimove.feature.soraeditor.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.FrameLayout
import com.scto.codelikebastimove.feature.soraeditor.language.LanguageRegistry
import com.scto.codelikebastimove.feature.soraeditor.model.CursorAnimationType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import com.scto.codelikebastimove.feature.soraeditor.model.HighlightingMode
import com.scto.codelikebastimove.feature.soraeditor.model.LineEndingType
import com.scto.codelikebastimove.feature.soraeditor.model.RenderWhitespaceMode
import com.scto.codelikebastimove.feature.soraeditor.theme.EditorThemeProvider
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.SelectionChangeEvent
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.Magnifier

class SoraEditorView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
  FrameLayout(context, attrs, defStyleAttr) {

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

      isLineNumberEnabled = currentConfig.showLineNumber
      isWordwrap = currentConfig.wordWrap
      tabWidth = currentConfig.tabSize

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
    val previousMode = currentConfig.highlightingMode
    currentConfig = config

    codeEditor.apply {
      setTextSize(config.textSize)
      isLineNumberEnabled = config.showLineNumber
      isWordwrap = config.wordWrap
      tabWidth = config.tabSize
      isHighlightCurrentLine = config.highlightCurrentLine
      isHighlightBracketPair = config.highlightBrackets

      setPinLineNumber(config.pinLineNumber)
      setStickyScroll(config.stickyScroll)
      setFastDelete(config.fastDelete)
      setCursorAnimation(config.cursorAnimation)
      setKeyboardSuggestion(config.keyboardSuggestion)
      setLineSpacing(config.lineSpacing)
      setRenderWhitespace(config.renderWhitespace)
      setHideSoftKeyboard(config.hideSoftKbd)

      getComponent(EditorAutoCompletion::class.java)?.isEnabled = config.autoComplete
    }

    if (previousMode != config.highlightingMode) {
      setLanguage(currentLanguageType)
    }
  }

  private fun CodeEditor.setPinLineNumber(pin: Boolean) {
    isLineNumberPinned = pin
  }

  private fun CodeEditor.setStickyScroll(enabled: Boolean) {
    isStickyScrollEnabled = enabled
  }

  private fun CodeEditor.setFastDelete(enabled: Boolean) {
    props.deleteEmptyLineFast = enabled
    props.deleteMultiSpaces = if (enabled) -1 else 1
  }

  private fun CodeEditor.setCursorAnimation(animationType: CursorAnimationType) {
    cursorAnimator.apply {
      when (animationType) {
        CursorAnimationType.NONE -> {
          isEnabled = false
        }
        CursorAnimationType.FADE -> {
          isEnabled = true
          duration = 200
        }
        CursorAnimationType.BLINK -> {
          isEnabled = true
          duration = 500
        }
        CursorAnimationType.SCALE -> {
          isEnabled = true
          duration = 150
        }
      }
    }
  }

  private fun CodeEditor.setKeyboardSuggestion(enabled: Boolean) {
    inputType = if (enabled) {
      android.text.InputType.TYPE_CLASS_TEXT or
        android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
    } else {
      android.text.InputType.TYPE_CLASS_TEXT or
        android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE or
        android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }
  }

  private fun CodeEditor.setLineSpacing(multiplier: Float) {
    lineSpacingMultiplier = multiplier
  }

  private fun CodeEditor.setRenderWhitespace(mode: RenderWhitespaceMode) {
    isNonPrintablePaintingEnabled = mode != RenderWhitespaceMode.NONE
    when (mode) {
      RenderWhitespaceMode.NONE -> {
        nonPrintablePaintingFlags = 0
      }
      RenderWhitespaceMode.SELECTION -> {
        nonPrintablePaintingFlags = CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
      }
      RenderWhitespaceMode.BOUNDARY -> {
        nonPrintablePaintingFlags = CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or
          CodeEditor.FLAG_DRAW_WHITESPACE_TRAILING
      }
      RenderWhitespaceMode.TRAILING -> {
        nonPrintablePaintingFlags = CodeEditor.FLAG_DRAW_WHITESPACE_TRAILING
      }
      RenderWhitespaceMode.ALL -> {
        nonPrintablePaintingFlags = CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or
          CodeEditor.FLAG_DRAW_WHITESPACE_TRAILING or
          CodeEditor.FLAG_DRAW_WHITESPACE_INNER or
          CodeEditor.FLAG_DRAW_LINE_SEPARATOR
      }
    }
  }

  private fun CodeEditor.setHideSoftKeyboard(hide: Boolean) {
    isEditable = !hide
    if (hide) {
      hideEditorWindows()
    }
  }

  fun getLineEnding(): LineEndingType {
    return currentConfig.lineEndingSetting
  }

  fun setLineEnding(type: LineEndingType) {
    currentConfig = currentConfig.copy(lineEndingSetting = type)
  }

  fun ensureFinalNewline(): String {
    val text = getText()
    return if (currentConfig.finalNewline && text.isNotEmpty() && !text.endsWith("\n")) {
      text + getLineEndingString()
    } else {
      text
    }
  }

  fun getLineEndingString(): String {
    return when (currentConfig.lineEndingSetting) {
      LineEndingType.LF -> "\n"
      LineEndingType.CRLF -> "\r\n"
      LineEndingType.CR -> "\r"
    }
  }

  fun normalizeLineEndings(text: String): String {
    val normalized = text.replace("\r\n", "\n").replace("\r", "\n")
    return when (currentConfig.lineEndingSetting) {
      LineEndingType.LF -> normalized
      LineEndingType.CRLF -> normalized.replace("\n", "\r\n")
      LineEndingType.CR -> normalized.replace("\n", "\r")
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
    val newText =
      if (all) {
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
    val regex: Boolean = false,
  )

  data class SearchResult(val startIndex: Int, val endIndex: Int, val line: Int, val column: Int)
}
