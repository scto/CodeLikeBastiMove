package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.lsp.BaseLspConnector
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspContext
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class FormatDocumentAction : LspAction() {
    override val id: String = "lsp.formatDocument"
    override val name: String = "Format Document"
    override val description: String = "Format the entire document using the language server"
    override val icon: String = "auto_fix"

    override val defaultKeybinding: Keybinding = Keybinding("F", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))

    override fun isSupported(connector: BaseLspConnector): Boolean {
        return connector.isFormattingSupported()
    }

    override suspend fun executeWithLsp(
        editor: SoraEditorView,
        connector: BaseLspConnector,
        uri: String,
        context: ActionContext
    ): ActionResult {
        val content = editor.getText()
        val edits = connector.formatDocument(uri, content)

        return if (edits != null && edits.isNotEmpty()) {
            applyEdits(editor, edits)
            ActionResult.Success("Document formatted successfully")
        } else if (edits != null) {
            ActionResult.Success("Document is already formatted")
        } else {
            ActionResult.Failure("Failed to format document")
        }
    }

    private fun applyEdits(editor: SoraEditorView, edits: List<com.scto.codelikebastimove.feature.soraeditor.lsp.LspTextEdit>) {
        val sortedEdits = edits.sortedByDescending { it.range.start.line * 10000 + it.range.start.character }
        for (edit in sortedEdits) {
            val codeEditor = editor.getCodeEditor()
            codeEditor.text.replace(
                edit.range.start.line,
                edit.range.start.character,
                edit.range.end.line,
                edit.range.end.character,
                edit.newText
            )
        }
    }
}
