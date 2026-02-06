package com.scto.codelikebastimove.feature.soraeditor.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import com.scto.codelikebastimove.feature.soraeditor.intelligent.AutoCloseTag
import com.scto.codelikebastimove.feature.soraeditor.intelligent.BulletContinuation
import com.scto.codelikebastimove.feature.soraeditor.intelligent.IntelligentFeatureRegistry
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
import com.scto.codelikebastimove.feature.soraeditor.util.FontCache
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.InterceptTarget
import io.github.rosemoe.sora.event.LongPressEvent
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
  private var actionModeCallback: EditorActionModeCallback? = null

  companion object {
    private const val MENU_ITEM_SELECT_ALL = 1
    private const val MENU_ITEM_CUT = 2
    private const val MENU_ITEM_COPY = 3
    private const val MENU_ITEM_PASTE = 4
    private const val MENU_ITEM_UNDO = 5
    private const val MENU_ITEM_REDO = 6
    private const val MENU_ITEM_FORMAT = 7
  }

  init {
    isFocusable = true
    isFocusableInTouchMode = true
    isClickable = true
    addView(codeEditor, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    setupEditor()
    setupEventListeners()
    setupActionMode()
    setupIntelligentFeatures()
  }

  private fun setupEditor() {
    codeEditor.apply {
      typefaceText = Typeface.MONOSPACE
      typefaceLineNumber = Typeface.MONOSPACE

      isLineNumberEnabled = true
      isWordwrap = currentConfig.wordWrap
      tabWidth = currentConfig.tabSize
      lineNumberMarginLeft = currentConfig.lineNumberMarginLeft

      isNestedScrollingEnabled = false
      isVerticalScrollBarEnabled = true
      isHorizontalScrollBarEnabled = true
      isFocusable = true
      isFocusableInTouchMode = true
      isClickable = true
      isEditable = true

      getComponent(EditorAutoCompletion::class.java)?.apply {
        isEnabled = currentConfig.autoComplete
        runCatching { setEnabledAnimation(currentConfig.autoCompletionAnimation) }
      }
      getComponent(Magnifier::class.java)?.isEnabled = true

      runCatching { props.stickyScroll = currentConfig.stickyScroll }
    }

    applyTheme(currentTheme)
  }

  override fun onInterceptTouchEvent(ev: android.view.MotionEvent?): Boolean {
    parent?.requestDisallowInterceptTouchEvent(true)
    return false
  }

  private fun setupEventListeners() {
    codeEditor.subscribeEvent(ContentChangeEvent::class.java) { event, _ ->
      onTextChangeListener?.invoke(codeEditor.text.toString())
    }

    codeEditor.subscribeEvent(SelectionChangeEvent::class.java) { event, _ ->
      val cursor = codeEditor.cursor
      onCursorChangeListener?.invoke(cursor.leftLine, cursor.leftColumn)
    }

    codeEditor.subscribeEvent(LongPressEvent::class.java) { _, _ ->
      showEditorContextMenu()
    }
  }

  private fun setupActionMode() {
    actionModeCallback = EditorActionModeCallback()
  }

  private fun setupIntelligentFeatures() {
    AutoCloseTag.setEnabled(currentConfig.autoCloseTag)
    BulletContinuation.setEnabled(currentConfig.bulletContinuation)

    codeEditor.subscribeEvent(EditorKeyEvent::class.java) { event, _ ->
      val features = IntelligentFeatureRegistry.getFeaturesForExtensions(currentLanguageType.fileExtensions)
      features.forEach { feature ->
        feature.handleKeyEvent(event, codeEditor)
      }
    }

    codeEditor.subscribeEvent(ContentChangeEvent::class.java) { event, _ ->
      if (event.action == ContentChangeEvent.ACTION_INSERT && event.changedText.length == 1) {
        val insertedChar = event.changedText[0]
        val features = IntelligentFeatureRegistry.getFeaturesForExtensions(currentLanguageType.fileExtensions)
        features.filter { insertedChar in it.triggerCharacters }.forEach { feature ->
          feature.handleInsertChar(insertedChar, codeEditor)
        }
      } else if (event.action == ContentChangeEvent.ACTION_DELETE && event.changedText.length == 1) {
        val deletedChar = event.changedText[0]
        val features = IntelligentFeatureRegistry.getFeaturesForExtensions(currentLanguageType.fileExtensions)
        features.filter { deletedChar in it.triggerCharacters }.forEach { feature ->
          feature.handleDeleteChar(deletedChar, codeEditor)
        }
      }
    }
  }

  fun getCurrentFileExtensions(): List<String> {
    return currentLanguageType.fileExtensions
  }

  fun applyCustomFont(fontPath: String, isAsset: Boolean = false) {
    val typeface = FontCache.getFont(context, fontPath, isAsset)
    if (typeface != null) {
      codeEditor.typefaceText = typeface
    }
  }

  fun resetToDefaultFont() {
    codeEditor.typefaceText = Typeface.MONOSPACE
    codeEditor.typefaceLineNumber = Typeface.MONOSPACE
  }

  private fun showEditorContextMenu() {
    codeEditor.startActionMode(actionModeCallback, ActionMode.TYPE_FLOATING)
  }

  inner class EditorActionModeCallback : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
      menu?.apply {
        add(Menu.NONE, MENU_ITEM_SELECT_ALL, 0, "Select All")
        add(Menu.NONE, MENU_ITEM_CUT, 1, "Cut")
        add(Menu.NONE, MENU_ITEM_COPY, 2, "Copy")
        add(Menu.NONE, MENU_ITEM_PASTE, 3, "Paste")
        add(Menu.NONE, MENU_ITEM_UNDO, 4, "Undo")
        add(Menu.NONE, MENU_ITEM_REDO, 5, "Redo")
        add(Menu.NONE, MENU_ITEM_FORMAT, 6, "Format")
      }
      return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
      menu?.findItem(MENU_ITEM_UNDO)?.isEnabled = canUndo()
      menu?.findItem(MENU_ITEM_REDO)?.isEnabled = canRedo()
      return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
      when (item?.itemId) {
        MENU_ITEM_SELECT_ALL -> selectAll()
        MENU_ITEM_CUT -> cut()
        MENU_ITEM_COPY -> copy()
        MENU_ITEM_PASTE -> paste()
        MENU_ITEM_UNDO -> undo()
        MENU_ITEM_REDO -> redo()
        MENU_ITEM_FORMAT -> formatCode()
        else -> return false
      }
      mode?.finish()
      return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {}
  }

  fun setText(text: String) {
    codeEditor.setText(text)
  }

  fun getText(): String {
    return codeEditor.text.toString()
  }

  fun setLanguage(languageType: EditorLanguageType) {
    currentLanguageType = languageType
    languageRegistry.setTheme(currentTheme)

    val mode = currentConfig.highlightingMode
    val isTextMateMode = mode == HighlightingMode.TEXTMATE ||
      (mode == HighlightingMode.TREESITTER &&
        !languageRegistry.getSupportedLanguages(HighlightingMode.TREESITTER).contains(languageType) &&
        languageRegistry.getTextMateProvider().supportsLanguage(languageType))

    if (isTextMateMode && codeEditor.colorScheme !is TextMateColorScheme) {
      val textMateScheme = languageRegistry.getTextMateProvider().getColorScheme()
      if (textMateScheme != null) {
        themeProvider.applyColorsToScheme(textMateScheme, currentTheme)
        codeEditor.colorScheme = textMateScheme
      }
    } else if (!isTextMateMode && codeEditor.colorScheme is TextMateColorScheme) {
      themeProvider.applyTheme(codeEditor, currentTheme)
    }

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
      lineNumberMarginLeft = config.lineNumberMarginLeft

      isNestedScrollingEnabled = false
      isVerticalScrollBarEnabled = true
      isHorizontalScrollBarEnabled = true
      isFocusable = true
      isFocusableInTouchMode = true
      isEditable = true

      setPinLineNumber(config.pinLineNumber)
      setFastDelete(config.fastDelete)
      setCursorAnimation(config.cursorAnimation)
      setKeyboardSuggestion(config.keyboardSuggestion)
      setLineSpacing(config.lineSpacing)
      setRenderWhitespace(config.renderWhitespace)
      setHideSoftKeyboard(config.hideSoftKbd)
      setStickyScroll(config.stickyScroll)

      getComponent(EditorAutoCompletion::class.java)?.apply {
        isEnabled = config.autoComplete
        runCatching { setEnabledAnimation(config.autoCompletionAnimation) }
      }
    }

    AutoCloseTag.setEnabled(config.autoCloseTag)
    BulletContinuation.setEnabled(config.bulletContinuation)

    if (config.useCustomFont && config.customFontPath != null) {
      applyCustomFont(config.customFontPath)
    } else {
      resetToDefaultFont()
    }

    if (previousMode != config.highlightingMode) {
      setLanguage(currentLanguageType)
    }
  }

  private fun CodeEditor.setPinLineNumber(pin: Boolean) {
    // Line number pinning controlled via props
    // Note: This feature may not be available in all sora-editor versions
  }

  private fun CodeEditor.setFastDelete(enabled: Boolean) {
    props.deleteEmptyLineFast = enabled
    props.deleteMultiSpaces = if (enabled) -1 else 1
  }

  private fun CodeEditor.setCursorAnimation(animationType: CursorAnimationType) {
    // Cursor animation control - implementation depends on sora-editor version
    // The cursor animation type setting is handled by the editor's default behavior
    // Note: Direct cursor animation control may require a custom CursorAnimator implementation
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
    nonPrintablePaintingFlags = when (mode) {
      RenderWhitespaceMode.NONE -> 0
      RenderWhitespaceMode.SELECTION -> CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
      RenderWhitespaceMode.BOUNDARY -> {
        CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_WHITESPACE_TRAILING
      }
      RenderWhitespaceMode.TRAILING -> CodeEditor.FLAG_DRAW_WHITESPACE_TRAILING
      RenderWhitespaceMode.ALL -> {
        CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or
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

  private fun CodeEditor.setStickyScroll(enabled: Boolean) {
    runCatching { props.stickyScroll = enabled }
  }

  fun trimTrailingWhitespace(): String {
    val text = getText()
    return text.lines().joinToString(getLineEndingString()) { line ->
      line.trimEnd()
    }
  }

  fun getTextForSave(): String {
    var text = getText()
    if (currentConfig.trimTrailingWhitespace) {
      text = trimTrailingWhitespace()
    }
    if (currentConfig.insertFinalNewline && text.isNotEmpty() && !text.endsWith("\n")) {
      text += getLineEndingString()
    }
    return normalizeLineEndings(text)
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
    languageRegistry.setTheme(theme)

    val isTextMateMode = (currentConfig.highlightingMode == HighlightingMode.TEXTMATE &&
      languageRegistry.getTextMateProvider().supportsLanguage(currentLanguageType)) ||
      (currentConfig.highlightingMode == HighlightingMode.TREESITTER &&
        !languageRegistry.getSupportedLanguages(HighlightingMode.TREESITTER).contains(currentLanguageType) &&
        languageRegistry.getTextMateProvider().supportsLanguage(currentLanguageType))

    if (isTextMateMode) {
      val textMateScheme = languageRegistry.getTextMateProvider().getColorScheme()
      if (textMateScheme != null) {
        themeProvider.applyColorsToScheme(textMateScheme, theme)
        codeEditor.colorScheme = textMateScheme
      } else {
        themeProvider.applyTheme(codeEditor, theme)
      }
    } else {
      themeProvider.applyTheme(codeEditor, theme)
    }

    val language = languageRegistry.getLanguage(currentLanguageType, currentConfig.highlightingMode)
    codeEditor.setEditorLanguage(language)
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

  fun selectCurrentWord() {
    val cursor = codeEditor.cursor
    val text = codeEditor.text
    val line = cursor.leftLine
    val column = cursor.leftColumn

    if (line >= text.lineCount) return
    val lineContent = text.getLine(line).toString()
    if (column > lineContent.length) return

    var start = column
    var end = column

    while (start > 0 && isWordChar(lineContent[start - 1])) {
      start--
    }
    while (end < lineContent.length && isWordChar(lineContent[end])) {
      end++
    }

    if (start < end) {
      codeEditor.setSelectionRegion(line, start, line, end)
    }
  }

  private fun isWordChar(c: Char): Boolean {
    return c.isLetterOrDigit() || c == '_'
  }

  fun getSelectedText(): String {
    val cursor = codeEditor.cursor
    return if (cursor.isSelected) {
      val text = codeEditor.text
      val startIndex = text.getCharIndex(cursor.leftLine, cursor.leftColumn)
      val endIndex = text.getCharIndex(cursor.rightLine, cursor.rightColumn)
      text.subSequence(startIndex, endIndex).toString()
    } else {
      ""
    }
  }

  fun hasSelection(): Boolean {
    return codeEditor.cursor.isSelected
  }

  fun replaceSelection(replacement: String) {
    if (hasSelection()) {
      val cursor = codeEditor.cursor
      val startLine = cursor.leftLine
      val startColumn = cursor.leftColumn
      val endLine = cursor.rightLine
      val endColumn = cursor.rightColumn
      codeEditor.text.replace(startLine, startColumn, endLine, endColumn, replacement)
    }
  }

  fun isWordWrapEnabled(): Boolean {
    return codeEditor.isWordwrap
  }

  fun setWordWrap(enabled: Boolean) {
    codeEditor.isWordwrap = enabled
    currentConfig = currentConfig.copy(wordWrap = enabled)
  }

  fun isEditable(): Boolean {
    return codeEditor.isEditable
  }

  fun setEditable(editable: Boolean) {
    codeEditor.isEditable = editable
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
