package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.lsp.BaseLspConnector
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspContext
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspPosition
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class GoToReferencesAction : LspAction() {
    override val id: String = "lsp.goToReferences"
    override val name: String = "Go to References"
    override val description: String = "Find all references to the symbol at cursor"
    override val icon: String = "manage_search"
    override val category: ActionCategory = ActionCategory.SEARCH

    override val defaultKeybinding: Keybinding = Keybinding("F12", setOf(KeyModifier.SHIFT))

    override fun isSupported(connector: BaseLspConnector): Boolean {
        return connector.isGoToReferencesSupported()
    }

    override suspend fun executeWithLsp(
        editor: SoraEditorView,
        connector: BaseLspConnector,
        uri: String,
        context: ActionContext
    ): ActionResult {
        val (line, column) = editor.getCursorPosition()
        val position = LspPosition(line, column)

        val locations = connector.findReferences(uri, position)

        return when {
            locations == null -> ActionResult.Failure("Failed to find references")
            locations.isEmpty() -> ActionResult.Failure("No references found")
            else -> {
                LspContext.notifyShowReferences(locations)
                ActionResult.Success("Found ${locations.size} reference(s)", mapOf("count" to locations.size))
            }
        }
    }
}
