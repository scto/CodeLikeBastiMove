package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.lsp.BaseLspConnector
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspPosition
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspRange
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspTextEdit
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class FormatSelectionAction : LspAction() {
    override val id: String = "lsp.formatSelection"
    override val name: String = "Format Selection"
    override val description: String = "Format the selected text using the language server"
    override val icon: String = "auto_fix"

    override val defaultKeybinding: Keybinding = Keybinding("K", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))

    override fun isSupported(connector: BaseLspConnector): Boolean {
        return connector.isRangeFormattingSupported()
    }

    override suspend fun executeWithLsp(
        editor: SoraEditorView,
        connector: BaseLspConnector,
        uri: String,
        context: ActionContext
    ): ActionResult {
        if (!editor.hasSelection()) {
            return ActionResult.Failure("No text selected")
        }

        val cursor = editor.getCodeEditor().cursor
        val range = LspRange(
            start = LspPosition(cursor.leftLine, cursor.leftColumn),
            end = LspPosition(cursor.rightLine, cursor.rightColumn)
        )

        val content = editor.getText()
        val edits = connector.formatRange(uri, content, range)

        return if (edits != null && edits.isNotEmpty()) {
            applyEdits(editor, edits)
            ActionResult.Success("Selection formatted successfully")
        } else if (edits != null) {
            ActionResult.Success("Selection is already formatted")
        } else {
            ActionResult.Failure("Failed to format selection")
        }
    }

    private fun applyEdits(editor: SoraEditorView, edits: List<LspTextEdit>) {
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
