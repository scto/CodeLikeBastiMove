package com.scto.codelikebastimove.feature.designer.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNode
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNodeType
import com.scto.codelikebastimove.feature.designer.data.model.ComposableFunction
import com.scto.codelikebastimove.feature.designer.data.model.ModifierCall
import com.scto.codelikebastimove.feature.designer.data.model.ModifierChain
import com.scto.codelikebastimove.feature.designer.data.model.ParsedComposeFile
import com.scto.codelikebastimove.feature.designer.domain.parser.ComposeParser
import com.scto.codelikebastimove.feature.designer.domain.sync.CodeSynchronizer
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PreviewUiState(
    val filePath: String = "",
    val fileName: String = "",
    val sourceCode: String = "",
    val originalCode: String = "",
    val parsedFile: ParsedComposeFile? = null,
    val selectedComposable: ComposableFunction? = null,
    val selectedComposableIndex: Int = 0,
    val previewNode: ComposeNode? = null,
    val selectedNodeId: String? = null,
    val selectedNode: ComposeNode? = null,
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val hasComposeContent: Boolean = false,
    val errorMessage: String? = null,
    val viewMode: PreviewViewMode = PreviewViewMode.SPLIT,
    val devicePreview: DevicePreview = DevicePreview.PHONE,
    val showPropertyEditor: Boolean = true,
    val showComponentPalette: Boolean = true,
    val zoomLevel: Float = 1f,
    val undoStack: List<String> = emptyList(),
    val redoStack: List<String> = emptyList(),
)

enum class PreviewViewMode {
    CODE_ONLY,
    PREVIEW_ONLY,
    SPLIT,
}

enum class DevicePreview(val displayName: String, val width: Int, val height: Int) {
    PHONE("Phone", 360, 640),
    PHONE_LANDSCAPE("Phone Landscape", 640, 360),
    TABLET("Tablet", 800, 1280),
    TABLET_LANDSCAPE("Tablet Landscape", 1280, 800),
    FOLDABLE("Foldable", 673, 841),
    DESKTOP("Desktop", 1920, 1080),
}

class ComposePreviewViewModel : ViewModel() {

    private val parser = ComposeParser()
    private val synchronizer = CodeSynchronizer(parser)

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    fun loadFile(filePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val content = withContext(Dispatchers.IO) {
                    File(filePath).readText()
                }

                val parsed = parser.parseFile(content, filePath)
                val firstComposable = parsed.composables.firstOrNull()

                _uiState.update {
                    it.copy(
                        filePath = filePath,
                        fileName = File(filePath).name,
                        sourceCode = content,
                        originalCode = content,
                        parsedFile = parsed,
                        selectedComposable = firstComposable,
                        selectedComposableIndex = 0,
                        previewNode = firstComposable?.body,
                        hasComposeContent = parsed.hasComposeContent,
                        isLoading = false,
                        hasUnsavedChanges = false,
                        undoStack = emptyList(),
                        redoStack = emptyList(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load file: ${e.message}",
                        hasComposeContent = false,
                    )
                }
            }
        }
    }

    fun loadFromContent(content: String, fileName: String = "Preview.kt") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val parsed = parser.parseFile(content, fileName)
                val firstComposable = parsed.composables.firstOrNull()

                _uiState.update {
                    it.copy(
                        filePath = "",
                        fileName = fileName,
                        sourceCode = content,
                        originalCode = content,
                        parsedFile = parsed,
                        selectedComposable = firstComposable,
                        selectedComposableIndex = 0,
                        previewNode = firstComposable?.body,
                        hasComposeContent = parsed.hasComposeContent,
                        isLoading = false,
                        hasUnsavedChanges = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to parse content: ${e.message}",
                        hasComposeContent = false,
                    )
                }
            }
        }
    }

    fun selectComposable(index: Int) {
        val parsed = _uiState.value.parsedFile ?: return
        if (index !in parsed.composables.indices) return

        val composable = parsed.composables[index]
        _uiState.update {
            it.copy(
                selectedComposable = composable,
                selectedComposableIndex = index,
                previewNode = composable.body,
                selectedNodeId = null,
                selectedNode = null,
            )
        }
    }

    fun selectNode(nodeId: String?) {
        val previewNode = _uiState.value.previewNode
        val selectedNode = if (nodeId != null) previewNode?.findById(nodeId) else null

        _uiState.update {
            it.copy(
                selectedNodeId = nodeId,
                selectedNode = selectedNode,
            )
        }
    }

    fun updateNodeProperty(propertyName: String, newValue: Any?) {
        val state = _uiState.value
        val nodeId = state.selectedNodeId ?: return
        val composable = state.selectedComposable ?: return

        val result = synchronizer.updateNodeProperty(
            sourceCode = state.sourceCode,
            nodeId = nodeId,
            propertyName = propertyName,
            newValue = newValue,
            composableFunction = composable,
        )

        if (result.success) {
            pushUndoState()
            updateSourceCode(result.updatedCode)
        } else {
            _uiState.update { it.copy(errorMessage = result.error) }
        }
    }

    fun addModifier(modifierName: String, arguments: Map<String, Any?> = emptyMap()) {
        val state = _uiState.value
        val nodeId = state.selectedNodeId ?: return
        val composable = state.selectedComposable ?: return

        val modifierCall = ModifierCall(modifierName, arguments)
        val result = synchronizer.addModifierCall(
            sourceCode = state.sourceCode,
            nodeId = nodeId,
            modifierCall = modifierCall,
            composableFunction = composable,
        )

        if (result.success) {
            pushUndoState()
            updateSourceCode(result.updatedCode)
        } else {
            _uiState.update { it.copy(errorMessage = result.error) }
        }
    }

    fun removeModifier(modifierName: String) {
        val state = _uiState.value
        val nodeId = state.selectedNodeId ?: return
        val composable = state.selectedComposable ?: return

        val result = synchronizer.removeModifierCall(
            sourceCode = state.sourceCode,
            nodeId = nodeId,
            modifierName = modifierName,
            composableFunction = composable,
        )

        if (result.success) {
            pushUndoState()
            updateSourceCode(result.updatedCode)
        } else {
            _uiState.update { it.copy(errorMessage = result.error) }
        }
    }

    fun addChildComponent(parentNodeId: String, componentType: ComposeNodeType) {
        val state = _uiState.value
        val composable = state.selectedComposable ?: return

        val newNodeCode = generateComponentCode(componentType)
        val result = synchronizer.addChildNode(
            sourceCode = state.sourceCode,
            parentNodeId = parentNodeId,
            newNodeCode = newNodeCode,
            composableFunction = composable,
        )

        if (result.success) {
            pushUndoState()
            updateSourceCode(result.updatedCode)
        } else {
            _uiState.update { it.copy(errorMessage = result.error) }
        }
    }

    fun deleteSelectedNode() {
        val state = _uiState.value
        val nodeId = state.selectedNodeId ?: return
        val composable = state.selectedComposable ?: return

        val result = synchronizer.removeNode(
            sourceCode = state.sourceCode,
            nodeId = nodeId,
            composableFunction = composable,
        )

        if (result.success) {
            pushUndoState()
            updateSourceCode(result.updatedCode)
            _uiState.update { it.copy(selectedNodeId = null, selectedNode = null) }
        } else {
            _uiState.update { it.copy(errorMessage = result.error) }
        }
    }

    fun wrapSelectedWithContainer(containerType: String) {
        val state = _uiState.value
        val nodeId = state.selectedNodeId ?: return
        val composable = state.selectedComposable ?: return

        val result = synchronizer.wrapNodeWithContainer(
            sourceCode = state.sourceCode,
            nodeId = nodeId,
            containerType = containerType,
            composableFunction = composable,
        )

        if (result.success) {
            pushUndoState()
            updateSourceCode(result.updatedCode)
        } else {
            _uiState.update { it.copy(errorMessage = result.error) }
        }
    }

    fun updateSourceCodeManually(newCode: String) {
        pushUndoState()
        updateSourceCode(newCode)
    }

    private fun updateSourceCode(newCode: String) {
        val parsed = parser.parseFile(newCode, _uiState.value.filePath)
        val composableIndex = _uiState.value.selectedComposableIndex
        val composable = parsed.composables.getOrNull(composableIndex)
            ?: parsed.composables.firstOrNull()

        _uiState.update {
            it.copy(
                sourceCode = newCode,
                parsedFile = parsed,
                selectedComposable = composable,
                previewNode = composable?.body,
                hasComposeContent = parsed.hasComposeContent,
                hasUnsavedChanges = newCode != it.originalCode,
            )
        }
    }

    private fun pushUndoState() {
        val currentCode = _uiState.value.sourceCode
        _uiState.update {
            it.copy(
                undoStack = it.undoStack + currentCode,
                redoStack = emptyList(),
            )
        }
    }

    fun undo() {
        val undoStack = _uiState.value.undoStack
        if (undoStack.isEmpty()) return

        val previousCode = undoStack.last()
        val currentCode = _uiState.value.sourceCode

        _uiState.update {
            it.copy(
                undoStack = undoStack.dropLast(1),
                redoStack = it.redoStack + currentCode,
            )
        }

        updateSourceCode(previousCode)
    }

    fun redo() {
        val redoStack = _uiState.value.redoStack
        if (redoStack.isEmpty()) return

        val nextCode = redoStack.last()
        val currentCode = _uiState.value.sourceCode

        _uiState.update {
            it.copy(
                redoStack = redoStack.dropLast(1),
                undoStack = it.undoStack + currentCode,
            )
        }

        updateSourceCode(nextCode)
    }

    fun saveFile() {
        val state = _uiState.value
        if (state.filePath.isBlank()) return

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    File(state.filePath).writeText(state.sourceCode)
                }
                _uiState.update {
                    it.copy(
                        originalCode = state.sourceCode,
                        hasUnsavedChanges = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to save file: ${e.message}")
                }
            }
        }
    }

    fun setViewMode(mode: PreviewViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun setDevicePreview(device: DevicePreview) {
        _uiState.update { it.copy(devicePreview = device) }
    }

    fun setZoomLevel(zoom: Float) {
        _uiState.update { it.copy(zoomLevel = zoom.coerceIn(0.25f, 3f)) }
    }

    fun togglePropertyEditor() {
        _uiState.update { it.copy(showPropertyEditor = !it.showPropertyEditor) }
    }

    fun toggleComponentPalette() {
        _uiState.update { it.copy(showComponentPalette = !it.showComponentPalette) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun generateComponentCode(type: ComposeNodeType): String {
        return when (type) {
            is ComposeNodeType.Column -> "Column {\n    // Content\n}"
            is ComposeNodeType.Row -> "Row {\n    // Content\n}"
            is ComposeNodeType.Box -> "Box {\n    // Content\n}"
            is ComposeNodeType.Card -> "Card {\n    // Content\n}"
            is ComposeNodeType.Text -> "Text(text = \"Text\")"
            is ComposeNodeType.Button -> "Button(onClick = { }) {\n    Text(\"Button\")\n}"
            is ComposeNodeType.TextField -> "TextField(\n    value = \"\",\n    onValueChange = { },\n    label = { Text(\"Label\") }\n)"
            is ComposeNodeType.Icon -> "Icon(\n    imageVector = Icons.Default.Star,\n    contentDescription = null\n)"
            is ComposeNodeType.Spacer -> "Spacer(modifier = Modifier.height(16.dp))"
            is ComposeNodeType.HorizontalDivider -> "HorizontalDivider()"
            else -> "${type.name}()"
        }
    }
}
