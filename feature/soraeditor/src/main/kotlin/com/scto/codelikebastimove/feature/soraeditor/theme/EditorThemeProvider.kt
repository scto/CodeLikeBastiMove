package com.scto.codelikebastimove.feature.soraeditor.theme

import androidx.compose.ui.graphics.toArgb
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class EditorThemeProvider {

    fun applyTheme(editor: CodeEditor, theme: EditorTheme) {
        val colorScheme = createColorScheme(theme)
        editor.colorScheme = colorScheme
    }

    fun createColorScheme(theme: EditorTheme): EditorColorScheme {
        return object : EditorColorScheme() {
            init {
                applyThemeColors(theme)
            }

            private fun applyThemeColors(theme: EditorTheme) {
                val foreground = theme.foregroundColor.toArgb()
                val background = theme.backgroundColor.toArgb()
                val syntax = theme.syntaxColors

                setColor(WHOLE_BACKGROUND, background)
                setColor(TEXT_NORMAL, foreground)
                setColor(LINE_NUMBER, theme.lineNumberColor.toArgb())
                setColor(LINE_NUMBER_BACKGROUND, theme.lineNumberBackgroundColor.toArgb())
                setColor(LINE_NUMBER_CURRENT, theme.lineNumberColor.toArgb())
                setColor(CURRENT_LINE, theme.currentLineColor.toArgb())
                setColor(SELECTION_INSERT, theme.cursorColor.toArgb())
                setColor(SELECTION_HANDLE, theme.cursorColor.toArgb())
                setColor(SELECTED_TEXT_BACKGROUND, theme.selectionColor.toArgb())
                setColor(LINE_DIVIDER, theme.gutterDividerColor.toArgb())
                setColor(BLOCK_LINE, theme.gutterDividerColor.toArgb())
                setColor(BLOCK_LINE_CURRENT, theme.cursorColor.toArgb())

                setColor(KEYWORD, syntax.keyword.toArgb())
                setColor(LITERAL, syntax.string.toArgb())
                setColor(OPERATOR, syntax.operator.toArgb())
                setColor(COMMENT, syntax.comment.toArgb())
                setColor(FUNCTION_NAME, syntax.function.toArgb())
                setColor(IDENTIFIER_NAME, foreground)
                setColor(IDENTIFIER_VAR, syntax.variable.toArgb())
                setColor(ATTRIBUTE_NAME, syntax.attribute.toArgb())
                setColor(ATTRIBUTE_VALUE, syntax.string.toArgb())
                setColor(HTML_TAG, syntax.tag.toArgb())
                setColor(ANNOTATION, syntax.annotation.toArgb())

                setColor(COMPLETION_WND_BACKGROUND, background)
                setColor(COMPLETION_WND_TEXT_PRIMARY, foreground)
                setColor(COMPLETION_WND_TEXT_SECONDARY, theme.lineNumberColor.toArgb())
                setColor(COMPLETION_WND_ITEM_CURRENT, theme.selectionColor.toArgb())
                setColor(COMPLETION_WND_CORNER, theme.gutterDividerColor.toArgb())

                setColor(MATCHED_TEXT_BACKGROUND, theme.selectionColor.toArgb())
                setColor(NON_PRINTABLE_CHAR, theme.lineNumberColor.toArgb())
                setColor(PROBLEM_ERROR, syntax.error.toArgb())
                setColor(PROBLEM_WARNING, syntax.annotation.toArgb())

                setColor(SIDE_BLOCK_LINE, theme.gutterDividerColor.toArgb())
                setColor(SCROLL_BAR_THUMB, theme.lineNumberColor.toArgb())
                setColor(SCROLL_BAR_THUMB_PRESSED, foreground)
                setColor(SCROLL_BAR_TRACK, background)

                setColor(TEXT_SELECTED, foreground)
                setColor(UNDERLINE, foreground)
                setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, syntax.operator.toArgb())
                setColor(HIGHLIGHTED_DELIMITERS_BACKGROUND, theme.currentLineColor.toArgb())
            }
        }
    }

    fun getAvailableThemes(): List<EditorTheme> {
        return EditorThemes.allThemes
    }

    fun getDarkThemes(): List<EditorTheme> {
        return EditorThemes.allThemes.filter {
            it.isDark
        }
    }

    fun getLightThemes(): List<EditorTheme> {
        return EditorThemes.allThemes.filter {
            !it.isDark
        }
    }
}
