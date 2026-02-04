package com.scto.codelikebastimove.feature.treeview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

sealed class FileContextAction {
  data object NewFile : FileContextAction()
  data object NewFolder : FileContextAction()
  data object Rename : FileContextAction()
  data object Delete : FileContextAction()
  data object Copy : FileContextAction()
  data object Cut : FileContextAction()
  data object Paste : FileContextAction()
  data object Refresh : FileContextAction()
}

private val ContextMenuBackground = Color(0xFF252526)
private val ContextMenuItemHover = Color(0xFF094771)
private val ContextMenuText = Color(0xFFCCCCCC)
private val ContextMenuDivider = Color(0xFF454545)

data class ContextMenuItem(
  val action: FileContextAction,
  val label: String,
  val icon: ImageVector,
  val enabled: Boolean = true,
  val destructive: Boolean = false,
)

@Composable
fun FileContextMenu(
  isVisible: Boolean,
  isDirectory: Boolean,
  hasClipboard: Boolean,
  onAction: (FileContextAction) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  if (!isVisible) return

  val menuItems = buildList {
    if (isDirectory) {
      add(ContextMenuItem(FileContextAction.NewFile, "New File", Icons.Default.NoteAdd))
      add(ContextMenuItem(FileContextAction.NewFolder, "New Folder", Icons.Default.CreateNewFolder))
    }
    add(ContextMenuItem(FileContextAction.Rename, "Rename", Icons.Default.DriveFileRenameOutline))
    add(ContextMenuItem(FileContextAction.Copy, "Copy", Icons.Default.ContentCopy))
    add(ContextMenuItem(FileContextAction.Cut, "Cut", Icons.Default.ContentCut))
    if (isDirectory && hasClipboard) {
      add(ContextMenuItem(FileContextAction.Paste, "Paste", Icons.Default.ContentPaste))
    }
    add(ContextMenuItem(FileContextAction.Delete, "Delete", Icons.Default.Delete, destructive = true))
    if (isDirectory) {
      add(ContextMenuItem(FileContextAction.Refresh, "Refresh", Icons.Default.Refresh))
    }
  }

  Popup(
    onDismissRequest = onDismiss,
    properties = PopupProperties(focusable = true),
  ) {
    Card(
      modifier = modifier,
      shape = RoundedCornerShape(6.dp),
      colors = CardDefaults.cardColors(containerColor = ContextMenuBackground),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        var previousWasDestructive = false
        menuItems.forEachIndexed { index, item ->
          if (item.destructive && !previousWasDestructive && index > 0) {
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 4.dp),
              color = ContextMenuDivider,
              thickness = 1.dp,
            )
          }

          ContextMenuItemRow(
            item = item,
            onClick = {
              onAction(item.action)
              onDismiss()
            },
          )

          previousWasDestructive = item.destructive
        }
      }
    }
  }
}

@Composable
private fun ContextMenuItemRow(
  item: ContextMenuItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(enabled = item.enabled, onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = item.icon,
      contentDescription = item.label,
      modifier = Modifier.size(16.dp),
      tint = if (item.destructive) Color(0xFFFF6B6B) else ContextMenuText,
    )

    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = item.label,
      style = MaterialTheme.typography.bodyMedium,
      color = if (item.destructive) Color(0xFFFF6B6B) else ContextMenuText,
      fontWeight = if (item.destructive) FontWeight.Medium else FontWeight.Normal,
    )
  }
}
