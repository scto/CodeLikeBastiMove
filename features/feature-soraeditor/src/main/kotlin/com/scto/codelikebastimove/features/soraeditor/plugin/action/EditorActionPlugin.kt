package com.scto.codelikebastimove.features.soraeditor.plugin.action

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.contribution.*
import com.scto.codelikebastimove.core.actions.api.keybinding.ResolvedKeybinding
import com.scto.codelikebastimove.core.plugin.api.extension.Extension
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionPointDescriptor

interface EditorActionPlugin : Extension, ActionContributor {
    val category: ActionCategory get() = ActionCategory.EDIT
    
    fun getEditorActions(): List<EditorPluginAction>
    
    fun getContextMenuActions(): List<EditorPluginAction> get() = emptyList()
    
    fun getToolbarActions(): List<EditorPluginAction> get() = emptyList()
}

data class EditorPluginAction(
    override val id: String,
    override val name: String,
    override val description: String = "",
    override val category: ActionCategory = ActionCategory.EDIT,
    override val icon: String? = null,
    val keybinding: Keybinding? = null,
    val whenCondition: ActionWhen? = null,
    val menuLocations: List<String> = emptyList(),
    val handler: suspend (EditorActionContext) -> ActionResult
) : Action {
    
    override val isEnabled: Boolean = true
    override val priority: Int = 0
    
    override suspend fun execute(context: ActionContext): ActionResult {
        val editorContext = EditorActionContext(
            filePath = context.filePath ?: "",
            fileName = context.fileName ?: "",
            fileExtension = context.fileExtension ?: "",
            selectedText = context.selectedText,
            cursorLine = context.cursorLine,
            cursorColumn = context.cursorColumn,
            content = context.content ?: ""
        )
        return handler(editorContext)
    }
    
    override fun canExecute(context: ActionContext): Boolean {
        return whenCondition?.evaluate(context) ?: true
    }
}

data class EditorActionContext(
    val filePath: String,
    val fileName: String,
    val fileExtension: String,
    val selectedText: String?,
    val cursorLine: Int,
    val cursorColumn: Int,
    val content: String,
    val editor: Any? = null
)

abstract class AbstractEditorActionPlugin : EditorActionPlugin {
    
    override val description: String get() = "Editor action plugin: $name"
    override val priority: Int get() = 0
    
    override fun getActionContributions(): List<EditorActionContribution> {
        return getEditorActions().map { action ->
            EditorActionContribution(
                action = action,
                keybinding = action.keybinding,
                menuContributions = action.menuLocations.map { menuId ->
                    MenuContribution(
                        actionId = action.id,
                        menuId = menuId,
                        order = action.priority
                    )
                },
                when = action.whenCondition
            )
        }
    }
    
    override fun getCommandContributions(): List<CommandContribution> {
        return getEditorActions().map { action ->
            CommandContribution(
                id = action.id,
                title = action.name,
                category = action.category.name,
                icon = action.icon,
                enablement = action.whenCondition,
                handler = { context -> action.execute(context) }
            )
        }
    }
    
    override fun getKeybindingContributions(): List<ResolvedKeybinding> {
        return getEditorActions()
            .filter { it.keybinding != null }
            .map { action ->
                ResolvedKeybinding(
                    actionId = action.id,
                    keybinding = action.keybinding!!,
                    when = action.whenCondition
                )
            }
    }
    
    override fun getMenuContributions(): Map<String, List<MenuItemContribution>> {
        val menuMap = mutableMapOf<String, MutableList<MenuItemContribution>>()
        
        getEditorActions().forEach { action ->
            action.menuLocations.forEach { menuId ->
                menuMap.getOrPut(menuId) { mutableListOf() }.add(
                    MenuItemContribution(
                        commandId = action.id,
                        order = action.priority,
                        when = action.whenCondition
                    )
                )
            }
        }
        
        return menuMap
    }
}

object EditorActionExtensionPoint {
    val DESCRIPTOR = ExtensionPointDescriptor(
        id = "com.scto.clbm.extension.editorActions",
        name = "Editor Actions",
        extensionClass = EditorActionPlugin::class,
        description = "Editor actions, commands, and keyboard shortcuts",
        allowMultiple = true
    )
}

object BuiltinEditorActions {
    fun createUndoAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.undo",
        name = "Undo",
        description = "Undo the last action",
        category = ActionCategory.EDIT,
        icon = "undo",
        keybinding = Keybinding("Z", setOf(KeyModifier.CTRL)),
        menuLocations = listOf(MenuIds.EDITOR_CONTEXT)
    ) { _ ->
        ActionResult.Success("Undo executed")
    }
    
    fun createRedoAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.redo",
        name = "Redo",
        description = "Redo the last undone action",
        category = ActionCategory.EDIT,
        icon = "redo",
        keybinding = Keybinding("Y", setOf(KeyModifier.CTRL)),
        menuLocations = listOf(MenuIds.EDITOR_CONTEXT)
    ) { _ ->
        ActionResult.Success("Redo executed")
    }
    
    fun createCutAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.cut",
        name = "Cut",
        description = "Cut selected text",
        category = ActionCategory.EDIT,
        icon = "content_cut",
        keybinding = Keybinding("X", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorTextFocus = true),
        menuLocations = listOf(MenuIds.EDITOR_CONTEXT)
    ) { context ->
        ActionResult.Success("Cut: ${context.selectedText}")
    }
    
    fun createCopyAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.copy",
        name = "Copy",
        description = "Copy selected text",
        category = ActionCategory.EDIT,
        icon = "content_copy",
        keybinding = Keybinding("C", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorTextFocus = true),
        menuLocations = listOf(MenuIds.EDITOR_CONTEXT)
    ) { context ->
        ActionResult.Success("Copied: ${context.selectedText}")
    }
    
    fun createPasteAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.paste",
        name = "Paste",
        description = "Paste from clipboard",
        category = ActionCategory.EDIT,
        icon = "content_paste",
        keybinding = Keybinding("V", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorTextFocus = true),
        menuLocations = listOf(MenuIds.EDITOR_CONTEXT)
    ) { _ ->
        ActionResult.Success("Paste executed")
    }
    
    fun createSelectAllAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.selectAll",
        name = "Select All",
        description = "Select all text",
        category = ActionCategory.EDIT,
        keybinding = Keybinding("A", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorTextFocus = true)
    ) { _ ->
        ActionResult.Success("Select all executed")
    }
    
    fun createFindAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.find",
        name = "Find",
        description = "Open find dialog",
        category = ActionCategory.SEARCH,
        icon = "search",
        keybinding = Keybinding("F", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorFocus = true)
    ) { _ ->
        ActionResult.Success("Find dialog opened")
    }
    
    fun createReplaceAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.replace",
        name = "Replace",
        description = "Open find and replace dialog",
        category = ActionCategory.SEARCH,
        icon = "find_replace",
        keybinding = Keybinding("H", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorFocus = true)
    ) { _ ->
        ActionResult.Success("Replace dialog opened")
    }
    
    fun createFormatDocumentAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.formatDocument",
        name = "Format Document",
        description = "Format the entire document",
        category = ActionCategory.EDIT,
        icon = "format_align_left",
        keybinding = Keybinding("F", setOf(KeyModifier.CTRL, KeyModifier.SHIFT)),
        whenCondition = ActionWhen(editorTextFocus = true),
        menuLocations = listOf(MenuIds.EDITOR_CONTEXT)
    ) { _ ->
        ActionResult.Success("Document formatted")
    }
    
    fun createCommentLineAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.commentLine",
        name = "Toggle Line Comment",
        description = "Toggle line comment",
        category = ActionCategory.EDIT,
        keybinding = Keybinding("/", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorTextFocus = true)
    ) { _ ->
        ActionResult.Success("Line comment toggled")
    }
    
    fun createGoToLineAction(): EditorPluginAction = EditorPluginAction(
        id = "editor.action.goToLine",
        name = "Go to Line",
        description = "Go to a specific line number",
        category = ActionCategory.NAVIGATION,
        keybinding = Keybinding("G", setOf(KeyModifier.CTRL)),
        whenCondition = ActionWhen(editorFocus = true)
    ) { _ ->
        ActionResult.Success("Go to line dialog opened")
    }
    
    fun getAllBuiltinActions(): List<EditorPluginAction> = listOf(
        createUndoAction(),
        createRedoAction(),
        createCutAction(),
        createCopyAction(),
        createPasteAction(),
        createSelectAllAction(),
        createFindAction(),
        createReplaceAction(),
        createFormatDocumentAction(),
        createCommentLineAction(),
        createGoToLineAction()
    )
}
