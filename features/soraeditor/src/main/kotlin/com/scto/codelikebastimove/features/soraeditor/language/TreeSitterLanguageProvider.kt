package com.scto.codelikebastimove.features.soraeditor.language

import android.content.Context
import com.scto.codelikebastimove.features.soraeditor.model.EditorLanguageType
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.treesitter.TsLanguage
import io.github.rosemoe.sora.langs.treesitter.TsLanguageSpec
import io.github.rosemoe.sora.langs.treesitter.TsTheme
import io.github.rosemoe.sora.langs.treesitter.TsThemeBuilder

class TreeSitterLanguageProvider : LanguageProvider {
    
    private val supportedLanguages = setOf(
        EditorLanguageType.JAVA,
        EditorLanguageType.KOTLIN,
        EditorLanguageType.XML,
        EditorLanguageType.CPP,
        EditorLanguageType.C,
        EditorLanguageType.MAKEFILE,
        EditorLanguageType.JSON
    )
    
    private val languageSpecs = mutableMapOf<EditorLanguageType, TsLanguageSpec?>()
    
    override fun createLanguage(context: Context, languageType: EditorLanguageType): Language {
        return try {
            val spec = getOrCreateLanguageSpec(context, languageType)
            if (spec != null) {
                TsLanguage(spec, true)
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
    
    private fun getOrCreateLanguageSpec(context: Context, languageType: EditorLanguageType): TsLanguageSpec? {
        return languageSpecs.getOrPut(languageType) {
            createLanguageSpec(context, languageType)
        }
    }
    
    private fun createLanguageSpec(context: Context, languageType: EditorLanguageType): TsLanguageSpec? {
        return try {
            val tsLanguage = getTreeSitterLanguage(languageType) ?: return null
            val highlightsQuery = loadQuery(context, languageType, "highlights.scm")
            val localsQuery = loadQuery(context, languageType, "locals.scm")
            
            TsLanguageSpec(
                tsLanguage,
                highlightsQuery,
                localsQuery
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun getTreeSitterLanguage(languageType: EditorLanguageType): com.itsaky.androidide.treesitter.TSLanguage? {
        return try {
            when (languageType) {
                EditorLanguageType.JAVA -> com.itsaky.androidide.treesitter.java.TSLanguageJava.getInstance()
                EditorLanguageType.KOTLIN -> com.itsaky.androidide.treesitter.kotlin.TSLanguageKotlin.getInstance()
                EditorLanguageType.XML -> com.itsaky.androidide.treesitter.xml.TSLanguageXml.getInstance()
                EditorLanguageType.CPP, EditorLanguageType.C -> com.itsaky.androidide.treesitter.cpp.TSLanguageCpp.getInstance()
                EditorLanguageType.MAKEFILE -> com.itsaky.androidide.treesitter.make.TSLanguageMake.getInstance()
                EditorLanguageType.JSON -> com.itsaky.androidide.treesitter.json.TSLanguageJson.getInstance()
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun loadQuery(context: Context, languageType: EditorLanguageType, queryFile: String): String {
        return try {
            val folder = getQueryFolder(languageType)
            context.assets.open("treesitter/queries/$folder/$queryFile").bufferedReader().use {
                it.readText()
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun getQueryFolder(languageType: EditorLanguageType): String {
        return when (languageType) {
            EditorLanguageType.JAVA -> "java"
            EditorLanguageType.KOTLIN -> "kotlin"
            EditorLanguageType.XML -> "xml"
            EditorLanguageType.CPP -> "cpp"
            EditorLanguageType.C -> "c"
            EditorLanguageType.MAKEFILE -> "make"
            EditorLanguageType.JSON -> "json"
            else -> "plain"
        }
    }
    
    fun createTheme(isDark: Boolean): TsTheme {
        return TsThemeBuilder().apply {
            if (isDark) {
                applyDarkTheme()
            } else {
                applyLightTheme()
            }
        }.build()
    }
    
    private fun TsThemeBuilder.applyDarkTheme() {
        sealed("keyword") { foreground = 0xFF569CD6.toInt() }
        sealed("type") { foreground = 0xFF4EC9B0.toInt() }
        sealed("string") { foreground = 0xFFCE9178.toInt() }
        sealed("number") { foreground = 0xFFB5CEA8.toInt() }
        sealed("comment") { foreground = 0xFF6A9955.toInt() }
        sealed("function") { foreground = 0xFFDCDCAA.toInt() }
        sealed("variable") { foreground = 0xFF9CDCFE.toInt() }
        sealed("operator") { foreground = 0xFFD4D4D4.toInt() }
        sealed("constant") { foreground = 0xFF4FC1FF.toInt() }
        sealed("property") { foreground = 0xFF9CDCFE.toInt() }
        sealed("attribute") { foreground = 0xFF9CDCFE.toInt() }
        sealed("tag") { foreground = 0xFF569CD6.toInt() }
        sealed("punctuation") { foreground = 0xFFD4D4D4.toInt() }
    }
    
    private fun TsThemeBuilder.applyLightTheme() {
        sealed("keyword") { foreground = 0xFF0000FF.toInt() }
        sealed("type") { foreground = 0xFF267F99.toInt() }
        sealed("string") { foreground = 0xFFA31515.toInt() }
        sealed("number") { foreground = 0xFF098658.toInt() }
        sealed("comment") { foreground = 0xFF008000.toInt() }
        sealed("function") { foreground = 0xFF795E26.toInt() }
        sealed("variable") { foreground = 0xFF001080.toInt() }
        sealed("operator") { foreground = 0xFF000000.toInt() }
        sealed("constant") { foreground = 0xFF0070C1.toInt() }
        sealed("property") { foreground = 0xFF001080.toInt() }
        sealed("attribute") { foreground = 0xFF001080.toInt() }
        sealed("tag") { foreground = 0xFF800000.toInt() }
        sealed("punctuation") { foreground = 0xFF000000.toInt() }
    }
}
