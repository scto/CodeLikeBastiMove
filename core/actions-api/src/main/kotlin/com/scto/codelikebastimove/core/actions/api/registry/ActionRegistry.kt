package com.scto.codelikebastimove.core.actions.api.registry

import com.scto.codelikebastimove.core.actions.api.action.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ActionRegistry {
    val actions: StateFlow<Map<String, Action>>
    
    fun registerAction(action: Action): Boolean
    
    fun registerActions(actions: List<Action>): List<Boolean>
    
    fun unregisterAction(actionId: String): Boolean
    
    fun unregisterActions(actionIds: List<String>): List<Boolean>
    
    fun getAction(actionId: String): Action?
    
    fun getActions(): List<Action>
    
    fun getActionsByCategory(category: ActionCategory): List<Action>
    
    fun hasAction(actionId: String): Boolean
    
    fun searchActions(query: String): List<Action>
    
    fun getActionsForContext(context: ActionContext): List<Action>
    
    fun clear()
}

interface ActionRegistryListener {
    fun onActionRegistered(action: Action)
    fun onActionUnregistered(actionId: String)
}

interface MutableActionRegistry : ActionRegistry {
    fun addListener(listener: ActionRegistryListener)
    fun removeListener(listener: ActionRegistryListener)
}

data class ActionRegistration(
    val action: Action,
    val registeredAt: Long = System.currentTimeMillis(),
    val pluginId: String? = null,
    val contributionId: String? = null
)

interface ActionContributionRegistry {
    fun registerContribution(pluginId: String, contribution: ActionContribution)
    
    fun unregisterContribution(pluginId: String)
    
    fun getContributions(): Map<String, ActionContribution>
    
    fun getContribution(pluginId: String): ActionContribution?
}

object BuiltinActions {
    const val UNDO = "editor.action.undo"
    const val REDO = "editor.action.redo"
    const val CUT = "editor.action.cut"
    const val COPY = "editor.action.copy"
    const val PASTE = "editor.action.paste"
    const val SELECT_ALL = "editor.action.selectAll"
    const val DELETE_LINE = "editor.action.deleteLine"
    const val DUPLICATE_LINE = "editor.action.duplicateLine"
    const val MOVE_LINE_UP = "editor.action.moveLineUp"
    const val MOVE_LINE_DOWN = "editor.action.moveLineDown"
    const val COMMENT_LINE = "editor.action.commentLine"
    const val BLOCK_COMMENT = "editor.action.blockComment"
    const val FORMAT_DOCUMENT = "editor.action.formatDocument"
    const val FORMAT_SELECTION = "editor.action.formatSelection"
    const val INDENT_LINE = "editor.action.indentLine"
    const val OUTDENT_LINE = "editor.action.outdentLine"
    const val FIND = "editor.action.find"
    const val REPLACE = "editor.action.replace"
    const val FIND_NEXT = "editor.action.findNext"
    const val FIND_PREVIOUS = "editor.action.findPrevious"
    const val GO_TO_LINE = "editor.action.goToLine"
    const val GO_TO_DEFINITION = "editor.action.goToDefinition"
    const val PEEK_DEFINITION = "editor.action.peekDefinition"
    const val FIND_REFERENCES = "editor.action.findReferences"
    const val RENAME_SYMBOL = "editor.action.renameSymbol"
    const val QUICK_FIX = "editor.action.quickFix"
    const val TRIGGER_SUGGEST = "editor.action.triggerSuggest"
    const val FOLD = "editor.action.fold"
    const val UNFOLD = "editor.action.unfold"
    const val FOLD_ALL = "editor.action.foldAll"
    const val UNFOLD_ALL = "editor.action.unfoldAll"
    const val TOGGLE_WORD_WRAP = "editor.action.toggleWordWrap"
    const val ZOOM_IN = "editor.action.zoomIn"
    const val ZOOM_OUT = "editor.action.zoomOut"
    const val RESET_ZOOM = "editor.action.resetZoom"
    
    const val FILE_NEW = "workbench.action.files.new"
    const val FILE_OPEN = "workbench.action.files.open"
    const val FILE_SAVE = "workbench.action.files.save"
    const val FILE_SAVE_AS = "workbench.action.files.saveAs"
    const val FILE_SAVE_ALL = "workbench.action.files.saveAll"
    const val FILE_CLOSE = "workbench.action.files.close"
    const val FILE_CLOSE_ALL = "workbench.action.files.closeAll"
    
    const val COMMAND_PALETTE = "workbench.action.showCommands"
    const val QUICK_OPEN = "workbench.action.quickOpen"
    const val TOGGLE_SIDEBAR = "workbench.action.toggleSidebar"
    const val TOGGLE_PANEL = "workbench.action.togglePanel"
    const val TOGGLE_TERMINAL = "workbench.action.terminal.toggle"
    const val SETTINGS = "workbench.action.openSettings"
    const val KEYBOARD_SHORTCUTS = "workbench.action.openKeyboardShortcuts"
}
