package com.scto.codelikebastimove.feature.main.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class EditorTab(
    val fileName: String,
    val filePath: String,
    val hasChanges: Boolean = false,
    val content: String = ""
)

@Composable
fun EditorContent(
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val tabs = remember {
        mutableStateOf(listOf(
            EditorTab("MainActivity.kt", "app/src/main/java/.../MainActivity.kt", content = sampleKotlinCode),
            EditorTab("build.gradle.kts", "app/build.gradle.kts", hasChanges = true, content = sampleGradleCode),
            EditorTab("Theme.kt", "core/ui/theme/Theme.kt", content = sampleThemeCode)
        ))
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        if (tabs.value.isNotEmpty()) {
            EditorTabRow(
                tabs = tabs.value,
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                onTabClose = { index ->
                    val newTabs = tabs.value.toMutableList()
                    newTabs.removeAt(index)
                    tabs.value = newTabs
                    if (selectedTabIndex >= newTabs.size) {
                        selectedTabIndex = (newTabs.size - 1).coerceAtLeast(0)
                    }
                }
            )
            
            CodeEditorArea(
                content = tabs.value.getOrNull(selectedTabIndex)?.content ?: "",
                onContentChange = { newContent ->
                    val newTabs = tabs.value.toMutableList()
                    val currentTab = newTabs.getOrNull(selectedTabIndex)
                    if (currentTab != null) {
                        newTabs[selectedTabIndex] = currentTab.copy(
                            content = newContent,
                            hasChanges = true
                        )
                        tabs.value = newTabs
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            EmptyEditorState(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun EditorTabRow(
    tabs: List<EditorTab>,
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
            tabs.forEachIndexed { index, tab ->
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
                            text = tab.fileName + if (tab.hasChanges) " •" else "",
                            fontSize = 12.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Medium else FontWeight.Normal,
                            color = if (tab.hasChanges) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurface
                        )
                        
                        IconButton(
                            onClick = { onTabClose(index) },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close tab",
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
                        text = { Text("Select All") },
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
                        text = { Text("Cut") },
                        onClick = {
                            val selection = textFieldValue.selection
                            if (!selection.collapsed) {
                                val selectedText = textFieldValue.text.substring(
                                    selection.start,
                                    selection.end
                                )
                                clipboardManager.setText(AnnotatedString(selectedText))
                                val newText = textFieldValue.text.removeRange(
                                    selection.start,
                                    selection.end
                                )
                                textFieldValue = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(selection.start)
                                )
                                onContentChange(newText)
                            }
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCut, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Copy") },
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
                        text = { Text("Paste") },
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
                    
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            if (!textFieldValue.selection.collapsed) {
                                val newText = textFieldValue.text.removeRange(
                                    textFieldValue.selection.start,
                                    textFieldValue.selection.end
                                )
                                textFieldValue = TextFieldValue(
                                    text = newText,
                                    selection = TextRange(textFieldValue.selection.start)
                                )
                                onContentChange(newText)
                            }
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete, 
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                    
                    HorizontalDivider()
                    
                    DropdownMenuItem(
                        text = { Text("Format Code") },
                        onClick = {
                            val formattedCode = formatCode(textFieldValue.text)
                            textFieldValue = TextFieldValue(
                                text = formattedCode,
                                selection = TextRange(formattedCode.length)
                            )
                            onContentChange(formattedCode)
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.FormatAlignLeft, contentDescription = null)
                        }
                    )
                    
                    HorizontalDivider()
                    
                    DropdownMenuItem(
                        text = { Text("Find") },
                        onClick = {
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Find and Replace") },
                        onClick = {
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.FindReplace, contentDescription = null)
                        }
                    )
                    
                    HorizontalDivider()
                    
                    DropdownMenuItem(
                        text = { Text("Undo") },
                        onClick = {
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Redo") },
                        onClick = {
                            showContextMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

private fun formatCode(code: String): String {
    val lines = code.lines()
    var indentLevel = 0
    val formattedLines = mutableListOf<String>()
    
    for (line in lines) {
        val trimmedLine = line.trim()
        
        if (trimmedLine.isEmpty()) {
            formattedLines.add("")
            continue
        }
        
        if (trimmedLine.startsWith("}") || trimmedLine.startsWith(")")) {
            indentLevel = (indentLevel - 1).coerceAtLeast(0)
        }
        
        val indent = "    ".repeat(indentLevel)
        formattedLines.add("$indent$trimmedLine")
        
        if (trimmedLine.endsWith("{") || trimmedLine.endsWith("(") && !trimmedLine.endsWith(")")) {
            indentLevel++
        }
    }
    
    return formattedLines.joinToString("\n")
}

@Composable
private fun EmptyEditorState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                text = "No files open",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Open a file from the Project view",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

private val sampleKotlinCode = """
package com.scto.codelikebastimove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeLikeBastiMoveTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}
""".trimIndent()

private val sampleGradleCode = """
plugins {
    id("codelikebastimove.android.application")
    id("codelikebastimove.android.application.compose")
}

android {
    namespace = "com.scto.codelikebastimove"
    
    defaultConfig {
        applicationId = "com.scto.codelikebastimove"
        versionCode = 11
        versionName = "0.1.0-alpha-11"
    }
}

dependencies {
    implementation(project(":features"))
    implementation(project(":core:core-ui"))
}
""".trimIndent()

private val sampleThemeCode = """
package com.scto.codelikebastimove.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CodeLikeBastiMoveTheme(
    themeMode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
""".trimIndent()
