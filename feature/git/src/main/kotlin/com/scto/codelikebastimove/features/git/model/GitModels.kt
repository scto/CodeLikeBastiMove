package com.scto.codelikebastimove.feature.git.model

data class GitRepository(
  val path: String,
  val name: String,
  val currentBranch: String,
  val isDetachedHead: Boolean = false,
  val hasUncommittedChanges: Boolean = false,
  val remoteName: String? = null,
  val remoteUrl: String? = null,
)

data class GitBranch(
  val name: String,
  val isLocal: Boolean,
  val isRemote: Boolean,
  val isCurrent: Boolean,
  val lastCommitHash: String?,
  val lastCommitMessage: String?,
  val trackingBranch: String? = null,
  val ahead: Int = 0,
  val behind: Int = 0,
)

data class GitCommit(
  val hash: String,
  val shortHash: String,
  val message: String,
  val author: String,
  val authorEmail: String,
  val date: Long,
  val parents: List<String> = emptyList(),
  val isMergeCommit: Boolean = false,
)

data class GitFileChange(
  val path: String,
  val oldPath: String? = null,
  val status: GitFileStatus,
  val staged: Boolean,
  val additions: Int = 0,
  val deletions: Int = 0,
)

enum class GitFileStatus(val displayName: String, val shortCode: String) {
  ADDED("Added", "A"),
  MODIFIED("Modified", "M"),
  DELETED("Deleted", "D"),
  RENAMED("Renamed", "R"),
  COPIED("Copied", "C"),
  UNTRACKED("Untracked", "?"),
  IGNORED("Ignored", "!"),
  CONFLICT("Conflict", "U"),
  TYPECHANGE("Type Changed", "T"),
}

data class GitStash(
  val index: Int,
  val message: String,
  val branch: String,
  val commitHash: String,
)

data class GitRemote(val name: String, val fetchUrl: String, val pushUrl: String)

data class GitTag(
  val name: String,
  val commitHash: String,
  val message: String?,
  val isAnnotated: Boolean,
  val taggerName: String? = null,
  val taggerEmail: String? = null,
  val date: Long? = null,
)

data class GitDiff(val oldPath: String?, val newPath: String, val hunks: List<GitDiffHunk>)

data class GitDiffHunk(
  val oldStart: Int,
  val oldCount: Int,
  val newStart: Int,
  val newCount: Int,
  val lines: List<GitDiffLine>,
)

data class GitDiffLine(
  val content: String,
  val type: DiffLineType,
  val oldLineNumber: Int?,
  val newLineNumber: Int?,
)

enum class DiffLineType {
  CONTEXT,
  ADDITION,
  DELETION,
  HEADER,
}

data class GitLog(val commits: List<GitCommit>, val totalCount: Int, val hasMore: Boolean)

data class GitStatus(
  val branch: String,
  val trackingBranch: String?,
  val ahead: Int,
  val behind: Int,
  val stagedChanges: List<GitFileChange>,
  val unstagedChanges: List<GitFileChange>,
  val untrackedFiles: List<String>,
  val hasConflicts: Boolean,
  val conflictedFiles: List<String>,
)

sealed class GitOperationResult<T> {
  data class Success<T>(val data: T) : GitOperationResult<T>()

  data class Error<T>(val message: String, val exception: Exception? = null) :
    GitOperationResult<T>()
}

enum class MergeStrategy {
  MERGE,
  REBASE,
  FAST_FORWARD,
}

data class GitCloneOptions(
  val url: String,
  val directory: String,
  val branch: String? = null,
  val depth: Int? = null,
  val recursive: Boolean = true,
)

data class GitPushOptions(
  val remote: String = "origin",
  val branch: String? = null,
  val force: Boolean = false,
  val setUpstream: Boolean = false,
  val tags: Boolean = false,
)

data class GitPullOptions(
  val remote: String = "origin",
  val branch: String? = null,
  val rebase: Boolean = false,
  val autostash: Boolean = false,
)

data class GitCommitOptions(
  val message: String,
  val amend: Boolean = false,
  val allowEmpty: Boolean = false,
  val signoff: Boolean = false,
)
