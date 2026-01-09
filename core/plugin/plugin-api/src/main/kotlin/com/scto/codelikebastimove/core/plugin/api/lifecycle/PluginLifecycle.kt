package com.scto.codelikebastimove.core.plugin.api.lifecycle

import com.scto.codelikebastimove.core.plugin.api.context.PluginContext
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginDescriptor

interface Plugin {
    val descriptor: PluginDescriptor
    
    suspend fun onLoad(context: PluginContext)
    
    suspend fun onActivate()
    
    suspend fun onDeactivate()
    
    suspend fun onUnload()
}

abstract class AbstractPlugin : Plugin {
    private var _context: PluginContext? = null
    protected val context: PluginContext
        get() = _context ?: throw IllegalStateException("Plugin not initialized")
    
    abstract override val descriptor: PluginDescriptor
    
    override suspend fun onLoad(context: PluginContext) {
        _context = context
    }
    
    override suspend fun onActivate() {}
    
    override suspend fun onDeactivate() {}
    
    override suspend fun onUnload() {
        _context = null
    }
}

interface PluginLifecycleListener {
    fun onPluginLoaded(plugin: Plugin)
    fun onPluginActivated(plugin: Plugin)
    fun onPluginDeactivated(plugin: Plugin)
    fun onPluginUnloaded(pluginId: String)
    fun onPluginError(pluginId: String, error: Throwable)
}

abstract class PluginLifecycleAdapter : PluginLifecycleListener {
    override fun onPluginLoaded(plugin: Plugin) {}
    override fun onPluginActivated(plugin: Plugin) {}
    override fun onPluginDeactivated(plugin: Plugin) {}
    override fun onPluginUnloaded(pluginId: String) {}
    override fun onPluginError(pluginId: String, error: Throwable) {}
}

data class PluginLoadResult(
    val pluginId: String,
    val success: Boolean,
    val plugin: Plugin? = null,
    val error: Throwable? = null,
    val errorMessage: String? = error?.message
)

data class PluginActivationResult(
    val pluginId: String,
    val success: Boolean,
    val error: Throwable? = null,
    val errorMessage: String? = error?.message
)
