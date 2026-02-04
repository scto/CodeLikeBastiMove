package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.AbstractAction
import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.ActionWhen
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

abstract class EditorAction : AbstractAction() {
    override val category: ActionCategory = ActionCategory.EDIT

    abstract val defaultKeybinding: Keybinding?

    open val condition: ActionWhen = ActionWhen(editorFocus = true)

    abstract suspend fun executeOnEditor(editor: SoraEditorView, context: ActionContext): ActionResult

    override suspend fun doExecute(context: ActionContext): ActionResult {
        val editor = EditorActionContext.currentEditor
            ?: return ActionResult.Failure("No active editor")
        return executeOnEditor(editor, context)
    }

    override fun canExecute(context: ActionContext): Boolean {
        return context.editorFocus && isEnabled
    }
}

object EditorActionContext {
    var currentEditor: SoraEditorView? = null
        private set

    var onSaveRequested: ((SoraEditorView) -> Unit)? = null
    var onRefreshRequested: ((SoraEditorView) -> Unit)? = null
    var onSearchRequested: ((String, SoraEditorView) -> Unit)? = null
    var onReplaceRequested: ((String, SoraEditorView) -> Unit)? = null

    private val focusListeners = mutableListOf<(SoraEditorView?) -> Unit>()

    fun setActiveEditor(editor: SoraEditorView?) {
        val previous = currentEditor
        currentEditor = editor
        if (previous != editor) {
            focusListeners.forEach { it(editor) }
        }
    }

    fun clearEditor() {
        setActiveEditor(null)
    }

    fun addFocusListener(listener: (SoraEditorView?) -> Unit) {
        focusListeners.add(listener)
    }

    fun removeFocusListener(listener: (SoraEditorView?) -> Unit) {
        focusListeners.remove(listener)
    }

    fun buildActionContext(): ActionContext {
        val editor = currentEditor
        return ActionContext(
            editorFocus = editor != null,
            editorTextFocus = editor != null,
            inputFocus = editor != null,
            selectedText = editor?.getSelectedText(),
            cursorLine = editor?.getCursorPosition()?.first ?: 0,
            cursorColumn = editor?.getCursorPosition()?.second ?: 0,
            content = editor?.getText(),
        )
    }
}

data class EditorState(
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isEditable: Boolean = true,
    val isDirty: Boolean = false,
    val isSearching: Boolean = false,
    val isReplaceShown: Boolean = false,
    val searchKeyword: String = "",
    val replaceKeyword: String = "",
)
