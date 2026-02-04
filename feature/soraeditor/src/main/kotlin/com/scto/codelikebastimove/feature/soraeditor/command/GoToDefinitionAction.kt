package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.lsp.BaseLspConnector
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspContext
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspPosition
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class GoToDefinitionAction : LspAction() {
    override val id: String = "lsp.goToDefinition"
    override val name: String = "Go to Definition"
    override val description: String = "Navigate to the definition of the symbol at cursor"
    override val icon: String = "jump_to_element"
    override val category: ActionCategory = ActionCategory.NAVIGATION

    override val defaultKeybinding: Keybinding = Keybinding("F12")

    override fun isSupported(connector: BaseLspConnector): Boolean {
        return connector.isGoToDefinitionSupported()
    }

    override suspend fun executeWithLsp(
        editor: SoraEditorView,
        connector: BaseLspConnector,
        uri: String,
        context: ActionContext
    ): ActionResult {
        val (line, column) = editor.getCursorPosition()
        val position = LspPosition(line, column)

        val locations = connector.goToDefinition(uri, position)

        return when {
            locations == null -> ActionResult.Failure("Failed to find definition")
            locations.isEmpty() -> ActionResult.Failure("No definition found")
            locations.size == 1 -> {
                LspContext.notifyGoToLocation(locations.first())
                ActionResult.Success("Navigating to definition")
            }
            else -> {
                LspContext.notifyShowReferences(locations)
                ActionResult.Success("Multiple definitions found", mapOf("count" to locations.size))
            }
        }
    }
}
