package com.scto.codelikebastimove.feature.soraeditor.plugin

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.event.ActionEventBus
import com.scto.codelikebastimove.core.actions.api.keybinding.KeybindingService
import com.scto.codelikebastimove.core.actions.api.registry.ActionRegistry
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionRegistry
import com.scto.codelikebastimove.feature.soraeditor.plugin.action.BuiltinEditorActions
import com.scto.codelikebastimove.feature.soraeditor.plugin.action.EditorActionExtensionPoint
import com.scto.codelikebastimove.feature.soraeditor.plugin.action.EditorActionPlugin
import com.scto.codelikebastimove.feature.soraeditor.plugin.action.EditorPluginAction
import com.scto.codelikebastimove.feature.soraeditor.plugin.contribution.EditorPluginManager
import com.scto.codelikebastimove.feature.soraeditor.plugin.language.LanguagePackExtensionPoint
import com.scto.codelikebastimove.feature.soraeditor.plugin.language.LanguagePackPlugin
import com.scto.codelikebastimove.feature.soraeditor.plugin.theme.EditorThemeExtensionPoint
import com.scto.codelikebastimove.feature.soraeditor.plugin.theme.EditorThemePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SoraEditorPluginSystem(
    private val actionRegistry: ActionRegistry,
    private val keybindingService: KeybindingService,
    private val eventBus: ActionEventBus,
    private val extensionRegistry: ExtensionRegistry? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    val pluginManager = EditorPluginManager(
        actionRegistry = actionRegistry,
        keybindingService = keybindingService,
        eventBus = eventBus,
        extensionRegistry = extensionRegistry
    )
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    fun initialize() {
        if (_isInitialized.value) return
        
        registerExtensionPoints()
        registerBuiltinActions()
        
        _isInitialized.value = true
    }
    
    private fun registerExtensionPoints() {
        extensionRegistry?.let { registry ->
            registry.registerExtensionPoint(EditorThemeExtensionPoint.DESCRIPTOR)
            registry.registerExtensionPoint(LanguagePackExtensionPoint.DESCRIPTOR)
            registry.registerExtensionPoint(EditorActionExtensionPoint.DESCRIPTOR)
        }
    }
    
    private fun registerBuiltinActions() {
        BuiltinEditorActions.getAllBuiltinActions().forEach { action ->
            actionRegistry.registerAction(action)
            action.keybinding?.let { keybinding ->
                keybindingService.registerKeybinding(action.id, keybinding, action.whenCondition)
            }
        }
    }
    
    fun registerTheme(theme: EditorThemePlugin): Boolean {
        val result = pluginManager.registerThemePlugin(theme)
        if (result) {
            extensionRegistry?.registerExtension(
                extensionPointId = EditorThemeExtensionPoint.DESCRIPTOR.id,
                pluginId = theme.id,
                extension = theme
            )
        }
        return result
    }
    
    fun unregisterTheme(themeId: String): Boolean {
        extensionRegistry?.unregisterExtension<EditorThemePlugin>(
            extensionPointId = EditorThemeExtensionPoint.DESCRIPTOR.id,
            pluginId = themeId,
            extensionId = themeId
        )
        return pluginManager.unregisterThemePlugin(themeId)
    }
    
    fun setCurrentTheme(themeId: String): Boolean {
        return pluginManager.setCurrentTheme(themeId)
    }
    
    fun getAvailableThemes(): List<EditorThemePlugin> {
        return pluginManager.themePlugins.value
    }
    
    fun registerLanguage(language: LanguagePackPlugin): Boolean {
        val result = pluginManager.registerLanguagePlugin(language)
        if (result) {
            extensionRegistry?.registerExtension(
                extensionPointId = LanguagePackExtensionPoint.DESCRIPTOR.id,
                pluginId = language.id,
                extension = language
            )
        }
        return result
    }
    
    fun unregisterLanguage(languageId: String): Boolean {
        extensionRegistry?.unregisterExtension<LanguagePackPlugin>(
            extensionPointId = LanguagePackExtensionPoint.DESCRIPTOR.id,
            pluginId = languageId,
            extensionId = languageId
        )
        return pluginManager.unregisterLanguagePlugin(languageId)
    }
    
    fun getLanguageForFile(filePath: String): LanguagePackPlugin? {
        return pluginManager.getLanguageForFile(filePath)
    }
    
    fun getAvailableLanguages(): List<LanguagePackPlugin> {
        return pluginManager.languagePlugins.value
    }
    
    fun registerActionPlugin(plugin: EditorActionPlugin): Boolean {
        val result = pluginManager.registerActionPlugin(plugin)
        if (result) {
            extensionRegistry?.registerExtension(
                extensionPointId = EditorActionExtensionPoint.DESCRIPTOR.id,
                pluginId = plugin.id,
                extension = plugin
            )
        }
        return result
    }
    
    fun unregisterActionPlugin(pluginId: String): Boolean {
        extensionRegistry?.unregisterExtension<EditorActionPlugin>(
            extensionPointId = EditorActionExtensionPoint.DESCRIPTOR.id,
            pluginId = pluginId,
            extensionId = pluginId
        )
        return pluginManager.unregisterActionPlugin(pluginId)
    }
    
    fun getContextMenuActions(context: ActionContext): List<EditorPluginAction> {
        return pluginManager.getContextMenuActions(context)
    }
    
    fun getToolbarActions(): List<EditorPluginAction> {
        return pluginManager.getToolbarActions()
    }
    
    suspend fun executeAction(actionId: String, context: ActionContext): ActionResult {
        return pluginManager.executeAction(actionId, context)
    }
    
    fun shutdown() {
        pluginManager.themePlugins.value.forEach { it.reset() }
        _isInitialized.value = false
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SoraEditorPluginSystem? = null
        
        fun getInstance(
            actionRegistry: ActionRegistry,
            keybindingService: KeybindingService,
            eventBus: ActionEventBus,
            extensionRegistry: ExtensionRegistry? = null
        ): SoraEditorPluginSystem {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoraEditorPluginSystem(
                    actionRegistry,
                    keybindingService,
                    eventBus,
                    extensionRegistry
                ).also { INSTANCE = it }
            }
        }
    }
}

data class EditorPluginInfo(
    val id: String,
    val name: String,
    val type: EditorPluginType,
    val version: String,
    val description: String,
    val author: String,
    val isEnabled: Boolean
)

enum class EditorPluginType {
    THEME, LANGUAGE, ACTION, EXTENSION
}

interface EditorPluginInstaller {
    suspend fun installFromManifest(manifestPath: String): Result<EditorPluginInfo>
    suspend fun installFromUrl(url: String): Result<EditorPluginInfo>
    suspend fun uninstall(pluginId: String): Boolean
    fun getInstalledPlugins(): List<EditorPluginInfo>
}
