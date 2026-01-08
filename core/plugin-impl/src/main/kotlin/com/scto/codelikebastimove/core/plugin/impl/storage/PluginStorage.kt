package com.scto.codelikebastimove.core.plugin.impl.storage

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PluginStorage(
    private val storageDirectory: String
) {
    private val gson = Gson()
    private val enabledPluginsFile: File
        get() = File(storageDirectory, "enabled_plugins.json")
    
    private val pluginSettingsDir: File
        get() = File(storageDirectory, "settings")
    
    init {
        File(storageDirectory).mkdirs()
        pluginSettingsDir.mkdirs()
    }
    
    suspend fun getEnabledPlugins(): Set<String> = withContext(Dispatchers.IO) {
        if (!enabledPluginsFile.exists()) {
            return@withContext emptySet()
        }
        
        try {
            val json = enabledPluginsFile.readText()
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson<Set<String>>(json, type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    suspend fun setPluginEnabled(pluginId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        val current = getEnabledPlugins().toMutableSet()
        
        if (enabled) {
            current.add(pluginId)
        } else {
            current.remove(pluginId)
        }
        
        saveEnabledPlugins(current)
    }
    
    private suspend fun saveEnabledPlugins(pluginIds: Set<String>) = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(pluginIds)
            enabledPluginsFile.writeText(json)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun getPluginSettings(pluginId: String): Map<String, Any> = withContext(Dispatchers.IO) {
        val settingsFile = File(pluginSettingsDir, "$pluginId.json")
        
        if (!settingsFile.exists()) {
            return@withContext emptyMap()
        }
        
        try {
            val json = settingsFile.readText()
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson<Map<String, Any>>(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    suspend fun savePluginSettings(pluginId: String, settings: Map<String, Any>) = withContext(Dispatchers.IO) {
        try {
            val settingsFile = File(pluginSettingsDir, "$pluginId.json")
            val json = gson.toJson(settings)
            settingsFile.writeText(json)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun removePluginData(pluginId: String) = withContext(Dispatchers.IO) {
        val settingsFile = File(pluginSettingsDir, "$pluginId.json")
        if (settingsFile.exists()) {
            settingsFile.delete()
        }
        
        setPluginEnabled(pluginId, false)
    }
    
    suspend fun clearAllPluginData() = withContext(Dispatchers.IO) {
        pluginSettingsDir.listFiles()?.forEach { it.delete() }
        if (enabledPluginsFile.exists()) {
            enabledPluginsFile.delete()
        }
    }
}
