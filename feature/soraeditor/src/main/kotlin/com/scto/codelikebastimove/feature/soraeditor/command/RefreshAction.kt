package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class RefreshAction : EditorAction() {
    override val id: String = "editor.refresh"
    override val name: String = "Refresh"
    override val description: String = "Refresh the file content from disk"
    override val icon: String = "refresh"
    override val category: ActionCategory = ActionCategory.FILE

    override val defaultKeybinding: Keybinding = Keybinding("R", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        EditorActionContext.onRefreshRequested?.invoke(editor)
        return ActionResult.Success("Refresh requested")
    }
}
