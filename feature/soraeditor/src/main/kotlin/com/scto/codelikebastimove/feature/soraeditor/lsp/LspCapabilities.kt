package com.scto.codelikebastimove.feature.soraeditor.lsp

data class LspCapabilities(
    val formattingSupported: Boolean = false,
    val rangeFormattingSupported: Boolean = false,
    val definitionSupported: Boolean = false,
    val referencesSupported: Boolean = false,
    val renameSupported: Boolean = false,
    val completionSupported: Boolean = false,
    val hoverSupported: Boolean = false,
    val signatureHelpSupported: Boolean = false,
    val documentSymbolSupported: Boolean = false,
    val workspaceSymbolSupported: Boolean = false,
    val codeActionSupported: Boolean = false,
    val codeLensSupported: Boolean = false,
    val diagnosticsSupported: Boolean = false,
    val semanticTokensSupported: Boolean = false,
) {
    companion object {
        val NONE = LspCapabilities()
        val ALL = LspCapabilities(
            formattingSupported = true,
            rangeFormattingSupported = true,
            definitionSupported = true,
            referencesSupported = true,
            renameSupported = true,
            completionSupported = true,
            hoverSupported = true,
            signatureHelpSupported = true,
            documentSymbolSupported = true,
            workspaceSymbolSupported = true,
            codeActionSupported = true,
            codeLensSupported = true,
            diagnosticsSupported = true,
            semanticTokensSupported = true,
        )
    }
}

data class LspPosition(
    val line: Int,
    val character: Int,
)

data class LspRange(
    val start: LspPosition,
    val end: LspPosition,
)

data class LspLocation(
    val uri: String,
    val range: LspRange,
)

data class LspTextEdit(
    val range: LspRange,
    val newText: String,
)

data class LspDiagnostic(
    val range: LspRange,
    val severity: DiagnosticSeverity,
    val code: String?,
    val source: String?,
    val message: String,
)

enum class DiagnosticSeverity(val value: Int) {
    ERROR(1),
    WARNING(2),
    INFORMATION(3),
    HINT(4),
}

data class LspSymbol(
    val name: String,
    val kind: SymbolKind,
    val location: LspLocation,
    val containerName: String? = null,
)

enum class SymbolKind(val value: Int) {
    FILE(1),
    MODULE(2),
    NAMESPACE(3),
    PACKAGE(4),
    CLASS(5),
    METHOD(6),
    PROPERTY(7),
    FIELD(8),
    CONSTRUCTOR(9),
    ENUM(10),
    INTERFACE(11),
    FUNCTION(12),
    VARIABLE(13),
    CONSTANT(14),
    STRING(15),
    NUMBER(16),
    BOOLEAN(17),
    ARRAY(18),
    OBJECT(19),
    KEY(20),
    NULL(21),
    ENUM_MEMBER(22),
    STRUCT(23),
    EVENT(24),
    OPERATOR(25),
    TYPE_PARAMETER(26),
}

data class LspCompletionItem(
    val label: String,
    val kind: CompletionItemKind?,
    val detail: String?,
    val documentation: String?,
    val insertText: String?,
    val textEdit: LspTextEdit?,
)

enum class CompletionItemKind(val value: Int) {
    TEXT(1),
    METHOD(2),
    FUNCTION(3),
    CONSTRUCTOR(4),
    FIELD(5),
    VARIABLE(6),
    CLASS(7),
    INTERFACE(8),
    MODULE(9),
    PROPERTY(10),
    UNIT(11),
    VALUE(12),
    ENUM(13),
    KEYWORD(14),
    SNIPPET(15),
    COLOR(16),
    FILE(17),
    REFERENCE(18),
    FOLDER(19),
    ENUM_MEMBER(20),
    CONSTANT(21),
    STRUCT(22),
    EVENT(23),
    OPERATOR(24),
    TYPE_PARAMETER(25),
}
