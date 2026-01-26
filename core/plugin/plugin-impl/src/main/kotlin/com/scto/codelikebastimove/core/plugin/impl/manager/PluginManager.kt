package com.scto.codelikebastimove.core.plugin.impl.manager

import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginDescriptor
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginState
import com.scto.codelikebastimove.core.plugin.api.event.PluginEvent
import com.scto.codelikebastimove.core.plugin.api.event.PluginEventBus
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionRegistry
import com.scto.codelikebastimove.core.plugin.api.extension.createDefaultExtensionPoints
import com.scto.codelikebastimove.core.plugin.api.lifecycle.Plugin
import com.scto.codelikebastimove.core.plugin.api.lifecycle.PluginActivationResult
import com.scto.codelikebastimove.core.plugin.api.lifecycle.PluginLifecycleListener
import com.scto.codelikebastimove.core.plugin.api.lifecycle.PluginLoadResult
import com.scto.codelikebastimove.core.plugin.impl.loader.PluginLoader
import com.scto.codelikebastimove.core.plugin.impl.registry.DefaultExtensionRegistry
import com.scto.codelikebastimove.core.plugin.impl.registry.DefaultPluginEventBus
import com.scto.codelikebastimove.core.plugin.impl.security.PluginSecurityManager
import com.scto.codelikebastimove.core.plugin.impl.storage.PluginStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class PluginManager(
    private val pluginDirectory: String,
    private val storageDirectory: String,
    private val hostServices: HostServiceRegistry = DefaultHostServiceRegistry(),
    private val hostVersion: String = "1.0.0",
    private val requireSignatureVerification: Boolean = false
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()
    
    private val pluginLoader = PluginLoader(this)
    private val pluginStorage = PluginStorage(storageDirectory)
    private val _securityManager = PluginSecurityManager()
    val securityManager: PluginSecurityManager get() = _securityManager
    
    private val _extensionRegistry: DefaultExtensionRegistry = DefaultExtensionRegistry()
    val extensionRegistry: ExtensionRegistry get() = _extensionRegistry
    
    private val _eventBus: DefaultPluginEventBus = DefaultPluginEventBus()
    val eventBus: PluginEventBus get() = _eventBus
    
    private val loadedPlugins = mutableMapOf<String, PluginEntry>()
    private val lifecycleListeners = mutableListOf<PluginLifecycleListener>()
    
    private val _pluginStates = MutableStateFlow<Map<String, PluginState>>(emptyMap())
    val pluginStates: StateFlow<Map<String, PluginState>> = _pluginStates.asStateFlow()
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    init {
        registerDefaultExtensionPoints()
    }
    
    private fun registerDefaultExtensionPoints() {
        createDefaultExtensionPoints().forEach { extensionPoint ->
            _extensionRegistry.registerExtensionPoint(extensionPoint)
        }
    }
    
    suspend fun initialize() = mutex.withLock {
        if (_isInitialized.value) return@withLock
        
        withContext(Dispatchers.IO) {
            File(pluginDirectory).mkdirs()
            File(storageDirectory).mkdirs()
        }
        
        val enabledPlugins = pluginStorage.getEnabledPlugins()
        discoverAndLoadPlugins(enabledPlugins)
        
        _isInitialized.value = true
    }
    
    private suspend fun discoverAndLoadPlugins(enabledPluginIds: Set<String>) {
        val pluginDir = File(pluginDirectory)
        if (!pluginDir.exists()) return
        
        val pluginFiles = pluginDir.listFiles { file ->
            file.isFile && (file.extension == "jar" || file.extension == "clbmplugin")
        } ?: return
        
        for (pluginFile in pluginFiles) {
            try {
                val result = loadPlugin(pluginFile.absolutePath)
                if (result.success && enabledPluginIds.contains(result.pluginId)) {
                    activatePlugin(result.pluginId)
                }
            } catch (e: Exception) {
                emitError("unknown", e, "discover")
            }
        }
    }
    
    suspend fun loadPlugin(pluginPath: String): PluginLoadResult = mutex.withLock {
        try {
            val pluginFile = File(pluginPath)
            
            if (requireSignatureVerification) {
                val verificationResult = _securityManager.verifyPluginSignature(pluginFile)
                if (!verificationResult.verified) {
                    return@withLock PluginLoadResult(
                        pluginId = "unknown",
                        success = false,
                        errorMessage = "Plugin signature verification failed: ${verificationResult.message}"
                    )
                }
            }
            
            val result = pluginLoader.loadPlugin(pluginPath, hostServices, _securityManager)
            
            val loadedPlugin = result.plugin
            if (result.success && loadedPlugin != null) {
                val compatResult = _securityManager.checkCompatibility(loadedPlugin.descriptor, hostVersion)
                if (!compatResult.compatible) {
                    return@withLock PluginLoadResult(
                        pluginId = result.pluginId,
                        success = false,
                        errorMessage = "Plugin incompatible: ${compatResult.reason}"
                    )
                }
                
                loadedPlugin.descriptor.permissions.forEach { permission ->
                    _securityManager.grantPermission(result.pluginId, permission)
                }
                
                val entry = PluginEntry(
                    plugin = loadedPlugin,
                    state = PluginState.INSTALLED,
                    path = pluginPath
                )
                loadedPlugins[result.pluginId] = entry
                updatePluginState(result.pluginId, PluginState.INSTALLED)
                
                lifecycleListeners.forEach { it.onPluginLoaded(loadedPlugin) }
                _eventBus.emit(PluginEvent.Loaded(result.pluginId))
            }
            
            result
        } catch (e: Exception) {
            PluginLoadResult("unknown", false, error = e)
        }
    }
    
    suspend fun activatePlugin(pluginId: String): PluginActivationResult = mutex.withLock {
        val entry = loadedPlugins[pluginId]
            ?: return@withLock PluginActivationResult(pluginId, false, errorMessage = "Plugin not loaded")
        
        if (entry.state == PluginState.ACTIVE) {
            return@withLock PluginActivationResult(pluginId, true)
        }
        
        return@withLock try {
            updatePluginState(pluginId, PluginState.STARTING)
            
            entry.plugin.onActivate()
            entry.state = PluginState.ACTIVE
            updatePluginState(pluginId, PluginState.ACTIVE)
            
            pluginStorage.setPluginEnabled(pluginId, true)
            
            lifecycleListeners.forEach { it.onPluginActivated(entry.plugin) }
            _eventBus.emit(PluginEvent.Activated(pluginId))
            
            PluginActivationResult(pluginId, true)
        } catch (e: Exception) {
            entry.state = PluginState.ERROR
            updatePluginState(pluginId, PluginState.ERROR)
            emitError(pluginId, e, "activate")
            PluginActivationResult(pluginId, false, error = e)
        }
    }
    
    suspend fun deactivatePlugin(pluginId: String): PluginActivationResult = mutex.withLock {
        val entry = loadedPlugins[pluginId]
            ?: return@withLock PluginActivationResult(pluginId, false, errorMessage = "Plugin not loaded")
        
        if (entry.state != PluginState.ACTIVE) {
            return@withLock PluginActivationResult(pluginId, true)
        }
        
        return@withLock try {
            updatePluginState(pluginId, PluginState.STOPPING)
            
            entry.plugin.onDeactivate()
            entry.state = PluginState.INSTALLED
            updatePluginState(pluginId, PluginState.INSTALLED)
            
            _extensionRegistry.unregisterAllExtensions(pluginId)
            
            pluginStorage.setPluginEnabled(pluginId, false)
            
            lifecycleListeners.forEach { it.onPluginDeactivated(entry.plugin) }
            _eventBus.emit(PluginEvent.Deactivated(pluginId))
            
            PluginActivationResult(pluginId, true)
        } catch (e: Exception) {
            entry.state = PluginState.ERROR
            updatePluginState(pluginId, PluginState.ERROR)
            emitError(pluginId, e, "deactivate")
            PluginActivationResult(pluginId, false, error = e)
        }
    }
    
    suspend fun unloadPlugin(pluginId: String): Boolean = mutex.withLock {
        val entry = loadedPlugins.remove(pluginId) ?: return@withLock false
        
        try {
            if (entry.state == PluginState.ACTIVE) {
                entry.plugin.onDeactivate()
            }
            entry.plugin.onUnload()
            
            _extensionRegistry.unregisterAllExtensions(pluginId)
            _securityManager.revokeAllPermissions(pluginId)
            updatePluginState(pluginId, PluginState.UNINSTALLED)
            
            lifecycleListeners.forEach { it.onPluginUnloaded(pluginId) }
            _eventBus.emit(PluginEvent.Unloaded(pluginId))
            
            true
        } catch (e: Exception) {
            emitError(pluginId, e, "unload")
            false
        }
    }
    
    suspend fun installPlugin(pluginPath: String): PluginLoadResult {
        val sourceFile = File(pluginPath)
        if (!sourceFile.exists()) {
            return PluginLoadResult("unknown", false, errorMessage = "Plugin file not found")
        }
        
        val targetFile = File(pluginDirectory, sourceFile.name)
        withContext(Dispatchers.IO) {
            sourceFile.copyTo(targetFile, overwrite = true)
        }
        
        val result = loadPlugin(targetFile.absolutePath)
        
        if (result.success) {
            _eventBus.emit(PluginEvent.Installed(result.pluginId, result.plugin!!.descriptor))
        }
        
        return result
    }
    
    suspend fun uninstallPlugin(pluginId: String): Boolean {
        val entry = loadedPlugins[pluginId] ?: return false
        
        unloadPlugin(pluginId)
        
        val pluginFile = File(entry.path)
        if (pluginFile.exists()) {
            withContext(Dispatchers.IO) {
                pluginFile.delete()
            }
        }
        
        pluginStorage.removePluginData(pluginId)
        
        _eventBus.emit(PluginEvent.Uninstalled(pluginId))
        
        return true
    }
    
    fun getPlugin(pluginId: String): Plugin? = loadedPlugins[pluginId]?.plugin
    
    fun getPluginDescriptor(pluginId: String): PluginDescriptor? = loadedPlugins[pluginId]?.plugin?.descriptor
    
    fun getPluginState(pluginId: String): PluginState = loadedPlugins[pluginId]?.state ?: PluginState.UNINSTALLED
    
    fun getLoadedPlugins(): List<Plugin> = loadedPlugins.values.map { it.plugin }
    
    fun getActivePlugins(): List<Plugin> = loadedPlugins.values
        .filter { it.state == PluginState.ACTIVE }
        .map { it.plugin }
    
    fun addLifecycleListener(listener: PluginLifecycleListener) {
        lifecycleListeners.add(listener)
    }
    
    fun removeLifecycleListener(listener: PluginLifecycleListener) {
        lifecycleListeners.remove(listener)
    }
    
    fun getHostService(serviceId: String): Any? = hostServices.getService(serviceId)
    
    fun <T : Any> getHostService(serviceClass: Class<T>): T? = hostServices.getService(serviceClass)
    
    fun registerHostService(serviceId: String, service: Any) {
        hostServices.registerService(serviceId, service)
    }
    
    fun <T : Any> registerHostService(serviceClass: Class<T>, service: T) {
        hostServices.registerService(serviceClass, service)
    }
    
    private fun updatePluginState(pluginId: String, state: PluginState) {
        _pluginStates.value = _pluginStates.value + (pluginId to state)
    }
    
    private suspend fun emitError(pluginId: String, error: Throwable, operation: String) {
        lifecycleListeners.forEach { it.onPluginError(pluginId, error) }
        _eventBus.emit(PluginEvent.Error(pluginId, error, error.message ?: "Unknown error", operation))
    }
    
    suspend fun shutdown() = mutex.withLock {
        for ((pluginId, entry) in loadedPlugins.toMap()) {
            try {
                if (entry.state == PluginState.ACTIVE) {
                    entry.plugin.onDeactivate()
                }
                entry.plugin.onUnload()
            } catch (e: Exception) {
                emitError(pluginId, e, "shutdown")
            }
        }
        loadedPlugins.clear()
        _isInitialized.value = false
    }
    
    private data class PluginEntry(
        val plugin: Plugin,
        var state: PluginState,
        val path: String
    )
}

interface HostServiceRegistry {
    fun getService(serviceId: String): Any?
    fun <T : Any> getService(serviceClass: Class<T>): T?
    fun registerService(serviceId: String, service: Any)
    fun <T : Any> registerService(serviceClass: Class<T>, service: T)
}

class DefaultHostServiceRegistry : HostServiceRegistry {
    private val servicesById = mutableMapOf<String, Any>()
    private val servicesByClass = mutableMapOf<Class<*>, Any>()
    
    override fun getService(serviceId: String): Any? = servicesById[serviceId]
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getService(serviceClass: Class<T>): T? = servicesByClass[serviceClass] as? T
    
    override fun registerService(serviceId: String, service: Any) {
        servicesById[serviceId] = service
    }
    
    override fun <T : Any> registerService(serviceClass: Class<T>, service: T) {
        servicesByClass[serviceClass] = service
        servicesById[serviceClass.name] = service
    }
}
