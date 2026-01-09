package com.scto.codelikebastimove.core.plugin.impl.loader

import com.scto.codelikebastimove.core.plugin.api.annotations.PluginInfo
import com.scto.codelikebastimove.core.plugin.api.context.PluginContext
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginCategory
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginDescriptor
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginPermission
import com.scto.codelikebastimove.core.plugin.api.lifecycle.Plugin
import com.scto.codelikebastimove.core.plugin.api.lifecycle.PluginLoadResult
import com.scto.codelikebastimove.core.plugin.impl.manager.HostServiceRegistry
import com.scto.codelikebastimove.core.plugin.impl.manager.PluginManager
import com.scto.codelikebastimove.core.plugin.impl.security.PluginSecurityManager
import dalvik.system.DexClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.File
import java.util.jar.JarFile

class PluginLoader(
    private val pluginManager: PluginManager
) {
    private val loadedClassLoaders = mutableMapOf<String, ClassLoader>()
    
    suspend fun loadPlugin(
        pluginPath: String,
        hostServices: HostServiceRegistry,
        securityManager: PluginSecurityManager
    ): PluginLoadResult = withContext(Dispatchers.IO) {
        try {
            val pluginFile = File(pluginPath)
            if (!pluginFile.exists()) {
                return@withContext PluginLoadResult(
                    pluginId = "unknown",
                    success = false,
                    errorMessage = "Plugin file not found: $pluginPath"
                )
            }
            
            val manifest = parsePluginManifest(pluginFile)
                ?: return@withContext PluginLoadResult(
                    pluginId = "unknown",
                    success = false,
                    errorMessage = "Could not parse plugin manifest"
                )
            
            val classLoader = createClassLoader(pluginFile, manifest.mainClass)
            
            val pluginClass = classLoader.loadClass(manifest.mainClass)
            
            if (!Plugin::class.java.isAssignableFrom(pluginClass)) {
                return@withContext PluginLoadResult(
                    pluginId = manifest.id,
                    success = false,
                    errorMessage = "Main class does not implement Plugin interface"
                )
            }
            
            val plugin = createPluginInstance(pluginClass, manifest)
            
            val context = createPluginContext(manifest.id, plugin.descriptor, hostServices, securityManager)
            plugin.onLoad(context)
            
            loadedClassLoaders[manifest.id] = classLoader
            
            PluginLoadResult(
                pluginId = manifest.id,
                success = true,
                plugin = plugin
            )
        } catch (e: Exception) {
            PluginLoadResult(
                pluginId = "unknown",
                success = false,
                error = e
            )
        }
    }
    
    private fun parsePluginManifest(pluginFile: File): PluginManifest? {
        return try {
            JarFile(pluginFile).use { jar ->
                val manifestEntry = jar.getEntry("plugin.properties")
                    ?: jar.getEntry("META-INF/plugin.properties")
                
                if (manifestEntry != null) {
                    val properties = java.util.Properties()
                    jar.getInputStream(manifestEntry).use { stream ->
                        properties.load(stream)
                    }
                    PluginManifest(
                        id = properties.getProperty("plugin.id", "unknown"),
                        name = properties.getProperty("plugin.name", "Unknown Plugin"),
                        version = properties.getProperty("plugin.version", "1.0.0"),
                        mainClass = properties.getProperty("plugin.mainClass", ""),
                        description = properties.getProperty("plugin.description", ""),
                        author = properties.getProperty("plugin.author", ""),
                        minHostVersion = properties.getProperty("plugin.minHostVersion", "1.0.0"),
                        category = properties.getProperty("plugin.category", "GENERAL")
                    )
                } else {
                    scanForPluginClass(jar)
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun scanForPluginClass(jar: JarFile): PluginManifest? {
        val entries = jar.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name.endsWith(".class") && !entry.name.contains("$")) {
                val className = entry.name
                    .replace("/", ".")
                    .removeSuffix(".class")
                
                if (className.contains("Plugin")) {
                    return PluginManifest(
                        id = className.substringAfterLast(".").lowercase(),
                        name = className.substringAfterLast("."),
                        version = "1.0.0",
                        mainClass = className
                    )
                }
            }
        }
        return null
    }
    
    private fun createClassLoader(pluginFile: File, mainClass: String): ClassLoader {
        val optimizedDir = File(pluginFile.parent, "optimized")
        optimizedDir.mkdirs()
        
        return DexClassLoader(
            pluginFile.absolutePath,
            optimizedDir.absolutePath,
            null,
            javaClass.classLoader
        )
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun createPluginInstance(pluginClass: Class<*>, manifest: PluginManifest): Plugin {
        val pluginInfo = pluginClass.getAnnotation(PluginInfo::class.java)
        
        val descriptor = if (pluginInfo != null) {
            PluginDescriptor(
                id = pluginInfo.id,
                name = pluginInfo.name,
                version = pluginInfo.version,
                description = pluginInfo.description,
                author = pluginInfo.author,
                minHostVersion = pluginInfo.minHostVersion,
                category = pluginInfo.category
            )
        } else {
            PluginDescriptor(
                id = manifest.id,
                name = manifest.name,
                version = manifest.version,
                description = manifest.description,
                author = manifest.author,
                minHostVersion = manifest.minHostVersion,
                category = PluginCategory.valueOf(manifest.category.uppercase())
            )
        }
        
        val constructor = pluginClass.getDeclaredConstructor()
        constructor.isAccessible = true
        return constructor.newInstance() as Plugin
    }
    
    private fun createPluginContext(
        pluginId: String,
        descriptor: PluginDescriptor,
        hostServices: HostServiceRegistry,
        securityManager: PluginSecurityManager
    ): PluginContext {
        return DefaultPluginContext(
            pluginId = pluginId,
            descriptor = descriptor,
            pluginManager = pluginManager,
            hostServices = hostServices,
            securityManager = securityManager
        )
    }
    
    fun unloadPlugin(pluginId: String) {
        loadedClassLoaders.remove(pluginId)
    }
}

private data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val mainClass: String,
    val description: String = "",
    val author: String = "",
    val minHostVersion: String = "1.0.0",
    val category: String = "GENERAL"
)

private class DefaultPluginContext(
    override val pluginId: String,
    override val descriptor: PluginDescriptor,
    private val pluginManager: PluginManager,
    private val hostServices: HostServiceRegistry,
    private val securityManager: PluginSecurityManager
) : PluginContext {
    
    override val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val logger = object : com.scto.codelikebastimove.core.plugin.api.context.PluginLogger {
        override fun verbose(message: String) {
            android.util.Log.v("Plugin:$pluginId", message)
        }
        
        override fun debug(message: String) {
            android.util.Log.d("Plugin:$pluginId", message)
        }
        
        override fun info(message: String) {
            android.util.Log.i("Plugin:$pluginId", message)
        }
        
        override fun warn(message: String) {
            android.util.Log.w("Plugin:$pluginId", message)
        }
        
        override fun error(message: String, throwable: Throwable?) {
            android.util.Log.e("Plugin:$pluginId", message, throwable)
        }
    }
    
    private val dataStore = InMemoryPluginDataStore()
    
    override fun getLogger() = logger
    
    override fun getDataStore() = dataStore
    
    override fun getExtensionRegistry() = pluginManager.extensionRegistry
    
    override fun getHostService(serviceId: String): Any? = hostServices.getService(serviceId)
    
    override fun <T : Any> getHostService(serviceClass: Class<T>): T? = hostServices.getService(serviceClass)
    
    override fun hasPermission(permission: PluginPermission): Boolean {
        return securityManager.hasPermission(pluginId, permission)
    }
    
    override fun requestPermission(permission: PluginPermission): Boolean {
        if (securityManager.hasPermission(pluginId, permission)) {
            return true
        }
        securityManager.grantPermission(pluginId, permission)
        return true
    }
    
    override fun getPluginDirectory(): String = "/data/plugins/$pluginId"
    
    override fun getStorageDirectory(): String = "/data/plugins/$pluginId/storage"
}

private class InMemoryPluginDataStore : com.scto.codelikebastimove.core.plugin.api.context.PluginDataStore {
    private val stringStore = mutableMapOf<String, String>()
    private val intStore = mutableMapOf<String, Int>()
    private val longStore = mutableMapOf<String, Long>()
    private val booleanStore = mutableMapOf<String, Boolean>()
    private val floatStore = mutableMapOf<String, Float>()
    private val stringSetStore = mutableMapOf<String, Set<String>>()
    
    override suspend fun getString(key: String, default: String) = stringStore[key] ?: default
    override suspend fun putString(key: String, value: String) { stringStore[key] = value }
    
    override suspend fun getInt(key: String, default: Int) = intStore[key] ?: default
    override suspend fun putInt(key: String, value: Int) { intStore[key] = value }
    
    override suspend fun getLong(key: String, default: Long) = longStore[key] ?: default
    override suspend fun putLong(key: String, value: Long) { longStore[key] = value }
    
    override suspend fun getBoolean(key: String, default: Boolean) = booleanStore[key] ?: default
    override suspend fun putBoolean(key: String, value: Boolean) { booleanStore[key] = value }
    
    override suspend fun getFloat(key: String, default: Float) = floatStore[key] ?: default
    override suspend fun putFloat(key: String, value: Float) { floatStore[key] = value }
    
    override suspend fun getStringSet(key: String, default: Set<String>) = stringSetStore[key] ?: default
    override suspend fun putStringSet(key: String, value: Set<String>) { stringSetStore[key] = value }
    
    override suspend fun remove(key: String) {
        stringStore.remove(key)
        intStore.remove(key)
        longStore.remove(key)
        booleanStore.remove(key)
        floatStore.remove(key)
        stringSetStore.remove(key)
    }
    
    override suspend fun clear() {
        stringStore.clear()
        intStore.clear()
        longStore.clear()
        booleanStore.clear()
        floatStore.clear()
        stringSetStore.clear()
    }
    
    override suspend fun contains(key: String): Boolean {
        return stringStore.containsKey(key) ||
                intStore.containsKey(key) ||
                longStore.containsKey(key) ||
                booleanStore.containsKey(key) ||
                floatStore.containsKey(key) ||
                stringSetStore.containsKey(key)
    }
}
