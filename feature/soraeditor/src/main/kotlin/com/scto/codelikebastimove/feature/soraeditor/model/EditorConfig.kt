package com.scto.codelikebastimove.feature.soraeditor.model

data class EditorConfig(
  val textSize: Float = 14f,
  val tabSize: Int = 4,
  val pinLineNumber: Boolean = false,
  val stickyScroll: Boolean = false,
  val fastDelete: Boolean = false,
  val showLineNumber: Boolean = true,
  val cursorAnimation: CursorAnimationType = CursorAnimationType.FADE,
  val wordWrap: Boolean = false,
  val keyboardSuggestion: Boolean = true,
  val lineSpacing: Float = 1.2f,
  val renderWhitespace: RenderWhitespaceMode = RenderWhitespaceMode.NONE,
  val hideSoftKbd: Boolean = false,
  val lineEndingSetting: LineEndingType = LineEndingType.LF,
  val finalNewline: Boolean = true,
  val autoIndent: Boolean = true,
  val autoComplete: Boolean = true,
  val highlightCurrentLine: Boolean = true,
  val highlightBrackets: Boolean = true,
  val useTabCharacter: Boolean = false,
  val fontFamily: String = "JetBrains Mono",
  val highlightingMode: HighlightingMode = HighlightingMode.TEXTMATE,
  val lineNumberMarginLeft: Float = 9f,
  val autoCompletionAnimation: Boolean = true,
  val trimTrailingWhitespace: Boolean = false,
  val insertFinalNewline: Boolean = false,
  val autoCloseTag: Boolean = true,
  val bulletContinuation: Boolean = true,
  val customFontPath: String? = null,
  val useCustomFont: Boolean = false,
)

enum class HighlightingMode {
  TEXTMATE,
  TREESITTER,
  SIMPLE,
}

enum class CursorAnimationType {
  NONE,
  FADE,
  BLINK,
  SCALE,
}

enum class RenderWhitespaceMode {
  NONE,
  SELECTION,
  BOUNDARY,
  TRAILING,
  ALL,
}

enum class LineEndingType {
  LF,
  CRLF,
  CR,
}

data class EditorFile(
  val path: String,
  val name: String,
  val content: String,
  val languageType: EditorLanguageType,
  val isModified: Boolean = false,
  val cursorPosition: Int = 0,
  val scrollPosition: Int = 0,
)

data class EditorTab(val id: String, val file: EditorFile, val isActive: Boolean = false)
