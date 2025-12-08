package com.scto.codelikebastimove.feature.slidingpanel.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

enum class FileViewMode(
    val title: String,
    val icon: ImageVector
) {
    MODULE("Module", Icons.Default.AccountTree),
    PROJECT("Projekt", Icons.Default.Source),
    FILES("Dateien", Icons.Default.FolderOpen)
}

data class OpenedFile(
    val path: String,
    val name: String,
    val isModified: Boolean = false,
    val isActive: Boolean = false
)

data class GitChangedFile(
    val path: String,
    val name: String,
    val status: GitFileStatus
)

enum class GitFileStatus {
    ADDED,
    MODIFIED,
    DELETED,
    UNTRACKED
}

@Composable
fun FileTreeContainer(
    openedFiles: List<OpenedFile>,
    gitChangedFiles: List<GitChangedFile>,
    currentViewMode: FileViewMode,
    onViewModeChanged: (FileViewMode) -> Unit,
    onFileClicked: (String) -> Unit,
    onFileCloseClicked: (String) -> Unit,
    onGitFileClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    fileTreeContent: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.height(56.dp)
        ) {
            FileViewMode.entries.forEach { mode ->
                NavigationBarItem(
                    selected = mode == currentViewMode,
                    onClick = { onViewModeChanged(mode) },
                    icon = {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = mode.title,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        
        HorizontalDivider()
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            fileTreeContent()
        }
        
        if (openedFiles.isNotEmpty()) {
            HorizontalDivider()
            OpenedFilesSection(
                files = openedFiles,
                onFileClicked = onFileClicked,
                onFileCloseClicked = onFileCloseClicked
            )
        }
        
        if (gitChangedFiles.isNotEmpty()) {
            HorizontalDivider()
            GitChangedFilesSection(
                files = gitChangedFiles,
                onFileClicked = onGitFileClicked
            )
        }
    }
}

@Composable
private fun OpenedFilesSection(
    files: List<OpenedFile>,
    onFileClicked: (String) -> Unit,
    onFileCloseClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Geöffnete Dateien",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            items(files, key = { it.path }) { file ->
                OpenedFileItem(
                    file = file,
                    onClick = { onFileClicked(file.path) },
                    onCloseClick = { onFileCloseClicked(file.path) }
                )
            }
        }
    }
}

@Composable
private fun OpenedFileItem(
    file: OpenedFile,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (file.isActive) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (file.isModified) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }
        
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = file.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Schließen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun GitChangedFilesSection(
    files: List<GitChangedFile>,
    onFileClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Git Änderungen",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            items(files, key = { it.path }) { file ->
                GitChangedFileItem(
                    file = file,
                    onClick = { onFileClicked(file.path) }
                )
            }
        }
    }
}

@Composable
private fun GitChangedFileItem(
    file: GitChangedFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (file.status) {
        GitFileStatus.ADDED -> Color(0xFF4CAF50)
        GitFileStatus.MODIFIED -> Color(0xFFFF9800)
        GitFileStatus.DELETED -> Color(0xFFF44336)
        GitFileStatus.UNTRACKED -> Color(0xFF9E9E9E)
    }
    
    val statusLabel = when (file.status) {
        GitFileStatus.ADDED -> "A"
        GitFileStatus.MODIFIED -> "M"
        GitFileStatus.DELETED -> "D"
        GitFileStatus.UNTRACKED -> "U"
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(statusColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = file.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
