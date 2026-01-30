package com.scto.codelikebastimove.feature.soraeditor.language

import android.content.Context
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import org.eclipse.tm4e.core.registry.IThemeSource

class TextMateLanguageProvider : LanguageProvider {

  private var isInitialized = false

  private val supportedLanguages =
    setOf(
      EditorLanguageType.JAVA,
      EditorLanguageType.KOTLIN,
      EditorLanguageType.XML,
      EditorLanguageType.GRADLE_GROOVY,
      EditorLanguageType.GRADLE_KOTLIN,
      EditorLanguageType.AIDL,
      EditorLanguageType.CPP,
      EditorLanguageType.C,
      EditorLanguageType.MAKEFILE,
      EditorLanguageType.LOG,
      EditorLanguageType.PROPERTIES,
      EditorLanguageType.JSON,
    )

  private fun initializeIfNeeded(context: Context) {
    if (isInitialized) return

    try {
      FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(context.assets))

      loadGrammars()
      loadThemes()

      isInitialized = true
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun loadGrammars() {
    val grammarRegistry = GrammarRegistry.getInstance()

    grammarRegistry.loadGrammars("textmate/languages.json")
  }

  private fun loadThemes() {
    val themeRegistry = ThemeRegistry.getInstance()

    try {
      val darkTheme =
        ThemeModel(
          IThemeSource.fromInputStream(
            FileProviderRegistry.getInstance()
              .tryGetInputStream("textmate/themes/dark_modern.json"),
            "textmate/themes/dark_modern.json",
            null,
          ),
          "dark_modern",
        )
      themeRegistry.loadTheme(darkTheme)

      val lightTheme =
        ThemeModel(
          IThemeSource.fromInputStream(
            FileProviderRegistry.getInstance()
              .tryGetInputStream("textmate/themes/light_modern.json"),
            "textmate/themes/light_modern.json",
            null,
          ),
          "light_modern",
        )
      themeRegistry.loadTheme(lightTheme)

      themeRegistry.setTheme("dark_modern")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun createLanguage(context: Context, languageType: EditorLanguageType): Language {
    initializeIfNeeded(context)

    return try {
      val scopeName = getTextMateScopeName(languageType)
      TextMateLanguage.create(scopeName, true)
    } catch (e: Exception) {
      e.printStackTrace()
      EmptyLanguage()
    }
  }

  override fun supportsLanguage(languageType: EditorLanguageType): Boolean {
    return supportedLanguages.contains(languageType)
  }

  private fun getTextMateScopeName(languageType: EditorLanguageType): String {
    return when (languageType) {
      EditorLanguageType.JAVA -> "source.java"
      EditorLanguageType.KOTLIN -> "source.kotlin"
      EditorLanguageType.GRADLE_KOTLIN -> "source.kotlin"
      EditorLanguageType.XML -> "text.xml"
      EditorLanguageType.GRADLE_GROOVY -> "source.groovy"
      EditorLanguageType.AIDL -> "source.java"
      EditorLanguageType.CPP -> "source.cpp"
      EditorLanguageType.C -> "source.c"
      EditorLanguageType.MAKEFILE -> "source.makefile"
      EditorLanguageType.LOG -> "text.log"
      EditorLanguageType.PROPERTIES -> "source.ini"
      EditorLanguageType.JSON -> "source.json"
      EditorLanguageType.PLAIN_TEXT -> "text.plain"
    }
  }

  fun setTheme(isDark: Boolean) {
    val themeRegistry = ThemeRegistry.getInstance()
    themeRegistry.setTheme(if (isDark) "dark_modern" else "light_modern")
  }

  fun getColorScheme(): TextMateColorScheme? {
    return try {
      TextMateColorScheme.create(ThemeRegistry.getInstance())
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}
