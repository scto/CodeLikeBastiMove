package com.scto.codelikebastimove.feature.soraeditor.language

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class TreeSitterLanguageProvider : LanguageProvider {

    private var currentEditorTheme: EditorTheme = EditorThemes.DarkModern

    private val supportedLanguages = setOf(
        EditorLanguageType.JAVA,
        EditorLanguageType.KOTLIN,
        EditorLanguageType.XML,
        EditorLanguageType.CPP,
        EditorLanguageType.C,
        EditorLanguageType.JSON,
        EditorLanguageType.LOG,
    )

    fun setTheme(theme: EditorTheme) {
        this.currentEditorTheme = theme
    }

    override fun createLanguage(context: Context, languageType: EditorLanguageType): Language {
        return try {
            createLanguageForType(languageType)
        } catch (e: Exception) {
            e.printStackTrace()
            EmptyLanguage()
        }
    }

    private fun createLanguageForType(languageType: EditorLanguageType): Language {
        return EmptyLanguage()
    }

    override fun supportsLanguage(languageType: EditorLanguageType): Boolean {
        return supportedLanguages.contains(languageType)
    }

    fun createColorScheme(): EditorColorScheme {
        val scheme = EditorColorScheme()
        val theme = currentEditorTheme

        scheme.setColor(EditorColorScheme.WHOLE_BACKGROUND, theme.backgroundColor.toArgb())
        scheme.setColor(EditorColorScheme.TEXT_NORMAL, theme.foregroundColor.toArgb())
        scheme.setColor(EditorColorScheme.LINE_NUMBER, theme.lineNumberColor.toArgb())
        scheme.setColor(EditorColorScheme.LINE_NUMBER_BACKGROUND, theme.backgroundColor.toArgb())
        scheme.setColor(EditorColorScheme.CURRENT_LINE, theme.currentLineColor.toArgb())
        scheme.setColor(EditorColorScheme.SELECTION_HANDLE, theme.selectionColor.toArgb())
        scheme.setColor(EditorColorScheme.SELECTION_INSERT, theme.cursorColor.toArgb())
        scheme.setColor(EditorColorScheme.SELECTED_TEXT_BACKGROUND, theme.selectionColor.toArgb())

        val syntax = theme.syntaxColors
        scheme.setColor(EditorColorScheme.KEYWORD, syntax.keyword.toArgb())
        scheme.setColor(EditorColorScheme.LITERAL, syntax.string.toArgb())
        scheme.setColor(EditorColorScheme.OPERATOR, syntax.operator.toArgb())
        scheme.setColor(EditorColorScheme.COMMENT, syntax.comment.toArgb())
        scheme.setColor(EditorColorScheme.FUNCTION_NAME, syntax.function.toArgb())
        scheme.setColor(EditorColorScheme.IDENTIFIER_NAME, syntax.variable.toArgb())
        scheme.setColor(EditorColorScheme.IDENTIFIER_VAR, syntax.variable.toArgb())

        return scheme
    }
}
