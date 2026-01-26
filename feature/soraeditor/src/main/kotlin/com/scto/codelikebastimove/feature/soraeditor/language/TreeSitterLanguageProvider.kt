package com.scto.codelikebastimove.feature.soraeditor.language

import android.content.Context
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language

class TreeSitterLanguageProvider : LanguageProvider {
    
    private val supportedLanguages = setOf(
        EditorLanguageType.JAVA,
        EditorLanguageType.KOTLIN,
        EditorLanguageType.XML,
        EditorLanguageType.CPP,
        EditorLanguageType.JSON
    )
    
    override fun createLanguage(context: Context, languageType: EditorLanguageType): Language {
        return EmptyLanguage()
    }
    
    override fun supportsLanguage(languageType: EditorLanguageType): Boolean {
        return supportedLanguages.contains(languageType)
    }
}
