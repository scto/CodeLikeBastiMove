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
        progressCallback: ((String, Int) -> Unit)? = null,
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
                override fun start(totalTasks: Int) {
                    progressCallback?.invoke("Starting clone...", 0)
                }

                override fun beginTask(title: String?, totalWork: Int) {
                    progressCallback?.invoke(title ?: "Working...", 0)
                }

                override fun update(completed: Int) {
                    progressCallback?.invoke("Cloning...", completed)
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

            status.added.forEach { stagedChanges.add(GitFileChange(it, FileChangeType.ADDED, true)) }
            status.changed.forEach { stagedChanges.add(GitFileChange(it, FileChangeType.MODIFIED, true)) }
            status.removed.forEach { stagedChanges.add(GitFileChange(it, FileChangeType.DELETED, true)) }

            status.modified.forEach { unstagedChanges.add(GitFileChange(it, FileChangeType.MODIFIED, false)) }
            status.missing.forEach { unstagedChanges.add(GitFileChange(it, FileChangeType.DELETED, false)) }

            untrackedFiles.addAll(status.untracked)

            val currentBranch = currentRepo?.branch ?: "HEAD"

            JGitResult.Success(
                GitStatus(
                    branch = currentBranch,
                    stagedChanges = stagedChanges,
                    unstagedChanges = unstagedChanges,
                    untrackedFiles = untrackedFiles,
                    hasConflicts = status.conflicting.isNotEmpty(),
                    conflictingFiles = status.conflicting.toList(),
                    isDetachedHead = ObjectId.isId(currentBranch),
                    ahead = 0,
                    behind = 0,
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

    suspend fun revert(commitHash: String): JGitResult<Unit> = withContext(Dispatchers.IO) {
        val git = currentGit ?: return@withContext JGitResult.Error("No repository open")
        val repo = currentRepo ?: return@withContext JGitResult.Error("No repository open")

        try {
            val objectId = repo.resolve(commitHash)
            if (objectId == null) {
                return@withContext JGitResult.Error("Commit not found: $commitHash")
            }
            git.revert().include(objectId).call()
            JGitResult.Success(Unit)
        } catch (e: Exception) {
            JGitResult.Error("Failed to revert: ${e.message}", e)
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
