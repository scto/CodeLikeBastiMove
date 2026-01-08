package com.scto.codelikebastimove.features.git.repository

import com.scto.codelikebastimove.features.git.api.*
import com.scto.codelikebastimove.features.git.model.*
import kotlinx.coroutines.flow.*

class DefaultGitRepository : GitOperations {
    
    private val executor = GitCommandExecutor()
    
    private val _currentRepository = MutableStateFlow<GitRepository?>(null)
    override val currentRepository: StateFlow<GitRepository?> = _currentRepository.asStateFlow()
    
    private val _status = MutableStateFlow<GitStatus?>(null)
    override val status: StateFlow<GitStatus?> = _status.asStateFlow()
    
    private val _isOperationInProgress = MutableStateFlow(false)
    override val isOperationInProgress: StateFlow<Boolean> = _isOperationInProgress.asStateFlow()
    
    private val _operationProgress = MutableSharedFlow<GitOperationProgress>()
    override val operationProgress: Flow<GitOperationProgress> = _operationProgress.asSharedFlow()
    
    override suspend fun openRepository(path: String): GitOperationResult<GitRepository> {
        executor.setWorkingDirectory(path)
        
        if (!executor.isGitRepository()) {
            return GitOperationResult.Error("Not a git repository: $path")
        }
        
        val rootPath = executor.getRepositoryRoot() ?: path
        val branch = executor.getCurrentBranch() ?: "HEAD"
        
        val remoteResult = executor.execute("remote", "get-url", "origin")
        
        val repo = GitRepository(
            path = rootPath,
            name = rootPath.substringAfterLast("/"),
            currentBranch = branch,
            isDetachedHead = branch == "HEAD",
            remoteName = "origin".takeIf { remoteResult.success },
            remoteUrl = remoteResult.stdout.takeIf { remoteResult.success }
        )
        
        _currentRepository.value = repo
        refresh()
        
        return GitOperationResult.Success(repo)
    }
    
    override suspend fun initRepository(path: String): GitOperationResult<GitRepository> {
        executor.setWorkingDirectory(path)
        val result = executor.execute("init")
        
        return if (result.success) {
            openRepository(path)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun cloneRepository(options: GitCloneOptions): GitOperationResult<GitRepository> {
        _isOperationInProgress.value = true
        emitProgress("Clone", "Cloning repository...", 0f, true)
        
        try {
            val args = mutableListOf("clone", options.url, options.directory)
            options.branch?.let { args.addAll(listOf("-b", it)) }
            options.depth?.let { args.addAll(listOf("--depth", it.toString())) }
            if (options.recursive) args.add("--recursive")
            
            val result = executor.execute(*args.toTypedArray())
            
            return if (result.success) {
                openRepository(options.directory)
            } else {
                GitOperationResult.Error(result.stderr)
            }
        } finally {
            _isOperationInProgress.value = false
        }
    }
    
    override suspend fun getStatus(): GitOperationResult<GitStatus> {
        return executor.getStatus()
    }
    
    override suspend fun refresh(): GitOperationResult<Unit> {
        val statusResult = executor.getStatus()
        if (statusResult is GitOperationResult.Success) {
            _status.value = statusResult.data
            _currentRepository.value = _currentRepository.value?.copy(
                hasUncommittedChanges = statusResult.data.stagedChanges.isNotEmpty() ||
                        statusResult.data.unstagedChanges.isNotEmpty() ||
                        statusResult.data.untrackedFiles.isNotEmpty()
            )
        }
        return GitOperationResult.Success(Unit)
    }
    
    override suspend fun stage(paths: List<String>): GitOperationResult<Unit> {
        val result = executor.execute("add", *paths.toTypedArray())
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun stageAll(): GitOperationResult<Unit> {
        val result = executor.execute("add", "-A")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun unstage(paths: List<String>): GitOperationResult<Unit> {
        val result = executor.execute("restore", "--staged", *paths.toTypedArray())
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun unstageAll(): GitOperationResult<Unit> {
        val result = executor.execute("reset", "HEAD")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun discardChanges(paths: List<String>): GitOperationResult<Unit> {
        val result = executor.execute("checkout", "--", *paths.toTypedArray())
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun discardAllChanges(): GitOperationResult<Unit> {
        val result = executor.execute("checkout", "--", ".")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun commit(options: GitCommitOptions): GitOperationResult<GitCommit> {
        val args = mutableListOf("commit", "-m", options.message)
        if (options.amend) args.add("--amend")
        if (options.allowEmpty) args.add("--allow-empty")
        if (options.signoff) args.add("--signoff")
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            val logResult = executor.getLog(null, 1, 0)
            if (logResult is GitOperationResult.Success && logResult.data.commits.isNotEmpty()) {
                refresh()
                GitOperationResult.Success(logResult.data.commits.first())
            } else {
                GitOperationResult.Error("Commit successful but could not retrieve commit details")
            }
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getLog(branch: String?, maxCount: Int, skip: Int): GitOperationResult<GitLog> {
        return executor.getLog(branch, maxCount, skip)
    }
    
    override suspend fun getCommitDetails(hash: String): GitOperationResult<GitCommit> {
        val result = executor.execute("show", "-s", "--format=%H|%h|%s|%an|%ae|%at|%P|%B", hash)
        
        return if (result.success) {
            val lines = result.stdout.lines()
            val firstLine = lines.firstOrNull() ?: return GitOperationResult.Error("Empty commit")
            val parts = firstLine.split("|", limit = 8)
            
            if (parts.size >= 6) {
                GitOperationResult.Success(
                    GitCommit(
                        hash = parts[0],
                        shortHash = parts[1],
                        message = parts.getOrNull(7) ?: parts[2],
                        author = parts[3],
                        authorEmail = parts[4],
                        date = parts[5].toLongOrNull()?.times(1000) ?: 0L,
                        parents = parts.getOrNull(6)?.split(" ")?.filter { it.isNotBlank() } ?: emptyList()
                    )
                )
            } else {
                GitOperationResult.Error("Could not parse commit")
            }
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getBranches(): GitOperationResult<List<GitBranch>> {
        return executor.getBranches()
    }
    
    override suspend fun createBranch(name: String, startPoint: String?, checkout: Boolean): GitOperationResult<GitBranch> {
        val args = mutableListOf(if (checkout) "checkout" else "branch")
        if (checkout) args.add("-b")
        args.add(name)
        startPoint?.let { args.add(it) }
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            refresh()
            GitOperationResult.Success(
                GitBranch(
                    name = name,
                    isLocal = true,
                    isRemote = false,
                    isCurrent = checkout,
                    lastCommitHash = null,
                    lastCommitMessage = null
                )
            )
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun deleteBranch(name: String, force: Boolean): GitOperationResult<Unit> {
        val flag = if (force) "-D" else "-d"
        val result = executor.execute("branch", flag, name)
        
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun renameBranch(oldName: String, newName: String): GitOperationResult<Unit> {
        val result = executor.execute("branch", "-m", oldName, newName)
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun checkout(branchOrCommit: String, createNew: Boolean): GitOperationResult<Unit> {
        val args = mutableListOf("checkout")
        if (createNew) args.add("-b")
        args.add(branchOrCommit)
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            refresh()
            _currentRepository.value = _currentRepository.value?.copy(
                currentBranch = branchOrCommit,
                isDetachedHead = !createNew && branchOrCommit.length == 40
            )
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun merge(branch: String, message: String?, noFastForward: Boolean): GitOperationResult<Unit> {
        val args = mutableListOf("merge", branch)
        if (noFastForward) args.add("--no-ff")
        message?.let { args.addAll(listOf("-m", it)) }
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun rebase(branch: String, interactive: Boolean): GitOperationResult<Unit> {
        val args = mutableListOf("rebase")
        if (interactive) args.add("-i")
        args.add(branch)
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun abortMerge(): GitOperationResult<Unit> {
        val result = executor.execute("merge", "--abort")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun abortRebase(): GitOperationResult<Unit> {
        val result = executor.execute("rebase", "--abort")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun continueMerge(): GitOperationResult<Unit> {
        val result = executor.execute("merge", "--continue")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun continueRebase(): GitOperationResult<Unit> {
        val result = executor.execute("rebase", "--continue")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getRemotes(): GitOperationResult<List<GitRemote>> {
        return executor.getRemotes()
    }
    
    override suspend fun addRemote(name: String, url: String): GitOperationResult<Unit> {
        val result = executor.execute("remote", "add", name, url)
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun removeRemote(name: String): GitOperationResult<Unit> {
        val result = executor.execute("remote", "remove", name)
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun renameRemote(oldName: String, newName: String): GitOperationResult<Unit> {
        val result = executor.execute("remote", "rename", oldName, newName)
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun fetch(remote: String, prune: Boolean): GitOperationResult<Unit> {
        _isOperationInProgress.value = true
        emitProgress("Fetch", "Fetching from $remote...", 0f, true)
        
        try {
            val args = mutableListOf("fetch", remote)
            if (prune) args.add("--prune")
            
            val result = executor.execute(*args.toTypedArray())
            
            return if (result.success) {
                refresh()
                GitOperationResult.Success(Unit)
            } else {
                GitOperationResult.Error(result.stderr)
            }
        } finally {
            _isOperationInProgress.value = false
        }
    }
    
    override suspend fun pull(options: GitPullOptions): GitOperationResult<Unit> {
        _isOperationInProgress.value = true
        emitProgress("Pull", "Pulling from ${options.remote}...", 0f, true)
        
        try {
            val args = mutableListOf("pull", options.remote)
            options.branch?.let { args.add(it) }
            if (options.rebase) args.add("--rebase")
            if (options.autostash) args.add("--autostash")
            
            val result = executor.execute(*args.toTypedArray())
            
            return if (result.success) {
                refresh()
                GitOperationResult.Success(Unit)
            } else {
                GitOperationResult.Error(result.stderr)
            }
        } finally {
            _isOperationInProgress.value = false
        }
    }
    
    override suspend fun push(options: GitPushOptions): GitOperationResult<Unit> {
        _isOperationInProgress.value = true
        emitProgress("Push", "Pushing to ${options.remote}...", 0f, true)
        
        try {
            val args = mutableListOf("push", options.remote)
            options.branch?.let { args.add(it) }
            if (options.force) args.add("--force")
            if (options.setUpstream) args.add("--set-upstream")
            if (options.tags) args.add("--tags")
            
            val result = executor.execute(*args.toTypedArray())
            
            return if (result.success) {
                refresh()
                GitOperationResult.Success(Unit)
            } else {
                GitOperationResult.Error(result.stderr)
            }
        } finally {
            _isOperationInProgress.value = false
        }
    }
    
    override suspend fun getStashes(): GitOperationResult<List<GitStash>> {
        return executor.getStashes()
    }
    
    override suspend fun stash(message: String?, includeUntracked: Boolean): GitOperationResult<GitStash> {
        val args = mutableListOf("stash", "push")
        message?.let { args.addAll(listOf("-m", it)) }
        if (includeUntracked) args.add("-u")
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            refresh()
            val stashesResult = getStashes()
            if (stashesResult is GitOperationResult.Success && stashesResult.data.isNotEmpty()) {
                GitOperationResult.Success(stashesResult.data.first())
            } else {
                GitOperationResult.Success(GitStash(0, message ?: "WIP", "", ""))
            }
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun stashPop(index: Int): GitOperationResult<Unit> {
        val result = executor.execute("stash", "pop", "stash@{$index}")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun stashApply(index: Int): GitOperationResult<Unit> {
        val result = executor.execute("stash", "apply", "stash@{$index}")
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun stashDrop(index: Int): GitOperationResult<Unit> {
        val result = executor.execute("stash", "drop", "stash@{$index}")
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun stashClear(): GitOperationResult<Unit> {
        val result = executor.execute("stash", "clear")
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getTags(): GitOperationResult<List<GitTag>> {
        return executor.getTags()
    }
    
    override suspend fun createTag(name: String, message: String?, commitHash: String?): GitOperationResult<GitTag> {
        val args = mutableListOf("tag")
        if (message != null) {
            args.addAll(listOf("-a", name, "-m", message))
        } else {
            args.add(name)
        }
        commitHash?.let { args.add(it) }
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            GitOperationResult.Success(
                GitTag(
                    name = name,
                    commitHash = commitHash ?: "",
                    message = message,
                    isAnnotated = message != null
                )
            )
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun deleteTag(name: String): GitOperationResult<Unit> {
        val result = executor.execute("tag", "-d", name)
        return if (result.success) {
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getDiff(path: String, staged: Boolean): GitOperationResult<GitDiff> {
        val args = mutableListOf("diff")
        if (staged) args.add("--cached")
        args.add("--")
        args.add(path)
        
        val result = executor.execute(*args.toTypedArray())
        
        return if (result.success) {
            GitOperationResult.Success(parseDiff(path, result.stdout))
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getFileDiff(commitHash: String, path: String): GitOperationResult<GitDiff> {
        val result = executor.execute("show", "--format=", commitHash, "--", path)
        
        return if (result.success) {
            GitOperationResult.Success(parseDiff(path, result.stdout))
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun getCommitDiff(commitHash: String): GitOperationResult<List<GitDiff>> {
        val result = executor.execute("show", "--format=", "--name-only", commitHash)
        
        if (!result.success) {
            return GitOperationResult.Error(result.stderr)
        }
        
        val files = result.stdout.lines().filter { it.isNotBlank() }
        val diffs = files.mapNotNull { file ->
            when (val diffResult = getFileDiff(commitHash, file)) {
                is GitOperationResult.Success -> diffResult.data
                is GitOperationResult.Error -> null
            }
        }
        
        return GitOperationResult.Success(diffs)
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
                    currentHunk?.let {
                        hunks.add(GitDiffHunk(oldStart, oldCount, newStart, newCount, it))
                    }
                    
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
                            currentHunk.add(GitDiffLine(line.removePrefix("+"), DiffLineType.ADDITION, null, newLine++))
                        }
                        line.startsWith("-") -> {
                            currentHunk.add(GitDiffLine(line.removePrefix("-"), DiffLineType.DELETION, oldLine++, null))
                        }
                        line.startsWith(" ") || line.isEmpty() -> {
                            currentHunk.add(GitDiffLine(line.removePrefix(" "), DiffLineType.CONTEXT, oldLine++, newLine++))
                        }
                    }
                }
            }
        }
        
        currentHunk?.let {
            hunks.add(GitDiffHunk(oldStart, oldCount, newStart, newCount, it))
        }
        
        return GitDiff(oldPath = null, newPath = path, hunks = hunks)
    }
    
    override suspend fun cherryPick(commitHash: String): GitOperationResult<Unit> {
        val result = executor.execute("cherry-pick", commitHash)
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun revert(commitHash: String): GitOperationResult<GitCommit> {
        val result = executor.execute("revert", "--no-edit", commitHash)
        
        return if (result.success) {
            val logResult = getLog(null, 1, 0)
            if (logResult is GitOperationResult.Success && logResult.data.commits.isNotEmpty()) {
                refresh()
                GitOperationResult.Success(logResult.data.commits.first())
            } else {
                GitOperationResult.Error("Revert successful but could not retrieve commit details")
            }
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun reset(commitHash: String, mode: ResetMode): GitOperationResult<Unit> {
        val modeFlag = when (mode) {
            ResetMode.SOFT -> "--soft"
            ResetMode.MIXED -> "--mixed"
            ResetMode.HARD -> "--hard"
        }
        
        val result = executor.execute("reset", modeFlag, commitHash)
        
        return if (result.success) {
            refresh()
            GitOperationResult.Success(Unit)
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    override suspend fun blame(path: String): GitOperationResult<List<BlameLine>> {
        val result = executor.execute("blame", "--line-porcelain", path)
        
        if (!result.success) {
            return GitOperationResult.Error(result.stderr)
        }
        
        val blameLines = mutableListOf<BlameLine>()
        val lines = result.stdout.lines()
        var lineNumber = 0
        var currentHash = ""
        var currentAuthor = ""
        var currentDate = 0L
        var currentContent = ""
        
        for (line in lines) {
            when {
                line.matches(Regex("^[a-f0-9]{40}.*")) -> {
                    currentHash = line.take(40)
                    lineNumber++
                }
                line.startsWith("author ") -> currentAuthor = line.removePrefix("author ")
                line.startsWith("author-time ") -> currentDate = line.removePrefix("author-time ").toLongOrNull()?.times(1000) ?: 0L
                line.startsWith("\t") -> {
                    currentContent = line.removePrefix("\t")
                    blameLines.add(
                        BlameLine(
                            lineNumber = lineNumber,
                            content = currentContent,
                            commitHash = currentHash,
                            author = currentAuthor,
                            date = currentDate
                        )
                    )
                }
            }
        }
        
        return GitOperationResult.Success(blameLines)
    }
    
    override suspend fun resolveConflict(path: String, resolution: ConflictResolution): GitOperationResult<Unit> {
        val args = when (resolution) {
            ConflictResolution.OURS -> arrayOf("checkout", "--ours", path)
            ConflictResolution.THEIRS -> arrayOf("checkout", "--theirs", path)
            ConflictResolution.MANUAL -> return GitOperationResult.Success(Unit)
        }
        
        val result = executor.execute(*args)
        
        return if (result.success) {
            stage(listOf(path))
        } else {
            GitOperationResult.Error(result.stderr)
        }
    }
    
    private suspend fun emitProgress(operation: String, message: String, progress: Float, isIndeterminate: Boolean = false) {
        _operationProgress.emit(GitOperationProgress(operation, message, progress, isIndeterminate))
    }
}
