package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

class SearchAction : EditorAction() {
    override val id: String = "editor.search"
    override val name: String = "Search"
    override val description: String = "Search in the editor"
    override val icon: String = "search"
    override val category: ActionCategory = ActionCategory.SEARCH

    override val defaultKeybinding: Keybinding = Keybinding("F", setOf(KeyModifier.CTRL))

    override suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult {
        val selectedText = editor.getSelectedText()
        EditorActionContext.onSearchRequested?.invoke(selectedText, editor)
        return ActionResult.Success("Search panel opened", mapOf("keyword" to selectedText))
    }
}
