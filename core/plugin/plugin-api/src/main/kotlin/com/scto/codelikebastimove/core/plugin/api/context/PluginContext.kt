package com.scto.codelikebastimove.core.plugin.api.context

import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginDescriptor
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginPermission
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionRegistry
import kotlinx.coroutines.CoroutineScope

interface PluginContext {
  val pluginId: String
  val descriptor: PluginDescriptor
  val scope: CoroutineScope

  fun getLogger(): PluginLogger

  fun getDataStore(): PluginDataStore

  fun getExtensionRegistry(): ExtensionRegistry

  fun getHostService(serviceId: String): Any?

  fun <T : Any> getHostService(serviceClass: Class<T>): T?

  fun hasPermission(permission: PluginPermission): Boolean

  fun requestPermission(permission: PluginPermission): Boolean

  fun getPluginDirectory(): String

  fun getStorageDirectory(): String
}

interface PluginLogger {
  fun verbose(message: String)

  fun debug(message: String)

  fun info(message: String)

  fun warn(message: String)

  fun error(message: String, throwable: Throwable? = null)
}

interface PluginDataStore {
  suspend fun getString(key: String, default: String = ""): String

  suspend fun putString(key: String, value: String)

  suspend fun getInt(key: String, default: Int = 0): Int

  suspend fun putInt(key: String, value: Int)

  suspend fun getLong(key: String, default: Long = 0L): Long

  suspend fun putLong(key: String, value: Long)

  suspend fun getBoolean(key: String, default: Boolean = false): Boolean

  suspend fun putBoolean(key: String, value: Boolean)

  suspend fun getFloat(key: String, default: Float = 0f): Float

  suspend fun putFloat(key: String, value: Float)

  suspend fun getStringSet(key: String, default: Set<String> = emptySet()): Set<String>

  suspend fun putStringSet(key: String, value: Set<String>)

  suspend fun remove(key: String)

  suspend fun clear()

  suspend fun contains(key: String): Boolean
}

class DefaultPluginLogger(private val pluginId: String) : PluginLogger {
  override fun verbose(message: String) {
    println("V/$pluginId: $message")
  }

  override fun debug(message: String) {
    println("D/$pluginId: $message")
  }

  override fun info(message: String) {
    println("I/$pluginId: $message")
  }

  override fun warn(message: String) {
    println("W/$pluginId: $message")
  }

  override fun error(message: String, throwable: Throwable?) {
    println("E/$pluginId: $message")
    throwable?.printStackTrace()
  }
}
