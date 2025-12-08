package com.scto.codelikebastimove.feature.designer.ui.screen

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.feature.designer.data.model.Block
import com.scto.codelikebastimove.feature.designer.data.model.BlockTree
import com.scto.codelikebastimove.feature.designer.data.model.BlockType
import com.scto.codelikebastimove.feature.designer.data.model.ComponentDefinition
import com.scto.codelikebastimove.feature.designer.data.model.DesignerProject
import com.scto.codelikebastimove.feature.designer.data.model.ExportConfig
import com.scto.codelikebastimove.feature.designer.data.model.SourceBinding
import com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor
import com.scto.codelikebastimove.feature.designer.data.model.ValidationResult
import com.scto.codelikebastimove.feature.designer.data.repository.ComponentLibraryRepository
import com.scto.codelikebastimove.feature.designer.data.repository.DesignerRepository
import com.scto.codelikebastimove.feature.designer.domain.codegen.CodeEmitter
import com.scto.codelikebastimove.feature.designer.domain.usecase.ExportManager
import com.scto.codelikebastimove.feature.designer.domain.usecase.ExportPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class DesignerUiState(
    val currentProject: DesignerProject? = null,
    val blocks: List<Block> = emptyList(),
    val selectedBlockId: String? = null,
    val selectedBlock: Block? = null,
    val scale: Float = 1f,
    val showPreview: Boolean = false,
    val showCodeDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showThemeSelector: Boolean = false,
    val showComponentCreator: Boolean = false,
    val generatedCode: String = "",
    val exportPreview: ExportPreview? = null,
    val validation: ValidationResult? = null,
    val customComponents: List<ComponentDefinition> = emptyList(),
    val savedThemes: List<ThemeDescriptor> = emptyList(),
    val selectedTheme: ThemeDescriptor? = null,
    val exportConfig: ExportConfig = ExportConfig(exportPath = ""),
    val isExporting: Boolean = false,
    val exportSuccess: Boolean? = null,
    val exportMessage: String = ""
)

class DesignerViewModel : ViewModel() {
    
    private val repository = DesignerRepository()
    private val componentRepository = ComponentLibraryRepository()
    private val codeEmitter = CodeEmitter()
    private val exportManager = ExportManager(codeEmitter)
    
    private val _uiState = MutableStateFlow(DesignerUiState())
    val uiState: StateFlow<DesignerUiState> = _uiState.asStateFlow()
    
    init {
        loadCustomComponents()
        loadSavedThemes()
    }
    
    private fun loadCustomComponents() {
        viewModelScope.launch {
            componentRepository.customComponents.collect { components ->
                _uiState.update { it.copy(customComponents = components) }
            }
        }
    }
    
    private fun loadSavedThemes() {
        viewModelScope.launch {
            repository.savedThemes.collect { themes ->
                _uiState.update { it.copy(savedThemes = themes) }
            }
        }
    }
    
    fun createNewProject(name: String, sourceBinding: SourceBinding? = null) {
        val project = repository.createProject(name, sourceBinding)
        _uiState.update { 
            it.copy(
                currentProject = project,
                blocks = emptyList(),
                selectedBlockId = null
            )
        }
    }
    
    fun loadProject(project: DesignerProject) {
        _uiState.update { 
            it.copy(
                currentProject = project,
                blocks = project.blockTree.rootBlocks,
                selectedBlockId = null,
                selectedTheme = project.themeDescriptor
            )
        }
    }
    
    fun addBlock(blockType: BlockType, position: Offset = Offset(50f, 50f)) {
        val newBlock = Block(
            id = UUID.randomUUID().toString(),
            type = blockType,
            position = position
        )
        _uiState.update { state ->
            state.copy(
                blocks = state.blocks + newBlock,
                selectedBlockId = newBlock.id,
                selectedBlock = newBlock
            )
        }
        updateProject()
    }
    
    fun addCustomComponentBlock(component: ComponentDefinition, position: Offset = Offset(50f, 50f)) {
        val newBlock = Block(
            id = UUID.randomUUID().toString(),
            type = BlockType.CUSTOM_COMPONENT,
            position = position,
            customComponentId = component.id,
            width = 200f,
            height = 100f
        )
        _uiState.update { state ->
            state.copy(
                blocks = state.blocks + newBlock,
                selectedBlockId = newBlock.id,
                selectedBlock = newBlock
            )
        }
        updateProject()
    }
    
    fun selectBlock(blockId: String?) {
        _uiState.update { state ->
            val block = state.blocks.find { it.id == blockId }
            state.copy(
                selectedBlockId = blockId,
                selectedBlock = block
            )
        }
    }
    
    fun moveBlock(blockId: String, newPosition: Offset) {
        _uiState.update { state ->
            val updatedBlocks = state.blocks.map { block ->
                if (block.id == blockId) {
                    block.copy(position = newPosition)
                } else block
            }
            val selectedBlock = updatedBlocks.find { it.id == blockId }
            state.copy(
                blocks = updatedBlocks,
                selectedBlock = selectedBlock
            )
        }
        updateProject()
    }
    
    fun deleteBlock(blockId: String) {
        _uiState.update { state ->
            state.copy(
                blocks = state.blocks.filter { it.id != blockId },
                selectedBlockId = if (state.selectedBlockId == blockId) null else state.selectedBlockId,
                selectedBlock = if (state.selectedBlockId == blockId) null else state.selectedBlock
            )
        }
        updateProject()
    }
    
    fun updateBlockProperty(propertyKey: String, value: Any?) {
        val selectedBlockId = _uiState.value.selectedBlockId ?: return
        
        _uiState.update { state ->
            val updatedBlocks = state.blocks.map { block ->
                if (block.id == selectedBlockId) {
                    when (propertyKey) {
                        "width" -> block.copy(width = value as? Float ?: block.width)
                        "height" -> block.copy(height = value as? Float ?: block.height)
                        "positionX" -> block.copy(position = block.position.copy(x = value as? Float ?: block.position.x))
                        "positionY" -> block.copy(position = block.position.copy(y = value as? Float ?: block.position.y))
                        else -> {
                            val existingProperty = block.properties[propertyKey]
                            if (existingProperty != null) {
                                val updatedProperty = existingProperty.copy(value = value)
                                block.copy(properties = block.properties + (propertyKey to updatedProperty))
                            } else block
                        }
                    }
                } else block
            }
            val selectedBlock = updatedBlocks.find { it.id == selectedBlockId }
            state.copy(
                blocks = updatedBlocks,
                selectedBlock = selectedBlock
            )
        }
        updateProject()
    }
    
    fun setScale(scale: Float) {
        _uiState.update { it.copy(scale = scale.coerceIn(0.2f, 2f)) }
    }
    
    fun togglePreview() {
        _uiState.update { it.copy(showPreview = !it.showPreview) }
    }
    
    fun showCodeDialog() {
        generateCode()
        _uiState.update { it.copy(showCodeDialog = true) }
    }
    
    fun hideCodeDialog() {
        _uiState.update { it.copy(showCodeDialog = false) }
    }
    
    fun showExportDialog() {
        prepareExport()
        _uiState.update { it.copy(showExportDialog = true) }
    }
    
    fun hideExportDialog() {
        _uiState.update { it.copy(showExportDialog = false, exportSuccess = null) }
    }
    
    fun showThemeSelector() {
        _uiState.update { it.copy(showThemeSelector = true) }
    }
    
    fun hideThemeSelector() {
        _uiState.update { it.copy(showThemeSelector = false) }
    }
    
    fun selectTheme(theme: ThemeDescriptor?) {
        _uiState.update { it.copy(selectedTheme = theme, showThemeSelector = false) }
        updateProject()
    }
    
    fun showComponentCreator() {
        _uiState.update { it.copy(showComponentCreator = true) }
    }
    
    fun hideComponentCreator() {
        _uiState.update { it.copy(showComponentCreator = false) }
    }
    
    fun createCustomComponent(component: ComponentDefinition) {
        componentRepository.addComponent(component)
        hideComponentCreator()
    }
    
    fun updateExportConfig(config: ExportConfig) {
        _uiState.update { it.copy(exportConfig = config) }
        prepareExport()
    }
    
    private fun generateCode() {
        val state = _uiState.value
        val blockTree = BlockTree(
            name = state.currentProject?.name ?: "GeneratedLayout",
            rootBlocks = state.blocks
        )
        
        val generatedCode = codeEmitter.generateCode(
            blockTree = blockTree,
            config = state.exportConfig,
            themeDescriptor = state.selectedTheme
        )
        
        val validation = codeEmitter.validateCode(generatedCode.code)
        
        _uiState.update { 
            it.copy(
                generatedCode = generatedCode.code,
                validation = validation
            )
        }
    }
    
    private fun prepareExport() {
        val state = _uiState.value
        val blockTree = BlockTree(
            name = state.currentProject?.name ?: "GeneratedLayout",
            rootBlocks = state.blocks
        )
        
        val preview = exportManager.prepareExport(
            blockTree = blockTree,
            config = state.exportConfig,
            themeDescriptor = state.selectedTheme
        )
        
        _uiState.update { 
            it.copy(
                exportPreview = preview,
                validation = preview.validation
            )
        }
    }
    
    fun performExport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            
            val state = _uiState.value
            val blockTree = BlockTree(
                name = state.currentProject?.name ?: "GeneratedLayout",
                rootBlocks = state.blocks
            )
            
            val usedCustomComponents = state.blocks
                .filter { it.type == BlockType.CUSTOM_COMPONENT }
                .mapNotNull { block -> 
                    state.customComponents.find { it.id == block.customComponentId }
                }
            
            val result = exportManager.export(
                blockTree = blockTree,
                config = state.exportConfig,
                themeDescriptor = state.selectedTheme,
                customComponents = usedCustomComponents
            )
            
            _uiState.update { 
                it.copy(
                    isExporting = false,
                    exportSuccess = result.success,
                    exportMessage = if (result.success) {
                        "Export successful: ${result.filePath}"
                    } else {
                        "Export failed: ${result.errors.joinToString(", ")}"
                    },
                    generatedCode = result.generatedCode
                )
            }
        }
    }
    
    private fun updateProject() {
        val state = _uiState.value
        val project = state.currentProject ?: return
        
        val updatedProject = project.copy(
            blockTree = project.blockTree.copy(
                rootBlocks = state.blocks,
                modifiedAt = System.currentTimeMillis()
            ),
            themeDescriptor = state.selectedTheme,
            modifiedAt = System.currentTimeMillis()
        )
        
        repository.updateProject(updatedProject)
        _uiState.update { it.copy(currentProject = updatedProject) }
    }
    
    fun saveThemeToRepository(theme: ThemeDescriptor, name: String, description: String) {
        val savedTheme = theme.copy(
            name = name,
            description = description
        )
        repository.saveTheme(savedTheme)
    }
}
