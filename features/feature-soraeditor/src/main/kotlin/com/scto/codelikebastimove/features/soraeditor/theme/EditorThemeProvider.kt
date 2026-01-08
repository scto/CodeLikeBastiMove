package com.scto.codelikebastimove.features.soraeditor.theme

import android.graphics.Typeface
import com.scto.codelikebastimove.features.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.features.soraeditor.model.EditorThemes
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
                setColor(WHOLE_BACKGROUND, theme.backgroundColor.toArgb())
                setColor(TEXT_NORMAL, theme.foregroundColor.toArgb())
                setColor(LINE_NUMBER, theme.lineNumberColor.toArgb())
                setColor(LINE_NUMBER_BACKGROUND, theme.lineNumberBackgroundColor.toArgb())
                setColor(CURRENT_LINE, theme.currentLineColor.toArgb())
                setColor(SELECTION_INSERT, theme.cursorColor.toArgb())
                setColor(SELECTION_HANDLE, theme.cursorColor.toArgb())
                setColor(SELECTED_TEXT_BACKGROUND, theme.selectionColor.toArgb())
                setColor(LINE_DIVIDER, theme.gutterDividerColor.toArgb())
                
                setColor(KEYWORD, theme.syntaxColors.keyword.toArgb())
                setColor(LITERAL, theme.syntaxColors.string.toArgb())
                setColor(OPERATOR, theme.syntaxColors.operator.toArgb())
                setColor(COMMENT, theme.syntaxColors.comment.toArgb())
                setColor(FUNCTION_NAME, theme.syntaxColors.function.toArgb())
                setColor(IDENTIFIER_NAME, theme.syntaxColors.variable.toArgb())
                setColor(IDENTIFIER_VAR, theme.syntaxColors.variable.toArgb())
                setColor(ATTRIBUTE_NAME, theme.syntaxColors.attribute.toArgb())
                setColor(ATTRIBUTE_VALUE, theme.syntaxColors.string.toArgb())
                setColor(HTML_TAG, theme.syntaxColors.tag.toArgb())
            }
        }
    }
    
    fun getAvailableThemes(): List<EditorTheme> {
        return EditorThemes.allThemes
    }
    
    fun getDarkThemes(): List<EditorTheme> {
        return EditorThemes.allThemes.filter { it.isDark }
    }
    
    fun getLightThemes(): List<EditorTheme> {
        return EditorThemes.allThemes.filter { !it.isDark }
    }
    
    private fun androidx.compose.ui.graphics.Color.toArgb(): Int {
        return android.graphics.Color.argb(
            (alpha * 255).toInt(),
            (red * 255).toInt(),
            (green * 255).toInt(),
            (blue * 255).toInt()
        )
    }
}
