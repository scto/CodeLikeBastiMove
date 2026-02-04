package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.lsp.BaseLspConnector
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspContext
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspPosition
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspTextEdit
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RenameSymbolAction : LspAction() {
    override val id: String = "lsp.renameSymbol"
    override val name: String = "Rename Symbol"
    override val description: String = "Rename the symbol at cursor across all files"
    override val icon: String = "manage_search"

    override val defaultKeybinding: Keybinding = Keybinding("F2")

    override fun isSupported(connector: BaseLspConnector): Boolean {
        return connector.isRenameSymbolSupported()
    }

    override suspend fun executeWithLsp(
        editor: SoraEditorView,
        connector: BaseLspConnector,
        uri: String,
        context: ActionContext
    ): ActionResult {
        val (line, column) = editor.getCursorPosition()
        val position = LspPosition(line, column)

        editor.selectCurrentWord()
        val currentName = editor.getSelectedText()

        if (currentName.isEmpty()) {
            return ActionResult.Failure("No symbol at cursor position")
        }

        val newName = suspendCoroutine { continuation ->
            LspContext.showRenameDialog(currentName) { name ->
                continuation.resume(name)
            }
        }

        if (newName.isEmpty() || newName == currentName) {
            return ActionResult.Cancelled
        }

        val edits = connector.renameSymbol(uri, position, newName)

        return if (edits != null && edits.isNotEmpty()) {
            var totalEdits = 0
            edits.forEach { (fileUri, fileEdits) ->
                if (fileUri == uri) {
                    applyEdits(editor, fileEdits)
                }
                totalEdits += fileEdits.size
            }
            ActionResult.Success(
                "Renamed '$currentName' to '$newName' ($totalEdits occurrence(s))",
                mapOf("oldName" to currentName, "newName" to newName, "editCount" to totalEdits)
            )
        } else {
            ActionResult.Failure("Failed to rename symbol")
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
