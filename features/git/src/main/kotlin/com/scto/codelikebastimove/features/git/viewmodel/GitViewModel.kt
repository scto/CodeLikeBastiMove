package com.scto.codelikebastimove.features.git.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.features.git.api.GitOperations
import com.scto.codelikebastimove.features.git.model.*
import com.scto.codelikebastimove.features.git.repository.DefaultGitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GitViewModel : ViewModel() {
    
    private val repository: GitOperations = DefaultGitRepository()
    
    val currentRepository = repository.currentRepository
    val status = repository.status
    val isOperationInProgress = repository.isOperationInProgress
    
    private val _uiState = MutableStateFlow(GitUiState())
    val uiState: StateFlow<GitUiState> = _uiState.asStateFlow()
    
    private val _branches = MutableStateFlow<List<GitBranch>>(emptyList())
    val branches: StateFlow<List<GitBranch>> = _branches.asStateFlow()
    
    private val _commits = MutableStateFlow<List<GitCommit>>(emptyList())
    val commits: StateFlow<List<GitCommit>> = _commits.asStateFlow()
    
    private val _stashes = MutableStateFlow<List<GitStash>>(emptyList())
    val stashes: StateFlow<List<GitStash>> = _stashes.asStateFlow()
    
    private val _remotes = MutableStateFlow<List<GitRemote>>(emptyList())
    val remotes: StateFlow<List<GitRemote>> = _remotes.asStateFlow()
    
    private val _tags = MutableStateFlow<List<GitTag>>(emptyList())
    val tags: StateFlow<List<GitTag>> = _tags.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()
    
    private val _success = MutableSharedFlow<String>()
    val success: SharedFlow<String> = _success.asSharedFlow()
    
    fun openRepository(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.openRepository(path)) {
                is GitOperationResult.Success -> {
                    loadAllData()
                    _success.emit("Repository opened: ${result.data.name}")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    fun initRepository(path: String) {
        viewModelScope.launch {
            when (val result = repository.initRepository(path)) {
                is GitOperationResult.Success -> {
                    loadAllData()
                    _success.emit("Repository initialized")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
            loadAllData()
        }
    }
    
    private fun loadAllData() {
        viewModelScope.launch {
            loadBranches()
            loadCommits()
            loadStashes()
            loadRemotes()
            loadTags()
        }
    }
    
    private suspend fun loadBranches() {
        when (val result = repository.getBranches()) {
            is GitOperationResult.Success -> _branches.value = result.data
            is GitOperationResult.Error -> _error.emit(result.message)
        }
    }
    
    private suspend fun loadCommits() {
        when (val result = repository.getLog(null, 50, 0)) {
            is GitOperationResult.Success -> _commits.value = result.data.commits
            is GitOperationResult.Error -> _error.emit(result.message)
        }
    }
    
    private suspend fun loadStashes() {
        when (val result = repository.getStashes()) {
            is GitOperationResult.Success -> _stashes.value = result.data
            is GitOperationResult.Error -> {}
        }
    }
    
    private suspend fun loadRemotes() {
        when (val result = repository.getRemotes()) {
            is GitOperationResult.Success -> _remotes.value = result.data
            is GitOperationResult.Error -> {}
        }
    }
    
    private suspend fun loadTags() {
        when (val result = repository.getTags()) {
            is GitOperationResult.Success -> _tags.value = result.data
            is GitOperationResult.Error -> {}
        }
    }
    
    fun stageFile(path: String) {
        viewModelScope.launch {
            when (val result = repository.stage(listOf(path))) {
                is GitOperationResult.Success -> _success.emit("Staged: $path")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun stageAll() {
        viewModelScope.launch {
            when (val result = repository.stageAll()) {
                is GitOperationResult.Success -> _success.emit("All changes staged")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun unstageFile(path: String) {
        viewModelScope.launch {
            when (val result = repository.unstage(listOf(path))) {
                is GitOperationResult.Success -> _success.emit("Unstaged: $path")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun unstageAll() {
        viewModelScope.launch {
            when (val result = repository.unstageAll()) {
                is GitOperationResult.Success -> _success.emit("All changes unstaged")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun discardChanges(path: String) {
        viewModelScope.launch {
            when (val result = repository.discardChanges(listOf(path))) {
                is GitOperationResult.Success -> _success.emit("Changes discarded: $path")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun commit(message: String, amend: Boolean = false) {
        if (message.isBlank() && !amend) {
            viewModelScope.launch { _error.emit("Commit message cannot be empty") }
            return
        }
        
        viewModelScope.launch {
            when (val result = repository.commit(GitCommitOptions(message = message, amend = amend))) {
                is GitOperationResult.Success -> {
                    loadCommits()
                    _success.emit("Committed: ${result.data.shortHash}")
                    _uiState.update { it.copy(commitMessage = "") }
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun checkout(branchOrCommit: String) {
        viewModelScope.launch {
            when (val result = repository.checkout(branchOrCommit)) {
                is GitOperationResult.Success -> {
                    loadBranches()
                    _success.emit("Checked out: $branchOrCommit")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun createBranch(name: String, checkout: Boolean = true) {
        viewModelScope.launch {
            when (val result = repository.createBranch(name, checkout = checkout)) {
                is GitOperationResult.Success -> {
                    loadBranches()
                    _success.emit("Branch created: $name")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun deleteBranch(name: String, force: Boolean = false) {
        viewModelScope.launch {
            when (val result = repository.deleteBranch(name, force)) {
                is GitOperationResult.Success -> {
                    loadBranches()
                    _success.emit("Branch deleted: $name")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun merge(branch: String) {
        viewModelScope.launch {
            when (val result = repository.merge(branch)) {
                is GitOperationResult.Success -> {
                    loadCommits()
                    _success.emit("Merged: $branch")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun fetch() {
        viewModelScope.launch {
            when (val result = repository.fetch()) {
                is GitOperationResult.Success -> _success.emit("Fetch completed")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun pull(rebase: Boolean = false) {
        viewModelScope.launch {
            when (val result = repository.pull(GitPullOptions(rebase = rebase))) {
                is GitOperationResult.Success -> {
                    loadCommits()
                    _success.emit("Pull completed")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun push(force: Boolean = false) {
        viewModelScope.launch {
            when (val result = repository.push(GitPushOptions(force = force))) {
                is GitOperationResult.Success -> _success.emit("Push completed")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun stash(message: String? = null) {
        viewModelScope.launch {
            when (val result = repository.stash(message)) {
                is GitOperationResult.Success -> {
                    loadStashes()
                    _success.emit("Changes stashed")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun stashPop(index: Int = 0) {
        viewModelScope.launch {
            when (val result = repository.stashPop(index)) {
                is GitOperationResult.Success -> {
                    loadStashes()
                    _success.emit("Stash applied and removed")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun stashApply(index: Int = 0) {
        viewModelScope.launch {
            when (val result = repository.stashApply(index)) {
                is GitOperationResult.Success -> _success.emit("Stash applied")
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun stashDrop(index: Int) {
        viewModelScope.launch {
            when (val result = repository.stashDrop(index)) {
                is GitOperationResult.Success -> {
                    loadStashes()
                    _success.emit("Stash dropped")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun createTag(name: String, message: String? = null) {
        viewModelScope.launch {
            when (val result = repository.createTag(name, message)) {
                is GitOperationResult.Success -> {
                    loadTags()
                    _success.emit("Tag created: $name")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun deleteTag(name: String) {
        viewModelScope.launch {
            when (val result = repository.deleteTag(name)) {
                is GitOperationResult.Success -> {
                    loadTags()
                    _success.emit("Tag deleted: $name")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun revert(commitHash: String) {
        viewModelScope.launch {
            when (val result = repository.revert(commitHash)) {
                is GitOperationResult.Success -> {
                    loadCommits()
                    _success.emit("Reverted: ${commitHash.take(7)}")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun cherryPick(commitHash: String) {
        viewModelScope.launch {
            when (val result = repository.cherryPick(commitHash)) {
                is GitOperationResult.Success -> {
                    loadCommits()
                    _success.emit("Cherry-picked: ${commitHash.take(7)}")
                }
                is GitOperationResult.Error -> _error.emit(result.message)
            }
        }
    }
    
    fun updateCommitMessage(message: String) {
        _uiState.update { it.copy(commitMessage = message) }
    }
    
    fun selectTab(tab: GitTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}

data class GitUiState(
    val isLoading: Boolean = false,
    val selectedTab: GitTab = GitTab.CHANGES,
    val commitMessage: String = "",
    val selectedFile: String? = null
)

enum class GitTab {
    CHANGES,
    BRANCHES,
    COMMITS,
    STASH,
    REMOTES,
    TAGS
}
