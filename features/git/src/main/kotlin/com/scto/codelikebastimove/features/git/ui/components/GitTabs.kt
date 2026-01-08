package com.scto.codelikebastimove.features.git.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.features.git.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BranchesTab(
    branches: List<GitBranch>,
    onCheckout: (String) -> Unit,
    onCreateBranch: (String) -> Unit,
    onDeleteBranch: (String) -> Unit,
    onMerge: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newBranchName by remember { mutableStateOf("") }
    
    val localBranches = branches.filter { it.isLocal }
    val remoteBranches = branches.filter { it.isRemote }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Branch")
            }
        }
        
        if (localBranches.isNotEmpty()) {
            item {
                Text(
                    text = "Local Branches",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(localBranches) { branch ->
                BranchItem(
                    branch = branch,
                    onCheckout = { onCheckout(branch.name) },
                    onDelete = { onDeleteBranch(branch.name) },
                    onMerge = { onMerge(branch.name) }
                )
            }
        }
        
        if (remoteBranches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Remote Branches",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(remoteBranches) { branch ->
                BranchItem(
                    branch = branch,
                    onCheckout = { onCheckout(branch.name) },
                    onDelete = null,
                    onMerge = { onMerge(branch.name) }
                )
            }
        }
    }
    
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Branch") },
            text = {
                OutlinedTextField(
                    value = newBranchName,
                    onValueChange = { newBranchName = it },
                    label = { Text("Branch name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newBranchName.isNotBlank()) {
                            onCreateBranch(newBranchName)
                            newBranchName = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BranchItem(
    branch: GitBranch,
    onCheckout: () -> Unit,
    onDelete: (() -> Unit)?,
    onMerge: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (branch.isCurrent) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckout() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (branch.isCurrent) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (branch.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
                
                Column {
                    Text(
                        text = branch.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (branch.isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                    
                    branch.lastCommitMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    if (branch.ahead > 0 || branch.behind > 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (branch.ahead > 0) {
                                Text(
                                    text = "↑${"branch.ahead"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (branch.behind > 0) {
                                Text(
                                    text = "↓${branch.behind}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (!branch.isCurrent) {
                        DropdownMenuItem(
                            text = { Text("Checkout") },
                            onClick = { onCheckout(); expanded = false },
                            leadingIcon = { Icon(Icons.Default.Check, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Merge into current") },
                            onClick = { onMerge(); expanded = false },
                            leadingIcon = { Icon(Icons.Default.MergeType, null) }
                        )
                    }
                    onDelete?.let {
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = { it(); expanded = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                            enabled = !branch.isCurrent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommitsTab(
    commits: List<GitCommit>,
    onRevert: (String) -> Unit,
    onCherryPick: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(commits) { commit ->
            CommitItem(
                commit = commit,
                dateFormat = dateFormat,
                onRevert = { onRevert(commit.hash) },
                onCherryPick = { onCherryPick(commit.hash) }
            )
        }
    }
}

@Composable
private fun CommitItem(
    commit: GitCommit,
    dateFormat: SimpleDateFormat,
    onRevert: () -> Unit,
    onCherryPick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = commit.shortHash,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    if (commit.isMergeCommit) {
                        Icon(
                            Icons.Default.MergeType,
                            contentDescription = "Merge commit",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = commit.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = commit.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(commit.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Revert") },
                        onClick = { onRevert(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Undo, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Cherry-pick") },
                        onClick = { onCherryPick(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                    )
                }
            }
        }
    }
}

@Composable
fun StashTab(
    stashes: List<GitStash>,
    onStash: (String?) -> Unit,
    onPop: (Int) -> Unit,
    onApply: (Int) -> Unit,
    onDrop: (Int) -> Unit
) {
    var showStashDialog by remember { mutableStateOf(false) }
    var stashMessage by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = { showStashDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Archive, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stash Changes")
            }
        }
        
        if (stashes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.Inbox, contentDescription = null)
                        Text("No stashes")
                    }
                }
            }
        } else {
            items(stashes) { stash ->
                StashItem(
                    stash = stash,
                    onPop = { onPop(stash.index) },
                    onApply = { onApply(stash.index) },
                    onDrop = { onDrop(stash.index) }
                )
            }
        }
    }
    
    if (showStashDialog) {
        AlertDialog(
            onDismissRequest = { showStashDialog = false },
            title = { Text("Stash Changes") },
            text = {
                OutlinedTextField(
                    value = stashMessage,
                    onValueChange = { stashMessage = it },
                    label = { Text("Message (optional)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStash(stashMessage.takeIf { it.isNotBlank() })
                        stashMessage = ""
                        showStashDialog = false
                    }
                ) {
                    Text("Stash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStashDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StashItem(
    stash: GitStash,
    onPop: () -> Unit,
    onApply: () -> Unit,
    onDrop: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "stash@{${stash.index}}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stash.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Row {
                IconButton(onClick = onPop) {
                    Icon(Icons.Default.Unarchive, contentDescription = "Pop")
                }
                
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Apply") },
                            onClick = { onApply(); expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Drop", color = MaterialTheme.colorScheme.error) },
                            onClick = { onDrop(); expanded = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemotesTab(remotes: List<GitRemote>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (remotes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.CloudOff, contentDescription = null)
                        Text("No remotes configured")
                    }
                }
            }
        } else {
            items(remotes) { remote ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = remote.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Text(
                                text = remote.fetchUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (remote.pushUrl != remote.fetchUrl) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                                Text(
                                    text = remote.pushUrl,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun TagsTab(
    tags: List<GitTag>,
    onCreateTag: (String, String?) -> Unit,
    onDeleteTag: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var tagName by remember { mutableStateOf("") }
    var tagMessage by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Tag")
            }
        }
        
        if (tags.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.LocalOffer, contentDescription = null)
                        Text("No tags")
                    }
                }
            }
        } else {
            items(tags) { tag ->
                TagItem(tag = tag, onDelete = { onDeleteTag(tag.name) })
            }
        }
    }
    
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Tag") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        label = { Text("Tag name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tagMessage,
                        onValueChange = { tagMessage = it },
                        label = { Text("Message (optional)") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tagName.isNotBlank()) {
                            onCreateTag(tagName, tagMessage.takeIf { it.isNotBlank() })
                            tagName = ""
                            tagMessage = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TagItem(
    tag: GitTag,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tag.isAnnotated) Icons.Default.LocalOffer else Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Column {
                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    tag.message?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Text(
                        text = tag.commitHash.take(7),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = { onDelete(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}
