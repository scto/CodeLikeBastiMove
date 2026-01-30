package com.scto.codelikebastimove.feature.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen(
  viewModel: ExplorerViewModel = viewModel(),
  onBackClick: () -> Unit,
  onProjectSelected: (path: String, name: String) -> Unit,
  onFileSelected: ((path: String) -> Unit)? = null,
  selectionMode: SelectionMode = SelectionMode.PROJECT,
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsState()
  var showSortMenu by remember { mutableStateOf(false) }
  var showFilterMenu by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(text = "Explorer", style = MaterialTheme.typography.titleMedium)
            Text(
              text = uiState.currentPath,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.navigateUp() }) {
            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Nach oben")
          }
          IconButton(onClick = { viewModel.navigateToProjectsRoot() }) {
            Icon(imageVector = Icons.Default.Home, contentDescription = "Projekte-Ordner")
          }
          IconButton(onClick = { viewModel.refreshCurrentDirectory() }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Aktualisieren")
          }
          Box {
            IconButton(onClick = { showSortMenu = true }) {
              Icon(imageVector = Icons.Default.Sort, contentDescription = "Sortieren")
            }
            DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
              SortOrder.entries.forEach { order ->
                DropdownMenuItem(
                  text = {
                    Text(
                      text =
                        when (order) {
                          SortOrder.NAME_ASC -> "Name (A-Z)"
                          SortOrder.NAME_DESC -> "Name (Z-A)"
                          SortOrder.DATE_ASC -> "Datum (Älteste)"
                          SortOrder.DATE_DESC -> "Datum (Neueste)"
                          SortOrder.SIZE_ASC -> "Größe (Kleinste)"
                          SortOrder.SIZE_DESC -> "Größe (Größte)"
                        }
                    )
                  },
                  onClick = {
                    viewModel.setSortOrder(order)
                    showSortMenu = false
                  },
                  leadingIcon = {
                    if (uiState.sortOrder == order) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                      )
                    }
                  },
                )
              }
            }
          }
          Box {
            IconButton(onClick = { showFilterMenu = true }) {
              Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Mehr")
            }
            DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
              DropdownMenuItem(
                text = { Text("Versteckte Dateien anzeigen") },
                onClick = {
                  viewModel.toggleHiddenFiles()
                  showFilterMenu = false
                },
                leadingIcon = {
                  if (uiState.showHiddenFiles) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                    )
                  }
                },
              )
              HorizontalDivider()
              FileFilter.entries.forEach { filter ->
                DropdownMenuItem(
                  text = {
                    Text(
                      text =
                        when (filter) {
                          FileFilter.ALL -> "Alle anzeigen"
                          FileFilter.DIRECTORIES_ONLY -> "Nur Ordner"
                          FileFilter.FILES_ONLY -> "Nur Dateien"
                          FileFilter.PROJECTS_ONLY -> "Nur Projekte"
                        }
                    )
                  },
                  onClick = {
                    viewModel.setFileFilter(filter)
                    showFilterMenu = false
                  },
                  leadingIcon = {
                    if (uiState.fileFilter == filter) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                      )
                    }
                  },
                )
              }
            }
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    modifier = modifier,
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      if (uiState.files.isEmpty() && !uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(
            text = "Dieser Ordner ist leer",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(vertical = 8.dp),
        ) {
          items(uiState.files, key = { it.path }) { file ->
            FileItemRow(
              fileItem = file,
              isSelected = file.path in uiState.selectedFiles,
              selectionMode = selectionMode,
              onClick = {
                if (file.isDirectory) {
                  if (file.isGradleProject && selectionMode == SelectionMode.PROJECT) {
                    onProjectSelected(file.path, file.name)
                  } else {
                    viewModel.navigateTo(file.path)
                  }
                } else {
                  onFileSelected?.invoke(file.path)
                }
              },
              onLongClick = { viewModel.toggleFileSelection(file.path) },
            )
          }
        }
      }
    }
  }
}

enum class SelectionMode {
  PROJECT,
  FILE,
  DIRECTORY,
  ANY,
}

@Composable
fun FileItemRow(
  fileItem: FileItem,
  isSelected: Boolean,
  selectionMode: SelectionMode,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor =
    if (isSelected) {
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
      Color.Transparent
    }

  val icon = getFileIcon(fileItem)
  val iconTint = getFileIconTint(fileItem)

  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(backgroundColor)
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = iconTint,
      modifier = Modifier.size(32.dp),
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = fileItem.name,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = if (fileItem.isGradleProject) FontWeight.Bold else FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false),
        )

        if (fileItem.isGradleProject) {
          Spacer(modifier = Modifier.width(8.dp))
          Card(
            colors =
              CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
          ) {
            Text(
              text = "Projekt",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
          text = formatDate(fileItem.lastModified),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (!fileItem.isDirectory) {
          Text(
            text = fileItem.displaySize,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    if (isSelected) {
      Checkbox(checked = true, onCheckedChange = null)
    }
  }
}

@Composable
private fun getFileIcon(fileItem: FileItem): ImageVector {
  return when {
    fileItem.isGradleProject -> Icons.Default.Android
    fileItem.isDirectory -> Icons.Default.Folder
    fileItem.extension in listOf("png", "jpg", "jpeg", "gif", "webp", "svg") -> Icons.Default.Image
    fileItem.extension in listOf("kt", "java", "xml", "json", "gradle", "kts") ->
      Icons.Default.Description
    else -> Icons.Default.InsertDriveFile
  }
}

@Composable
private fun getFileIconTint(fileItem: FileItem): Color {
  return when {
    fileItem.isGradleProject -> Color(0xFF3DDC84)
    fileItem.isDirectory -> MaterialTheme.colorScheme.primary
    fileItem.extension in listOf("kt", "kts") -> Color(0xFF7F52FF)
    fileItem.extension == "java" -> Color(0xFFE76F00)
    fileItem.extension == "xml" -> Color(0xFFF57C00)
    fileItem.extension == "json" -> Color(0xFFFFCA28)
    fileItem.extension in listOf("png", "jpg", "jpeg", "gif", "webp", "svg") -> Color(0xFF26A69A)
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }
}

private fun formatDate(timestamp: Long): String {
  val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
  return dateFormat.format(Date(timestamp))
}
