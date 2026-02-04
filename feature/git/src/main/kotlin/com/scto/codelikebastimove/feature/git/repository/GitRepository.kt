package com.scto.codelikebastimove.feature.git.repository

import com.scto.codelikebastimove.feature.git.api.*
import com.scto.codelikebastimove.feature.git.library.JGitLibrary
import com.scto.codelikebastimove.feature.git.library.JGitResult
import com.scto.codelikebastimove.feature.git.model.*
import kotlinx.coroutines.flow.*
import com.scto.codelikebastimove.feature.git.library.ResetMode as JGitResetMode

class DefaultGitRepository : GitOperations {

  private val jgitLibrary = JGitLibrary()

  private val _currentRepository = MutableStateFlow<GitRepository?>(null)
  override val currentRepository: StateFlow<GitRepository?> = _currentRepository.asStateFlow()

  private val _status = MutableStateFlow<GitStatus?>(null)
  override val status: StateFlow<GitStatus?> = _status.asStateFlow()

  private val _isOperationInProgress = MutableStateFlow(false)
  override val isOperationInProgress: StateFlow<Boolean> = _isOperationInProgress.asStateFlow()

  private val _operationProgress = MutableSharedFlow<GitOperationProgress>()
  override val operationProgress: Flow<GitOperationProgress> = _operationProgress.asSharedFlow()

  override suspend fun openRepository(path: String): GitOperationResult<GitRepository> {
    val result = jgitLibrary.openRepository(path)

    return when (result) {
      is JGitResult.Success -> {
        val branch = jgitLibrary.getCurrentBranch() ?: "HEAD"
        val remotesResult = jgitLibrary.getRemotes()
        val remoteUrl = when (remotesResult) {
          is JGitResult.Success -> remotesResult.data.firstOrNull()?.fetchUrl
          is JGitResult.Error -> null
        }

        val repo = GitRepository(
          path = jgitLibrary.getRepositoryRoot() ?: path,
          name = path.substringAfterLast("/"),
          currentBranch = branch,
          isDetachedHead = branch == "HEAD",
          remoteName = "origin",
          remoteUrl = remoteUrl,
        )

        _currentRepository.value = repo
        refresh()

        GitOperationResult.Success(repo)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun initRepository(path: String): GitOperationResult<GitRepository> {
    val result = jgitLibrary.initRepository(path)

    return when (result) {
      is JGitResult.Success -> openRepository(path)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun cloneRepository(
    options: GitCloneOptions
  ): GitOperationResult<GitRepository> {
    _isOperationInProgress.value = true
    emitProgress("Clone", "Cloning repository...", 0f, true)

    try {
      val result = jgitLibrary.cloneRepository(
        url = options.url,
        directory = options.directory,
        branch = options.branch,
        depth = options.depth,
        recursive = options.recursive,
      )

      return when (result) {
        is JGitResult.Success -> {
          openRepository(options.directory)
        }
        is JGitResult.Error -> {
          GitOperationResult.Error(result.message)
        }
      }
    } finally {
      _isOperationInProgress.value = false
    }
  }

  override suspend fun getStatus(): GitOperationResult<GitStatus> {
    return when (val result = jgitLibrary.getStatus()) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun refresh(): GitOperationResult<Unit> {
    val statusResult = jgitLibrary.getStatus()
    if (statusResult is JGitResult.Success) {
      _status.value = statusResult.data
      _currentRepository.value = _currentRepository.value?.copy(
        hasUncommittedChanges =
          statusResult.data.stagedChanges.isNotEmpty() ||
            statusResult.data.unstagedChanges.isNotEmpty() ||
            statusResult.data.untrackedFiles.isNotEmpty()
      )
    }
    return GitOperationResult.Success(Unit)
  }

  override suspend fun stage(paths: List<String>): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.stage(paths)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun stageAll(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.stageAll()) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun unstage(paths: List<String>): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.unstage(paths)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun unstageAll(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.reset(JGitResetMode.MIXED, "HEAD")) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun discardChanges(paths: List<String>): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.discardChanges(paths)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun discardAllChanges(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.discardAllChanges()) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun commit(options: GitCommitOptions): GitOperationResult<GitCommit> {
    return when (val result = jgitLibrary.commit(
      message = options.message,
      amend = options.amend,
    )) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(result.data)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getLog(
    branch: String?,
    maxCount: Int,
    skip: Int,
  ): GitOperationResult<GitLog> {
    return when (val result = jgitLibrary.getLog(maxCount)) {
      is JGitResult.Success -> {
        val commits = if (skip > 0) result.data.drop(skip) else result.data
        GitOperationResult.Success(GitLog(commits = commits, totalCount = result.data.size, hasMore = false))
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getCommitDetails(hash: String): GitOperationResult<GitCommit> {
    return when (val result = jgitLibrary.getCommitDetails(hash)) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getBranches(): GitOperationResult<List<GitBranch>> {
    return when (val result = jgitLibrary.getBranches()) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun createBranch(
    name: String,
    startPoint: String?,
    checkout: Boolean,
  ): GitOperationResult<GitBranch> {
    return when (val result = jgitLibrary.createBranch(name, checkout)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(result.data)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun deleteBranch(name: String, force: Boolean): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.deleteBranch(name, force)) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun renameBranch(oldName: String, newName: String): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.renameBranch(oldName, newName)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun checkout(
    branchOrCommit: String,
    createNew: Boolean,
  ): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.checkout(branchOrCommit, createNew)) {
      is JGitResult.Success -> {
        refresh()
        _currentRepository.value = _currentRepository.value?.copy(
          currentBranch = branchOrCommit,
          isDetachedHead = !createNew && branchOrCommit.length == 40,
        )
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun merge(
    branch: String,
    message: String?,
    noFastForward: Boolean,
  ): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.merge(branch)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun rebase(branch: String, interactive: Boolean): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.rebase(branch)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun abortMerge(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.reset(JGitResetMode.HARD, "HEAD")) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun abortRebase(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.abortRebase()) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun continueMerge(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.commit("Merge commit")) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun continueRebase(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.continueRebase()) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getRemotes(): GitOperationResult<List<GitRemote>> {
    return when (val result = jgitLibrary.getRemotes()) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun addRemote(name: String, url: String): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.addRemote(name, url)) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun removeRemote(name: String): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.removeRemote(name)) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun renameRemote(oldName: String, newName: String): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.renameRemote(oldName, newName)) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun fetch(remote: String, prune: Boolean): GitOperationResult<Unit> {
    _isOperationInProgress.value = true
    emitProgress("Fetch", "Fetching from $remote...", 0f, true)

    try {
      return when (val result = jgitLibrary.fetch(remote)) {
        is JGitResult.Success -> {
          refresh()
          GitOperationResult.Success(Unit)
        }
        is JGitResult.Error -> GitOperationResult.Error(result.message)
      }
    } finally {
      _isOperationInProgress.value = false
    }
  }

  override suspend fun pull(options: GitPullOptions): GitOperationResult<Unit> {
    _isOperationInProgress.value = true
    emitProgress("Pull", "Pulling from ${options.remote}...", 0f, true)

    try {
      return when (val result = jgitLibrary.pull(options.remote, options.rebase)) {
        is JGitResult.Success -> {
          refresh()
          GitOperationResult.Success(Unit)
        }
        is JGitResult.Error -> GitOperationResult.Error(result.message)
      }
    } finally {
      _isOperationInProgress.value = false
    }
  }

  override suspend fun push(options: GitPushOptions): GitOperationResult<Unit> {
    _isOperationInProgress.value = true
    emitProgress("Push", "Pushing to ${options.remote}...", 0f, true)

    try {
      return when (val result = jgitLibrary.push(options.remote, options.force)) {
        is JGitResult.Success -> {
          refresh()
          GitOperationResult.Success(Unit)
        }
        is JGitResult.Error -> GitOperationResult.Error(result.message)
      }
    } finally {
      _isOperationInProgress.value = false
    }
  }

  override suspend fun getStashes(): GitOperationResult<List<GitStash>> {
    return when (val result = jgitLibrary.getStashes()) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun stash(
    message: String?,
    includeUntracked: Boolean,
  ): GitOperationResult<GitStash> {
    return when (val result = jgitLibrary.stash(message)) {
      is JGitResult.Success -> {
        refresh()
        val stashesResult = getStashes()
        if (stashesResult is GitOperationResult.Success && stashesResult.data.isNotEmpty()) {
          GitOperationResult.Success(stashesResult.data.first())
        } else {
          GitOperationResult.Success(GitStash(0, message ?: "WIP", "", ""))
        }
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun stashPop(index: Int): GitOperationResult<Unit> {
    return when (val applyResult = jgitLibrary.stashApply(index)) {
      is JGitResult.Success -> {
        when (val dropResult = jgitLibrary.stashDrop(index)) {
          is JGitResult.Success -> {
            refresh()
            GitOperationResult.Success(Unit)
          }
          is JGitResult.Error -> GitOperationResult.Error(dropResult.message)
        }
      }
      is JGitResult.Error -> GitOperationResult.Error(applyResult.message)
    }
  }

  override suspend fun stashApply(index: Int): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.stashApply(index)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun stashDrop(index: Int): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.stashDrop(index)) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun stashClear(): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.stashClear()) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getTags(): GitOperationResult<List<GitTag>> {
    return when (val result = jgitLibrary.getTags()) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun createTag(
    name: String,
    message: String?,
    commitHash: String?,
  ): GitOperationResult<GitTag> {
    return when (val result = jgitLibrary.createTag(name, message)) {
      is JGitResult.Success -> GitOperationResult.Success(result.data)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun deleteTag(name: String): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.deleteTag(name)) {
      is JGitResult.Success -> GitOperationResult.Success(Unit)
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getDiff(path: String, staged: Boolean): GitOperationResult<GitDiff> {
    return when (val result = jgitLibrary.getDiff(path, staged)) {
      is JGitResult.Success -> GitOperationResult.Success(parseDiff(path, result.data))
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getFileDiff(commitHash: String, path: String): GitOperationResult<GitDiff> {
    return when (val result = jgitLibrary.getCommitDiff(commitHash)) {
      is JGitResult.Success -> GitOperationResult.Success(parseDiff(path, result.data))
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun getCommitDiff(commitHash: String): GitOperationResult<List<GitDiff>> {
    return when (val result = jgitLibrary.getCommitDiff(commitHash)) {
      is JGitResult.Success -> {
        val diffs = parseDiffs(result.data)
        GitOperationResult.Success(diffs)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  private fun parseDiffs(diffOutput: String): List<GitDiff> {
    val diffs = mutableListOf<GitDiff>()
    val diffSections = diffOutput.split(Regex("(?=diff --git)")).filter { it.isNotBlank() }

    for (section in diffSections) {
      val pathMatch = Regex("diff --git a/(.*?) b/(.*)").find(section)
      val path = pathMatch?.groupValues?.get(2) ?: "unknown"
      diffs.add(parseDiff(path, section))
    }

    return diffs
  }

  private fun parseDiff(path: String, diffOutput: String): GitDiff {
    val hunks = mutableListOf<GitDiffHunk>()
    val lines = diffOutput.lines()

    var currentHunk: MutableList<GitDiffLine>? = null
    var oldStart = 0
    var oldCount = 0
    var newStart = 0
    var newCount = 0
    var oldLine = 0
    var newLine = 0

    for (line in lines) {
      when {
        line.startsWith("@@") -> {
          currentHunk?.let { hunks.add(GitDiffHunk(oldStart, oldCount, newStart, newCount, it)) }

          val match = Regex("""@@ -(\d+)(?:,(\d+))? \+(\d+)(?:,(\d+))? @@""").find(line)
          if (match != null) {
            oldStart = match.groupValues[1].toIntOrNull() ?: 0
            oldCount = match.groupValues[2].toIntOrNull() ?: 1
            newStart = match.groupValues[3].toIntOrNull() ?: 0
            newCount = match.groupValues[4].toIntOrNull() ?: 1
            oldLine = oldStart
            newLine = newStart
          }

          currentHunk = mutableListOf()
          currentHunk.add(GitDiffLine(line, DiffLineType.HEADER, null, null))
        }
        currentHunk != null -> {
          when {
            line.startsWith("+") -> {
              currentHunk.add(
                GitDiffLine(line.removePrefix("+"), DiffLineType.ADDITION, null, newLine++)
              )
            }
            line.startsWith("-") -> {
              currentHunk.add(
                GitDiffLine(line.removePrefix("-"), DiffLineType.DELETION, oldLine++, null)
              )
            }
            line.startsWith(" ") || line.isEmpty() -> {
              currentHunk.add(
                GitDiffLine(line.removePrefix(" "), DiffLineType.CONTEXT, oldLine++, newLine++)
              )
            }
          }
        }
      }
    }

    currentHunk?.let { hunks.add(GitDiffHunk(oldStart, oldCount, newStart, newCount, it)) }

    return GitDiff(oldPath = null, newPath = path, hunks = hunks)
  }

  override suspend fun cherryPick(commitHash: String): GitOperationResult<Unit> {
    return when (val result = jgitLibrary.cherryPick(commitHash)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun revert(commitHash: String): GitOperationResult<GitCommit> {
    return when (val result = jgitLibrary.revert(commitHash)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(result.data)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun reset(
    commitHash: String,
    mode: ResetMode,
  ): GitOperationResult<Unit> {
    val resetMode = when (mode) {
      ResetMode.SOFT -> JGitResetMode.SOFT
      ResetMode.MIXED -> JGitResetMode.MIXED
      ResetMode.HARD -> JGitResetMode.HARD
    }

    return when (val result = jgitLibrary.reset(resetMode, commitHash)) {
      is JGitResult.Success -> {
        refresh()
        GitOperationResult.Success(Unit)
      }
      is JGitResult.Error -> GitOperationResult.Error(result.message)
    }
  }

  override suspend fun blame(path: String): GitOperationResult<List<BlameLine>> {
    return GitOperationResult.Error("Blame operation not yet supported with JGit")
  }

  override suspend fun resolveConflict(
    path: String,
    resolution: ConflictResolution,
  ): GitOperationResult<Unit> {
    return GitOperationResult.Error("Conflict resolution not yet supported with JGit")
  }

  override suspend fun getConfig(key: String): GitOperationResult<String?> {
    return GitOperationResult.Error("Config retrieval not yet supported with JGit")
  }

  override suspend fun setConfig(
    key: String,
    value: String,
    global: Boolean,
  ): GitOperationResult<Unit> {
    return GitOperationResult.Error("Config setting not yet supported with JGit")
  }

  override suspend fun clean(
    directories: Boolean,
    force: Boolean,
    dryRun: Boolean,
  ): GitOperationResult<List<String>> {
    return GitOperationResult.Error("Clean operation not yet supported with JGit")
  }

  override suspend fun setCredentials(username: String, password: String) {
    jgitLibrary.setCredentials(username, password)
  }

  override suspend fun clearCredentials() {
    jgitLibrary.clearCredentials()
  }

  private suspend fun emitProgress(
    operation: String,
    message: String,
    progress: Float,
    indeterminate: Boolean
  ) {
    _operationProgress.emit(GitOperationProgress(operation, message, progress, indeterminate))
  }
}
