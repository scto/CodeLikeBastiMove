package com.scto.codelikebastimove.feature.soraeditor.language

import android.content.Context

import androidx.compose.ui.graphics.toArgb

import com.itsaky.androidide.treesitter.languages.cpp.CppLanguage
import com.itsaky.androidide.treesitter.languages.java.JavaLanguage
import com.itsaky.androidide.treesitter.languages.json.JsonLanguage
import com.itsaky.androidide.treesitter.languages.kotlin.KotlinLanguage
import com.itsaky.androidide.treesitter.languages.log.LogLanguage
import com.itsaky.androidide.treesitter.languages.xml.XmlLanguage

import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes

import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.treesitter.TreeLanguage
import io.github.rosemoe.sora.langs.treesitter.theme.Theme
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
            val tsLanguage = when (languageType) {
                EditorLanguageType.JAVA -> JavaLanguage.INSTANCE
                EditorLanguageType.KOTLIN -> KotlinLanguage.INSTANCE
                EditorLanguageType.XML -> XmlLanguage.INSTANCE
                EditorLanguageType.JSON -> JsonLanguage.INSTANCE
                EditorLanguageType.CPP, EditorLanguageType.C -> CppLanguage.INSTANCE
                EditorLanguageType.LOG -> LogLanguage.INSTANCE
                else -> null
            }

            if (tsLanguage != null) {
                // Wir erstellen eine TreeSitter Theme Spezifikation basierend auf unserem EditorTheme
                val treeSitterTheme = createTreeSitterTheme(currentEditorTheme)

                // Initialisiere die TreeLanguage mit dem Parser und dem Theme
                val language = TreeLanguage(tsLanguage, treeSitterTheme)

                // Optional: TabSize konfigurieren, falls nötig
                language.tabSize = 4

                language
            } else {
                EmptyLanguage()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            EmptyLanguage()
        }
    }

    override fun supportsLanguage(languageType: EditorLanguageType): Boolean {
        return supportedLanguages.contains(languageType)
    }

    /**
   * Erstellt ein TreeSitter Theme Objekt, das die Farben aus unserem EditorTheme mapped.
   * Dies behebt das Problem, dass TreeSitter Tokens unsichtbar sind (weil sie keine Farbe zugewiesen bekommen).
   */
    private fun createTreeSitterTheme(editorTheme: EditorTheme): Theme {
        val theme = Theme()

        // Helper für ARGB Konvertierung
        val color = {
            c: androidx.compose.ui.graphics.Color -> c.toArgb() }
            val syntax = editorTheme.syntaxColors

            // Basis Mapping
            // Wir mappen generische TreeSitter Scopes auf unsere definierten Farben

            // Keywords
            theme.putStyle("keyword", color(syntax.keyword))
            theme.putStyle("include", color(syntax.keyword))
            theme.putStyle("constructor", color(syntax.keyword))
            theme.putStyle("keyword.return", color(syntax.keyword))
            theme.putStyle("keyword.operator", color(syntax.operator))
            theme.putStyle("operator", color(syntax.operator))

            // Literals (Strings, Numbers, Booleans)
            theme.putStyle("string", color(syntax.string))
            theme.putStyle("string.literal", color(syntax.string))
            theme.putStyle("number", color(syntax.number))
            theme.putStyle("boolean", color(syntax.constant))
            theme.putStyle("constant", color(syntax.constant))
            theme.putStyle("constant.builtin", color(syntax.constant))

            // Comments
            theme.putStyle("comment", color(syntax.comment))

            // Functions & Methods
            theme.putStyle("function", color(syntax.function))
            theme.putStyle("function.method", color(syntax.function))
            theme.putStyle("function.call", color(syntax.function))
            theme.putStyle("method", color(syntax.function))

            // Variables & Properties
            theme.putStyle("variable", color(syntax.variable))
            theme.putStyle("variable.parameter", color(syntax.variable))
            theme.putStyle("variable.field", color(syntax.variable))
            theme.putStyle("property", color(syntax.property))
            theme.putStyle("field", color(syntax.property))

            // Types
            theme.putStyle("type", color(syntax.type))
            theme.putStyle("type.builtin", color(syntax.keyword)) // int, boolean etc oft wie keywords
            theme.putStyle("class", color(syntax.type))

            // Markup / XML
            theme.putStyle("tag", color(syntax.tag))
            theme.putStyle("attribute", color(syntax.attribute))

            // Annotations
            theme.putStyle("annotation", color(syntax.annotation))
            theme.putStyle("attribute", color(syntax.annotation)) // manchmal als attributes geparsed

            // Punctuation (Klammern etc.) - Nehmen wir meist die Foreground Farbe oder Operator
            theme.putStyle("punctuation", color(editorTheme.foregroundColor))
            theme.putStyle("punctuation.bracket", color(editorTheme.foregroundColor))
            theme.putStyle("punctuation.delimiter", color(editorTheme.foregroundColor))

            // ERROR Handling - damit Fehler rot markiert werden
            theme.putStyle("error", color(syntax.error))

            return theme
        }
}
