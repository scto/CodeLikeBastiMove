package com.scto.codelikebastimove.feature.editor

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.scto.codelikebastimove.feature.editor.highlighting.EditorLanguage
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileProvider
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

object EditorUtils {

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        // Initialize TextMate registries
        FileProviderRegistry.setFileProvider(AssetsFileProvider(context.assets))
        ThemeRegistry.getInstance().loadThemes(context.assets.open("textmate/themes/themes.json").reader())
        GrammarRegistry.getInstance().loadGrammars(context.assets.open("textmate/languages/languages.json").reader())

        // Register custom languages / overrides if needed
        // For example:
        // GrammarRegistry.getInstance().addGrammar("source.kotlin", "textmate/grammars/kotlin.tmLanguage.json")

        // Initialize Treesitter (stub)
        TreesitterRegistry.initialize(context)

        // Initialize LSP (stub)
        LspConnectionManager.initialize(context)

        isInitialized = true
    }

    object ThemeRegistry {
        private val loadedThemes = mutableMapOf<String, EditorColorScheme>()

        fun getTheme(themeName: String): EditorColorScheme? {
            return loadedThemes[themeName] ?: ThemeRegistry.getInstance().getTheme(themeName)?.let { themeModel ->
                val colorScheme = EditorColorScheme(EditorColorScheme.LIGHT).apply {
                    applyFromTheme(themeModel)
                }
                loadedThemes[themeName] = colorScheme
                colorScheme
            }
        }

        fun getAvailableThemeNames(): List<String> {
            return ThemeRegistry.getInstance().themes.map { it.name }
        }
    }

    object GrammarRegistry {
        fun getLanguage(scopeName: String): TextMateLanguage? {
            return GrammarRegistry.getInstance().getLanguage(scopeName)
        }
    }

    //region Stubs for Treesitter and LSP
    object TreesitterRegistry {
        fun initialize(context: Context) {
            // Stub: Initialize Treesitter parsers
            // e.g., TreesitterLanguage.loadGrammar(context.assets, "tree-sitter-java.wasm")
            println("TreesitterRegistry initialized (stub)")
        }

        fun getLanguage(lang: EditorLanguage): Language? {
            // Stub: Return a TreesitterLanguage instance for a given EditorLanguage
            println("Treesitter language for ${lang.displayName} requested (stub)")
            return null // Replace with actual Treesitter language instance
        }
    }

    object LspConnectionManager {
        fun initialize(context: Context) {
            // Stub: Initialize LSP client management
            println("LspConnectionManager initialized (stub)")
        }

        fun connect(editor: CodeEditor, filePath: String) {
            // Stub: Connect to LSP server for the given file
            println("LSP connection requested for $filePath (stub)")
        }

        fun disconnect(filePath: String) {
            // Stub: Disconnect from LSP server
            println("LSP disconnection requested for $filePath (stub)")
        }

        fun provideCompletion(editor: CodeEditor, line: Int, column: Int) {
            // Stub: Request LSP completion
            println("LSP completion requested at line $line, column $column (stub)")
        }

        fun provideHover(editor: CodeEditor, line: Int, column: Int) {
            // Stub: Request LSP hover info
            println("LSP hover requested at line $line, column $column (stub)")
        }
    }
    //endregion
}
