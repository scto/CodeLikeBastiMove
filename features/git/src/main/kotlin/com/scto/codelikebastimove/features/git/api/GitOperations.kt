package com.scto.codelikebastimove.features.git.api

import com.scto.codelikebastimove.features.git.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GitOperations {
    
    val currentRepository: StateFlow<GitRepository?>
    val status: StateFlow<GitStatus?>
    val isOperationInProgress: StateFlow<Boolean>
    val operationProgress: Flow<GitOperationProgress>
    
    suspend fun openRepository(path: String): GitOperationResult<GitRepository>
    suspend fun initRepository(path: String): GitOperationResult<GitRepository>
    suspend fun cloneRepository(options: GitCloneOptions): GitOperationResult<GitRepository>
    
    suspend fun getStatus(): GitOperationResult<GitStatus>
    suspend fun refresh(): GitOperationResult<Unit>
    
    suspend fun stage(paths: List<String>): GitOperationResult<Unit>
    suspend fun stageAll(): GitOperationResult<Unit>
    suspend fun unstage(paths: List<String>): GitOperationResult<Unit>
    suspend fun unstageAll(): GitOperationResult<Unit>
    suspend fun discardChanges(paths: List<String>): GitOperationResult<Unit>
    suspend fun discardAllChanges(): GitOperationResult<Unit>
    
    suspend fun commit(options: GitCommitOptions): GitOperationResult<GitCommit>
    suspend fun getLog(branch: String? = null, maxCount: Int = 50, skip: Int = 0): GitOperationResult<GitLog>
    suspend fun getCommitDetails(hash: String): GitOperationResult<GitCommit>
    
    suspend fun getBranches(): GitOperationResult<List<GitBranch>>
    suspend fun createBranch(name: String, startPoint: String? = null, checkout: Boolean = true): GitOperationResult<GitBranch>
    suspend fun deleteBranch(name: String, force: Boolean = false): GitOperationResult<Unit>
    suspend fun renameBranch(oldName: String, newName: String): GitOperationResult<Unit>
    suspend fun checkout(branchOrCommit: String, createNew: Boolean = false): GitOperationResult<Unit>
    
    suspend fun merge(branch: String, message: String? = null, noFastForward: Boolean = false): GitOperationResult<Unit>
    suspend fun rebase(branch: String, interactive: Boolean = false): GitOperationResult<Unit>
    suspend fun abortMerge(): GitOperationResult<Unit>
    suspend fun abortRebase(): GitOperationResult<Unit>
    suspend fun continueMerge(): GitOperationResult<Unit>
    suspend fun continueRebase(): GitOperationResult<Unit>
    
    suspend fun getRemotes(): GitOperationResult<List<GitRemote>>
    suspend fun addRemote(name: String, url: String): GitOperationResult<Unit>
    suspend fun removeRemote(name: String): GitOperationResult<Unit>
    suspend fun renameRemote(oldName: String, newName: String): GitOperationResult<Unit>
    
    suspend fun fetch(remote: String = "origin", prune: Boolean = false): GitOperationResult<Unit>
    suspend fun pull(options: GitPullOptions = GitPullOptions()): GitOperationResult<Unit>
    suspend fun push(options: GitPushOptions = GitPushOptions()): GitOperationResult<Unit>
    
    suspend fun getStashes(): GitOperationResult<List<GitStash>>
    suspend fun stash(message: String? = null, includeUntracked: Boolean = false): GitOperationResult<GitStash>
    suspend fun stashPop(index: Int = 0): GitOperationResult<Unit>
    suspend fun stashApply(index: Int = 0): GitOperationResult<Unit>
    suspend fun stashDrop(index: Int): GitOperationResult<Unit>
    suspend fun stashClear(): GitOperationResult<Unit>
    
    suspend fun getTags(): GitOperationResult<List<GitTag>>
    suspend fun createTag(name: String, message: String? = null, commitHash: String? = null): GitOperationResult<GitTag>
    suspend fun deleteTag(name: String): GitOperationResult<Unit>
    
    suspend fun getDiff(path: String, staged: Boolean = false): GitOperationResult<GitDiff>
    suspend fun getFileDiff(commitHash: String, path: String): GitOperationResult<GitDiff>
    suspend fun getCommitDiff(commitHash: String): GitOperationResult<List<GitDiff>>
    
    suspend fun cherryPick(commitHash: String): GitOperationResult<Unit>
    suspend fun revert(commitHash: String): GitOperationResult<GitCommit>
    suspend fun reset(commitHash: String, mode: ResetMode = ResetMode.MIXED): GitOperationResult<Unit>
    
    suspend fun blame(path: String): GitOperationResult<List<BlameLine>>
    
    suspend fun resolveConflict(path: String, resolution: ConflictResolution): GitOperationResult<Unit>
}

enum class ResetMode {
    SOFT,
    MIXED,
    HARD
}

enum class ConflictResolution {
    OURS,
    THEIRS,
    MANUAL
}

data class BlameLine(
    val lineNumber: Int,
    val content: String,
    val commitHash: String,
    val author: String,
    val date: Long
)

data class GitOperationProgress(
    val operation: String,
    val message: String,
    val progress: Float,
    val isIndeterminate: Boolean = false
)
