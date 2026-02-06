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

    fun applyColorsToScheme(scheme: EditorColorScheme, theme: EditorTheme) {
        val foreground = theme.foregroundColor.toArgb()
        val background = theme.backgroundColor.toArgb()
        val syntax = theme.syntaxColors
        val lineNumber = theme.lineNumberColor.toArgb()
        val selection = theme.selectionColor.toArgb()
        val cursor = theme.cursorColor.toArgb()
        val divider = theme.gutterDividerColor.toArgb()

        scheme.setColor(EditorColorScheme.WHOLE_BACKGROUND, background)
        scheme.setColor(EditorColorScheme.TEXT_NORMAL, foreground)
        scheme.setColor(EditorColorScheme.LINE_NUMBER, lineNumber)
        scheme.setColor(EditorColorScheme.LINE_NUMBER_BACKGROUND, theme.lineNumberBackgroundColor.toArgb())
        scheme.setColor(EditorColorScheme.LINE_NUMBER_CURRENT, lineNumber)
        scheme.setColor(EditorColorScheme.LINE_NUMBER_PANEL, theme.lineNumberBackgroundColor.toArgb())
        scheme.setColor(EditorColorScheme.CURRENT_LINE, theme.currentLineColor.toArgb())
        scheme.setColor(EditorColorScheme.SELECTION_INSERT, cursor)
        scheme.setColor(EditorColorScheme.SELECTION_HANDLE, cursor)
        scheme.setColor(EditorColorScheme.SELECTED_TEXT_BACKGROUND, selection)
        scheme.setColor(EditorColorScheme.LINE_DIVIDER, divider)
        scheme.setColor(EditorColorScheme.BLOCK_LINE, setAlpha(foreground, 0.4f))
        scheme.setColor(EditorColorScheme.BLOCK_LINE_CURRENT, setAlpha(foreground, 0.6f))

        scheme.setColor(EditorColorScheme.COMPLETION_WND_BACKGROUND, background)
        scheme.setColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY, foreground)
        scheme.setColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY, lineNumber)
        scheme.setColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT, selection)
        scheme.setColor(EditorColorScheme.COMPLETION_WND_CORNER, divider)

        scheme.setColor(EditorColorScheme.DIAGNOSTIC_TOOLTIP_BACKGROUND, background)
        scheme.setColor(EditorColorScheme.DIAGNOSTIC_TOOLTIP_BRIEF_MSG, foreground)
        scheme.setColor(EditorColorScheme.DIAGNOSTIC_TOOLTIP_DETAILED_MSG, foreground)
        scheme.setColor(EditorColorScheme.DIAGNOSTIC_TOOLTIP_ACTION, cursor)

        scheme.setColor(EditorColorScheme.SIGNATURE_BACKGROUND, background)
        scheme.setColor(EditorColorScheme.SIGNATURE_TEXT_NORMAL, foreground)
        scheme.setColor(EditorColorScheme.SIGNATURE_TEXT_HIGHLIGHTED_PARAMETER, cursor)

        scheme.setColor(EditorColorScheme.STICKY_SCROLL_DIVIDER, divider)

        scheme.setColor(EditorColorScheme.MATCHED_TEXT_BACKGROUND, selection)
        scheme.setColor(EditorColorScheme.NON_PRINTABLE_CHAR, setAlpha(foreground, 0.4f))
        scheme.setColor(EditorColorScheme.PROBLEM_ERROR, syntax.error.toArgb())
        scheme.setColor(EditorColorScheme.PROBLEM_WARNING, syntax.annotation.toArgb())

        scheme.setColor(EditorColorScheme.SIDE_BLOCK_LINE, divider)
        scheme.setColor(EditorColorScheme.SCROLL_BAR_THUMB, setAlpha(foreground, 0.3f))
        scheme.setColor(EditorColorScheme.SCROLL_BAR_THUMB_PRESSED, setAlpha(foreground, 0.2f))
        scheme.setColor(EditorColorScheme.SCROLL_BAR_TRACK, background)

        scheme.setColor(EditorColorScheme.TEXT_SELECTED, foreground)
        scheme.setColor(EditorColorScheme.UNDERLINE, foreground)
        scheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_FOREGROUND, cursor)
        scheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_BACKGROUND, theme.currentLineColor.toArgb())
        scheme.setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_UNDERLINE, android.graphics.Color.TRANSPARENT)
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
                val lineNumber = theme.lineNumberColor.toArgb()
                val selection = theme.selectionColor.toArgb()
                val cursor = theme.cursorColor.toArgb()
                val divider = theme.gutterDividerColor.toArgb()

                setColor(WHOLE_BACKGROUND, background)
                setColor(TEXT_NORMAL, foreground)
                setColor(LINE_NUMBER, lineNumber)
                setColor(LINE_NUMBER_BACKGROUND, theme.lineNumberBackgroundColor.toArgb())
                setColor(LINE_NUMBER_CURRENT, lineNumber)
                setColor(LINE_NUMBER_PANEL, theme.lineNumberBackgroundColor.toArgb())
                setColor(CURRENT_LINE, theme.currentLineColor.toArgb())
                setColor(SELECTION_INSERT, cursor)
                setColor(SELECTION_HANDLE, cursor)
                setColor(SELECTED_TEXT_BACKGROUND, selection)
                setColor(LINE_DIVIDER, divider)
                setColor(BLOCK_LINE, setAlpha(foreground, 0.4f))
                setColor(BLOCK_LINE_CURRENT, setAlpha(foreground, 0.6f))

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
                setColor(COMPLETION_WND_TEXT_SECONDARY, lineNumber)
                setColor(COMPLETION_WND_ITEM_CURRENT, selection)
                setColor(COMPLETION_WND_CORNER, divider)

                setColor(DIAGNOSTIC_TOOLTIP_BACKGROUND, background)
                setColor(DIAGNOSTIC_TOOLTIP_BRIEF_MSG, foreground)
                setColor(DIAGNOSTIC_TOOLTIP_DETAILED_MSG, foreground)
                setColor(DIAGNOSTIC_TOOLTIP_ACTION, cursor)

                setColor(SIGNATURE_BACKGROUND, background)
                setColor(SIGNATURE_TEXT_NORMAL, foreground)
                setColor(SIGNATURE_TEXT_HIGHLIGHTED_PARAMETER, cursor)

                setColor(STICKY_SCROLL_DIVIDER, divider)

                setColor(MATCHED_TEXT_BACKGROUND, selection)
                setColor(NON_PRINTABLE_CHAR, setAlpha(foreground, 0.4f))
                setColor(PROBLEM_ERROR, syntax.error.toArgb())
                setColor(PROBLEM_WARNING, syntax.annotation.toArgb())

                setColor(SIDE_BLOCK_LINE, divider)
                setColor(SCROLL_BAR_THUMB, setAlpha(foreground, 0.3f))
                setColor(SCROLL_BAR_THUMB_PRESSED, setAlpha(foreground, 0.2f))
                setColor(SCROLL_BAR_TRACK, background)

                setColor(TEXT_SELECTED, foreground)
                setColor(UNDERLINE, foreground)
                setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, cursor)
                setColor(HIGHLIGHTED_DELIMITERS_BACKGROUND, theme.currentLineColor.toArgb())
                setColor(HIGHLIGHTED_DELIMITERS_UNDERLINE, android.graphics.Color.TRANSPARENT)
            }

            private fun setAlpha(color: Int, factor: Float): Int {
                val a = android.graphics.Color.alpha(color)
                val r = android.graphics.Color.red(color)
                val g = android.graphics.Color.green(color)
                val b = android.graphics.Color.blue(color)
                val newAlpha = (a * factor).toInt().coerceIn(0, 255)
                return android.graphics.Color.argb(newAlpha, r, g, b)
            }
        }
    }

    private fun setAlpha(color: Int, factor: Float): Int {
        val a = android.graphics.Color.alpha(color)
        val r = android.graphics.Color.red(color)
        val g = android.graphics.Color.green(color)
        val b = android.graphics.Color.blue(color)
        val newAlpha = (a * factor).toInt().coerceIn(0, 255)
        return android.graphics.Color.argb(newAlpha, r, g, b)
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
