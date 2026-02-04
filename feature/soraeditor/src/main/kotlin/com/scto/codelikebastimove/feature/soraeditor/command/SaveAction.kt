package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class SaveAction : EditorAction() {
    override val id: String = "editor.save"
    override val name: String = "Save"
    override val description: String = "Save the current file"
    override val icon: String = "save"
    override val category: ActionCategory = ActionCategory.FILE

    override val defaultKeybinding: Keybinding = Keybinding("S", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        EditorActionContext.onSaveRequested?.invoke(editor)
        return ActionResult.Success("Save requested")
    }
}
