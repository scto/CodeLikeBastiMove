package com.scto.codelikebastimove.feature.soraeditor.model

data class EditorConfig(
    val textSize: Float = 14f,
    val tabWidth: Int = 4,
    val showLineNumbers: Boolean = true,
    val showNonPrintableChars: Boolean = false,
    val wordWrap: Boolean = false,
    val autoIndent: Boolean = true,
    val autoComplete: Boolean = true,
    val highlightCurrentLine: Boolean = true,
    val highlightBrackets: Boolean = true,
    val useTabCharacter: Boolean = false,
    val stickyScroll: Boolean = false,
    val fontFamily: String = "JetBrains Mono",
    val highlightingMode: HighlightingMode = HighlightingMode.TEXTMATE
)

enum class HighlightingMode {
    TEXTMATE,
    TREESITTER,
    SIMPLE
}

data class EditorFile(
    val path: String,
    val name: String,
    val content: String,
    val languageType: EditorLanguageType,
    val isModified: Boolean = false,
    val cursorPosition: Int = 0,
    val scrollPosition: Int = 0
)

data class EditorTab(
    val id: String,
    val file: EditorFile,
    val isActive: Boolean = false
)
