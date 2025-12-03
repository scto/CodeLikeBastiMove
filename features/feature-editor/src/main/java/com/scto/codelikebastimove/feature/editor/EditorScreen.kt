package com.scto.codelikebastimove.feature.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.feature.treeview.TreeView
import kotlin.math.roundToInt

@Composable
fun EditorScreen(
    project: Project,
    viewModel: EditorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var isTreeViewVisible by remember { mutableStateOf(true) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val treeViewWidth = 280.dp
    val treeViewWidthPx = with(density) { treeViewWidth.toPx() }
    val swipeThreshold = treeViewWidthPx * 0.3f
    
    if (state.project == null) {
        viewModel.setProject(project)
    }
    
    val animatedOffset by animateFloatAsState(
        targetValue = if (isTreeViewVisible) 0f else -treeViewWidthPx,
        animationSpec = tween(durationMillis = 300),
        label = "treeViewOffset"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { dragOffset = 0f },
                    onDragEnd = {
                        if (!isTreeViewVisible && dragOffset > swipeThreshold) {
                            isTreeViewVisible = true
                        } else if (isTreeViewVisible && dragOffset < -swipeThreshold) {
                            isTreeViewVisible = false
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .offset { IntOffset((animatedOffset + dragOffset).roundToInt(), 0) }
                    .width(treeViewWidth)
                    .fillMaxHeight()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = project.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 8.dp)
                                )
                                IconButton(
                                    onClick = { isTreeViewVisible = false }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Schließen",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                        
                        TreeView(
                            nodes = state.treeNodes,
                            onNodeClick = { node ->
                                if (!node.isDirectory) {
                                    viewModel.selectFile(node.path)
                                    viewModel.openFileInTab(node.path)
                                }
                            },
                            selectedPath = state.selectedFilePath,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            if (isTreeViewVisible) {
                VerticalDivider(
                    modifier = Modifier.offset { IntOffset((animatedOffset + dragOffset).roundToInt(), 0) }
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isTreeViewVisible) {
                            IconButton(onClick = { isTreeViewVisible = true }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Projektbaum öffnen"
                                )
                            }
                        }
                        
                        if (state.openTabs.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(state.openTabs) { tab ->
                                    EditorTab(
                                        fileName = tab.substringAfterLast("/"),
                                        isSelected = tab == state.selectedFilePath,
                                        onClick = { viewModel.selectFile(tab) },
                                        onClose = { viewModel.closeTab(tab) }
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Keine Dateien geöffnet",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
                
                HorizontalDivider()
                
                if (state.selectedFile != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "File",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.selectedFile?.relativePath ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    CodeEditor(
                        code = state.fileContent,
                        onCodeChange = { viewModel.updateFileContent(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "No file selected",
                                modifier = Modifier.height(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Wähle eine Datei aus dem Projektbaum",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            if (!isTreeViewVisible) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Wische von links nach rechts um den Projektbaum zu öffnen",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorTab(
    fileName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { 
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodySmall
            ) 
        },
        trailingIcon = {
            IconButton(
                onClick = onClose,
                modifier = Modifier.height(16.dp).width(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Schließen",
                    modifier = Modifier.height(12.dp).width(12.dp)
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

@Composable
private fun CodeEditor(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollStateVertical = rememberScrollState()
    val scrollStateHorizontal = rememberScrollState()
    
    Row(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .verticalScroll(scrollStateVertical)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(end = 8.dp, top = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            val lineCount = code.lines().size.coerceAtLeast(1)
            for (i in 1..lineCount) {
                Text(
                    text = i.toString(),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(vertical = 0.dp)
                )
            }
        }
        
        VerticalDivider()
        
        BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(scrollStateVertical)
                .horizontalScroll(scrollStateHorizontal)
                .padding(8.dp),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )
    }
}
