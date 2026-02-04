package com.scto.codelikebastimove.feature.soraeditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.soraeditor.compose.SoraEditor
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType

data class EditorFile(
    val name: String,
    val path: String,
    val content: String,
    val isModified: Boolean = false,
)

@Composable
fun EditorContent(
    openFiles: List<EditorFile>,
    activeFileIndex: Int,
    isLoading: Boolean,
    onSelectFile: (Int) -> Unit,
    onCloseFile: (Int) -> Unit,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (openFiles.isNotEmpty()) {
            EditorTabRow(
                files = openFiles,
                selectedIndex = activeFileIndex,
                onTabSelected = onSelectFile,
                onTabClose = onCloseFile,
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val activeFile = openFiles.getOrNull(activeFileIndex)
                if (activeFile != null) {
                    SoraCodeEditorArea(
                        file = activeFile,
                        onContentChange = onContentChange,
                        modifier = Modifier.fillMaxSize(),
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
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            divider = {},
        ) {
            files.forEachIndexed { index, file ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.height(36.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = file.name + if (file.isModified) " *" else "",
                            fontSize = 12.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Medium
                            else FontWeight.Normal,
                            color = if (file.isModified) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                        )

                        IconButton(
                            onClick = { onTabClose(index) },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close tab",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoraCodeEditorArea(
    file: EditorFile,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val languageType = remember(file.name) { EditorLanguageType.fromFileName(file.name) }

    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest)) {
        SoraEditor(
            text = file.content,
            onTextChange = onContentChange,
            languageType = languageType,
            config = EditorConfig(
                showLineNumbers = true,
                wordWrap = false,
                tabWidth = 4,
                autoIndent = true,
                highlightCurrentLine = true,
                showNonPrintableChars = false,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun EmptyEditorState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
            Text(
                text = "No file open",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select a file from the project tree on the left.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
