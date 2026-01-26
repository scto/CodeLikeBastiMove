package com.scto.codelikebastimove.core.actions.impl.keybinding

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionWhen
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.core.actions.api.keybinding.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

class DefaultKeybindingService : KeybindingService, KeybindingResolver {
    
    private val _keybindings = MutableStateFlow<List<ResolvedKeybinding>>(emptyList())
    override val keybindings: StateFlow<List<ResolvedKeybinding>> = _keybindings.asStateFlow()
    
    private val keybindingMap = ConcurrentHashMap<String, MutableList<ResolvedKeybinding>>()
    private var enabled = true
    
    override fun registerKeybinding(actionId: String, keybinding: Keybinding, condition: ActionWhen?): Boolean {
        val resolved = ResolvedKeybinding(actionId, keybinding, condition)
        
        val list = keybindingMap.getOrPut(actionId) { mutableListOf() }
        
        if (list.any { it.keybinding == keybinding }) {
            return false
        }
        
        list.add(resolved)
        updateKeybindingsFlow()
        return true
    }
    
    override fun unregisterKeybinding(actionId: String, keybinding: Keybinding): Boolean {
        val list = keybindingMap[actionId] ?: return false
        val removed = list.removeIf { it.keybinding == keybinding }
        if (removed) {
            updateKeybindingsFlow()
        }
        return removed
    }
    
    override fun unregisterAllKeybindings(actionId: String) {
        keybindingMap.remove(actionId)
        updateKeybindingsFlow()
    }
    
    override fun getKeybindingsForAction(actionId: String): List<Keybinding> {
        return keybindingMap[actionId]?.map { it.keybinding } ?: emptyList()
    }
    
    override fun getActionForKeybinding(keybinding: Keybinding, context: ActionContext): String? {
        return keybindingMap.values
            .flatten()
            .filter { it.keybinding == keybinding }
            .filter { evaluateCondition(it.condition, context) }
            .maxByOrNull { it.priority }
            ?.actionId
    }
    
    override fun resolveKeybinding(keyEvent: KeyEvent, context: ActionContext): String? {
        return resolve(keyEvent, context)?.actionId
    }
    
    override fun getConflicts(keybinding: Keybinding): List<String> {
        return keybindingMap.values
            .flatten()
            .filter { it.keybinding == keybinding }
            .map { it.actionId }
    }
    
    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
    
    override fun isEnabled(): Boolean = enabled
    
    override fun resolve(event: KeyEvent, context: ActionContext): ResolvedKeybinding? {
        if (!enabled) return null
        
        return findMatches(event)
            .filter { evaluateCondition(it.condition, context) }
            .maxByOrNull { it.priority }
    }
    
    override fun findMatches(event: KeyEvent): List<ResolvedKeybinding> {
        val eventKeybinding = event.toKeybinding()
        return keybindingMap.values
            .flatten()
            .filter { it.keybinding.key.equals(eventKeybinding.key, ignoreCase = true) &&
                     it.keybinding.modifiers == eventKeybinding.modifiers }
    }
    
    override fun evaluateCondition(condition: ActionWhen?, context: ActionContext): Boolean {
        return condition?.evaluate(context) ?: true
    }
    
    private fun updateKeybindingsFlow() {
        _keybindings.value = keybindingMap.values.flatten()
    }
}

class DefaultKeybindingHandler(
    private val keybindingService: KeybindingService,
    private val actionExecutor: suspend (String, ActionContext) -> Unit
) : KeybindingHandler {
    
    override suspend fun handleKeyEvent(event: KeyEvent, context: ActionContext): Boolean {
        if (!keybindingService.isEnabled()) return false
        
        val actionId = keybindingService.resolveKeybinding(event, context) ?: return false
        
        actionExecutor(actionId, context)
        return true
    }
}
