package com.scto.codelikebastimove.feature.git.library

import com.scto.codelikebastimove.feature.git.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.BranchConfig
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.File

sealed class JGitResult<T> {
    data class Success<T>(val data: T) : JGitResult<T>()
    data class Error<T>(val message: String, val exception: Exception? = null) : JGitResult<T>()
}

enum class ResetMode {
    SOFT,
    MIXED,
    HARD,
}

class JGitLibrary {
    private var currentGit: Git? = null
    private var currentRepo: Repository? = null
    private var credentialsProvider: CredentialsProvider? = null

    fun setCredentials(username: String, password: String) {
        credentialsProvider = UsernamePasswordCredentialsProvider(username, password)
    }

    fun clearCredentials() {
        credentialsProvider = null
    }

    suspend fun openRepository(path: String): JGitResult<Repository> = withContext(Dispatchers.IO) {
        try {
            val gitDir = File(path, ".git")
            val repoBuilder = FileRepositoryBuilder()
                .setGitDir(if (gitDir.exists()) gitDir else File(path))
                .readEnvironment()
                .findGitDir()

            val repo = repoBuilder.build()
            currentRepo = repo
            currentGit = Git(repo)
            JGitResult.Success(repo)
        } catch (e: Exception) {
            JGitResult.Error("Failed to open repository: ${e.message}", e)
        }
    }

    suspend fun initRepository(path: String): JGitResult<Repository> = withContext(Dispatchers.IO) {
        try {
            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val git = Git.init()
                .setDirectory(dir)
                .call()
            currentGit = git
            currentRepo = git.repository
            JGitResult.Success(git.repository)
        } catch (e: Exception) {
            JGitResult.Error("Failed to initialize repository: ${e.message}", e)
        }
    }

    suspend fun cloneRepository(
        url: String,
        directory: String,
        branch: String? = null,
        depth: Int? = null,
        recursive: Boolean = true,
        progressCallback: ((taskName: String, percentDone: Int, isIndeterminate: Boolean) -> Unit)? = null,
    ): JGitResult<Repository> = withContext(Dispatchers.IO) {
        try {
            val targetDir = File(directory)
            if (targetDir.exists() && targetDir.listFiles()?.isNotEmpty() == true) {
                return@withContext JGitResult.Error("Directory already exists and is not empty: $directory")
            }

            val cloneCommand: CloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(targetDir)
                .setCloneSubmodules(recursive)

            branch?.let { cloneCommand.setBranch(it) }

            depth?.let { d ->
                cloneCommand.setDepth(d)
            }

            credentialsProvider?.let { cloneCommand.setCredentialsProvider(it) }

            cloneCommand.setProgressMonitor(object : ProgressMonitor {
                private var currentTask = ""
                private var totalWork = 0
                private var completed = 0
                private var isIndeterminate = false

                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String, totalWork: Int) {
                    currentTask = title
                    this.totalWork = totalWork
                    this.completed = 0
                    this.isIndeterminate = totalWork <= 0
                    progressCallback?.invoke(currentTask, 0, isIndeterminate)
                }
                override fun update(completed: Int) {
                    this.completed += completed
                    val percent = if (totalWork > 0) ((this.completed.toLong() * 100) / totalWork).toInt().coerceIn(0, 100) else 0
                    progressCallback?.invoke(currentTask, percent, isIndeterminate)
                }
                override fun endTask() {}
                override fun isCancelled(): Boolean = false
                override fun showDuration(enabled: Boolean) {}
            })

            val git = cloneCommand.call()
            currentGit = git
            currentRepo = git.repository

            JGitResult.Success(git.repository)
        } catch (e: GitAPIException) {
            JGitResult.Error("Clone failed: ${e.message}", e)
        } catch (e: Exception) {
            JGitResult.Error("Clone failed: ${e.message}", e)
        }
    }

    suspend fun getStatus(): JGitResult<GitStatus> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val status = git.status().call()

            val stagedChanges = mutableListOf<GitFileChange>()
            val unstagedChanges = mutableListOf<GitFileChange>()
            val untrackedFiles = mutableListOf<String>()

            status.added.forEach { stagedChanges.add(GitFileChange(path = it, status = GitFileStatus.ADDED, staged = true)) }
            status.changed.forEach { stagedChanges.add(GitFileChange(path = it, status = GitFileStatus.MODIFIED, staged = true)) }
            status.removed.forEach { stagedChanges.add(GitFileChange(path = it, status = GitFileStatus.DELETED, staged = true)) }

            status.modified.forEach { unstagedChanges.add(GitFileChange(path = it, status = GitFileStatus.MODIFIED, staged = false)) }
            status.missing.forEach { unstagedChanges.add(GitFileChange(path = it, status = GitFileStatus.DELETED, staged = false)) }

            untrackedFiles.addAll(status.untracked)

            val currentBranch = currentRepo?.branch ?: "HEAD"

            JGitResult.Success(
                GitStatus(
                    branch = currentBranch,
                    trackingBranch = null,
                    ahead = 0,
                    behind = 0,
                    stagedChanges = stagedChanges,
                    unstagedChanges = unstagedChanges,
                    untrackedFiles = untrackedFiles,
                    hasConflicts = status.conflicting.isNotEmpty(),
                    conflictedFiles = status.conflicting.toList(),
                )
            )
        } catch (e: Exception) {
            JGitResult.Error("Failed to get status: ${e.message}", e)
        }
    }

    suspend fun stage(paths: List<String>): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val addCommand = git.add()
            paths.forEach { addCommand.addFilepattern(it) }
            addCommand.call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to stage files: ${e.message}", e)
        }
    }

    suspend fun stageAll(): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.add().addFilepattern(".").call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to stage all files: ${e.message}", e)
        }
    }

    suspend fun unstage(paths: List<String>): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val resetCommand = git.reset()
            paths.forEach { resetCommand.addPath(it) }
            resetCommand.call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to unstage files: ${e.message}", e)
        }
    }

    suspend fun commit(
        message: String,
        amend: Boolean = false,
        author: String? = null,
        email: String? = null,
    ): JGitResult<GitCommit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val commitCommand = git.commit()
                .setMessage(message)
                .setAmend(amend)

            if (author != null && email != null) {
                commitCommand.setAuthor(author, email)
            }

            val revCommit = commitCommand.call()

            JGitResult.Success(
                GitCommit(
                    hash = revCommit.name,
                    shortHash = revCommit.abbreviate(7).name(),
                    message = revCommit.fullMessage,
                    author = revCommit.authorIdent.name,
                    authorEmail = revCommit.authorIdent.emailAddress,
                    date = revCommit.commitTime.toLong() * 1000,
                    parents = revCommit.parents.map { it.name },
                )
            )
        } catch (e: Exception) {
            JGitResult.Error("Failed to commit: ${e.message}", e)
        }
    }

    suspend fun getBranches(): JGitResult<List<GitBranch>> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val currentBranchName = repo.branch

            val localBranches = git.branchList().call().map { ref ->
                val name = ref.name.removePrefix("refs/heads/")
                GitBranch(
                    name = name,
                    isLocal = true,
                    isRemote = false,
                    isCurrent = name == currentBranchName,
                    lastCommitHash = ref.objectId?.name,
                    lastCommitMessage = null,
                )
            }

            val remoteBranches = git.branchList()
                .setListMode(ListBranchCommand.ListMode.REMOTE)
                .call()
                .map { ref ->
                    val name = ref.name.removePrefix("refs/remotes/")
                    GitBranch(
                        name = name,
                        isLocal = false,
                        isRemote = true,
                        isCurrent = false,
                        lastCommitHash = ref.objectId?.name,
                        lastCommitMessage = null,
                    )
                }

            JGitResult.Success(localBranches + remoteBranches)
        } catch (e: Exception) {
            JGitResult.Error("Failed to get branches: ${e.message}", e)
        }
    }

    suspend fun createBranch(name: String, checkout: Boolean = false): JGitResult<GitBranch> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                val ref = git.branchCreate()
                    .setName(name)
                    .call()

                if (checkout) {
                    git.checkout().setName(name).call()
                }

                JGitResult.Success(
                    GitBranch(
                        name = name,
                        isLocal = true,
                        isRemote = false,
                        isCurrent = checkout,
                        lastCommitHash = ref.objectId?.name,
                        lastCommitMessage = null,
                    )
                )
            } catch (e: Exception) {
                JGitResult.Error("Failed to create branch: ${e.message}", e)
            }
        }

    suspend fun deleteBranch(name: String, force: Boolean = false): JGitResult<Unit> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                git.branchDelete()
                    .setBranchNames(name)
                    .setForce(force)
                    .call()
                JGitResult.Success(Unit)
            } catch (e: Exception) {
                JGitResult.Error("Failed to delete branch: ${e.message}", e)
            }
        }

    suspend fun checkout(branchOrCommit: String, createNew: Boolean = false): JGitResult<Unit> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                git.checkout()
                    .setName(branchOrCommit)
                    .setCreateBranch(createNew)
                    .call()
                JGitResult.Success(Unit)
            } catch (e: Exception) {
                JGitResult.Error("Failed to checkout: ${e.message}", e)
            }
        }

    suspend fun merge(branch: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val ref = repo.findRef(branch)
            if (ref == null) {
                return@withContext JGitResult.Error("Branch not found: $branch")
            }

            git.merge().include(ref).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to merge: ${e.message}", e)
        }
    }

    suspend fun fetch(remote: String = "origin"): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val fetchCommand = git.fetch().setRemote(remote)
            credentialsProvider?.let { fetchCommand.setCredentialsProvider(it) }
            fetchCommand.call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to fetch: ${e.message}", e)
        }
    }

    suspend fun pull(remote: String = "origin", rebase: Boolean = false): JGitResult<Unit> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                val pullCommand = git.pull()
                    .setRemote(remote)
                    .setRebase(rebase)
                credentialsProvider?.let { pullCommand.setCredentialsProvider(it) }
                pullCommand.call()
                JGitResult.Success(Unit)
            } catch (e: Exception) {
                JGitResult.Error("Failed to pull: ${e.message}", e)
            }
        }

    suspend fun push(
        remote: String = "origin",
        force: Boolean = false,
    ): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val pushCommand = git.push()
                .setRemote(remote)
                .setForce(force)
            credentialsProvider?.let { pushCommand.setCredentialsProvider(it) }
            pushCommand.call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to push: ${e.message}", e)
        }
    }

    suspend fun getLog(maxCount: Int = 50): JGitResult<List<GitCommit>> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val commits = git.log()
                .setMaxCount(maxCount)
                .call()
                .map { revCommit ->
                    GitCommit(
                        hash = revCommit.name,
                        shortHash = revCommit.abbreviate(7).name(),
                        message = revCommit.shortMessage,
                        author = revCommit.authorIdent.name,
                        authorEmail = revCommit.authorIdent.emailAddress,
                        date = revCommit.commitTime.toLong() * 1000,
                        parents = revCommit.parents.map { it.name },
                    )
                }
            JGitResult.Success(commits)
        } catch (e: Exception) {
            JGitResult.Error("Failed to get log: ${e.message}", e)
        }
    }

    suspend fun stash(message: String? = null): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val stashCommand = git.stashCreate()
            message?.let { stashCommand.setWorkingDirectoryMessage(it) }
            stashCommand.call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to stash: ${e.message}", e)
        }
    }

    suspend fun stashPop(): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.stashApply().call()
            git.stashDrop().call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to pop stash: ${e.message}", e)
        }
    }

    suspend fun getRemotes(): JGitResult<List<GitRemote>> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val remotes = git.remoteList().call().map { remoteConfig ->
                GitRemote(
                    name = remoteConfig.name,
                    fetchUrl = remoteConfig.urIs.firstOrNull()?.toString() ?: "",
                    pushUrl = remoteConfig.pushURIs.firstOrNull()?.toString()
                        ?: remoteConfig.urIs.firstOrNull()?.toString() ?: "",
                )
            }
            JGitResult.Success(remotes)
        } catch (e: Exception) {
            JGitResult.Error("Failed to get remotes: ${e.message}", e)
        }
    }

    suspend fun addRemote(name: String, url: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.remoteAdd()
                .setName(name)
                .setUri(org.eclipse.jgit.transport.URIish(url))
                .call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to add remote: ${e.message}", e)
        }
    }

    suspend fun removeRemote(name: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.remoteRemove().setRemoteName(name).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to remove remote: ${e.message}", e)
        }
    }

    suspend fun createTag(name: String, message: String? = null): JGitResult<GitTag> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                val tagCommand = git.tag().setName(name)
                message?.let {
                    tagCommand.setMessage(it)
                    tagCommand.setAnnotated(true)
                }
                val ref = tagCommand.call()
                JGitResult.Success(
                    GitTag(
                        name = name,
                        commitHash = ref.objectId?.name ?: "",
                        message = message,
                        isAnnotated = message != null,
                    )
                )
            } catch (e: Exception) {
                JGitResult.Error("Failed to create tag: ${e.message}", e)
            }
        }

    suspend fun deleteTag(name: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.tagDelete().setTags(name).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to delete tag: ${e.message}", e)
        }
    }

    suspend fun getTags(): JGitResult<List<GitTag>> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val tags = git.tagList().call().map { ref ->
                GitTag(
                    name = ref.name.removePrefix("refs/tags/"),
                    commitHash = ref.objectId?.name ?: "",
                    message = null,
                    isAnnotated = false,
                )
            }
            JGitResult.Success(tags)
        } catch (e: Exception) {
            JGitResult.Error("Failed to get tags: ${e.message}", e)
        }
    }

    suspend fun reset(mode: ResetMode, ref: String = "HEAD"): JGitResult<Unit> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                val resetMode = when (mode) {
                    ResetMode.SOFT -> ResetCommand.ResetType.SOFT
                    ResetMode.MIXED -> ResetCommand.ResetType.MIXED
                    ResetMode.HARD -> ResetCommand.ResetType.HARD
                }
                git.reset().setMode(resetMode).setRef(ref).call()
                JGitResult.Success(Unit)
            } catch (e: Exception) {
                JGitResult.Error("Failed to reset: ${e.message}", e)
            }
        }

    suspend fun cherryPick(commitHash: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val objectId = repo.resolve(commitHash)
            if (objectId == null) {
                return@withContext JGitResult.Error("Commit not found: $commitHash")
            }
            git.cherryPick().include(objectId).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to cherry-pick: ${e.message}", e)
        }
    }

    suspend fun revert(commitHash: String): JGitResult<GitCommit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val objectId = repo.resolve(commitHash)
            if (objectId == null) {
                return@withContext JGitResult.Error("Commit not found: $commitHash")
            }
            val revertResult = git.revert().include(objectId).call()
            val headCommit = git.log().setMaxCount(1).call().firstOrNull()
            if (headCommit != null) {
                JGitResult.Success(
                    GitCommit(
                        hash = headCommit.name,
                        shortHash = headCommit.abbreviate(7).name(),
                        message = headCommit.fullMessage,
                        author = headCommit.authorIdent.name,
                        authorEmail = headCommit.authorIdent.emailAddress,
                        date = headCommit.commitTime.toLong() * 1000,
                        parents = headCommit.parents.map { it.name },
                    )
                )
            } else {
                JGitResult.Error("Revert successful but could not retrieve commit details")
            }
        } catch (e: Exception) {
            JGitResult.Error("Failed to revert: ${e.message}", e)
        }
    }

    suspend fun discardChanges(paths: List<String>): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val checkoutCommand = git.checkout()
            paths.forEach { checkoutCommand.addPath(it) }
            checkoutCommand.call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to discard changes: ${e.message}", e)
        }
    }

    suspend fun discardAllChanges(): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.checkout().setAllPaths(true).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to discard all changes: ${e.message}", e)
        }
    }

    suspend fun renameBranch(oldName: String, newName: String): JGitResult<Unit> =
        withContext(Dispatchers.IO) {
            val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

            try {
                git.branchRename()
                    .setOldName(oldName)
                    .setNewName(newName)
                    .call()
                JGitResult.Success(Unit)
            } catch (e: Exception) {
                JGitResult.Error("Failed to rename branch: ${e.message}", e)
            }
        }

    suspend fun rebase(branch: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.rebase().setUpstream(branch).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to rebase: ${e.message}", e)
        }
    }

    suspend fun abortRebase(): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.rebase().setOperation(org.eclipse.jgit.api.RebaseCommand.Operation.ABORT).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to abort rebase: ${e.message}", e)
        }
    }

    suspend fun continueRebase(): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.rebase().setOperation(org.eclipse.jgit.api.RebaseCommand.Operation.CONTINUE).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to continue rebase: ${e.message}", e)
        }
    }

    suspend fun getStashes(): JGitResult<List<GitStash>> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val stashes = git.stashList().call().mapIndexed { index, revCommit ->
                GitStash(
                    index = index,
                    message = revCommit.shortMessage,
                    branch = "",
                    commitHash = revCommit.name,
                )
            }
            JGitResult.Success(stashes)
        } catch (e: Exception) {
            JGitResult.Error("Failed to get stashes: ${e.message}", e)
        }
    }

    suspend fun stashApply(index: Int): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val stashRef = "stash@{$index}"
            git.stashApply().setStashRef(stashRef).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to apply stash: ${e.message}", e)
        }
    }

    suspend fun stashDrop(index: Int): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            git.stashDrop().setStashRef(index).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to drop stash: ${e.message}", e)
        }
    }

    suspend fun stashClear(): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val stashList = git.stashList().call()
            repeat(stashList.size) {
                git.stashDrop().setStashRef(0).call()
            }
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to clear stashes: ${e.message}", e)
        }
    }

    suspend fun getCommitDetails(hash: String): JGitResult<GitCommit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val objectId = repo.resolve(hash) ?: return@withContext JGitResult.Error("Commit not found: $hash")
            RevWalk(repo).use { revWalk ->
                val commit = revWalk.parseCommit(objectId)
                JGitResult.Success(
                    GitCommit(
                        hash = commit.name,
                        shortHash = commit.abbreviate(7).name(),
                        message = commit.fullMessage,
                        author = commit.authorIdent.name,
                        authorEmail = commit.authorIdent.emailAddress,
                        date = commit.commitTime.toLong() * 1000,
                        parents = commit.parents.map { it.name },
                    )
                )
            }
        } catch (e: Exception) {
            JGitResult.Error("Failed to get commit details: ${e.message}", e)
        }
    }

    suspend fun getDiff(path: String, staged: Boolean): JGitResult<String> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val outputStream = java.io.ByteArrayOutputStream()
            val diffCommand = git.diff().setOutputStream(outputStream)

            if (staged) {
                diffCommand.setCached(true)
            }

            diffCommand.setPathFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(path))
            diffCommand.call()

            JGitResult.Success(outputStream.toString())
        } catch (e: Exception) {
            JGitResult.Error("Failed to get diff: ${e.message}", e)
        }
    }

    suspend fun getCommitDiff(commitHash: String): JGitResult<String> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val objectId = repo.resolve(commitHash) ?: return@withContext JGitResult.Error("Commit not found")
            RevWalk(repo).use { revWalk ->
                val commit = revWalk.parseCommit(objectId)
                val parentId = if (commit.parentCount > 0) commit.getParent(0) else null

                val outputStream = java.io.ByteArrayOutputStream()
                val diffCommand = git.diff().setOutputStream(outputStream)

                if (parentId != null) {
                    val parentCommit = revWalk.parseCommit(parentId)
                    diffCommand.setOldTree(prepareTreeParser(repo, parentCommit))
                }
                diffCommand.setNewTree(prepareTreeParser(repo, commit))
                diffCommand.call()

                JGitResult.Success(outputStream.toString())
            }
        } catch (e: Exception) {
            JGitResult.Error("Failed to get commit diff: ${e.message}", e)
        }
    }

    private fun prepareTreeParser(repo: Repository, commit: RevCommit): org.eclipse.jgit.treewalk.AbstractTreeIterator {
        val treeWalk = TreeWalk(repo)
        treeWalk.addTree(commit.tree)
        treeWalk.isRecursive = true
        return org.eclipse.jgit.treewalk.CanonicalTreeParser().apply {
            reset(repo.newObjectReader(), commit.tree)
        }
    }

    suspend fun getCurrentBranch(): String? = withContext(Dispatchers.IO) {
        currentRepo?.branch
    }

    suspend fun isGitRepository(path: String): Boolean = withContext(Dispatchers.IO) {
        val gitDir = File(path, ".git")
        gitDir.exists() && gitDir.isDirectory
    }

    suspend fun getRepositoryRoot(): String? = withContext(Dispatchers.IO) {
        currentRepo?.directory?.parentFile?.absolutePath
    }

    suspend fun renameRemote(oldName: String, newName: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")

        try {
            val remotes = git.remoteList().call()
            val remote = remotes.find { it.name == oldName }
                ?: return@withContext JGitResult.Error("Remote not found: $oldName")

            val url = remote.urIs.firstOrNull()?.toString() ?: ""

            git.remoteRemove().setRemoteName(oldName).call()
            git.remoteAdd()
                .setName(newName)
                .setUri(org.eclipse.jgit.transport.URIish(url))
                .call()

            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to rename remote: ${e.message}", e)
        }
    }

    fun close() {
        currentGit?.close()
        currentRepo?.close()
        currentGit = null
        currentRepo = null
    }

    fun isRepositoryOpen(): Boolean = currentGit != null && currentRepo != null

    fun getRepositoryPath(): String? = currentRepo?.directory?.parentFile?.absolutePath
}
