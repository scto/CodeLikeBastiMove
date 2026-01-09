package com.scto.codelikebastimove.core.actions.api.contribution

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.keybinding.ResolvedKeybinding

interface ActionContributionPoint {
    val id: String
    val name: String
    val description: String
}

object ActionContributionPoints {
    val EDITOR_ACTIONS = object : ActionContributionPoint {
        override val id = "com.scto.clbm.contribution.editorActions"
        override val name = "Editor Actions"
        override val description = "Actions available in the editor"
    }
    
    val COMMANDS = object : ActionContributionPoint {
        override val id = "com.scto.clbm.contribution.commands"
        override val name = "Commands"
        override val description = "Commands available in the command palette"
    }
    
    val KEYBINDINGS = object : ActionContributionPoint {
        override val id = "com.scto.clbm.contribution.keybindings"
        override val name = "Keybindings"
        override val description = "Keyboard shortcuts"
    }
    
    val MENUS = object : ActionContributionPoint {
        override val id = "com.scto.clbm.contribution.menus"
        override val name = "Menus"
        override val description = "Menu contributions"
    }
}

data class EditorActionContribution(
    val action: Action,
    val keybinding: Keybinding? = null,
    val menuContributions: List<MenuContribution> = emptyList(),
    val when: ActionWhen? = null
)

data class CommandContribution(
    val id: String,
    val title: String,
    val category: String = "General",
    val icon: String? = null,
    val enablement: ActionWhen? = null,
    val handler: suspend (ActionContext) -> ActionResult
)

data class MenuItemContribution(
    val commandId: String,
    val group: String? = null,
    val order: Int = 0,
    val when: ActionWhen? = null
)

data class SubMenuContribution(
    val id: String,
    val label: String,
    val icon: String? = null,
    val items: List<MenuItemContribution> = emptyList()
)

interface ActionContributor {
    val contributorId: String
    val contributorName: String
    
    fun getActionContributions(): List<EditorActionContribution>
    fun getCommandContributions(): List<CommandContribution>
    fun getKeybindingContributions(): List<ResolvedKeybinding>
    fun getMenuContributions(): Map<String, List<MenuItemContribution>>
}

abstract class AbstractActionContributor : ActionContributor {
    override fun getActionContributions(): List<EditorActionContribution> = emptyList()
    override fun getCommandContributions(): List<CommandContribution> = emptyList()
    override fun getKeybindingContributions(): List<ResolvedKeybinding> = emptyList()
    override fun getMenuContributions(): Map<String, List<MenuItemContribution>> = emptyMap()
}

interface ContributorRegistry {
    fun registerContributor(contributor: ActionContributor)
    fun unregisterContributor(contributorId: String)
    fun getContributors(): List<ActionContributor>
    fun getContributor(contributorId: String): ActionContributor?
    
    fun getAllActionContributions(): List<EditorActionContribution>
    fun getAllCommandContributions(): List<CommandContribution>
    fun getAllKeybindingContributions(): List<ResolvedKeybinding>
    fun getMenuContributions(menuId: String): List<MenuItemContribution>
}
