package com.scto.codelikebastimove.features.soraeditor.plugin.language

import com.scto.codelikebastimove.core.plugin.api.extension.Extension
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionPointDescriptor
import kotlinx.coroutines.flow.StateFlow

interface LanguagePackPlugin : Extension {
    val languageId: String
    val languageName: String
    val aliases: List<String> get() = emptyList()
    val fileExtensions: List<String>
    val mimeTypes: List<String> get() = emptyList()
    val firstLine: String? get() = null
    val icon: String? get() = null
    val version: String get() = "1.0.0"
    
    fun getLanguageConfiguration(): LanguageConfiguration
    
    fun getGrammarDefinition(): GrammarDefinition?
    
    fun getTreeSitterQueries(): TreeSitterQueries?
    
    fun getSnippets(): List<Snippet> get() = emptyList()
    
    fun getCompletionProvider(): CompletionProvider? get() = null
    
    fun getHoverProvider(): HoverProvider? get() = null
    
    fun getFormattingProvider(): FormattingProvider? get() = null
}

data class LanguageConfiguration(
    val comments: CommentConfiguration? = null,
    val brackets: List<BracketPair> = emptyList(),
    val autoClosingPairs: List<AutoClosePair> = emptyList(),
    val surroundingPairs: List<SurroundPair> = emptyList(),
    val folding: FoldingConfiguration? = null,
    val wordPattern: String? = null,
    val indentationRules: IndentationRules? = null,
    val onEnterRules: List<OnEnterRule> = emptyList()
)

data class CommentConfiguration(
    val lineComment: String? = null,
    val blockComment: Pair<String, String>? = null
)

data class BracketPair(
    val open: String,
    val close: String
)

data class AutoClosePair(
    val open: String,
    val close: String,
    val notIn: List<String> = emptyList()
)

data class SurroundPair(
    val open: String,
    val close: String
)

data class FoldingConfiguration(
    val markers: FoldingMarkers? = null,
    val offSide: Boolean = false
)

data class FoldingMarkers(
    val start: String,
    val end: String
)

data class IndentationRules(
    val increaseIndentPattern: String,
    val decreaseIndentPattern: String,
    val indentNextLinePattern: String? = null,
    val unIndentedLinePattern: String? = null
)

data class OnEnterRule(
    val beforeText: String,
    val afterText: String? = null,
    val previousLineText: String? = null,
    val action: EnterAction
)

data class EnterAction(
    val indentAction: IndentAction,
    val appendText: String? = null,
    val removeText: Int? = null
)

enum class IndentAction {
    NONE, INDENT, INDENT_OUTDENT, OUTDENT
}

data class GrammarDefinition(
    val scopeName: String,
    val patterns: List<GrammarPattern>,
    val repository: Map<String, GrammarRule> = emptyMap(),
    val embeddedLanguages: Map<String, String> = emptyMap()
)

data class GrammarPattern(
    val include: String? = null,
    val match: String? = null,
    val begin: String? = null,
    val end: String? = null,
    val name: String? = null,
    val captures: Map<Int, CaptureRule>? = null,
    val beginCaptures: Map<Int, CaptureRule>? = null,
    val endCaptures: Map<Int, CaptureRule>? = null,
    val patterns: List<GrammarPattern>? = null
)

data class GrammarRule(
    val patterns: List<GrammarPattern>
)

data class CaptureRule(
    val name: String
)

data class TreeSitterQueries(
    val highlights: String,
    val locals: String? = null,
    val injections: String? = null,
    val folds: String? = null,
    val indents: String? = null
)

data class Snippet(
    val name: String,
    val prefix: String,
    val body: List<String>,
    val description: String = "",
    val scope: String? = null
)

interface CompletionProvider {
    suspend fun provideCompletions(
        content: String,
        line: Int,
        column: Int,
        triggerCharacter: Char?
    ): List<CompletionItem>
}

data class CompletionItem(
    val label: String,
    val kind: CompletionItemKind,
    val detail: String? = null,
    val documentation: String? = null,
    val insertText: String? = null,
    val insertTextFormat: InsertTextFormat = InsertTextFormat.PLAIN_TEXT,
    val filterText: String? = null,
    val sortText: String? = null,
    val preselect: Boolean = false
)

enum class CompletionItemKind {
    TEXT, METHOD, FUNCTION, CONSTRUCTOR, FIELD, VARIABLE, CLASS, INTERFACE,
    MODULE, PROPERTY, UNIT, VALUE, ENUM, KEYWORD, SNIPPET, COLOR, FILE,
    REFERENCE, FOLDER, ENUM_MEMBER, CONSTANT, STRUCT, EVENT, OPERATOR, TYPE_PARAMETER
}

enum class InsertTextFormat {
    PLAIN_TEXT, SNIPPET
}

interface HoverProvider {
    suspend fun provideHover(content: String, line: Int, column: Int): HoverInfo?
}

data class HoverInfo(
    val contents: List<String>,
    val range: TextRange? = null
)

data class TextRange(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int
)

interface FormattingProvider {
    suspend fun formatDocument(content: String): String
    
    suspend fun formatRange(content: String, range: TextRange): String
}

interface LanguagePackRegistry {
    val languages: StateFlow<List<LanguagePackPlugin>>
    
    fun registerLanguage(plugin: LanguagePackPlugin): Boolean
    
    fun unregisterLanguage(languageId: String): Boolean
    
    fun getLanguage(languageId: String): LanguagePackPlugin?
    
    fun getLanguageByExtension(extension: String): LanguagePackPlugin?
    
    fun getLanguageByMimeType(mimeType: String): LanguagePackPlugin?
    
    fun getLanguages(): List<LanguagePackPlugin>
}

abstract class AbstractLanguagePackPlugin : LanguagePackPlugin {
    override val description: String get() = "Language support for $languageName"
    override val priority: Int get() = 0
    
    override fun getTreeSitterQueries(): TreeSitterQueries? = null
    override fun getSnippets(): List<Snippet> = emptyList()
    override fun getCompletionProvider(): CompletionProvider? = null
    override fun getHoverProvider(): HoverProvider? = null
    override fun getFormattingProvider(): FormattingProvider? = null
}

object LanguagePackExtensionPoint {
    val DESCRIPTOR = ExtensionPointDescriptor(
        id = "com.scto.clbm.extension.languagePacks",
        name = "Language Packs",
        extensionClass = LanguagePackPlugin::class,
        description = "Language support including syntax highlighting, snippets, and language features",
        allowMultiple = true
    )
}
