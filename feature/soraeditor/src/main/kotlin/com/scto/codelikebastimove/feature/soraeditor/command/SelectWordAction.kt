package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class SelectWordAction : EditorAction() {
    override val id: String = "editor.selectWord"
    override val name: String = "Select Word"
    override val description: String = "Select the word at the cursor position"
    override val icon: String = "select"

    override val defaultKeybinding: Keybinding = Keybinding("W", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        editor.selectCurrentWord()
        return ActionResult.Success("Word selected")
    }
}
