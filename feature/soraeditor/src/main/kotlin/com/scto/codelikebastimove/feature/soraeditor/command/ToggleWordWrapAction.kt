package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class ToggleWordWrapAction : EditorAction() {
    override val id: String = "editor.toggleWordWrap"
    override val name: String = "Toggle Word Wrap"
    override val description: String = "Toggle word wrap in the editor"
    override val icon: String = "edit_note"
    override val category: ActionCategory = ActionCategory.VIEW

    override val defaultKeybinding: Keybinding = Keybinding("Z", setOf(KeyModifier.ALT))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val newValue = !editor.isWordWrapEnabled()
        editor.setWordWrap(newValue)
        return ActionResult.Success(if (newValue) "Word wrap enabled" else "Word wrap disabled")
    }
}
