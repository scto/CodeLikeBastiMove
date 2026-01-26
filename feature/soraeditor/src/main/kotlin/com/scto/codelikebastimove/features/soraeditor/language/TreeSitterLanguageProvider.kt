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
                EditorLanguageType.CPP -> com.itsaky.androidide.treesitter.cpp.TSLanguageCpp.getInstance()
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
        group("keyword", 0xFF569CD6.toInt())
        group("type", 0xFF4EC9B0.toInt())
        group("string", 0xFFCE9178.toInt())
        group("number", 0xFFB5CEA8.toInt())
        group("comment", 0xFF6A9955.toInt())
        group("function", 0xFFDCDCAA.toInt())
        group("variable", 0xFF9CDCFE.toInt())
        group("operator", 0xFFD4D4D4.toInt())
        group("constant", 0xFF4FC1FF.toInt())
        group("property", 0xFF9CDCFE.toInt())
        group("attribute", 0xFF9CDCFE.toInt())
        group("tag", 0xFF569CD6.toInt())
        group("punctuation", 0xFFD4D4D4.toInt())
    }
    
    private fun TsThemeBuilder.applyLightTheme() {
        group("keyword", 0xFF0000FF.toInt())
        group("type", 0xFF267F99.toInt())
        group("string", 0xFFA31515.toInt())
        group("number", 0xFF098658.toInt())
        group("comment", 0xFF008000.toInt())
        group("function", 0xFF795E26.toInt())
        group("variable", 0xFF001080.toInt())
        group("operator", 0xFF000000.toInt())
        group("constant", 0xFF0070C1.toInt())
        group("property", 0xFF001080.toInt())
        group("attribute", 0xFF001080.toInt())
        group("tag", 0xFF800000.toInt())
        group("punctuation", 0xFF000000.toInt())
    }
}
