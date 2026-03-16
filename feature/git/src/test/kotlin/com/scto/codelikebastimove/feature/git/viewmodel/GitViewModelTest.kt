package com.scto.codelikebastimove.feature.git.viewmodel

import app.cash.turbine.test
import com.scto.codelikebastimove.feature.git.api.GitOperations
import com.scto.codelikebastimove.feature.git.model.*
import com.scto.codelikebastimove.feature.git.repository.DefaultGitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.unmockkConstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GitViewModel
    private lateinit var mockRepository: DefaultGitRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(DefaultGitRepository::class)
        mockRepository = io.mockk.mockk<DefaultGitRepository>(relaxed = true)

        // Setup base mock responses
        val emptyBranchFlow = MutableStateFlow<GitRepository?>(null)
        val emptyStatusFlow = MutableStateFlow<GitStatus?>(null)
        val emptyBooleanFlow = MutableStateFlow(false)
        val emptyCloneProgressFlow = MutableStateFlow(com.scto.codelikebastimove.feature.git.api.CloneProgressInfo())

        every { anyConstructed<DefaultGitRepository>().currentRepository } returns emptyBranchFlow
        every { anyConstructed<DefaultGitRepository>().status } returns emptyStatusFlow
        every { anyConstructed<DefaultGitRepository>().isOperationInProgress } returns emptyBooleanFlow
        every { anyConstructed<DefaultGitRepository>().cloneProgress } returns emptyCloneProgressFlow

        viewModel = GitViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `openRepository failure propagates error message`() = runTest {
        val errorMessage = "Failed to open repository"
        val path = "/some/path"
        coEvery { anyConstructed<DefaultGitRepository>().openRepository(path) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.openRepository(path)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTag failure propagates error message`() = runTest {
        val errorMessage = "Failed to delete tag"
        val name = "v1.0.0"
        coEvery { anyConstructed<DefaultGitRepository>().deleteTag(name) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.deleteTag(name)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `revert failure propagates error message`() = runTest {
        val errorMessage = "Failed to revert"
        val commitHash = "abcdef123"
        coEvery { anyConstructed<DefaultGitRepository>().revert(commitHash) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.revert(commitHash)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cherryPick failure propagates error message`() = runTest {
        val errorMessage = "Failed to cherry pick"
        val commitHash = "abcdef123"
        coEvery { anyConstructed<DefaultGitRepository>().cherryPick(commitHash) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.cherryPick(commitHash)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addRemote failure propagates error message`() = runTest {
        val errorMessage = "Failed to add remote"
        val name = "origin"
        val url = "https://github.com/user/repo.git"
        coEvery { anyConstructed<DefaultGitRepository>().addRemote(name, url) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.addRemote(name, url)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeRemote failure propagates error message`() = runTest {
        val errorMessage = "Failed to remove remote"
        val name = "origin"
        coEvery { anyConstructed<DefaultGitRepository>().removeRemote(name) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.removeRemote(name)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stashPop failure propagates error message`() = runTest {
        val errorMessage = "Failed to pop stash"
        val index = 0
        coEvery { anyConstructed<DefaultGitRepository>().stashPop(index) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.stashPop(index)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stashApply failure propagates error message`() = runTest {
        val errorMessage = "Failed to apply stash"
        val index = 0
        coEvery { anyConstructed<DefaultGitRepository>().stashApply(index) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.stashApply(index)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stashDrop failure propagates error message`() = runTest {
        val errorMessage = "Failed to drop stash"
        val index = 0
        coEvery { anyConstructed<DefaultGitRepository>().stashDrop(index) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.stashDrop(index)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createTag failure propagates error message`() = runTest {
        val errorMessage = "Failed to create tag"
        val name = "v1.0.0"
        val message = "Release 1.0.0"
        coEvery { anyConstructed<DefaultGitRepository>().createTag(name, message) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.createTag(name, message)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteBranch failure propagates error message`() = runTest {
        val errorMessage = "Failed to delete branch"
        val branch = "feature/old-feature"
        coEvery { anyConstructed<DefaultGitRepository>().deleteBranch(branch, false) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.deleteBranch(branch)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `merge failure propagates error message`() = runTest {
        val errorMessage = "Failed to merge"
        val branch = "feature/new-feature"
        coEvery { anyConstructed<DefaultGitRepository>().merge(branch) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.merge(branch)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetch failure propagates error message`() = runTest {
        val errorMessage = "Failed to fetch"
        coEvery { anyConstructed<DefaultGitRepository>().fetch() } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.fetch()
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stash failure propagates error message`() = runTest {
        val errorMessage = "Failed to stash"
        val message = "WIP"
        coEvery { anyConstructed<DefaultGitRepository>().stash(message) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.stash(message)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stageAll failure propagates error message`() = runTest {
        val errorMessage = "Failed to stage all"
        coEvery { anyConstructed<DefaultGitRepository>().stageAll() } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.stageAll()
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unstageFile failure propagates error message`() = runTest {
        val errorMessage = "Failed to unstage file"
        val path = "file.txt"
        coEvery { anyConstructed<DefaultGitRepository>().unstage(listOf(path)) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.unstageFile(path)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unstageAll failure propagates error message`() = runTest {
        val errorMessage = "Failed to unstage all"
        coEvery { anyConstructed<DefaultGitRepository>().unstageAll() } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.unstageAll()
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `discardChanges failure propagates error message`() = runTest {
        val errorMessage = "Failed to discard changes"
        val path = "file.txt"
        coEvery { anyConstructed<DefaultGitRepository>().discardChanges(listOf(path)) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.discardChanges(path)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initRepository failure propagates error message`() = runTest {
        val errorMessage = "Failed to initialize repository"
        val path = "/some/path"
        coEvery { anyConstructed<DefaultGitRepository>().initRepository(path) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.initRepository(path)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stageFile failure propagates error message`() = runTest {
        val errorMessage = "Failed to stage file"
        val path = "file.txt"
        coEvery { anyConstructed<DefaultGitRepository>().stage(listOf(path)) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.stageFile(path)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `commit failure propagates error message`() = runTest {
        val errorMessage = "Failed to commit"
        val message = "Initial commit"
        coEvery { anyConstructed<DefaultGitRepository>().commit(any()) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.commit(message)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `commit empty message without amend propagates error message`() = runTest {
        val errorMessage = "Commit message cannot be empty"

        viewModel.error.test {
            viewModel.commit("", amend = false)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `checkout failure propagates error message`() = runTest {
        val errorMessage = "Failed to checkout"
        val branch = "main"
        coEvery { anyConstructed<DefaultGitRepository>().checkout(branch) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.checkout(branch)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `createBranch failure propagates error message`() = runTest {
        val errorMessage = "Failed to create branch"
        val branch = "feature/new-feature"
        coEvery { anyConstructed<DefaultGitRepository>().createBranch(branch, checkout = true) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.createBranch(branch)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pull failure propagates error message`() = runTest {
        val errorMessage = "Failed to pull"
        coEvery { anyConstructed<DefaultGitRepository>().pull(any()) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.pull()
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `push failure propagates error message`() = runTest {
        val errorMessage = "Failed to push"
        coEvery { anyConstructed<DefaultGitRepository>().push(any()) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.push()
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cloneRepository failure propagates error message`() = runTest {
        val errorMessage = "Failed to clone repository"
        val url = "https://github.com/user/repo.git"
        val dir = "/some/dir"
        coEvery { anyConstructed<DefaultGitRepository>().cloneRepository(any()) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.cloneRepository(url, dir)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getDiff failure propagates error message`() = runTest {
        val errorMessage = "Failed to get diff"
        val path = "file.txt"
        coEvery { anyConstructed<DefaultGitRepository>().getDiff(path, false) } returns GitOperationResult.Error(errorMessage)

        viewModel.error.test {
            viewModel.getDiff(path, false)
            assertEquals(errorMessage, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
