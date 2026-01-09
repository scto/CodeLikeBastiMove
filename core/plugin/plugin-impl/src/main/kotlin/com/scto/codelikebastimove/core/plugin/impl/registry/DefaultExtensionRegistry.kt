package com.scto.codelikebastimove.core.plugin.impl.registry

import com.scto.codelikebastimove.core.plugin.api.extension.Extension
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionPointDescriptor
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionRegistry
import kotlin.reflect.KClass

class DefaultExtensionRegistry : ExtensionRegistry {
    
    private val extensionPoints = mutableMapOf<String, ExtensionPointDescriptor<*>>()
    private val extensions = mutableMapOf<String, MutableMap<String, MutableList<Extension>>>()
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Extension> registerExtensionPoint(descriptor: ExtensionPointDescriptor<T>) {
        extensionPoints[descriptor.id] = descriptor
        extensions.getOrPut(descriptor.id) { mutableMapOf() }
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Extension> getExtensionPoint(id: String): ExtensionPointDescriptor<T>? {
        return extensionPoints[id] as? ExtensionPointDescriptor<T>
    }
    
    override fun getExtensionPoints(): List<ExtensionPointDescriptor<*>> {
        return extensionPoints.values.toList()
    }
    
    override fun <T : Extension> registerExtension(
        extensionPointId: String,
        pluginId: String,
        extension: T
    ): Boolean {
        val point = extensionPoints[extensionPointId] ?: return false
        
        if (!point.extensionClass.java.isAssignableFrom(extension::class.java)) {
            return false
        }
        
        val pointExtensions = extensions.getOrPut(extensionPointId) { mutableMapOf() }
        val pluginExtensions = pointExtensions.getOrPut(pluginId) { mutableListOf() }
        
        if (!point.allowMultiple && pluginExtensions.isNotEmpty()) {
            return false
        }
        
        pluginExtensions.add(extension)
        return true
    }
    
    override fun <T : Extension> unregisterExtension(
        extensionPointId: String,
        pluginId: String,
        extensionId: String
    ): Boolean {
        val pointExtensions = extensions[extensionPointId] ?: return false
        val pluginExtensions = pointExtensions[pluginId] ?: return false
        
        return pluginExtensions.removeIf { it.id == extensionId }
    }
    
    override fun unregisterAllExtensions(pluginId: String) {
        extensions.values.forEach { pointExtensions ->
            pointExtensions.remove(pluginId)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Extension> getExtensions(extensionPointId: String): List<T> {
        val pointExtensions = extensions[extensionPointId] ?: return emptyList()
        
        return pointExtensions.values
            .flatten()
            .sortedByDescending { it.priority }
            .map { it as T }
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Extension> getExtensions(extensionClass: KClass<T>): List<T> {
        val matchingPoint = extensionPoints.values.find { 
            it.extensionClass == extensionClass 
        } ?: return emptyList()
        
        return getExtensions(matchingPoint.id)
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : Extension> getExtension(extensionPointId: String, extensionId: String): T? {
        val pointExtensions = extensions[extensionPointId] ?: return null
        
        for (pluginExtensions in pointExtensions.values) {
            val extension = pluginExtensions.find { it.id == extensionId }
            if (extension != null) {
                return extension as T
            }
        }
        
        return null
    }
    
    override fun getExtensionsByPlugin(pluginId: String): Map<String, List<Extension>> {
        val result = mutableMapOf<String, List<Extension>>()
        
        for ((extensionPointId, pointExtensions) in extensions) {
            val pluginExtensions = pointExtensions[pluginId]
            if (!pluginExtensions.isNullOrEmpty()) {
                result[extensionPointId] = pluginExtensions.toList()
            }
        }
        
        return result
    }
}
