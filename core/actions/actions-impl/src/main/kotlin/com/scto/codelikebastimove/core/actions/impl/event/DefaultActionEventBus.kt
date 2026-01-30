package com.scto.codelikebastimove.core.actions.impl.event

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.event.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DefaultActionEventBus : ActionEventBus, ActionEventEmitter {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private val _events = MutableSharedFlow<ActionEvent>(replay = 10, extraBufferCapacity = 100)
  override val events: SharedFlow<ActionEvent> = _events.asSharedFlow()

  private val listeners = CopyOnWriteArrayList<ActionEventListener>()

  init {
    scope.launch {
      events.collect { event ->
        listeners.forEach { listener ->
          try {
            listener.onEvent(event)
          } catch (e: Exception) {
            // Log error but don't propagate
          }
        }
      }
    }
  }

  override suspend fun emit(event: ActionEvent) {
    _events.emit(event)
  }

  override fun subscribe(listener: ActionEventListener) {
    listeners.add(listener)
  }

  override fun unsubscribe(listener: ActionEventListener) {
    listeners.remove(listener)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : ActionEvent> filter(eventType: Class<T>): Flow<T> {
    return events.filter { eventType.isInstance(it) }.map { it as T }
  }

  override suspend fun emitRegistered(actionId: String, name: String) {
    emit(ActionEvent.Registered(actionId, name))
  }

  override suspend fun emitUnregistered(actionId: String) {
    emit(ActionEvent.Unregistered(actionId))
  }

  override suspend fun emitExecuting(actionId: String, context: ActionContext) {
    emit(ActionEvent.Executing(actionId, context))
  }

  override suspend fun emitExecuted(actionId: String, result: ActionResult, durationMs: Long) {
    emit(ActionEvent.Executed(actionId, result, durationMs))
  }

  override suspend fun emitFailed(actionId: String, error: String, exception: Throwable?) {
    emit(ActionEvent.Failed(actionId, error, exception))
  }

  override suspend fun emitCancelled(actionId: String) {
    emit(ActionEvent.Cancelled(actionId))
  }

  override suspend fun emitKeybindingTriggered(actionId: String, keybinding: String) {
    emit(ActionEvent.KeybindingTriggered(actionId, keybinding))
  }
}

class TypedActionEventSubscriber<T : ActionEvent>(
  private val eventBus: ActionEventBus,
  private val eventType: Class<T>,
  private val handler: ActionEventHandler<T>,
) : TypedEventSubscription {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  private var isActive = true

  init {
    scope.launch {
      eventBus.filter(eventType).collect { event ->
        if (isActive) {
          handler(event)
        }
      }
    }
  }

  override fun cancel() {
    isActive = false
  }
}

inline fun <reified T : ActionEvent> ActionEventBus.on(
  noinline handler: ActionEventHandler<T>
): TypedEventSubscription {
  return TypedActionEventSubscriber(this, T::class.java, handler)
}
