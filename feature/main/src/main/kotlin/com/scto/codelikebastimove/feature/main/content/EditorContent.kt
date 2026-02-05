package com.scto.codelikebastimove.feature.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.feature.soraeditor.compose.SoraEditor
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.viewmodel.SoraEditorViewModel

@Composable
fun EditorContent(
    editorViewModel: SoraEditorViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by editorViewModel.uiState.collectAsState()
    val tabs = uiState.tabs
    val activeTabId = uiState.activeTabId

    Column(modifier = modifier.fillMaxSize()) {
        if (tabs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No files open",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                tabs.forEach { tab ->
                    FileTab(
                        fileName = tab.file.name,
                        isSelected = tab.id == activeTabId,
                        isModified = tab.file.isModified,
                        onClick = { editorViewModel.selectTab(tab.id) },
                        onClose = { editorViewModel.closeTab(tab.id) },
                    )
                }
            }

            HorizontalDivider()

            val activeTab = tabs.find { it.id == activeTabId }
            if (activeTab != null) {
                EditorPane(
                    content = activeTab.file.content,
                    onContentChange = { editorViewModel.updateContent(activeTab.id, it) },
                    modifier = Modifier.weight(1f),
                    fileName = activeTab.file.name,
                )
            }
        }
    }
}

@Composable
private fun FileTab(
    fileName: String,
    isSelected: Boolean,
    isModified: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (isSelected) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isModified) "$fileName *" else fileName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.padding(start = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.padding(2.dp),
                )
            }
        }
    }
}

@Composable
private fun EditorPane(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fileName: String = "",
) {
    val languageType = EditorLanguageType.fromFileName(fileName)

    SoraEditor(
        text = content,
        onTextChange = onContentChange,
        languageType = languageType,
        modifier = modifier.fillMaxSize(),
    )
}
