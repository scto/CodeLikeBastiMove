package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class RedoAction : EditorAction() {
    override val id: String = "editor.redo"
    override val name: String = "Redo"
    override val description: String = "Redo the last undone edit"
    override val icon: String = "redo"

    override val defaultKeybinding: Keybinding = Keybinding("Y", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        return if (editor.canRedo()) {
            editor.redo()
            ActionResult.Success("Redo successful")
        } else {
            ActionResult.Failure("Nothing to redo")
        }
    }

    override val isEnabled: Boolean
        get() = EditorActionContext.currentEditor?.canRedo() == true
}
