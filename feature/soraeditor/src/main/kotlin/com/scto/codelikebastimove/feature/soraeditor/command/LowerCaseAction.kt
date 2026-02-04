package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class LowerCaseAction : EditorAction() {
    override val id: String = "editor.lowercase"
    override val name: String = "Transform to Lowercase"
    override val description: String = "Transform selected text to lowercase"
    override val icon: String = "letters"

    override val defaultKeybinding: Keybinding = Keybinding("L", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val selectedText = editor.getSelectedText()
        return if (selectedText.isNotEmpty()) {
            editor.replaceSelection(selectedText.lowercase())
            ActionResult.Success("Text transformed to lowercase")
        } else {
            ActionResult.Failure("No text selected")
        }
    }

    override val isEnabled: Boolean
        get() = EditorActionContext.currentEditor?.hasSelection() == true
}
