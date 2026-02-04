package com.scto.codelikebastimove.feature.soraeditor.lsp

import kotlinx.coroutines.flow.StateFlow

interface BaseLspConnector {
    val isConnected: StateFlow<Boolean>
    val capabilities: StateFlow<LspCapabilities>
    val diagnostics: StateFlow<List<LspDiagnostic>>

    suspend fun connect(): Boolean
    suspend fun disconnect()

    fun isFormattingSupported(): Boolean = capabilities.value.formattingSupported
    fun isRangeFormattingSupported(): Boolean = capabilities.value.rangeFormattingSupported
    fun isGoToDefinitionSupported(): Boolean = capabilities.value.definitionSupported
    fun isGoToReferencesSupported(): Boolean = capabilities.value.referencesSupported
    fun isRenameSymbolSupported(): Boolean = capabilities.value.renameSupported
    fun isCompletionSupported(): Boolean = capabilities.value.completionSupported
    fun isHoverSupported(): Boolean = capabilities.value.hoverSupported
    fun isDiagnosticsSupported(): Boolean = capabilities.value.diagnosticsSupported

    suspend fun formatDocument(uri: String, content: String): List<LspTextEdit>?
    suspend fun formatRange(uri: String, content: String, range: LspRange): List<LspTextEdit>?
    suspend fun goToDefinition(uri: String, position: LspPosition): List<LspLocation>?
    suspend fun findReferences(uri: String, position: LspPosition): List<LspLocation>?
    suspend fun renameSymbol(uri: String, position: LspPosition, newName: String): Map<String, List<LspTextEdit>>?
    suspend fun getCompletions(uri: String, position: LspPosition): List<LspCompletionItem>?
    suspend fun getHover(uri: String, position: LspPosition): String?
    suspend fun getDocumentSymbols(uri: String): List<LspSymbol>?

    fun didOpen(uri: String, languageId: String, version: Int, content: String)
    fun didChange(uri: String, version: Int, content: String)
    fun didSave(uri: String)
    fun didClose(uri: String)
}

abstract class AbstractLspConnector : BaseLspConnector {
    override suspend fun formatDocument(uri: String, content: String): List<LspTextEdit>? = null
    override suspend fun formatRange(uri: String, content: String, range: LspRange): List<LspTextEdit>? = null
    override suspend fun goToDefinition(uri: String, position: LspPosition): List<LspLocation>? = null
    override suspend fun findReferences(uri: String, position: LspPosition): List<LspLocation>? = null
    override suspend fun renameSymbol(uri: String, position: LspPosition, newName: String): Map<String, List<LspTextEdit>>? = null
    override suspend fun getCompletions(uri: String, position: LspPosition): List<LspCompletionItem>? = null
    override suspend fun getHover(uri: String, position: LspPosition): String? = null
    override suspend fun getDocumentSymbols(uri: String): List<LspSymbol>? = null

    override fun didOpen(uri: String, languageId: String, version: Int, content: String) {}
    override fun didChange(uri: String, version: Int, content: String) {}
    override fun didSave(uri: String) {}
    override fun didClose(uri: String) {}
}
