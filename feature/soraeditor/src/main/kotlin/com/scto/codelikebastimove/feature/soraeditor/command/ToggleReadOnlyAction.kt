package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class ToggleReadOnlyAction : EditorAction() {
    override val id: String = "editor.toggleReadOnly"
    override val name: String
        get() = if (EditorActionContext.currentEditor?.isEditable() == true) "Read Mode" else "Edit Mode"
    override val description: String = "Toggle read-only mode in the editor"
    override val icon: String
        get() = if (EditorActionContext.currentEditor?.isEditable() == true) "lock" else "edit"
    override val category: ActionCategory = ActionCategory.VIEW

    override val defaultKeybinding: Keybinding = Keybinding("E", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val newValue = !editor.isEditable()
        editor.setEditable(newValue)
        return ActionResult.Success(if (newValue) "Edit mode enabled" else "Read-only mode enabled")
    }
}
