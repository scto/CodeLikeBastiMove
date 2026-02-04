package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class UndoAction : EditorAction() {
    override val id: String = "editor.undo"
    override val name: String = "Undo"
    override val description: String = "Undo the last edit"
    override val icon: String = "undo"

    override val defaultKeybinding: Keybinding = Keybinding("Z", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        return if (editor.canUndo()) {
            editor.undo()
            ActionResult.Success("Undo successful")
        } else {
            ActionResult.Failure("Nothing to undo")
        }
    }

    override val isEnabled: Boolean
        get() = EditorActionContext.currentEditor?.canUndo() == true
}
