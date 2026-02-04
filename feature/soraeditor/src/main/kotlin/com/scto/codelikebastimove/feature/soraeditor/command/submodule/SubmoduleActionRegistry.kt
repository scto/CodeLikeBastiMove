package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionDescriptor
import com.scto.codelikebastimove.core.actions.api.action.KeybindingContribution

object SubmoduleActionRegistry {
    private val actions = mutableMapOf<String, SubmoduleAction>()

    val allActions: List<SubmoduleAction>
        get() = actions.values.toList()

    init {
        registerDefaults()
    }

    private fun registerDefaults() {
        register(SubmoduleInitAction())
        register(SubmoduleUpdateAction())
        register(SubmoduleAddAction())
        register(SubmoduleSyncAction())
        register(SubmoduleRemoveAction())
        register(SubmoduleStatusAction())
        register(SubmoduleForeachAction())
    }

    fun register(action: SubmoduleAction) {
        actions[action.id] = action
    }

    fun unregister(actionId: String) {
        actions.remove(actionId)
    }

    fun getAction(actionId: String): SubmoduleAction? = actions[actionId]

    @Suppress("UNCHECKED_CAST")
    fun <T : SubmoduleAction> getActionAs(actionId: String): T? = actions[actionId] as? T

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

    fun getActionsForCategory(category: ActionCategory): List<SubmoduleAction> {
        return actions.values.filter { it.category == category }
    }

    fun setActiveProject(projectPath: String?) {
        SubmoduleActionContext.setActiveProject(projectPath)
    }

    fun setOnSubmoduleInitRequested(callback: (String) -> Unit) {
        SubmoduleActionContext.onSubmoduleInitRequested = callback
    }

    fun setOnSubmoduleUpdateRequested(callback: (String) -> Unit) {
        SubmoduleActionContext.onSubmoduleUpdateRequested = callback
    }

    fun setOnSubmoduleAddRequested(callback: (String, String, String?) -> Unit) {
        SubmoduleActionContext.onSubmoduleAddRequested = callback
    }

    fun setOnSubmoduleRemoveRequested(callback: (String, String) -> Unit) {
        SubmoduleActionContext.onSubmoduleRemoveRequested = callback
    }

    fun setOnSubmoduleSyncRequested(callback: (String) -> Unit) {
        SubmoduleActionContext.onSubmoduleSyncRequested = callback
    }

    fun setOnSubmoduleStatusRequested(callback: (String) -> Unit) {
        SubmoduleActionContext.onSubmoduleStatusRequested = callback
    }

    fun setOnSubmoduleListReceived(callback: (List<SubmoduleInfo>) -> Unit) {
        SubmoduleActionContext.onSubmoduleListReceived = callback
    }

    fun setOnSubmoduleError(callback: (String) -> Unit) {
        SubmoduleActionContext.onSubmoduleError = callback
    }

    fun getSubmoduleContribution(): SubmoduleActionContribution = SubmoduleActionContribution

    fun buildActionContext(): ActionContext {
        return SubmoduleActionContext.buildActionContext()
    }
}
