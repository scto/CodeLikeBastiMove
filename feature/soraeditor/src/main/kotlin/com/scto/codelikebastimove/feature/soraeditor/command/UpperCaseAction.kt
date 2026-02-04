package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class UpperCaseAction : EditorAction() {
    override val id: String = "editor.uppercase"
    override val name: String = "Transform to Uppercase"
    override val description: String = "Transform selected text to uppercase"
    override val icon: String = "letters"

    override val defaultKeybinding: Keybinding = Keybinding("U", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val selectedText = editor.getSelectedText()
        return if (selectedText.isNotEmpty()) {
            editor.replaceSelection(selectedText.uppercase())
            ActionResult.Success("Text transformed to uppercase")
        } else {
            ActionResult.Failure("No text selected")
        }
    }

    override val isEnabled: Boolean
        get() = EditorActionContext.currentEditor?.hasSelection() == true
}
