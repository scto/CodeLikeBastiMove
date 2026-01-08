package com.scto.codelikebastimove.core.actions.api.event

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

sealed class ActionEvent {
    abstract val actionId: String
    abstract val timestamp: Long
    
    data class Registered(
        override val actionId: String,
        val name: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
    
    data class Unregistered(
        override val actionId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
    
    data class Executing(
        override val actionId: String,
        val context: ActionContext,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
    
    data class Executed(
        override val actionId: String,
        val result: ActionResult,
        val durationMs: Long,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
    
    data class Failed(
        override val actionId: String,
        val error: String,
        val exception: Throwable?,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
    
    data class Cancelled(
        override val actionId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
    
    data class KeybindingTriggered(
        override val actionId: String,
        val keybinding: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ActionEvent()
}

interface ActionEventBus {
    val events: SharedFlow<ActionEvent>
    
    suspend fun emit(event: ActionEvent)
    
    fun subscribe(listener: ActionEventListener)
    
    fun unsubscribe(listener: ActionEventListener)
    
    fun <T : ActionEvent> filter(eventType: Class<T>): Flow<T>
}

interface ActionEventListener {
    fun onEvent(event: ActionEvent)
}

abstract class ActionEventAdapter : ActionEventListener {
    override fun onEvent(event: ActionEvent) {
        when (event) {
            is ActionEvent.Registered -> onRegistered(event)
            is ActionEvent.Unregistered -> onUnregistered(event)
            is ActionEvent.Executing -> onExecuting(event)
            is ActionEvent.Executed -> onExecuted(event)
            is ActionEvent.Failed -> onFailed(event)
            is ActionEvent.Cancelled -> onCancelled(event)
            is ActionEvent.KeybindingTriggered -> onKeybindingTriggered(event)
        }
    }
    
    open fun onRegistered(event: ActionEvent.Registered) {}
    open fun onUnregistered(event: ActionEvent.Unregistered) {}
    open fun onExecuting(event: ActionEvent.Executing) {}
    open fun onExecuted(event: ActionEvent.Executed) {}
    open fun onFailed(event: ActionEvent.Failed) {}
    open fun onCancelled(event: ActionEvent.Cancelled) {}
    open fun onKeybindingTriggered(event: ActionEvent.KeybindingTriggered) {}
}

typealias ActionEventHandler<T> = suspend (T) -> Unit

interface TypedEventSubscription {
    fun cancel()
}

interface ActionEventEmitter {
    suspend fun emitRegistered(actionId: String, name: String)
    suspend fun emitUnregistered(actionId: String)
    suspend fun emitExecuting(actionId: String, context: ActionContext)
    suspend fun emitExecuted(actionId: String, result: ActionResult, durationMs: Long)
    suspend fun emitFailed(actionId: String, error: String, exception: Throwable?)
    suspend fun emitCancelled(actionId: String)
    suspend fun emitKeybindingTriggered(actionId: String, keybinding: String)
}
