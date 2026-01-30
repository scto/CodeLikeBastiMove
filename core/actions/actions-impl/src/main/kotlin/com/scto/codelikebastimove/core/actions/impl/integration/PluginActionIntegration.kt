package com.scto.codelikebastimove.core.actions.impl.integration

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.event.ActionEventBus
import com.scto.codelikebastimove.core.actions.api.keybinding.KeybindingService
import com.scto.codelikebastimove.core.actions.api.registry.ActionRegistry
import com.scto.codelikebastimove.core.actions.api.registry.BuiltinActions
import com.scto.codelikebastimove.core.actions.impl.event.DefaultActionEventBus
import com.scto.codelikebastimove.core.actions.impl.executor.ActionExecutor
import com.scto.codelikebastimove.core.actions.impl.executor.DefaultActionExecutor
import com.scto.codelikebastimove.core.actions.impl.keybinding.DefaultKeybindingService
import com.scto.codelikebastimove.core.actions.impl.registry.DefaultActionRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ActionSystem private constructor() {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  val registry: ActionRegistry = DefaultActionRegistry()
  val eventBus: ActionEventBus = DefaultActionEventBus()
  val keybindingService: KeybindingService = DefaultKeybindingService()
  val executor: ActionExecutor =
    DefaultActionExecutor(
      registry,
      eventBus as com.scto.codelikebastimove.core.actions.api.event.ActionEventEmitter,
    )

  private var initialized = false

  fun initialize() {
    if (initialized) return
    initialized = true

    registerBuiltinActions()
    registerDefaultKeybindings()
  }

  private fun registerBuiltinActions() {
    val builtinActions =
      listOf(
        createAction(BuiltinActions.UNDO, "Undo", ActionCategory.EDIT),
        createAction(BuiltinActions.REDO, "Redo", ActionCategory.EDIT),
        createAction(BuiltinActions.CUT, "Cut", ActionCategory.EDIT),
        createAction(BuiltinActions.COPY, "Copy", ActionCategory.EDIT),
        createAction(BuiltinActions.PASTE, "Paste", ActionCategory.EDIT),
        createAction(BuiltinActions.SELECT_ALL, "Select All", ActionCategory.EDIT),
        createAction(BuiltinActions.FIND, "Find", ActionCategory.SEARCH),
        createAction(BuiltinActions.REPLACE, "Find and Replace", ActionCategory.SEARCH),
        createAction(BuiltinActions.GO_TO_LINE, "Go to Line", ActionCategory.NAVIGATION),
        createAction(BuiltinActions.FORMAT_DOCUMENT, "Format Document", ActionCategory.EDIT),
        createAction(BuiltinActions.COMMENT_LINE, "Toggle Line Comment", ActionCategory.EDIT),
        createAction(BuiltinActions.COMMAND_PALETTE, "Command Palette", ActionCategory.VIEW),
        createAction(BuiltinActions.QUICK_OPEN, "Quick Open", ActionCategory.FILE),
        createAction(BuiltinActions.FILE_SAVE, "Save", ActionCategory.FILE),
        createAction(BuiltinActions.FILE_SAVE_ALL, "Save All", ActionCategory.FILE),
        createAction(BuiltinActions.TOGGLE_SIDEBAR, "Toggle Sidebar", ActionCategory.VIEW),
        createAction(BuiltinActions.TOGGLE_TERMINAL, "Toggle Terminal", ActionCategory.VIEW),
        createAction(BuiltinActions.SETTINGS, "Open Settings", ActionCategory.GENERAL),
      )

    builtinActions.forEach { registry.registerAction(it) }
  }

  private fun registerDefaultKeybindings() {
    val defaultBindings =
      mapOf(
        BuiltinActions.UNDO to Keybinding("Z", setOf(KeyModifier.CTRL)),
        BuiltinActions.REDO to Keybinding("Y", setOf(KeyModifier.CTRL)),
        BuiltinActions.CUT to Keybinding("X", setOf(KeyModifier.CTRL)),
        BuiltinActions.COPY to Keybinding("C", setOf(KeyModifier.CTRL)),
        BuiltinActions.PASTE to Keybinding("V", setOf(KeyModifier.CTRL)),
        BuiltinActions.SELECT_ALL to Keybinding("A", setOf(KeyModifier.CTRL)),
        BuiltinActions.FIND to Keybinding("F", setOf(KeyModifier.CTRL)),
        BuiltinActions.REPLACE to Keybinding("H", setOf(KeyModifier.CTRL)),
        BuiltinActions.GO_TO_LINE to Keybinding("G", setOf(KeyModifier.CTRL)),
        BuiltinActions.FORMAT_DOCUMENT to
          Keybinding("F", setOf(KeyModifier.CTRL, KeyModifier.SHIFT)),
        BuiltinActions.COMMENT_LINE to Keybinding("/", setOf(KeyModifier.CTRL)),
        BuiltinActions.COMMAND_PALETTE to
          Keybinding("P", setOf(KeyModifier.CTRL, KeyModifier.SHIFT)),
        BuiltinActions.QUICK_OPEN to Keybinding("P", setOf(KeyModifier.CTRL)),
        BuiltinActions.FILE_SAVE to Keybinding("S", setOf(KeyModifier.CTRL)),
        BuiltinActions.FILE_SAVE_ALL to Keybinding("S", setOf(KeyModifier.CTRL, KeyModifier.SHIFT)),
        BuiltinActions.TOGGLE_SIDEBAR to Keybinding("B", setOf(KeyModifier.CTRL)),
        BuiltinActions.TOGGLE_TERMINAL to Keybinding("`", setOf(KeyModifier.CTRL)),
      )

    defaultBindings.forEach { (actionId, keybinding) ->
      keybindingService.registerKeybinding(actionId, keybinding)
    }
  }

  private fun createAction(id: String, name: String, category: ActionCategory): Action =
    object : AbstractAction() {
      override val id: String = id
      override val name: String = name
      override val category: ActionCategory = category

      override suspend fun doExecute(context: ActionContext): ActionResult {
        return ActionResult.Success("Action $name executed")
      }
    }

  suspend fun executeAction(
    actionId: String,
    context: ActionContext = ActionContext.EMPTY,
  ): ActionResult {
    return executor.execute(actionId, context)
  }

  fun registerAction(action: Action): Boolean {
    return registry.registerAction(action)
  }

  fun unregisterAction(actionId: String): Boolean {
    keybindingService.unregisterAllKeybindings(actionId)
    return registry.unregisterAction(actionId)
  }

  fun registerKeybinding(
    actionId: String,
    keybinding: Keybinding,
    condition: ActionWhen? = null,
  ): Boolean {
    return keybindingService.registerKeybinding(actionId, keybinding, condition)
  }

  fun getAvailableActions(): List<Action> = registry.getActions()

  fun searchActions(query: String): List<Action> = registry.searchActions(query)

  companion object {
    @Volatile private var INSTANCE: ActionSystem? = null

    fun getInstance(): ActionSystem {
      return INSTANCE ?: synchronized(this) { INSTANCE ?: ActionSystem().also { INSTANCE = it } }
    }
  }
}

interface ActionSystemProvider {
  fun getActionSystem(): ActionSystem
}

class DefaultActionSystemProvider : ActionSystemProvider {
  override fun getActionSystem(): ActionSystem = ActionSystem.getInstance()
}
