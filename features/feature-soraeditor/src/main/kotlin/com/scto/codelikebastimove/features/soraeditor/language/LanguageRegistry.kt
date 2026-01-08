package com.scto.codelikebastimove.features.soraeditor.language

import android.content.Context
import com.scto.codelikebastimove.features.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.features.soraeditor.model.HighlightingMode
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language

interface LanguageProvider {
    fun createLanguage(context: Context, languageType: EditorLanguageType): Language
    fun supportsLanguage(languageType: EditorLanguageType): Boolean
}

class LanguageRegistry(private val context: Context) {
    
    private val textMateProvider = TextMateLanguageProvider()
    private val treeSitterProvider = TreeSitterLanguageProvider()
    
    fun getLanguage(
        languageType: EditorLanguageType,
        mode: HighlightingMode
    ): Language {
        return when (mode) {
            HighlightingMode.TEXTMATE -> {
                if (textMateProvider.supportsLanguage(languageType)) {
                    textMateProvider.createLanguage(context, languageType)
                } else {
                    EmptyLanguage()
                }
            }
            HighlightingMode.TREESITTER -> {
                if (treeSitterProvider.supportsLanguage(languageType)) {
                    treeSitterProvider.createLanguage(context, languageType)
                } else if (textMateProvider.supportsLanguage(languageType)) {
                    textMateProvider.createLanguage(context, languageType)
                } else {
                    EmptyLanguage()
                }
            }
            HighlightingMode.SIMPLE -> {
                EmptyLanguage()
            }
        }
    }
    
    fun getSupportedLanguages(mode: HighlightingMode): List<EditorLanguageType> {
        return when (mode) {
            HighlightingMode.TEXTMATE -> EditorLanguageType.entries.filter { 
                textMateProvider.supportsLanguage(it) 
            }
            HighlightingMode.TREESITTER -> EditorLanguageType.entries.filter { 
                treeSitterProvider.supportsLanguage(it) 
            }
            HighlightingMode.SIMPLE -> EditorLanguageType.entries
        }
    }
}
