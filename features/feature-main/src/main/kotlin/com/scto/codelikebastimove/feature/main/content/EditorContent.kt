package com.scto.codelikebastimove.feature.main.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.main.EditorFile
import com.scto.codelikebastimove.feature.main.MainViewModel

@Composable
fun EditorContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val openFiles = uiState.openFiles
    val activeIndex = uiState.activeFileIndex
    
    Column(modifier = modifier.fillMaxSize()) {
        if (openFiles.isNotEmpty()) {
            EditorTabRow(
                files = openFiles,
                selectedIndex = activeIndex,
                onTabSelected = { viewModel.selectFile(it) },
                onTabClose = { viewModel.closeFile(it) }
            )
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val activeFile = openFiles.getOrNull(activeIndex)
                if (activeFile != null) {
                    CodeEditorArea(
                        content = activeFile.content,
                        onContentChange = { newContent ->
                            viewModel.updateFileContent(newContent)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    EmptyEditorState(modifier = Modifier.fillMaxSize())
                }
            }
        } else {
            EmptyEditorState(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun EditorTabRow(
    files: List<EditorFile>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabClose: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            divider = { }
        ) {
            files.forEachIndexed { index, file ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = file.name + if (file.isModified) " •" else "",
                            fontSize = 12.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Medium else FontWeight.Normal,
                            color = if (file.isModified) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurface
                        )
                        
                        IconButton(
                            onClick = { onTabClose(index) },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tab schließen",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CodeEditorArea(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(content) {
        mutableStateOf(TextFieldValue(text = content))
    }
    
    // Einfache Synchronisation, um Cursor-Sprünge zu minimieren, aber Updates zuzulassen
    if (textFieldValue.text != content) {
        textFieldValue = textFieldValue.copy(text = content)
    }

    var showContextMenu by remember { mutableStateOf(false) }
    var contextMenuOffset by remember { mutableStateOf(Offset.Zero) }
    val clipboardManager = LocalClipboardManager.current
    
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )
    
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            val lines = textFieldValue.text.lines()
            
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.End
            ) {
                lines.forEachIndexed { index, _ ->
                    Text(
                        text = "${index + 1}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { offset ->
                                contextMenuOffset = offset
                                showContextMenu = true
                            }
                        )
                    }
            ) {
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                            onContentChange(newValue.text)
                        },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                DropdownMenu(
                    expanded = showContextMenu,
                    onDismissRequest = { showContextMenu = false },
                    offset = DpOffset(
                        x = (contextMenuOffset.x / 2).dp,
                        y = (contextMenuOffset.y / 2).dp
                    )
                ) {
                    DropdownMenuItem(
                        text = { Text("Alles auswählen") },
                        onClick = {
                            textFieldValue = textFieldValue.copy(
                                selection = TextRange(0, textFieldValue.text.length)
                            )
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SelectAll, contentDescription = null)
                        }
                    )
                    
                    HorizontalDivider()
                    
                    DropdownMenuItem(
                        text = { Text("Kopieren") },
                        onClick = {
                            val selection = textFieldValue.selection
                            if (!selection.collapsed) {
                                val selectedText = textFieldValue.text.substring(
                                    selection.start,
                                    selection.end
                                )
                                clipboardManager.setText(AnnotatedString(selectedText))
                            }
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Einfügen") },
                        onClick = {
                            val clipboardText = clipboardManager.getText()?.text ?: ""
                            val newText = StringBuilder(textFieldValue.text).apply {
                                if (textFieldValue.selection.collapsed) {
                                    insert(textFieldValue.selection.start, clipboardText)
                                } else {
                                    replace(
                                        textFieldValue.selection.start,
                                        textFieldValue.selection.end,
                                        clipboardText
                                    )
                                }
                            }.toString()
                            val newCursorPos = textFieldValue.selection.start + clipboardText.length
                            textFieldValue = TextFieldValue(
                                text = newText,
                                selection = TextRange(newCursorPos)
                            )
                            onContentChange(newText)
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ContentPaste, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyEditorState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "Keine Datei geöffnet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Wähle eine Datei im Projektbaum links aus.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}