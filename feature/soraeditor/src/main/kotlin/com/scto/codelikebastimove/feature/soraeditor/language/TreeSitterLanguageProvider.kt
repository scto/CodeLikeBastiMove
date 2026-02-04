package com.scto.codelikebastimove.feature.soraeditor.language

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.itsaky.androidide.treesitter.TSLanguage
import com.itsaky.androidide.treesitter.java.TSLanguageJava
import com.itsaky.androidide.treesitter.json.TSLanguageJson
import com.itsaky.androidide.treesitter.kotlin.TSLanguageKotlin
import com.itsaky.androidide.treesitter.xml.TSLanguageXml
import com.itsaky.androidide.treesitter.cpp.TSLanguageCpp
import com.itsaky.androidide.treesitter.log.TSLanguageLog
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import io.github.rosemoe.sora.editor.ts.TsLanguage
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec
import io.github.rosemoe.sora.editor.ts.TsTheme
import io.github.rosemoe.sora.editor.ts.TsThemeBuilder
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.styling.TextStyle.makeStyle
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
            createLanguageForType(context, languageType)
        } catch (e: Exception) {
            e.printStackTrace()
            EmptyLanguage()
        }
    }

    private fun createLanguageForType(context: Context, languageType: EditorLanguageType): Language {
        val tsLanguage = getTsLanguage(languageType) ?: return EmptyLanguage()
        val queryDir = getQueryDirectory(languageType)

        return try {
            val highlightsScm = loadQuery(context, queryDir, "highlights.scm")
            val blocksScm = loadQueryOrNull(context, queryDir, "blocks.scm")
            val bracketsScm = loadQueryOrNull(context, queryDir, "brackets.scm")
            val localsScm = loadQueryOrNull(context, queryDir, "locals.scm")

            val languageSpec = TsLanguageSpec(
                language = tsLanguage,
                highlightScmSource = highlightsScm,
                codeBlocksScmSource = blocksScm,
                bracketsScmSource = bracketsScm,
                localsScmSource = localsScm
            )

            TsLanguage(languageSpec, createTsTheme())
        } catch (e: Exception) {
            e.printStackTrace()
            EmptyLanguage()
        }
    }

    private fun getTsLanguage(languageType: EditorLanguageType): TSLanguage? {
        return when (languageType) {
            EditorLanguageType.JAVA -> TSLanguageJava.getInstance()
            EditorLanguageType.KOTLIN -> TSLanguageKotlin.getInstance()
            EditorLanguageType.XML -> TSLanguageXml.getInstance()
            EditorLanguageType.JSON -> TSLanguageJson.getInstance()
            EditorLanguageType.CPP, EditorLanguageType.C -> TSLanguageCpp.getInstance()
            EditorLanguageType.LOG -> TSLanguageLog.getInstance()
            else -> null
        }
    }

    private fun getQueryDirectory(languageType: EditorLanguageType): String {
        return when (languageType) {
            EditorLanguageType.JAVA -> "java"
            EditorLanguageType.KOTLIN -> "kotlin"
            EditorLanguageType.XML -> "xml"
            EditorLanguageType.JSON -> "json"
            EditorLanguageType.CPP, EditorLanguageType.C -> "cpp"
            EditorLanguageType.LOG -> "log"
            else -> "java"
        }
    }

    private fun loadQuery(context: Context, queryDir: String, fileName: String): String {
        return context.assets.open("tree-sitter-queries/$queryDir/$fileName")
            .bufferedReader()
            .use { it.readText() }
    }

    private fun loadQueryOrNull(context: Context, queryDir: String, fileName: String): String? {
        return try {
            loadQuery(context, queryDir, fileName)
        } catch (e: Exception) {
            null
        }
    }

    private fun createTsTheme(): TsTheme {
        val theme = currentEditorTheme
        val syntax = theme.syntaxColors

        return TsThemeBuilder().apply {
            applyTo("keyword") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.function") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.import") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.type") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.return") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.conditional") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.repeat") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.exception") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.modifier") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.coroutine") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("keyword.directive") { makeStyle(syntax.keyword.toArgb()) }

            applyTo("string") { makeStyle(syntax.string.toArgb()) }
            applyTo("string.escape") { makeStyle(syntax.string.toArgb()) }
            applyTo("string.special") { makeStyle(syntax.string.toArgb()) }
            applyTo("string.regexp") { makeStyle(syntax.string.toArgb()) }
            applyTo("character") { makeStyle(syntax.string.toArgb()) }

            applyTo("number") { makeStyle(syntax.number.toArgb()) }
            applyTo("number.float") { makeStyle(syntax.number.toArgb()) }
            applyTo("boolean") { makeStyle(syntax.number.toArgb()) }

            applyTo("comment") { makeStyle(syntax.comment.toArgb()) }

            applyTo("type") { makeStyle(syntax.type.toArgb()) }
            applyTo("type.builtin") { makeStyle(syntax.type.toArgb()) }
            applyTo("type.definition") { makeStyle(syntax.type.toArgb()) }

            applyTo("function") { makeStyle(syntax.function.toArgb()) }
            applyTo("function.call") { makeStyle(syntax.function.toArgb()) }
            applyTo("function.method") { makeStyle(syntax.function.toArgb()) }
            applyTo("function.builtin") { makeStyle(syntax.function.toArgb()) }
            applyTo("constructor") { makeStyle(syntax.function.toArgb()) }

            applyTo("variable") { makeStyle(syntax.variable.toArgb()) }
            applyTo("variable.builtin") { makeStyle(syntax.variable.toArgb()) }
            applyTo("variable.parameter") { makeStyle(syntax.parameter.toArgb()) }
            applyTo("variable.member") { makeStyle(syntax.property.toArgb()) }
            applyTo("variable.field") { makeStyle(syntax.property.toArgb()) }

            applyTo("constant") { makeStyle(syntax.constant.toArgb()) }
            applyTo("constant.builtin") { makeStyle(syntax.constant.toArgb()) }

            applyTo("operator") { makeStyle(syntax.operator.toArgb()) }

            applyTo("attribute") { makeStyle(syntax.annotation.toArgb()) }

            applyTo("tag") { makeStyle(syntax.keyword.toArgb()) }
            applyTo("property") { makeStyle(syntax.property.toArgb()) }
            applyTo("label") { makeStyle(syntax.annotation.toArgb()) }
            applyTo("namespace") { makeStyle(syntax.type.toArgb()) }
            applyTo("module") { makeStyle(syntax.type.toArgb()) }

            applyTo("punctuation.bracket") { makeStyle(syntax.punctuation.toArgb()) }
            applyTo("punctuation.delimiter") { makeStyle(syntax.punctuation.toArgb()) }
            applyTo("punctuation.special") { makeStyle(syntax.punctuation.toArgb()) }

            applyTo("error") { makeStyle(syntax.error.toArgb()) }
        }.build()
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
