package com.scto.codelikebastimove.feature.git

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GitScreen(
    modifier: Modifier = Modifier,
    onCommandClick: (GitCommand) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Git Kommandos",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        GitCommandCategory.entries.forEach { category ->
            item {
                GitCategorySection(
                    category = category,
                    commands = GitCommand.getCommandsByCategory(category),
                    onCommandClick = onCommandClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GitCategorySection(
    category: GitCommandCategory,
    commands: List<GitCommand>,
    onCommandClick: (GitCommand) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = category.displayName,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                        contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    commands.forEachIndexed { index, command ->
                        GitCommandItem(
                            command = command,
                            onClick = { onCommandClick(command) }
                        )
                        if (index < commands.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GitCommandItem(
    command: GitCommand,
    onClick: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                showDetails = !showDetails
                onClick()
            }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getCommandIcon(command),
                contentDescription = command.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = command.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = command.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (showDetails) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        AnimatedVisibility(
            visible = showDetails,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Verwendung:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = command.usage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun getCategoryIcon(category: GitCommandCategory): ImageVector {
    return when (category) {
        GitCommandCategory.SETUP -> Icons.Default.Settings
        GitCommandCategory.BASIC_SNAPSHOTTING -> Icons.Default.Save
        GitCommandCategory.BRANCHING -> Icons.Default.CallSplit
        GitCommandCategory.SHARING -> Icons.Default.CloudUpload
        GitCommandCategory.INSPECTION -> Icons.Default.Search
        GitCommandCategory.PATCHING -> Icons.Default.Edit
        GitCommandCategory.ADMINISTRATION -> Icons.Default.AdminPanelSettings
    }
}

@Composable
private fun getCommandIcon(command: GitCommand): ImageVector {
    return when (command) {
        is GitCommand.Init -> Icons.Default.CreateNewFolder
        is GitCommand.Clone -> Icons.Default.ContentCopy
        is GitCommand.Add -> Icons.Default.Add
        is GitCommand.Status -> Icons.Default.Info
        is GitCommand.Diff -> Icons.Default.Compare
        is GitCommand.Commit -> Icons.Default.Save
        is GitCommand.Notes -> Icons.Default.Description
        is GitCommand.Restore -> Icons.Default.Restore
        is GitCommand.Reset -> Icons.Default.Undo
        is GitCommand.Rm -> Icons.Default.Delete
        is GitCommand.Mv -> Icons.Default.FolderOpen
        is GitCommand.Branch -> Icons.Default.CallSplit
        is GitCommand.Checkout -> Icons.Default.Sync
        is GitCommand.Switch -> Icons.Default.Sync
        is GitCommand.Merge -> Icons.Default.CallMerge
        is GitCommand.Mergetool -> Icons.Default.MergeType
        is GitCommand.Log -> Icons.Default.History
        is GitCommand.Stash -> Icons.Default.Storage
        is GitCommand.Tag -> Icons.Default.Label
        is GitCommand.Worktree -> Icons.Default.FolderOpen
        is GitCommand.Fetch -> Icons.Default.CloudDownload
        is GitCommand.Pull -> Icons.Default.Download
        is GitCommand.Push -> Icons.Default.Upload
        is GitCommand.Remote -> Icons.Default.CloudUpload
        is GitCommand.Submodule -> Icons.Default.FolderOpen
        is GitCommand.Show -> Icons.Default.Visibility
        is GitCommand.Shortlog -> Icons.Default.List
        is GitCommand.Describe -> Icons.Default.Description
        is GitCommand.Bisect -> Icons.Default.Search
        is GitCommand.Blame -> Icons.Default.Info
        is GitCommand.Grep -> Icons.Default.Search
        is GitCommand.Apply -> Icons.Default.PlayArrow
        is GitCommand.CherryPick -> Icons.Default.ContentCopy
        is GitCommand.DiffCommand -> Icons.Default.Compare
        is GitCommand.Rebase -> Icons.Default.Timeline
        is GitCommand.Revert -> Icons.Default.Undo
        is GitCommand.Config -> Icons.Default.Settings
        is GitCommand.Reflog -> Icons.Default.History
        is GitCommand.Gc -> Icons.Default.Refresh
        is GitCommand.Clean -> Icons.Default.Delete
        is GitCommand.Fsck -> Icons.Default.Search
        is GitCommand.Prune -> Icons.Default.RemoveCircle
        is GitCommand.Archive -> Icons.Default.Archive
        is GitCommand.Bundle -> Icons.Default.Archive
    }
}
