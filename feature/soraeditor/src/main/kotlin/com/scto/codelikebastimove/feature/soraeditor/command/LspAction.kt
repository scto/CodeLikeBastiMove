package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.feature.soraeditor.lsp.BaseLspConnector
import com.scto.codelikebastimove.feature.soraeditor.lsp.LspContext
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

abstract class LspAction : EditorAction() {
    override val category: ActionCategory = ActionCategory.REFACTOR

    abstract fun isSupported(connector: BaseLspConnector): Boolean

    abstract suspend fun executeWithLsp(
        editor: SoraEditorView,
        connector: BaseLspConnector,
        uri: String,
        context: ActionContext
    ): ActionResult

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val connector = LspContext.getCurrentConnector()
            ?: return ActionResult.Failure("No LSP server connected")

        if (!isSupported(connector)) {
            return ActionResult.Failure("This operation is not supported by the current language server")
        }

        val filePath = context.filePath ?: LspContext.getCurrentFilePath()
            ?: return ActionResult.Failure("No file path available")
        val uri = LspContext.pathToUri(filePath)
        return executeWithLsp(editor, connector, uri, context)
    }

    override fun canExecute(context: ActionContext): Boolean {
        val connector = LspContext.getCurrentConnector() ?: return false
        return context.editorFocus && isSupported(connector)
    }

    override val isEnabled: Boolean
        get() {
            val connector = LspContext.getCurrentConnector() ?: return false
            return isSupported(connector)
        }
}
