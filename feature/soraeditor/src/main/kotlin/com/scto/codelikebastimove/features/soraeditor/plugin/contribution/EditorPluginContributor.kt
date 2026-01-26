package com.scto.codelikebastimove.features.soraeditor.plugin.contribution

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.contribution.*
import com.scto.codelikebastimove.core.actions.api.event.ActionEventBus
import com.scto.codelikebastimove.core.actions.api.keybinding.KeybindingService
import com.scto.codelikebastimove.core.actions.api.keybinding.ResolvedKeybinding
import com.scto.codelikebastimove.core.actions.api.registry.ActionRegistry
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionRegistry
import com.scto.codelikebastimove.features.soraeditor.plugin.action.EditorActionPlugin
import com.scto.codelikebastimove.features.soraeditor.plugin.action.EditorPluginAction
import com.scto.codelikebastimove.features.soraeditor.plugin.language.LanguagePackPlugin
import com.scto.codelikebastimove.features.soraeditor.plugin.theme.EditorThemePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorPluginManager(
    private val actionRegistry: ActionRegistry,
    private val keybindingService: KeybindingService,
    private val eventBus: ActionEventBus,
    private val extensionRegistry: ExtensionRegistry? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val _themePlugins = MutableStateFlow<List<EditorThemePlugin>>(emptyList())
    val themePlugins: StateFlow<List<EditorThemePlugin>> = _themePlugins.asStateFlow()
    
    private val _languagePlugins = MutableStateFlow<List<LanguagePackPlugin>>(emptyList())
    val languagePlugins: StateFlow<List<LanguagePackPlugin>> = _languagePlugins.asStateFlow()
    
    private val _actionPlugins = MutableStateFlow<List<EditorActionPlugin>>(emptyList())
    val actionPlugins: StateFlow<List<EditorActionPlugin>> = _actionPlugins.asStateFlow()
    
    private val _currentTheme = MutableStateFlow<EditorThemePlugin?>(null)
    val currentTheme: StateFlow<EditorThemePlugin?> = _currentTheme.asStateFlow()
    
    fun registerThemePlugin(plugin: EditorThemePlugin): Boolean {
        val current = _themePlugins.value.toMutableList()
        if (current.any { it.id == plugin.id }) return false
        current.add(plugin)
        _themePlugins.value = current
        return true
    }
    
    fun unregisterThemePlugin(pluginId: String): Boolean {
        val current = _themePlugins.value.toMutableList()
        val removed = current.removeIf { it.id == pluginId }
        if (removed) {
            _themePlugins.value = current
            if (_currentTheme.value?.id == pluginId) {
                _currentTheme.value = null
            }
        }
        return removed
    }
    
    fun setCurrentTheme(pluginId: String): Boolean {
        val plugin = _themePlugins.value.find { it.id == pluginId } ?: return false
        _currentTheme.value = plugin
        return true
    }
    
    fun registerLanguagePlugin(plugin: LanguagePackPlugin): Boolean {
        val current = _languagePlugins.value.toMutableList()
        if (current.any { it.id == plugin.id }) return false
        current.add(plugin)
        _languagePlugins.value = current
        return true
    }
    
    fun unregisterLanguagePlugin(pluginId: String): Boolean {
        val current = _languagePlugins.value.toMutableList()
        val removed = current.removeIf { it.id == pluginId }
        if (removed) {
            _languagePlugins.value = current
        }
        return removed
    }
    
    fun getLanguageForFile(filePath: String): LanguagePackPlugin? {
        val extension = filePath.substringAfterLast('.', "")
        return _languagePlugins.value.find { extension in it.fileExtensions }
    }
    
    fun registerActionPlugin(plugin: EditorActionPlugin): Boolean {
        val current = _actionPlugins.value.toMutableList()
        if (current.any { it.id == plugin.id }) return false
        current.add(plugin)
        _actionPlugins.value = current
        
        plugin.getEditorActions().forEach { action ->
            actionRegistry.registerAction(action)
            action.keybinding?.let { keybinding ->
                keybindingService.registerKeybinding(action.id, keybinding, action.whenCondition)
            }
        }
        
        return true
    }
    
    fun unregisterActionPlugin(pluginId: String): Boolean {
        val plugin = _actionPlugins.value.find { it.id == pluginId } ?: return false
        
        plugin.getEditorActions().forEach { action ->
            actionRegistry.unregisterAction(action.id)
            keybindingService.unregisterAllKeybindings(action.id)
        }
        
        val current = _actionPlugins.value.toMutableList()
        current.removeIf { it.id == pluginId }
        _actionPlugins.value = current
        
        return true
    }
    
    fun getActionForContext(context: ActionContext): List<EditorPluginAction> {
        return _actionPlugins.value
            .flatMap { it.getEditorActions() }
            .filter { it.canExecute(context) }
    }
    
    fun getContextMenuActions(context: ActionContext): List<EditorPluginAction> {
        return _actionPlugins.value
            .flatMap { it.getContextMenuActions() }
            .filter { it.canExecute(context) }
    }
    
    fun getToolbarActions(): List<EditorPluginAction> {
        return _actionPlugins.value.flatMap { it.getToolbarActions() }
    }
    
    suspend fun executeAction(actionId: String, context: ActionContext): ActionResult {
        val action = _actionPlugins.value
            .flatMap { it.getEditorActions() }
            .find { it.id == actionId }
            ?: return ActionResult.Failure("Action not found: $actionId")
        
        return action.execute(context)
    }
}

class EditorContributionLoader(
    private val pluginManager: EditorPluginManager,
    private val contributorRegistry: ContributorRegistry
) {
    fun loadContributions() {
        pluginManager.actionPlugins.value.forEach { plugin ->
            contributorRegistry.registerContributor(plugin)
        }
    }
    
    fun unloadContributions() {
        pluginManager.actionPlugins.value.forEach { plugin ->
            contributorRegistry.unregisterContributor(plugin.contributorId)
        }
    }
}

data class EditorPluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val description: String = "",
    val author: String = "",
    val repository: String? = null,
    val license: String? = null,
    val engines: EngineRequirements = EngineRequirements(),
    val categories: List<String> = emptyList(),
    val activationEvents: List<String> = emptyList(),
    val contributes: EditorPluginContributes = EditorPluginContributes()
)

data class EngineRequirements(
    val clbm: String = ">=1.0.0",
    val soraEditor: String = ">=0.23.0"
)

data class EditorPluginContributes(
    val themes: List<ThemeContribute> = emptyList(),
    val languages: List<LanguageContribute> = emptyList(),
    val grammars: List<GrammarContribute> = emptyList(),
    val snippets: List<SnippetContribute> = emptyList(),
    val commands: List<CommandContribute> = emptyList(),
    val keybindings: List<KeybindingContribute> = emptyList(),
    val menus: Map<String, List<MenuItemContribute>> = emptyMap(),
    val configuration: List<ConfigurationContribute> = emptyList()
)

data class ThemeContribute(
    val id: String,
    val label: String,
    val uiTheme: String,
    val path: String
)

data class LanguageContribute(
    val id: String,
    val aliases: List<String> = emptyList(),
    val extensions: List<String> = emptyList(),
    val configuration: String? = null,
    val icon: IconContribute? = null
)

data class IconContribute(
    val light: String,
    val dark: String
)

data class GrammarContribute(
    val language: String,
    val scopeName: String,
    val path: String,
    val embeddedLanguages: Map<String, String> = emptyMap()
)

data class SnippetContribute(
    val language: String,
    val path: String
)

data class CommandContribute(
    val command: String,
    val title: String,
    val category: String? = null,
    val icon: String? = null,
    val enablement: String? = null
)

data class KeybindingContribute(
    val command: String,
    val key: String,
    val mac: String? = null,
    val linux: String? = null,
    val win: String? = null,
    val `when`: String? = null
)

data class MenuItemContribute(
    val command: String,
    val group: String? = null,
    val `when`: String? = null
)

data class ConfigurationContribute(
    val title: String,
    val properties: Map<String, ConfigurationProperty>
)

data class ConfigurationProperty(
    val type: String,
    val default: Any? = null,
    val description: String = "",
    val enumValues: List<Any>? = null,
    val enumDescriptions: List<String>? = null
)
