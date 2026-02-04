package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContribution
import com.scto.codelikebastimove.core.actions.api.action.ActionDescriptor
import com.scto.codelikebastimove.core.actions.api.action.KeybindingContribution
import com.scto.codelikebastimove.core.actions.api.action.MenuContribution
import com.scto.codelikebastimove.core.actions.api.action.MenuIds

object SubmoduleActionContribution : ActionContribution {
    override val contributedActions: List<ActionDescriptor>
        get() = SubmoduleActionRegistry.getActionDescriptors()

    override val contributedKeybindings: List<KeybindingContribution>
        get() = SubmoduleActionRegistry.getKeybindingContributions()

    override val contributedMenus: List<MenuContribution>
        get() = listOf(
            MenuContribution(
                actionId = "submodule.init",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 1,
            ),
            MenuContribution(
                actionId = "submodule.update",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 2,
            ),
            MenuContribution(
                actionId = "submodule.add",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 3,
            ),
            MenuContribution(
                actionId = "submodule.sync",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 4,
            ),
            MenuContribution(
                actionId = "submodule.status",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 5,
            ),
            MenuContribution(
                actionId = "submodule.remove",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 6,
            ),
            MenuContribution(
                actionId = "submodule.foreach",
                menuId = MenuIds.COMMAND_PALETTE,
                group = "submodule",
                order = 7,
            ),
            MenuContribution(
                actionId = "submodule.init",
                menuId = MenuIds.EXPLORER_CONTEXT,
                group = "git.submodule",
                order = 1,
            ),
            MenuContribution(
                actionId = "submodule.update",
                menuId = MenuIds.EXPLORER_CONTEXT,
                group = "git.submodule",
                order = 2,
            ),
            MenuContribution(
                actionId = "submodule.status",
                menuId = MenuIds.EXPLORER_CONTEXT,
                group = "git.submodule",
                order = 3,
            ),
        )
}
