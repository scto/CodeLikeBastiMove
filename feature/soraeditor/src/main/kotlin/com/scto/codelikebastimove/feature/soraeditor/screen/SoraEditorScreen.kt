package com.scto.codelikebastimove.feature.soraeditor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import com.scto.codelikebastimove.core.resources.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.feature.soraeditor.compose.SoraEditor
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTab
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoraEditorScreen(
  tabs: List<EditorTab>,
  activeTabId: String?,
  onTabSelect: (String) -> Unit,
  onTabClose: (String) -> Unit,
  onTextChange: (String, String) -> Unit,
  onSave: (String) -> Unit,
  modifier: Modifier = Modifier,
  config: EditorConfig = EditorConfig(),
  theme: EditorTheme = EditorThemes.Dracula,
  onConfigChange: (EditorConfig) -> Unit = {},
  onThemeChange: (EditorTheme) -> Unit = {},
) {
  var showMenu by remember { mutableStateOf(false) }
  var cursorLine by remember { mutableIntStateOf(0) }
  var cursorColumn by remember { mutableIntStateOf(0) }
  var editorViewRef by remember { mutableStateOf<SoraEditorView?>(null) }

  val activeTab = tabs.find { it.id == activeTabId }

  Scaffold(
    modifier = modifier,
    topBar = {
      Column {
        TopAppBar(
          title = {
            Text(
              text = activeTab?.file?.name ?: "Editor",
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          },
          actions = {
            IconButton(onClick = { editorViewRef?.undo() }) {
              Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
            }
            IconButton(onClick = { editorViewRef?.redo() }) {
              Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
            }
            IconButton(onClick = { activeTabId?.let { onSave(it) } }) {
              Icon(Icons.Default.Save, contentDescription = "Save")
            }
            Box {
              IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
              }
              EditorMenu(
                expanded = showMenu,
                onDismiss = { showMenu = false },
                onCopy = { editorViewRef?.copy() },
                onCut = { editorViewRef?.cut() },
                onPaste = { editorViewRef?.paste() },
                onSelectAll = { editorViewRef?.selectAll() },
                onFormat = { editorViewRef?.formatCode() },
                onFind = {},
                onSettings = {},
              )
            }
          },
          colors =
            TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )

        if (tabs.isNotEmpty()) {
          EditorTabBar(
            tabs = tabs,
            activeTabId = activeTabId,
            onTabSelect = onTabSelect,
            onTabClose = onTabClose,
          )
        }
      }
    },
    bottomBar = {
      EditorStatusBar(
        languageType = activeTab?.file?.languageType ?: EditorLanguageType.PLAIN_TEXT,
        cursorLine = cursorLine,
        cursorColumn = cursorColumn,
        isModified = activeTab?.file?.isModified ?: false,
        encoding = "UTF-8",
      )
    },
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      if (activeTab != null) {
        SoraEditor(
          text = activeTab.file.content,
          onTextChange = { newText -> onTextChange(activeTab.id, newText) },
          languageType = activeTab.file.languageType,
          config = config,
          theme = theme,
          onCursorChange = { line, column ->
            cursorLine = line
            cursorColumn = column
          },
          editorViewRef = { view -> editorViewRef = view },
          modifier = Modifier.fillMaxSize(),
        )
      } else {
        EmptyEditorPlaceholder()
      }
    }
  }
}

@Composable
private fun EditorTabBar(
  tabs: List<EditorTab>,
  activeTabId: String?,
  onTabSelect: (String) -> Unit,
  onTabClose: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 4.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.Start,
    ) {
      tabs.forEach { tab ->
        EditorTabItem(
          tab = tab,
          isActive = tab.id == activeTabId,
          onSelect = { onTabSelect(tab.id) },
          onClose = { onTabClose(tab.id) },
        )
        Spacer(modifier = Modifier.width(2.dp))
      }
    }
  }
}

@Composable
private fun EditorTabItem(
  tab: EditorTab,
  isActive: Boolean,
  onSelect: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onSelect,
    color = if (isActive) {
      MaterialTheme.colorScheme.primaryContainer
    } else {
      MaterialTheme.colorScheme.surfaceContainer
    },
    shape = RoundedCornerShape(6.dp),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Default.Code,
        contentDescription = null,
        modifier = Modifier.size(14.dp),
        tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (tab.file.isModified) "${tab.file.name}*" else tab.file.name,
        style = MaterialTheme.typography.bodySmall,
        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.width(6.dp))
      IconButton(onClick = onClose, modifier = Modifier.size(18.dp)) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close tab",
          modifier = Modifier.size(12.dp),
          tint = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun EditorStatusBar(
  languageType: EditorLanguageType,
  cursorLine: Int,
  cursorColumn: Int,
  isModified: Boolean,
  encoding: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .padding(horizontal = 12.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Ln ${cursorLine + 1}, Col ${cursorColumn + 1}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
      )
      if (isModified) {
        Text(
          text = "Modified",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.error,
        )
      }
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = encoding, style = MaterialTheme.typography.labelSmall)
      Text(
        text = languageType.displayName,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
private fun EditorMenu(
  expanded: Boolean,
  onDismiss: () -> Unit,
  onCopy: () -> Unit,
  onCut: () -> Unit,
  onPaste: () -> Unit,
  onSelectAll: () -> Unit,
  onFormat: () -> Unit,
  onFind: () -> Unit,
  onSettings: () -> Unit,
) {
  DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
    DropdownMenuItem(
      text = { Text(stringResource(R.string.copy)) },
      leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
      onClick = {
        onCopy()
        onDismiss()
      },
    )
    DropdownMenuItem(
      text = { Text(stringResource(R.string.editor_action_cut)) },
      leadingIcon = { Icon(Icons.Default.ContentCut, null) },
      onClick = {
        onCut()
        onDismiss()
      },
    )
    DropdownMenuItem(
      text = { Text(stringResource(R.string.editor_action_paste)) },
      leadingIcon = { Icon(Icons.Default.ContentPaste, null) },
      onClick = {
        onPaste()
        onDismiss()
      },
    )
    DropdownMenuItem(
      text = { Text(stringResource(R.string.select_all)) },
      onClick = {
        onSelectAll()
        onDismiss()
      },
    )
    DropdownMenuItem(
      text = { Text(stringResource(R.string.format_code)) },
      leadingIcon = { Icon(Icons.Default.FormatAlignLeft, null) },
      onClick = {
        onFormat()
        onDismiss()
      },
    )
    DropdownMenuItem(
      text = { Text(stringResource(R.string.find)) },
      leadingIcon = { Icon(Icons.Default.Search, null) },
      onClick = {
        onFind()
        onDismiss()
      },
    )
    DropdownMenuItem(
      text = { Text(stringResource(R.string.settings)) },
      leadingIcon = { Icon(Icons.Default.Settings, null) },
      onClick = {
        onSettings()
        onDismiss()
      },
    )
  }
}

@Composable
private fun EmptyEditorPlaceholder(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        imageVector = Icons.Default.Code,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.outline,
      )
      Text(
        text = "No file open",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.outline,
      )
      Text(
        text = "Open a file to start editing",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline,
      )
    }
  }
}
