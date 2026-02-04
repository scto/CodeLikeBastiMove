package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class ReplaceAction : EditorAction() {
    override val id: String = "editor.replace"
    override val name: String = "Replace"
    override val description: String = "Find and replace in the editor"
    override val icon: String = "find_replace"
    override val category: ActionCategory = ActionCategory.SEARCH

    override val defaultKeybinding: Keybinding = Keybinding("H", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val selectedText = editor.getSelectedText()
        EditorActionContext.onReplaceRequested?.invoke(selectedText, editor)
        return ActionResult.Success("Replace panel opened", mapOf("keyword" to selectedText))
    }
}
