package com.scto.codelikebastimove.feature.git.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.feature.git.model.*
import com.scto.codelikebastimove.feature.git.viewmodel.GitViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.collectLatest

enum class GitSection {
  CHANGES,
  HISTORY,
  BRANCHES,
  REMOTES,
  STASH,
  TAGS,
  SETTINGS,
}

private val GitAccentColor = Color(0xFF8B7355)
private val GitAccentColorLight = Color(0xFFA89078)
private val GitCardBackground = Color(0xFF2A2A2A)
private val GitSurfaceBackground = Color(0xFF1A1A1A)
private val GitStatusModified = Color(0xFFE59400)
private val GitStatusUntracked = Color(0xFFE59400)
private val GitCommitHashColor = Color(0xFF4A9F7A)

@Composable
fun GitScreen(
  projectPath: String,
  modifier: Modifier = Modifier,
  viewModel: GitViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  val status by viewModel.status.collectAsState()
  val branches by viewModel.branches.collectAsState()
  val commits by viewModel.commits.collectAsState()
  val remotes by viewModel.remotes.collectAsState()
  val stashes by viewModel.stashes.collectAsState()
  val tags by viewModel.tags.collectAsState()
  val repository by viewModel.currentRepository.collectAsState()
  val isOperationInProgress by viewModel.isOperationInProgress.collectAsState()

  var selectedSection by remember { mutableStateOf(GitSection.CHANGES) }
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
    containerColor = GitSurfaceBackground,
    modifier = modifier,
  ) { padding ->
    Row(modifier = Modifier.fillMaxSize().padding(padding)) {
      GitNavigationRail(
        selectedSection = selectedSection,
        onSectionSelected = { selectedSection = it },
      )

      Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
        when (selectedSection) {
          GitSection.CHANGES ->
            ChangesContent(
              status = status,
              commitMessage = uiState.commitMessage,
              isLoading = isOperationInProgress,
              onRefresh = { viewModel.refresh() },
              onStageFile = { viewModel.stageFile(it) },
              onUnstageFile = { viewModel.unstageFile(it) },
              onStageAll = { viewModel.stageAll() },
              onUnstageAll = { viewModel.unstageAll() },
              onDiscardFile = { viewModel.discardChanges(it) },
              onCommitMessageChanged = { viewModel.updateCommitMessage(it) },
              onCommit = { viewModel.commit(uiState.commitMessage) },
            )
          GitSection.HISTORY ->
            HistoryContent(
              commits = commits,
              isLoading = isOperationInProgress,
              onRefresh = { viewModel.refresh() },
            )
          GitSection.BRANCHES ->
            BranchesContent(
              branches = branches,
              currentBranch = repository?.currentBranch ?: "main",
              isLoading = isOperationInProgress,
              onRefresh = { viewModel.refresh() },
              onCheckout = { viewModel.checkout(it) },
              onDelete = { viewModel.deleteBranch(it) },
              onCreateBranch = { viewModel.createBranch(it) },
            )
          GitSection.REMOTES ->
            RemotesContent(
              remotes = remotes,
              isLoading = isOperationInProgress,
              onRefresh = { viewModel.refresh() },
              onPush = { viewModel.push() },
              onPull = { viewModel.pull() },
              onFetch = { viewModel.fetch() },
              onRemoveRemote = { viewModel.removeRemote(it) },
            )
          GitSection.STASH ->
            GitStashContent(
              stashes = stashes,
              isLoading = isOperationInProgress,
              onRefresh = { viewModel.refresh() },
              onStash = { viewModel.stash(it) },
              onStashPop = { viewModel.stashPop(it) },
              onStashApply = { viewModel.stashApply(it) },
              onStashDrop = { viewModel.stashDrop(it) },
            )
          GitSection.TAGS ->
            GitTagsContent(
              tags = tags,
              isLoading = isOperationInProgress,
              onRefresh = { viewModel.refresh() },
              onCreateTag = { name, message -> viewModel.createTag(name, message) },
              onDeleteTag = { viewModel.deleteTag(it) },
            )
          GitSection.SETTINGS -> SettingsContent(repository = repository)
        }
      }
    }
  }
}

@Composable
private fun GitNavigationRail(
  selectedSection: GitSection,
  onSectionSelected: (GitSection) -> Unit,
) {
  Column(
    modifier =
      Modifier.width(80.dp)
        .fillMaxHeight()
        .background(GitSurfaceBackground)
        .padding(vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
      text = "Git client",
      style = MaterialTheme.typography.labelMedium,
      color = Color(0xFFE0D4C8),
      modifier = Modifier.padding(bottom = 16.dp),
    )

    GitNavItem(
      icon = Icons.Outlined.SwapVert,
      label = "Changes",
      selected = selectedSection == GitSection.CHANGES,
      onClick = { onSectionSelected(GitSection.CHANGES) },
    )

    GitNavItem(
      icon = Icons.Outlined.History,
      label = "History",
      selected = selectedSection == GitSection.HISTORY,
      onClick = { onSectionSelected(GitSection.HISTORY) },
    )

    GitNavItem(
      icon = Icons.Outlined.CallSplit,
      label = "Branches",
      selected = selectedSection == GitSection.BRANCHES,
      onClick = { onSectionSelected(GitSection.BRANCHES) },
    )

    GitNavItem(
      icon = Icons.Outlined.Wifi,
      label = "Remotes",
      selected = selectedSection == GitSection.REMOTES,
      onClick = { onSectionSelected(GitSection.REMOTES) },
    )

    GitNavItem(
      icon = Icons.Outlined.Archive,
      label = "Stash",
      selected = selectedSection == GitSection.STASH,
      onClick = { onSectionSelected(GitSection.STASH) },
    )

    GitNavItem(
      icon = Icons.Outlined.LocalOffer,
      label = "Tags",
      selected = selectedSection == GitSection.TAGS,
      onClick = { onSectionSelected(GitSection.TAGS) },
    )

    GitNavItem(
      icon = Icons.Outlined.Settings,
      label = "Settings",
      selected = selectedSection == GitSection.SETTINGS,
      onClick = { onSectionSelected(GitSection.SETTINGS) },
    )
  }
}

@Composable
private fun GitNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
  val backgroundColor = if (selected) GitAccentColor.copy(alpha = 0.3f) else Color.Transparent
  val contentColor = if (selected) Color(0xFFE0D4C8) else Color(0xFF888888)

  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    color = backgroundColor,
    modifier = Modifier.padding(horizontal = 8.dp),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = contentColor,
        modifier = Modifier.size(24.dp),
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = contentColor,
        fontSize = 10.sp,
      )
    }
  }
}

@Composable
private fun ChangesContent(
  status: GitStatus?,
  commitMessage: String,
  isLoading: Boolean,
  onRefresh: () -> Unit,
  onStageFile: (String) -> Unit,
  onUnstageFile: (String) -> Unit,
  onStageAll: () -> Unit,
  onUnstageAll: () -> Unit,
  onDiscardFile: (String) -> Unit,
  onCommitMessageChanged: (String) -> Unit,
  onCommit: () -> Unit,
) {
  LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    item {
      GitActionCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          GitActionButton(text = "Stage All", icon = Icons.Default.Add, onClick = onStageAll)
          GitActionButton(
            text = "Refresh",
            icon = Icons.Default.Refresh,
            onClick = onRefresh,
            isLoading = isLoading,
          )
        }
      }
    }

    val stagedChanges = status?.stagedChanges ?: emptyList()
    if (stagedChanges.isNotEmpty()) {
      item {
        GitActionCard {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Staged Changes (${stagedChanges.size})",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFE0D4C8),
                fontWeight = FontWeight.SemiBold,
              )
              GitOutlinedButton(text = "Unstage All", onClick = onUnstageAll)
            }

            stagedChanges.forEach { change ->
              StagedFileItem(
                fileName = change.path,
                status = change.status,
                onUnstage = { onUnstageFile(change.path) },
              )
            }

            HorizontalDivider(color = Color(0xFF444444))

            OutlinedTextField(
              value = commitMessage,
              onValueChange = onCommitMessageChanged,
              modifier = Modifier.fillMaxWidth(),
              placeholder = { Text("Commit message", color = Color(0xFF666666)) },
              minLines = 2,
              maxLines = 4,
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color(0xFFE0D4C8),
                  unfocusedTextColor = Color(0xFFE0D4C8),
                  focusedBorderColor = GitAccentColor,
                  unfocusedBorderColor = Color(0xFF444444),
                  cursorColor = GitAccentColor,
                ),
            )

            GitFilledButton(
              text = "Commit",
              onClick = onCommit,
              enabled = commitMessage.isNotBlank() && stagedChanges.isNotEmpty(),
            )
          }
        }
      }
    }

    val unstagedChanges = mutableListOf<Pair<String, GitFileStatus>>()
    status?.unstagedChanges?.forEach { change -> unstagedChanges.add(change.path to change.status) }
    status?.untrackedFiles?.forEach { file -> unstagedChanges.add(file to GitFileStatus.UNTRACKED) }

    items(unstagedChanges) { (path, fileStatus) ->
      FileChangeCard(
        fileName = path,
        status = fileStatus,
        onStage = { onStageFile(path) },
        onDiscard = { onDiscardFile(path) },
      )
    }

    if (unstagedChanges.isEmpty() && stagedChanges.isEmpty() && status != null) {
      item {
        GitActionCard {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GitCommitHashColor)
            Text("Working tree clean", color = Color(0xFFE0D4C8))
          }
        }
      }
    }
  }
}

@Composable
private fun StagedFileItem(fileName: String, status: GitFileStatus, onUnstage: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f),
    ) {
      FileStatusBadge(status = status)
      Text(
        text = fileName.let { if (it.length > 25) "${it.take(10)}...${it.takeLast(10)}" else it },
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFFE0D4C8),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
    GitOutlinedButton(text = "Unstage", onClick = onUnstage)
  }
}

@Composable
private fun FileChangeCard(
  fileName: String,
  status: GitFileStatus,
  onStage: () -> Unit,
  onDiscard: () -> Unit,
) {
  GitActionCard {
    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        FileStatusBadge(status = status)
        Text(
          text = fileName.let { if (it.length > 20) "${it.take(8)}...${it.takeLast(8)}" else it },
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFFE0D4C8),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GitOutlinedButton(text = "Stage", onClick = onStage)
        GitOutlinedButton(text = "Discard", onClick = onDiscard)
      }
    }
  }
}

@Composable
private fun FileStatusBadge(status: GitFileStatus) {
  val (backgroundColor, text) =
    when (status) {
      GitFileStatus.MODIFIED -> GitStatusModified to "M"
      GitFileStatus.UNTRACKED -> GitStatusUntracked to "U"
      GitFileStatus.ADDED -> Color(0xFF4CAF50) to "A"
      GitFileStatus.DELETED -> Color(0xFFE53935) to "D"
      GitFileStatus.RENAMED -> Color(0xFF2196F3) to "R"
      GitFileStatus.COPIED -> Color(0xFF9C27B0) to "C"
      GitFileStatus.CONFLICT -> Color(0xFFE53935) to "!"
      else -> GitStatusModified to "?"
    }

  Surface(
    shape = RoundedCornerShape(4.dp),
    color = backgroundColor,
    modifier = Modifier.size(28.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Color.White,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
private fun HistoryContent(commits: List<GitCommit>, isLoading: Boolean, onRefresh: () -> Unit) {
  val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

  LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    item {
      GitActionCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Commit History",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFE0D4C8),
            fontWeight = FontWeight.SemiBold,
          )
          GitActionButton(
            text = "Refresh",
            icon = Icons.Default.Refresh,
            onClick = onRefresh,
            isLoading = isLoading,
          )
        }
      }
    }

    items(commits) { commit -> CommitCard(commit = commit, dateFormat = dateFormat) }
  }
}

@Composable
private fun CommitCard(commit: GitCommit, dateFormat: SimpleDateFormat) {
  GitActionCard {
    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = commit.message,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFFE0D4C8),
        maxLines = 4,
        overflow = TextOverflow.Ellipsis,
      )

      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(shape = RoundedCornerShape(4.dp), color = GitCommitHashColor) {
          Text(
            text = commit.shortHash,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
          )
        }

        Text(
          text = dateFormat.format(Date(commit.date)),
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF888888),
        )
      }
    }
  }
}

@Composable
private fun BranchesContent(
  branches: List<GitBranch>,
  currentBranch: String,
  isLoading: Boolean,
  onRefresh: () -> Unit,
  onCheckout: (String) -> Unit,
  onDelete: (String) -> Unit,
  onCreateBranch: (String) -> Unit,
) {
  var showNewBranchDialog by remember { mutableStateOf(false) }
  var newBranchName by remember { mutableStateOf("") }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 80.dp),
    ) {
      item {
        GitActionCard {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Current: $currentBranch",
              style = MaterialTheme.typography.titleMedium,
              color = Color(0xFFE0D4C8),
              fontWeight = FontWeight.SemiBold,
            )
            GitActionButton(
              text = "Refresh",
              icon = Icons.Default.Refresh,
              onClick = onRefresh,
              isLoading = isLoading,
            )
          }
        }
      }

      val localBranches = branches.filter { it.isLocal }
      val remoteBranches = branches.filter { it.isRemote }

      if (localBranches.isNotEmpty()) {
        item {
          Text(
            text = "Local Branches",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF888888),
            modifier = Modifier.padding(vertical = 4.dp),
          )
        }
      }

      items(localBranches) { branch ->
        BranchCard(
          branch = branch,
          isCurrent = branch.isCurrent,
          onCheckout = { onCheckout(branch.name) },
          onDelete = { onDelete(branch.name) },
        )
      }

      if (remoteBranches.isNotEmpty()) {
        item {
          Text(
            text = "Remote Branches",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF888888),
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
          )
        }

        items(remoteBranches) { branch ->
          BranchCard(
            branch = branch,
            isCurrent = false,
            onCheckout = { onCheckout(branch.name) },
            onDelete = null,
          )
        }
      }
    }

    GitFab(
      text = "New Branch",
      onClick = { showNewBranchDialog = true },
      modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
    )
  }

  if (showNewBranchDialog) {
    AlertDialog(
      onDismissRequest = { showNewBranchDialog = false },
      containerColor = GitCardBackground,
      title = { Text("Create Branch", color = Color(0xFFE0D4C8)) },
      text = {
        OutlinedTextField(
          value = newBranchName,
          onValueChange = { newBranchName = it },
          label = { Text("Branch name") },
          singleLine = true,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color(0xFFE0D4C8),
              unfocusedTextColor = Color(0xFFE0D4C8),
            ),
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            if (newBranchName.isNotBlank()) {
              onCreateBranch(newBranchName)
              newBranchName = ""
              showNewBranchDialog = false
            }
          }
        ) {
          Text("Create")
        }
      },
      dismissButton = { TextButton(onClick = { showNewBranchDialog = false }) { Text("Cancel") } },
    )
  }
}

@Composable
private fun BranchCard(
  branch: GitBranch,
  isCurrent: Boolean,
  onCheckout: () -> Unit,
  onDelete: (() -> Unit)?,
) {
  GitActionCard {
    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(
        text = branch.name,
        style = MaterialTheme.typography.bodyLarge,
        color = Color(0xFFE0D4C8),
        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
      )

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GitOutlinedButton(text = "Checkout", onClick = onCheckout, enabled = !isCurrent)
        if (onDelete != null) {
          GitOutlinedButton(text = "Delete", onClick = onDelete, enabled = !isCurrent)
        }
      }
    }
  }
}

@Composable
private fun RemotesContent(
  remotes: List<GitRemote>,
  isLoading: Boolean,
  onRefresh: () -> Unit,
  onPush: () -> Unit,
  onPull: () -> Unit,
  onFetch: () -> Unit,
  onRemoveRemote: (String) -> Unit,
) {
  var showAddRemoteDialog by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 80.dp),
    ) {
      item {
        GitActionCard {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Remote Operations",
              style = MaterialTheme.typography.titleMedium,
              color = Color(0xFFE0D4C8),
              fontWeight = FontWeight.SemiBold,
            )
            GitActionButton(
              text = "Refresh",
              icon = Icons.Default.Refresh,
              onClick = onRefresh,
              isLoading = isLoading,
            )

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(top = 8.dp),
            ) {
              GitOutlinedButton(text = "Push", icon = Icons.Default.Upload, onClick = onPush)
              GitOutlinedButton(text = "Pull", icon = Icons.Default.Download, onClick = onPull)
              GitOutlinedButton(text = "Fetch", icon = Icons.Default.Sync, onClick = onFetch)
            }
          }
        }
      }

      items(remotes) { remote ->
        RemoteCard(remote = remote, onRemove = { onRemoveRemote(remote.name) })
      }
    }

    GitFab(
      text = "Add Remote",
      onClick = { showAddRemoteDialog = true },
      modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
    )
  }
}

@Composable
private fun RemoteCard(remote: GitRemote, onRemove: () -> Unit) {
  GitActionCard {
    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = remote.name,
          style = MaterialTheme.typography.bodyLarge,
          color = Color(0xFFE0D4C8),
          fontWeight = FontWeight.SemiBold,
        )
        GitOutlinedButton(text = "Remove", onClick = onRemove)
      }

      Text(
        text =
          remote.fetchUrl.let { if (it.length > 30) "${it.take(15)}...${it.takeLast(12)}" else it },
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF888888),
      )
    }
  }
}

@Composable
private fun SettingsContent(repository: GitRepository?) {
  var rememberCredentials by remember { mutableStateOf(true) }

  LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    item {
      GitActionCard {
        Column(
          modifier = Modifier.padding(4.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(
            text = "Git User Configuration",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFE0D4C8),
            fontWeight = FontWeight.SemiBold,
          )

          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
              text = "Name:",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF888888),
            )
            Text(
              text = "User",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFE0D4C8),
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
              text = "Email:",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF888888),
            )
            Text(
              text = "user@example.com",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFE0D4C8),
            )
          }

          GitFilledButton(text = "Edit User Config", onClick = {})
        }
      }
    }

    item {
      GitActionCard {
        Column(
          modifier = Modifier.padding(4.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(
            text = "Credentials",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFE0D4C8),
            fontWeight = FontWeight.SemiBold,
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                text = "Remember credentials",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE0D4C8),
              )
              Text(
                text = "Save username and password for push/pull",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888888),
              )
            }

            Switch(
              checked = rememberCredentials,
              onCheckedChange = { rememberCredentials = it },
              colors =
                SwitchDefaults.colors(
                  checkedThumbColor = GitAccentColor,
                  checkedTrackColor = GitAccentColor.copy(alpha = 0.5f),
                ),
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
              text = "Status:",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF888888),
            )
            Text(
              text = if (rememberCredentials) "Saved" else "Not saved",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFE0D4C8),
            )
          }

          GitOutlinedButton(text = "Clear Saved Credentials", onClick = {})
        }
      }
    }
  }
}

@Composable
private fun GitActionCard(
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = GitCardBackground,
  ) {
    Column(modifier = Modifier.padding(16.dp), content = content)
  }
}

@Composable
private fun GitActionButton(
  text: String,
  icon: ImageVector? = null,
  onClick: () -> Unit,
  isLoading: Boolean = false,
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(24.dp),
    color = GitAccentColor,
    enabled = !isLoading,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(16.dp),
          strokeWidth = 2.dp,
          color = Color.White,
        )
      } else {
        icon?.let {
          Icon(
            imageVector = it,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
        }
      }
      Text(text = text, color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
  }
}

@Composable
private fun GitOutlinedButton(
  text: String,
  icon: ImageVector? = null,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(24.dp),
    colors =
      ButtonDefaults.outlinedButtonColors(
        contentColor = Color(0xFFE0D4C8),
        disabledContentColor = Color(0xFF666666),
      ),
    border =
      ButtonDefaults.outlinedButtonBorder(enabled)
        .copy(
          brush =
            androidx.compose.ui.graphics.SolidColor(
              if (enabled) Color(0xFF555555) else Color(0xFF333333)
            )
        ),
  ) {
    icon?.let {
      Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(4.dp))
    }
    Text(text = text)
  }
}

@Composable
private fun GitFilledButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
  Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(24.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = GitAccentColor,
        disabledContainerColor = GitAccentColor.copy(alpha = 0.5f),
      ),
  ) {
    Text(text = text, color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f))
  }
}

@Composable
private fun GitFab(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  ExtendedFloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    containerColor = GitAccentColor,
    contentColor = Color.White,
    shape = RoundedCornerShape(28.dp),
  ) {
    Icon(Icons.Default.Add, contentDescription = null)
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = text)
  }
}
