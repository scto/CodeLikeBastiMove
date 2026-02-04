package com.scto.codelikebastimove.feature.soraeditor.command

import com.scto.codelikebastimove.core.actions.api.action.ActionContribution
import com.scto.codelikebastimove.core.actions.api.action.ActionDescriptor
import com.scto.codelikebastimove.core.actions.api.action.KeybindingContribution
import com.scto.codelikebastimove.core.actions.api.action.MenuContribution
import com.scto.codelikebastimove.core.actions.api.action.MenuIds

object EditorActionContribution : ActionContribution {
    override val contributedActions: List<ActionDescriptor>
        get() = EditorActionRegistry.getActionDescriptors()

    override val contributedKeybindings: List<KeybindingContribution>
        get() = EditorActionRegistry.getKeybindingContributions()

    override val contributedMenus: List<MenuContribution>
        get() = buildList {
            add(MenuContribution(
                actionId = "editor.undo",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "1_modification",
                order = 1
            ))
            add(MenuContribution(
                actionId = "editor.redo",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "1_modification",
                order = 2
            ))
            add(MenuContribution(
                actionId = "editor.selectAll",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "2_selection",
                order = 1
            ))
            add(MenuContribution(
                actionId = "editor.selectWord",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "2_selection",
                order = 2
            ))
            add(MenuContribution(
                actionId = "editor.uppercase",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "3_transform",
                order = 1
            ))
            add(MenuContribution(
                actionId = "editor.lowercase",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "3_transform",
                order = 2
            ))
            add(MenuContribution(
                actionId = "editor.search",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "4_search",
                order = 1
            ))
            add(MenuContribution(
                actionId = "editor.replace",
                menuId = MenuIds.EDITOR_CONTEXT,
                group = "4_search",
                order = 2
            ))
            add(MenuContribution(
                actionId = "editor.toggleWordWrap",
                menuId = MenuIds.EDITOR_TITLE,
                group = "view",
                order = 1
            ))
            add(MenuContribution(
                actionId = "editor.toggleReadOnly",
                menuId = MenuIds.EDITOR_TITLE,
                group = "view",
                order = 2
            ))
            add(MenuContribution(
                actionId = "editor.save",
                menuId = MenuIds.EDITOR_TITLE,
                group = "file",
                order = 1
            ))
            add(MenuContribution(
                actionId = "editor.refresh",
                menuId = MenuIds.EDITOR_TITLE,
                group = "file",
                order = 2
            ))
            EditorActionRegistry.allActions.forEach { action ->
                add(MenuContribution(
                    actionId = action.id,
                    menuId = MenuIds.COMMAND_PALETTE,
                    order = action.priority
                ))
            }
        }
}
