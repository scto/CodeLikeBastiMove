package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class SelectAllAction : EditorAction() {
    override val id: String = "editor.selectAll"
    override val name: String = "Select All"
    override val description: String = "Select all text in the editor"
    override val icon: String = "select_all"

    override val defaultKeybinding: Keybinding = Keybinding("A", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        editor.selectAll()
        return ActionResult.Success("All text selected")
    }
}
