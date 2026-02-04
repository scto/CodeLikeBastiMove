package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.Action
import com.scto.codelikebastimove.core.actions.api.action.ActionDescriptor
import com.scto.codelikebastimove.core.actions.api.action.KeybindingContribution
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

object EditorActionRegistry {
    private val actions = mutableMapOf<String, EditorAction>()

    val allActions: List<EditorAction>
        get() = actions.values.toList()

    init {
        registerDefaults()
    }

    private fun registerDefaults() {
        register(UndoAction())
        register(RedoAction())
        register(SelectAllAction())
        register(SelectWordAction())
        register(UpperCaseAction())
        register(LowerCaseAction())
        register(SearchAction())
        register(ReplaceAction())
        register(ToggleWordWrapAction())
        register(ToggleReadOnlyAction())
        register(SaveAction())
        register(RefreshAction())
    }

    fun register(action: EditorAction) {
        actions[action.id] = action
    }

    fun unregister(actionId: String) {
        actions.remove(actionId)
    }

    fun getAction(actionId: String): EditorAction? = actions[actionId]

    fun getActionDescriptors(): List<ActionDescriptor> {
        return actions.values.map { action ->
            ActionDescriptor(
                id = action.id,
                name = action.name,
                description = action.description,
                category = action.category,
                icon = action.icon,
                defaultKeybinding = action.defaultKeybinding,
                condition = action.condition,
            )
        }
    }

    fun getKeybindingContributions(): List<KeybindingContribution> {
        return actions.values.mapNotNull { action ->
            action.defaultKeybinding?.let { keybinding ->
                KeybindingContribution(
                    actionId = action.id,
                    keybinding = keybinding,
                    condition = action.condition,
                )
            }
        }
    }

    fun getActionsForCategory(category: com.scto.codelikebastimove.core.actions.api.action.ActionCategory): List<EditorAction> {
        return actions.values.filter { it.category == category }
    }

    fun setActiveEditor(editor: SoraEditorView?) {
        EditorActionContext.setActiveEditor(editor)
    }

    fun setOnSaveRequested(callback: (SoraEditorView) -> Unit) {
        EditorActionContext.onSaveRequested = callback
    }

    fun setOnRefreshRequested(callback: (SoraEditorView) -> Unit) {
        EditorActionContext.onRefreshRequested = callback
    }

    fun setOnSearchRequested(callback: (String, SoraEditorView) -> Unit) {
        EditorActionContext.onSearchRequested = callback
    }

    fun setOnReplaceRequested(callback: (String, SoraEditorView) -> Unit) {
        EditorActionContext.onReplaceRequested = callback
    }

    fun getActionContribution(): EditorActionContribution = EditorActionContribution

    fun buildActionContext(): com.scto.codelikebastimove.core.actions.api.action.ActionContext {
        return EditorActionContext.buildActionContext()
    }
}
