package com.scto.codelikebastimove.feature.designer.ui.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.Splitscreen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNode
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNodeType
import com.scto.codelikebastimove.feature.designer.data.model.NodeProperty
import com.scto.codelikebastimove.feature.designer.domain.preview.RenderComposeNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposePreviewScreen(
    viewModel: ComposePreviewViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            PreviewTopBar(
                fileName = uiState.fileName,
                hasUnsavedChanges = uiState.hasUnsavedChanges,
                viewMode = uiState.viewMode,
                canUndo = uiState.undoStack.isNotEmpty(),
                canRedo = uiState.redoStack.isNotEmpty(),
                onBackClick = onBackClick,
                onViewModeChange = viewModel::setViewMode,
                onUndo = viewModel::undo,
                onRedo = viewModel::redo,
                onSave = viewModel::saveFile,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (!uiState.hasComposeContent) {
            NoComposeContentState(
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AnimatedVisibility(
                    visible = uiState.showComponentPalette && uiState.viewMode != PreviewViewMode.CODE_ONLY,
                    enter = slideInHorizontally { -it },
                    exit = slideOutHorizontally { -it },
                ) {
                    ComponentPalette(
                        onComponentSelected = { type ->
                            uiState.selectedNodeId?.let { parentId ->
                                viewModel.addChildComponent(parentId, type)
                            }
                        },
                        modifier = Modifier
                            .width(180.dp)
                            .fillMaxHeight(),
                    )

                    VerticalDivider()
                }

                when (uiState.viewMode) {
                    PreviewViewMode.CODE_ONLY -> {
                        CodeEditorPane(
                            code = uiState.sourceCode,
                            onCodeChange = viewModel::updateSourceCodeManually,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    PreviewViewMode.PREVIEW_ONLY -> {
                        PreviewPane(
                            previewNode = uiState.previewNode,
                            selectedNodeId = uiState.selectedNodeId,
                            devicePreview = uiState.devicePreview,
                            zoomLevel = uiState.zoomLevel,
                            onNodeSelected = viewModel::selectNode,
                            onDeviceChange = viewModel::setDevicePreview,
                            onZoomChange = viewModel::setZoomLevel,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    PreviewViewMode.SPLIT -> {
                        CodeEditorPane(
                            code = uiState.sourceCode,
                            onCodeChange = viewModel::updateSourceCodeManually,
                            modifier = Modifier
                                .weight(0.45f)
                                .fillMaxHeight(),
                        )

                        VerticalDivider()

                        PreviewPane(
                            previewNode = uiState.previewNode,
                            selectedNodeId = uiState.selectedNodeId,
                            devicePreview = uiState.devicePreview,
                            zoomLevel = uiState.zoomLevel,
                            onNodeSelected = viewModel::selectNode,
                            onDeviceChange = viewModel::setDevicePreview,
                            onZoomChange = viewModel::setZoomLevel,
                            modifier = Modifier.weight(0.55f),
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.showPropertyEditor && uiState.selectedNode != null,
                    enter = slideInHorizontally { it },
                    exit = slideOutHorizontally { it },
                ) {
                    VerticalDivider()

                    PropertyEditorPane(
                        selectedNode = uiState.selectedNode,
                        onPropertyChange = viewModel::updateNodeProperty,
                        onAddModifier = viewModel::addModifier,
                        onRemoveModifier = viewModel::removeModifier,
                        onDeleteNode = viewModel::deleteSelectedNode,
                        onWrapWith = viewModel::wrapSelectedWithContainer,
                        modifier = Modifier
                            .width(250.dp)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewTopBar(
    fileName: String,
    hasUnsavedChanges: Boolean,
    viewMode: PreviewViewMode,
    canUndo: Boolean,
    canRedo: Boolean,
    onBackClick: () -> Unit,
    onViewModeChange: (PreviewViewMode) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = fileName + if (hasUnsavedChanges) " *" else "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        },
        actions = {
            IconButton(onClick = onUndo, enabled = canUndo) {
                Icon(Icons.Default.Undo, contentDescription = "Undo")
            }
            IconButton(onClick = onRedo, enabled = canRedo) {
                Icon(Icons.Default.Redo, contentDescription = "Redo")
            }

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = viewMode == PreviewViewMode.CODE_ONLY,
                onClick = { onViewModeChange(PreviewViewMode.CODE_ONLY) },
                label = { Icon(Icons.Default.Code, contentDescription = "Code", modifier = Modifier.size(16.dp)) },
            )
            FilterChip(
                selected = viewMode == PreviewViewMode.SPLIT,
                onClick = { onViewModeChange(PreviewViewMode.SPLIT) },
                label = { Icon(Icons.Outlined.Splitscreen, contentDescription = "Split", modifier = Modifier.size(16.dp)) },
            )
            FilterChip(
                selected = viewMode == PreviewViewMode.PREVIEW_ONLY,
                onClick = { onViewModeChange(PreviewViewMode.PREVIEW_ONLY) },
                label = { Icon(Icons.Outlined.DesignServices, contentDescription = "Design", modifier = Modifier.size(16.dp)) },
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSave) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier,
    )
}

@Composable
private fun CodeEditorPane(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
private fun PreviewPane(
    previewNode: ComposeNode?,
    selectedNodeId: String?,
    devicePreview: DevicePreview,
    zoomLevel: Float,
    onNodeSelected: (String?) -> Unit,
    onDeviceChange: (DevicePreview) -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeviceMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        FilterChip(
                            selected = true,
                            onClick = { showDeviceMenu = true },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (devicePreview.name.contains("TABLET"))
                                            Icons.Default.Tablet else Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(devicePreview.displayName, fontSize = 11.sp)
                                }
                            },
                        )

                        DropdownMenu(
                            expanded = showDeviceMenu,
                            onDismissRequest = { showDeviceMenu = false },
                        ) {
                            DevicePreview.entries.forEach { device ->
                                DropdownMenuItem(
                                    text = { Text(device.displayName) },
                                    onClick = {
                                        onDeviceChange(device)
                                        showDeviceMenu = false
                                    },
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onZoomChange(zoomLevel - 0.25f) },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "${(zoomLevel * 100).toInt()}%",
                        fontSize = 11.sp,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center,
                    )

                    IconButton(
                        onClick = { onZoomChange(zoomLevel + 0.25f) },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        HorizontalDivider()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        onZoomChange(zoomLevel * zoom)
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            DeviceFrame(
                devicePreview = devicePreview,
                zoomLevel = zoomLevel,
            ) {
                if (previewNode != null) {
                    RenderComposeNode(
                        node = previewNode,
                        selectedNodeId = selectedNodeId,
                        onNodeSelected = onNodeSelected,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No preview available",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceFrame(
    devicePreview: DevicePreview,
    zoomLevel: Float,
    content: @Composable () -> Unit,
) {
    val aspectRatio = devicePreview.width.toFloat() / devicePreview.height.toFloat()

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = zoomLevel
                scaleY = zoomLevel
            }
            .clip(RoundedCornerShape(24.dp))
            .border(3.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .size(
                width = (devicePreview.width * 0.5f).dp,
                height = (devicePreview.height * 0.5f).dp,
            ),
    ) {
        content()
    }
}

@Composable
private fun ComponentPalette(
    onComponentSelected: (ComposeNodeType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Components",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                item {
                    Text(
                        text = "Layout",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                items(listOf(
                    ComposeNodeType.Column,
                    ComposeNodeType.Row,
                    ComposeNodeType.Box,
                    ComposeNodeType.Card,
                )) { type ->
                    ComponentItem(type = type, onClick = { onComponentSelected(type) })
                }

                item {
                    Text(
                        text = "Text & Input",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                items(listOf(
                    ComposeNodeType.Text,
                    ComposeNodeType.TextField,
                    ComposeNodeType.OutlinedTextField,
                )) { type ->
                    ComponentItem(type = type, onClick = { onComponentSelected(type) })
                }

                item {
                    Text(
                        text = "Buttons",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                items(listOf(
                    ComposeNodeType.Button,
                    ComposeNodeType.OutlinedButton,
                    ComposeNodeType.TextButton,
                    ComposeNodeType.IconButton,
                    ComposeNodeType.FloatingActionButton,
                )) { type ->
                    ComponentItem(type = type, onClick = { onComponentSelected(type) })
                }

                item {
                    Text(
                        text = "Media",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                items(listOf(
                    ComposeNodeType.Icon,
                    ComposeNodeType.Image,
                )) { type ->
                    ComponentItem(type = type, onClick = { onComponentSelected(type) })
                }

                item {
                    Text(
                        text = "Selection",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                items(listOf(
                    ComposeNodeType.Switch,
                    ComposeNodeType.Checkbox,
                    ComposeNodeType.RadioButton,
                    ComposeNodeType.Slider,
                )) { type ->
                    ComponentItem(type = type, onClick = { onComponentSelected(type) })
                }

                item {
                    Text(
                        text = "Other",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                items(listOf(
                    ComposeNodeType.Spacer,
                    ComposeNodeType.HorizontalDivider,
                    ComposeNodeType.CircularProgressIndicator,
                )) { type ->
                    ComponentItem(type = type, onClick = { onComponentSelected(type) })
                }
            }
        }
    }
}

@Composable
private fun ComponentItem(
    type: ComposeNodeType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = type.name,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun PropertyEditorPane(
    selectedNode: ComposeNode?,
    onPropertyChange: (String, Any?) -> Unit,
    onAddModifier: (String, Map<String, Any?>) -> Unit,
    onRemoveModifier: (String) -> Unit,
    onDeleteNode: () -> Unit,
    onWrapWith: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        if (selectedNode == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Select a component",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = selectedNode.type.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Properties",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                selectedNode.properties.forEach { (name, property) ->
                    PropertyEditor(
                        name = name,
                        property = property,
                        onValueChange = { onPropertyChange(name, it) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                var showWrapMenu by remember { mutableStateOf(false) }

                Box {
                    Card(
                        onClick = { showWrapMenu = true },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Wrap with...",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                        )
                    }

                    DropdownMenu(
                        expanded = showWrapMenu,
                        onDismissRequest = { showWrapMenu = false },
                    ) {
                        listOf("Column", "Row", "Box", "Card").forEach { container ->
                            DropdownMenuItem(
                                text = { Text(container) },
                                onClick = {
                                    onWrapWith(container)
                                    showWrapMenu = false
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    onClick = onDeleteNode,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delete",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyEditor(
    name: String,
    property: NodeProperty,
    onValueChange: (Any?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(4.dp))

        when (property.value) {
            is Boolean -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (property.value) "True" else "False",
                        fontSize = 12.sp,
                    )
                    Switch(
                        checked = property.value,
                        onCheckedChange = onValueChange,
                    )
                }
            }
            is Number -> {
                var textValue by remember(property.value) {
                    mutableStateOf(property.value.toString())
                }
                OutlinedTextField(
                    value = textValue,
                    onValueChange = {
                        textValue = it
                        it.toFloatOrNull()?.let(onValueChange)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                )
            }
            else -> {
                var textValue by remember(property.value) {
                    mutableStateOf(property.value?.toString() ?: "")
                }
                OutlinedTextField(
                    value = textValue,
                    onValueChange = {
                        textValue = it
                        onValueChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                )
            }
        }
    }
}

@Composable
private fun NoComposeContentState(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analyzing file...")
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
                Text(
                    text = "No Compose UI Found",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "This file doesn't contain any @Composable functions with UI components.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
            }
        }
    }
}
