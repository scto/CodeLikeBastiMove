package com.scto.codelikebastimove.core.actions.api.action

import kotlin.reflect.KClass

interface Action {
    val id: String
    val name: String
    val description: String get() = ""
    val category: ActionCategory get() = ActionCategory.GENERAL
    val icon: String? get() = null
    val isEnabled: Boolean get() = true
    val priority: Int get() = 0
    
    suspend fun execute(context: ActionContext): ActionResult
    
    fun canExecute(context: ActionContext): Boolean = isEnabled
}

enum class ActionCategory {
    GENERAL,
    EDIT,
    FILE,
    VIEW,
    NAVIGATION,
    SEARCH,
    DEBUG,
    BUILD,
    REFACTOR,
    GIT,
    TERMINAL,
    WINDOW,
    HELP,
    PLUGIN,
    CUSTOM
}

data class ActionDescriptor(
    val id: String,
    val name: String,
    val description: String = "",
    val category: ActionCategory = ActionCategory.GENERAL,
    val icon: String? = null,
    val defaultKeybinding: Keybinding? = null,
    val condition: ActionWhen? = null,
    val args: Map<String, Any> = emptyMap()
)

data class Keybinding(
    val key: String,
    val modifiers: Set<KeyModifier> = emptySet(),
    val platform: Platform? = null
) {
    override fun toString(): String {
        val mods = modifiers.joinToString("+") { it.symbol }
        return if (mods.isNotEmpty()) "$mods+$key" else key
    }
    
    companion object {
        fun parse(keybinding: String): Keybinding {
            val parts = keybinding.split("+")
            val key = parts.last()
            val modifiers = parts.dropLast(1).mapNotNull { mod ->
                KeyModifier.entries.find { it.symbol.equals(mod, ignoreCase = true) || it.name.equals(mod, ignoreCase = true) }
            }.toSet()
            return Keybinding(key, modifiers)
        }
    }
}

enum class KeyModifier(val symbol: String) {
    CTRL("Ctrl"),
    SHIFT("Shift"),
    ALT("Alt"),
    META("Meta"),
    CMD("Cmd")
}

enum class Platform {
    ANDROID, DESKTOP, ALL
}

data class ActionWhen(
    val editorFocus: Boolean? = null,
    val editorTextFocus: Boolean? = null,
    val inputFocus: Boolean? = null,
    val resourceScheme: String? = null,
    val resourceExtname: String? = null,
    val isInDiffEditor: Boolean? = null,
    val custom: Map<String, Any> = emptyMap()
) {
    fun evaluate(context: ActionContext): Boolean {
        if (editorFocus != null && context.editorFocus != editorFocus) return false
        if (editorTextFocus != null && context.editorTextFocus != editorTextFocus) return false
        if (inputFocus != null && context.inputFocus != inputFocus) return false
        if (resourceExtname != null && context.fileExtension != resourceExtname) return false
        return true
    }
}

data class ActionContext(
    val editorFocus: Boolean = false,
    val editorTextFocus: Boolean = false,
    val inputFocus: Boolean = false,
    val filePath: String? = null,
    val fileName: String? = null,
    val fileExtension: String? = null,
    val selectedText: String? = null,
    val cursorLine: Int = 0,
    val cursorColumn: Int = 0,
    val content: String? = null,
    val customData: Map<String, Any> = emptyMap()
) {
    companion object {
        val EMPTY = ActionContext()
    }
}

sealed class ActionResult {
    data class Success(
        val message: String? = null,
        val data: Any? = null
    ) : ActionResult()
    
    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : ActionResult()
    
    data object Cancelled : ActionResult()
    
    data class Pending(
        val message: String = "Action pending..."
    ) : ActionResult()
    
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
}

abstract class AbstractAction : Action {
    override suspend fun execute(context: ActionContext): ActionResult {
        return try {
            if (!canExecute(context)) {
                ActionResult.Failure("Action cannot be executed in current context")
            } else {
                doExecute(context)
            }
        } catch (e: Exception) {
            ActionResult.Failure(e.message ?: "Unknown error", e)
        }
    }
    
    protected abstract suspend fun doExecute(context: ActionContext): ActionResult
}

interface ActionGroup : Action {
    val actions: List<Action>
    val isFlat: Boolean get() = false
}

abstract class AbstractActionGroup : ActionGroup {
    override suspend fun execute(context: ActionContext): ActionResult {
        return ActionResult.Success("Action group displayed")
    }
}

interface ActionContribution {
    val contributedActions: List<ActionDescriptor>
    val contributedKeybindings: List<KeybindingContribution>
    val contributedMenus: List<MenuContribution>
}

data class KeybindingContribution(
    val actionId: String,
    val keybinding: Keybinding,
    val condition: ActionWhen? = null
)

data class MenuContribution(
    val actionId: String,
    val menuId: String,
    val group: String? = null,
    val order: Int = 0,
    val condition: ActionWhen? = null
)

object MenuIds {
    const val EDITOR_CONTEXT = "editor.context"
    const val EDITOR_TITLE = "editor.title"
    const val EDITOR_TITLE_CONTEXT = "editor.title.context"
    const val EXPLORER_CONTEXT = "explorer.context"
    const val COMMAND_PALETTE = "commandPalette"
    const val VIEW_TITLE = "view.title"
    const val VIEW_ITEM_CONTEXT = "view.item.context"
    const val TOUCH_BAR = "touchBar"
    const val SCM_TITLE = "scm.title"
    const val SCM_RESOURCE_GROUP_CONTEXT = "scm.resourceGroup.context"
    const val SCM_RESOURCE_STATE_CONTEXT = "scm.resourceState.context"
    const val DEBUG_TOOLBAR = "debug.toolbar"
    const val TERMINAL_CONTEXT = "terminal.context"
}
