package com.scto.codelikebastimove.core.plugin.api.event

import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginDescriptor
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginState
import kotlinx.coroutines.flow.Flow

sealed class PluginEvent {
  abstract val pluginId: String
  abstract val timestamp: Long

  data class Installed(
    override val pluginId: String,
    val descriptor: PluginDescriptor,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Loaded(
    override val pluginId: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Activated(
    override val pluginId: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Deactivated(
    override val pluginId: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Unloaded(
    override val pluginId: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Uninstalled(
    override val pluginId: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class StateChanged(
    override val pluginId: String,
    val previousState: PluginState,
    val newState: PluginState,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Error(
    override val pluginId: String,
    val error: Throwable,
    val errorMessage: String,
    val operation: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()

  data class Updated(
    override val pluginId: String,
    val previousVersion: String,
    val newVersion: String,
    override val timestamp: Long = System.currentTimeMillis(),
  ) : PluginEvent()
}

interface PluginEventBus {
  val events: Flow<PluginEvent>

  suspend fun emit(event: PluginEvent)

  fun subscribe(listener: PluginEventListener)

  fun unsubscribe(listener: PluginEventListener)
}

interface PluginEventListener {
  fun onEvent(event: PluginEvent)
}

abstract class PluginEventAdapter : PluginEventListener {
  override fun onEvent(event: PluginEvent) {
    when (event) {
      is PluginEvent.Installed -> onInstalled(event)
      is PluginEvent.Loaded -> onLoaded(event)
      is PluginEvent.Activated -> onActivated(event)
      is PluginEvent.Deactivated -> onDeactivated(event)
      is PluginEvent.Unloaded -> onUnloaded(event)
      is PluginEvent.Uninstalled -> onUninstalled(event)
      is PluginEvent.StateChanged -> onStateChanged(event)
      is PluginEvent.Error -> onError(event)
      is PluginEvent.Updated -> onUpdated(event)
    }
  }

  open fun onInstalled(event: PluginEvent.Installed) {}

  open fun onLoaded(event: PluginEvent.Loaded) {}

  open fun onActivated(event: PluginEvent.Activated) {}

  open fun onDeactivated(event: PluginEvent.Deactivated) {}

  open fun onUnloaded(event: PluginEvent.Unloaded) {}

  open fun onUninstalled(event: PluginEvent.Uninstalled) {}

  open fun onStateChanged(event: PluginEvent.StateChanged) {}

  open fun onError(event: PluginEvent.Error) {}

  open fun onUpdated(event: PluginEvent.Updated) {}
}
