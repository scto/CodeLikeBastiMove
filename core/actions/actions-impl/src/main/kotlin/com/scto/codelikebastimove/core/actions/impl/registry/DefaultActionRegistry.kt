package com.scto.codelikebastimove.core.actions.impl.registry

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.registry.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultActionRegistry : MutableActionRegistry {

  private val _actions = MutableStateFlow<Map<String, Action>>(emptyMap())
  override val actions: StateFlow<Map<String, Action>> = _actions.asStateFlow()

  private val registrations = ConcurrentHashMap<String, ActionRegistration>()
  private val listeners = CopyOnWriteArrayList<ActionRegistryListener>()

  override fun registerAction(action: Action): Boolean {
    if (registrations.containsKey(action.id)) {
      return false
    }

    val registration = ActionRegistration(action)
    registrations[action.id] = registration
    updateActionsFlow()

    listeners.forEach { it.onActionRegistered(action) }
    return true
  }

  override fun registerActions(actions: List<Action>): List<Boolean> {
    return actions.map { registerAction(it) }
  }

  override fun unregisterAction(actionId: String): Boolean {
    val removed = registrations.remove(actionId) != null
    if (removed) {
      updateActionsFlow()
      listeners.forEach { it.onActionUnregistered(actionId) }
    }
    return removed
  }

  override fun unregisterActions(actionIds: List<String>): List<Boolean> {
    return actionIds.map { unregisterAction(it) }
  }

  override fun getAction(actionId: String): Action? {
    return registrations[actionId]?.action
  }

  override fun getActions(): List<Action> {
    return registrations.values.map { it.action }.sortedBy { it.priority }
  }

  override fun getActionsByCategory(category: ActionCategory): List<Action> {
    return registrations.values
      .map { it.action }
      .filter { it.category == category }
      .sortedBy { it.priority }
  }

  override fun hasAction(actionId: String): Boolean {
    return registrations.containsKey(actionId)
  }

  override fun searchActions(query: String): List<Action> {
    val lowerQuery = query.lowercase()
    return registrations.values
      .map { it.action }
      .filter { action ->
        action.name.lowercase().contains(lowerQuery) ||
          action.id.lowercase().contains(lowerQuery) ||
          action.description.lowercase().contains(lowerQuery)
      }
      .sortedBy { action ->
        when {
          action.name.lowercase().startsWith(lowerQuery) -> 0
          action.id.lowercase().startsWith(lowerQuery) -> 1
          action.name.lowercase().contains(lowerQuery) -> 2
          else -> 3
        }
      }
  }

  override fun getActionsForContext(context: ActionContext): List<Action> {
    return registrations.values
      .map { it.action }
      .filter { it.canExecute(context) && it.isEnabled }
      .sortedBy { it.priority }
  }

  override fun clear() {
    val actionIds = registrations.keys.toList()
    registrations.clear()
    updateActionsFlow()
    actionIds.forEach { actionId -> listeners.forEach { it.onActionUnregistered(actionId) } }
  }

  override fun addListener(listener: ActionRegistryListener) {
    listeners.add(listener)
  }

  override fun removeListener(listener: ActionRegistryListener) {
    listeners.remove(listener)
  }

  private fun updateActionsFlow() {
    _actions.value = registrations.mapValues { it.value.action }
  }
}

class DefaultActionContributionRegistry : ActionContributionRegistry {

  private val contributions = ConcurrentHashMap<String, ActionContribution>()

  override fun registerContribution(pluginId: String, contribution: ActionContribution) {
    contributions[pluginId] = contribution
  }

  override fun unregisterContribution(pluginId: String) {
    contributions.remove(pluginId)
  }

  override fun getContributions(): Map<String, ActionContribution> {
    return contributions.toMap()
  }

  override fun getContribution(pluginId: String): ActionContribution? {
    return contributions[pluginId]
  }
}
