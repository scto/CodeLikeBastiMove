package com.scto.codelikebastimove.feature.git.repository

import com.scto.codelikebastimove.feature.git.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class GitCommandExecutor(
    private var workingDirectory: String = ""
) {
    
    companion object {
        private var gitBinaryPath: String = "git"
        private var isGitAvailable: Boolean? = null
        
        fun setGitBinaryPath(path: String) {
            gitBinaryPath = path
            isGitAvailable = null
        }
        
        suspend fun checkGitAvailability(): Boolean {
            if (isGitAvailable != null) return isGitAvailable!!
            
            return withContext(Dispatchers.IO) {
                try {
                    val process = ProcessBuilder(gitBinaryPath, "--version").start()
                    val exitCode = process.waitFor()
                    isGitAvailable = exitCode == 0
                    isGitAvailable!!
                } catch (e: Exception) {
                    isGitAvailable = false
                    false
                }
            }
        }
    }
    
    fun setWorkingDirectory(path: String) {
        workingDirectory = path
    }
    
    suspend fun execute(vararg args: String): CommandResult = withContext(Dispatchers.IO) {
        try {
            if (!checkGitAvailability()) {
                return@withContext CommandResult(
                    exitCode = -1,
                    stdout = "",
                    stderr = "Git binary not available. This feature requires git to be installed or bundled with the application (e.g., via Termux packages or AndroidIDE bootstrap).",
                    success = false
                )
            }
            
            val command = listOf(gitBinaryPath) + args.toList()
            val processBuilder = ProcessBuilder(command)
                .directory(File(workingDirectory))
                .redirectErrorStream(false)
            
            val process = processBuilder.start()
            
            val stdout = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val stderr = BufferedReader(InputStreamReader(process.errorStream)).readText()
            val exitCode = process.waitFor()
            
            CommandResult(
                exitCode = exitCode,
                stdout = stdout.trim(),
                stderr = stderr.trim(),
                success = exitCode == 0
            )
        } catch (e: Exception) {
            CommandResult(
                exitCode = -1,
                stdout = "",
                stderr = e.message ?: "Unknown error",
                success = false
            )
        }
    }
    
    suspend fun isGitRepository(): Boolean {
        val result = execute("rev-parse", "--is-inside-work-tree")
        return result.success && result.stdout == "true"
    }
    
    suspend fun getRepositoryRoot(): String? {
        val result = execute("rev-parse", "--show-toplevel")
        return if (result.success) result.stdout else null
    }
    
    suspend fun getCurrentBranch(): String? {
        val result = execute("rev-parse", "--abbrev-ref", "HEAD")
        return if (result.success) result.stdout else null
    }
    
    suspend fun getStatus(): GitOperationResult<GitStatus> {
        val statusResult = execute("status", "--porcelain=v2", "--branch")
        if (!statusResult.success) {
            return GitOperationResult.Error(statusResult.stderr)
        }
        
        val lines = statusResult.stdout.lines()
        var branch = ""
        var trackingBranch: String? = null
        var ahead = 0
        var behind = 0
        val stagedChanges = mutableListOf<GitFileChange>()
        val unstagedChanges = mutableListOf<GitFileChange>()
        val untrackedFiles = mutableListOf<String>()
        val conflictedFiles = mutableListOf<String>()
        
        for (line in lines) {
            when {
                line.startsWith("# branch.head ") -> branch = line.removePrefix("# branch.head ")
                line.startsWith("# branch.upstream ") -> trackingBranch = line.removePrefix("# branch.upstream ")
                line.startsWith("# branch.ab ") -> {
                    val parts = line.removePrefix("# branch.ab ").split(" ")
                    ahead = parts.getOrNull(0)?.removePrefix("+")?.toIntOrNull() ?: 0
                    behind = parts.getOrNull(1)?.removePrefix("-")?.toIntOrNull() ?: 0
                }
                line.startsWith("1 ") || line.startsWith("2 ") -> {
                    parseFileChange(line)?.let { change ->
                        if (change.staged) stagedChanges.add(change)
                        else unstagedChanges.add(change)
                    }
                }
                line.startsWith("u ") -> {
                    val path = line.substringAfterLast(" ")
                    conflictedFiles.add(path)
                }
                line.startsWith("? ") -> {
                    untrackedFiles.add(line.removePrefix("? "))
                }
            }
        }
        
        return GitOperationResult.Success(
            GitStatus(
                branch = branch,
                trackingBranch = trackingBranch,
                ahead = ahead,
                behind = behind,
                stagedChanges = stagedChanges,
                unstagedChanges = unstagedChanges,
                untrackedFiles = untrackedFiles,
                hasConflicts = conflictedFiles.isNotEmpty(),
                conflictedFiles = conflictedFiles
            )
        )
    }
    
    private fun parseFileChange(line: String): GitFileChange? {
        val parts = line.split(" ", limit = 9)
        if (parts.size < 9) return null
        
        val statusCodes = parts[1]
        val stagedStatus = statusCodes.getOrNull(0) ?: '.'
        val unstagedStatus = statusCodes.getOrNull(1) ?: '.'
        val path = parts[8]
        
        val status = when {
            stagedStatus == 'A' || unstagedStatus == 'A' -> GitFileStatus.ADDED
            stagedStatus == 'D' || unstagedStatus == 'D' -> GitFileStatus.DELETED
            stagedStatus == 'M' || unstagedStatus == 'M' -> GitFileStatus.MODIFIED
            stagedStatus == 'R' || unstagedStatus == 'R' -> GitFileStatus.RENAMED
            stagedStatus == 'C' || unstagedStatus == 'C' -> GitFileStatus.COPIED
            stagedStatus == 'T' || unstagedStatus == 'T' -> GitFileStatus.TYPECHANGE
            else -> GitFileStatus.MODIFIED
        }
        
        return GitFileChange(
            path = path,
            status = status,
            staged = stagedStatus != '.'
        )
    }
    
    suspend fun getLog(branch: String?, maxCount: Int, skip: Int): GitOperationResult<GitLog> {
        val args = mutableListOf(
            "log",
            "--format=%H|%h|%s|%an|%ae|%at|%P",
            "-n", maxCount.toString(),
            "--skip", skip.toString()
        )
        branch?.let { args.add(it) }
        
        val result = execute(*args.toTypedArray())
        if (!result.success) {
            return GitOperationResult.Error(result.stderr)
        }
        
        val commits = result.stdout.lines()
            .filter { it.isNotBlank() }
            .mapNotNull { parseCommitLine(it) }
        
        val countResult = execute("rev-list", "--count", branch ?: "HEAD")
        val totalCount = countResult.stdout.toIntOrNull() ?: commits.size
        
        return GitOperationResult.Success(
            GitLog(
                commits = commits,
                totalCount = totalCount,
                hasMore = skip + commits.size < totalCount
            )
        )
    }
    
    private fun parseCommitLine(line: String): GitCommit? {
        val parts = line.split("|")
        if (parts.size < 6) return null
        
        return GitCommit(
            hash = parts[0],
            shortHash = parts[1],
            message = parts[2],
            author = parts[3],
            authorEmail = parts[4],
            date = parts[5].toLongOrNull()?.times(1000) ?: 0L,
            parents = parts.getOrNull(6)?.split(" ")?.filter { it.isNotBlank() } ?: emptyList(),
            isMergeCommit = (parts.getOrNull(6)?.split(" ")?.size ?: 0) > 1
        )
    }
    
    suspend fun getBranches(): GitOperationResult<List<GitBranch>> {
        val localResult = execute("branch", "--format=%(refname:short)|%(objectname:short)|%(subject)|%(upstream:short)|%(upstream:track,nobracket)")
        val remoteResult = execute("branch", "-r", "--format=%(refname:short)|%(objectname:short)|%(subject)")
        val currentResult = execute("rev-parse", "--abbrev-ref", "HEAD")
        
        val currentBranch = currentResult.stdout.trim()
        val branches = mutableListOf<GitBranch>()
        
        localResult.stdout.lines().filter { it.isNotBlank() }.forEach { line ->
            val parts = line.split("|")
            if (parts.isNotEmpty()) {
                val trackInfo = parts.getOrNull(4) ?: ""
                var ahead = 0
                var behind = 0
                
                Regex("""ahead (\d+)""").find(trackInfo)?.let { ahead = it.groupValues[1].toInt() }
                Regex("""behind (\d+)""").find(trackInfo)?.let { behind = it.groupValues[1].toInt() }
                
                branches.add(
                    GitBranch(
                        name = parts[0],
                        isLocal = true,
                        isRemote = false,
                        isCurrent = parts[0] == currentBranch,
                        lastCommitHash = parts.getOrNull(1),
                        lastCommitMessage = parts.getOrNull(2),
                        trackingBranch = parts.getOrNull(3)?.takeIf { it.isNotBlank() },
                        ahead = ahead,
                        behind = behind
                    )
                )
            }
        }
        
        remoteResult.stdout.lines().filter { it.isNotBlank() }.forEach { line ->
            val parts = line.split("|")
            if (parts.isNotEmpty()) {
                branches.add(
                    GitBranch(
                        name = parts[0],
                        isLocal = false,
                        isRemote = true,
                        isCurrent = false,
                        lastCommitHash = parts.getOrNull(1),
                        lastCommitMessage = parts.getOrNull(2)
                    )
                )
            }
        }
        
        return GitOperationResult.Success(branches)
    }
    
    suspend fun getStashes(): GitOperationResult<List<GitStash>> {
        val result = execute("stash", "list", "--format=%gd|%s|%gs")
        if (!result.success && result.stderr.isNotBlank()) {
            return GitOperationResult.Error(result.stderr)
        }
        
        val stashes = result.stdout.lines()
            .filter { it.isNotBlank() }
            .mapIndexedNotNull { index, line ->
                val parts = line.split("|")
                if (parts.size >= 2) {
                    GitStash(
                        index = index,
                        message = parts.getOrNull(2) ?: parts[1],
                        branch = "",
                        commitHash = ""
                    )
                } else null
            }
        
        return GitOperationResult.Success(stashes)
    }
    
    suspend fun getRemotes(): GitOperationResult<List<GitRemote>> {
        val result = execute("remote", "-v")
        if (!result.success) {
            return GitOperationResult.Error(result.stderr)
        }
        
        val remotes = mutableMapOf<String, Pair<String, String>>()
        
        result.stdout.lines().filter { it.isNotBlank() }.forEach { line ->
            val parts = line.split(Regex("\\s+"))
            if (parts.size >= 2) {
                val name = parts[0]
                val url = parts[1]
                val type = parts.getOrNull(2)?.trim('(', ')') ?: ""
                
                val existing = remotes[name]
                if (existing != null) {
                    if (type == "push") {
                        remotes[name] = existing.first to url
                    }
                } else {
                    remotes[name] = url to url
                }
            }
        }
        
        return GitOperationResult.Success(
            remotes.map { (name, urls) ->
                GitRemote(name = name, fetchUrl = urls.first, pushUrl = urls.second)
            }
        )
    }
    
    suspend fun getTags(): GitOperationResult<List<GitTag>> {
        val result = execute("tag", "-l", "--format=%(refname:short)|%(objectname:short)|%(contents:subject)|%(taggername)|%(taggeremail)|%(creatordate:unix)")
        if (!result.success) {
            return GitOperationResult.Error(result.stderr)
        }
        
        val tags = result.stdout.lines()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = line.split("|")
                if (parts.isNotEmpty()) {
                    GitTag(
                        name = parts[0],
                        commitHash = parts.getOrNull(1) ?: "",
                        message = parts.getOrNull(2)?.takeIf { it.isNotBlank() },
                        isAnnotated = parts.getOrNull(3)?.isNotBlank() == true,
                        taggerName = parts.getOrNull(3)?.takeIf { it.isNotBlank() },
                        taggerEmail = parts.getOrNull(4)?.trim('<', '>')?.takeIf { it.isNotBlank() },
                        date = parts.getOrNull(5)?.toLongOrNull()?.times(1000)
                    )
                } else null
            }
        
        return GitOperationResult.Success(tags)
    }
}

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val success: Boolean
)
