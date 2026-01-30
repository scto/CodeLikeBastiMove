package com.scto.codelikebastimove.core.plugin.impl.registry

import com.scto.codelikebastimove.core.plugin.api.event.PluginEvent
import com.scto.codelikebastimove.core.plugin.api.event.PluginEventBus
import com.scto.codelikebastimove.core.plugin.api.event.PluginEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DefaultPluginEventBus : PluginEventBus {

  private val _events = MutableSharedFlow<PluginEvent>(replay = 0, extraBufferCapacity = 64)

  override val events: Flow<PluginEvent> = _events.asSharedFlow()

  private val listeners = mutableListOf<PluginEventListener>()

  override suspend fun emit(event: PluginEvent) {
    _events.emit(event)

    listeners.forEach { listener ->
      try {
        listener.onEvent(event)
      } catch (e: Exception) {
        // Log error but continue notifying other listeners
      }
    }
  }

  override fun subscribe(listener: PluginEventListener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener)
    }
  }

  override fun unsubscribe(listener: PluginEventListener) {
    listeners.remove(listener)
  }
}
