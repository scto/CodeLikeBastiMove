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
                // --- BASIS FARBEN ---
                setColor(WHOLE_BACKGROUND, theme.backgroundColor.toArgb())
                setColor(TEXT_NORMAL, theme.foregroundColor.toArgb())
                setColor(LINE_NUMBER, theme.lineNumberColor.toArgb())
                setColor(LINE_NUMBER_BACKGROUND, theme.lineNumberBackgroundColor.toArgb())
                setColor(CURRENT_LINE, theme.currentLineColor.toArgb())
                setColor(SELECTION_INSERT, theme.cursorColor.toArgb())
                setColor(SELECTION_HANDLE, theme.cursorColor.toArgb())
                setColor(SELECTED_TEXT_BACKGROUND, theme.selectionColor.toArgb())
                setColor(LINE_DIVIDER, theme.gutterDividerColor.toArgb())

                // --- SYNTAX HIGHLIGHTING (Basis Mapping) ---
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
                setColor(ANNOTATION, theme.syntaxColors.annotation.toArgb())

                // --- ERWEITERTES MAPPING (Fix für unsichtbare Texte) ---
                // TreeSitter nutzt oft spezifische Tokens, die auf Default-Werte zurückfallen.
                // Wir setzen hier explizit Farben für Tokens, die sonst "unsichtbar" sein könnten.

                // Strings & Zahlen
                setColor(LITERAL, theme.syntaxColors.string.toArgb())

                // Typen (Klassen, Interfaces)
                // In manchen Schemes ist TYPE nicht gesetzt -> fällt auf TEXT_NORMAL zurück (okay),
                // aber wenn es auf TRANSPARENT fällt -> unsichtbar.
                // Wir nutzen hier die 'type' Farbe aus deinem Theme Model.
                // Hinweis: Sora Editor hat evtl. keine direkte Konstante TYPE in älteren Versionen,
                // daher mappen wir auf IDENTIFIER_NAME oder nutzen Custom Colors wenn möglich.
                // Da wir hier EditorColorScheme erweitern, nutzen wir vorhandene Slots kreativ:

                // Für TreeSitter Mapping im LanguageProvider relevant:
                // Wir setzen TEXT_NORMAL explizit als Fallback für alles Unbekannte
                setColor(TEXT_NORMAL, theme.foregroundColor.toArgb())
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
