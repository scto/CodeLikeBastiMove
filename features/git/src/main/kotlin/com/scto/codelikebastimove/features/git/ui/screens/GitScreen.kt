package com.scto.codelikebastimove.features.git.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.features.git.model.*
import com.scto.codelikebastimove.features.git.ui.components.*
import com.scto.codelikebastimove.features.git.viewmodel.GitTab
import com.scto.codelikebastimove.features.git.viewmodel.GitViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitScreen(
    projectPath: String,
    modifier: Modifier = Modifier,
    viewModel: GitViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val status by viewModel.status.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val commits by viewModel.commits.collectAsState()
    val stashes by viewModel.stashes.collectAsState()
    val remotes by viewModel.remotes.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val repository by viewModel.currentRepository.collectAsState()
    val isOperationInProgress by viewModel.isOperationInProgress.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(projectPath) {
        if (projectPath.isNotBlank()) {
            viewModel.openRepository(projectPath)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error, duration = SnackbarDuration.Short)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.success.collectLatest { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GitTopBar(
                repository = repository,
                isLoading = isOperationInProgress || uiState.isLoading,
                onRefresh = { viewModel.refresh() },
                onFetch = { viewModel.fetch() },
                onPull = { viewModel.pull() },
                onPush = { viewModel.push() }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GitTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
            
            HorizontalDivider()
            
            when (uiState.selectedTab) {
                GitTab.CHANGES -> ChangesTab(
                    status = status,
                    commitMessage = uiState.commitMessage,
                    onCommitMessageChanged = { viewModel.updateCommitMessage(it) },
                    onStageFile = { viewModel.stageFile(it) },
                    onUnstageFile = { viewModel.unstageFile(it) },
                    onStageAll = { viewModel.stageAll() },
                    onUnstageAll = { viewModel.unstageAll() },
                    onDiscardChanges = { viewModel.discardChanges(it) },
                    onCommit = { viewModel.commit(uiState.commitMessage) }
                )
                GitTab.BRANCHES -> BranchesTab(
                    branches = branches,
                    onCheckout = { viewModel.checkout(it) },
                    onCreateBranch = { viewModel.createBranch(it) },
                    onDeleteBranch = { viewModel.deleteBranch(it) },
                    onMerge = { viewModel.merge(it) }
                )
                GitTab.COMMITS -> CommitsTab(
                    commits = commits,
                    onRevert = { viewModel.revert(it) },
                    onCherryPick = { viewModel.cherryPick(it) }
                )
                GitTab.STASH -> StashTab(
                    stashes = stashes,
                    onStash = { viewModel.stash(it) },
                    onPop = { viewModel.stashPop(it) },
                    onApply = { viewModel.stashApply(it) },
                    onDrop = { viewModel.stashDrop(it) }
                )
                GitTab.REMOTES -> RemotesTab(remotes = remotes)
                GitTab.TAGS -> TagsTab(
                    tags = tags,
                    onCreateTag = { name, message -> viewModel.createTag(name, message) },
                    onDeleteTag = { viewModel.deleteTag(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GitTopBar(
    repository: GitRepository?,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onFetch: () -> Unit,
    onPull: () -> Unit,
    onPush: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = repository?.name ?: "Git",
                    style = MaterialTheme.typography.titleMedium
                )
                repository?.let {
                    Text(
                        text = "${it.currentBranch}${if (it.hasUncommittedChanges) " *" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            
            IconButton(onClick = onFetch) {
                Icon(Icons.Outlined.CloudDownload, contentDescription = "Fetch")
            }
            
            IconButton(onClick = onPull) {
                Icon(Icons.Default.Download, contentDescription = "Pull")
            }
            
            IconButton(onClick = onPush) {
                Icon(Icons.Default.Upload, contentDescription = "Push")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}

@Composable
private fun GitTabRow(
    selectedTab: GitTab,
    onTabSelected: (GitTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        edgePadding = 8.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        GitTab.entries.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.name.lowercase().replaceFirstChar { it.uppercase() }) },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            GitTab.CHANGES -> Icons.Outlined.Edit
                            GitTab.BRANCHES -> Icons.Outlined.AccountTree
                            GitTab.COMMITS -> Icons.Outlined.History
                            GitTab.STASH -> Icons.Outlined.Archive
                            GitTab.REMOTES -> Icons.Outlined.Cloud
                            GitTab.TAGS -> Icons.Outlined.LocalOffer
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun ChangesTab(
    status: GitStatus?,
    commitMessage: String,
    onCommitMessageChanged: (String) -> Unit,
    onStageFile: (String) -> Unit,
    onUnstageFile: (String) -> Unit,
    onStageAll: () -> Unit,
    onUnstageAll: () -> Unit,
    onDiscardChanges: (String) -> Unit,
    onCommit: () -> Unit
) {
    if (status == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No repository loaded")
        }
        return
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CommitSection(
                message = commitMessage,
                onMessageChanged = onCommitMessageChanged,
                onCommit = onCommit,
                canCommit = status.stagedChanges.isNotEmpty()
            )
        }
        
        if (status.stagedChanges.isNotEmpty()) {
            item {
                FileChangeSection(
                    title = "Staged Changes (${status.stagedChanges.size})",
                    changes = status.stagedChanges,
                    onUnstageFile = onUnstageFile,
                    onUnstageAll = onUnstageAll,
                    isStaged = true
                )
            }
        }
        
        if (status.unstagedChanges.isNotEmpty()) {
            item {
                FileChangeSection(
                    title = "Changes (${status.unstagedChanges.size})",
                    changes = status.unstagedChanges,
                    onStageFile = onStageFile,
                    onStageAll = onStageAll,
                    onDiscardChanges = onDiscardChanges,
                    isStaged = false
                )
            }
        }
        
        if (status.untrackedFiles.isNotEmpty()) {
            item {
                UntrackedFilesSection(
                    files = status.untrackedFiles,
                    onStageFile = onStageFile
                )
            }
        }
        
        if (status.stagedChanges.isEmpty() && status.unstagedChanges.isEmpty() && status.untrackedFiles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Working tree clean")
                    }
                }
            }
        }
    }
}

@Composable
private fun CommitSection(
    message: String,
    onMessageChanged: (String) -> Unit,
    onCommit: () -> Unit,
    canCommit: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Commit",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Commit message") },
                minLines = 2,
                maxLines = 4
            )
            
            Button(
                onClick = onCommit,
                enabled = canCommit && message.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Commit")
            }
        }
    }
}

@Composable
private fun FileChangeSection(
    title: String,
    changes: List<GitFileChange>,
    onStageFile: ((String) -> Unit)? = null,
    onUnstageFile: ((String) -> Unit)? = null,
    onStageAll: (() -> Unit)? = null,
    onUnstageAll: (() -> Unit)? = null,
    onDiscardChanges: ((String) -> Unit)? = null,
    isStaged: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (isStaged && onUnstageAll != null) {
                    TextButton(onClick = onUnstageAll) {
                        Text("Unstage All")
                    }
                } else if (!isStaged && onStageAll != null) {
                    TextButton(onClick = onStageAll) {
                        Text("Stage All")
                    }
                }
            }
            
            HorizontalDivider()
            
            changes.forEach { change ->
                FileChangeItem(
                    change = change,
                    onStage = if (!isStaged && onStageFile != null) {{ onStageFile(change.path) }} else null,
                    onUnstage = if (isStaged && onUnstageFile != null) {{ onUnstageFile(change.path) }} else null,
                    onDiscard = if (!isStaged && onDiscardChanges != null) {{ onDiscardChanges(change.path) }} else null
                )
            }
        }
    }
}

@Composable
private fun FileChangeItem(
    change: GitFileChange,
    onStage: (() -> Unit)?,
    onUnstage: (() -> Unit)?,
    onDiscard: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(status = change.status)
            
            Text(
                text = change.path.substringAfterLast("/"),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Row {
            onStage?.let {
                IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Stage", modifier = Modifier.size(18.dp))
                }
            }
            onUnstage?.let {
                IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Unstage", modifier = Modifier.size(18.dp))
                }
            }
            onDiscard?.let {
                IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Discard", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: GitFileStatus) {
    val (color, text) = when (status) {
        GitFileStatus.ADDED -> MaterialTheme.colorScheme.primary to "A"
        GitFileStatus.MODIFIED -> MaterialTheme.colorScheme.tertiary to "M"
        GitFileStatus.DELETED -> MaterialTheme.colorScheme.error to "D"
        GitFileStatus.RENAMED -> MaterialTheme.colorScheme.secondary to "R"
        GitFileStatus.COPIED -> MaterialTheme.colorScheme.secondary to "C"
        GitFileStatus.UNTRACKED -> MaterialTheme.colorScheme.outline to "?"
        GitFileStatus.IGNORED -> MaterialTheme.colorScheme.outline to "!"
        GitFileStatus.CONFLICT -> MaterialTheme.colorScheme.error to "U"
        GitFileStatus.TYPECHANGE -> MaterialTheme.colorScheme.tertiary to "T"
    }
    
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UntrackedFilesSection(
    files: List<String>,
    onStageFile: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Untracked Files (${files.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            HorizontalDivider()
            
            files.forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = GitFileStatus.UNTRACKED)
                        Text(
                            text = file.substringAfterLast("/"),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    IconButton(onClick = { onStageFile(file) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Stage", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
