package com.scto.codelikebastimove.feature.git.repository

import com.scto.codelikebastimove.feature.git.library.JGitLibrary
import com.scto.codelikebastimove.feature.git.library.JGitResult
import com.scto.codelikebastimove.feature.git.model.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.eclipse.jgit.lib.Repository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitRepositoryTest {

    private lateinit var defaultGitRepository: DefaultGitRepository

    @Before
    fun setup() {
        mockkConstructor(JGitLibrary::class)
        defaultGitRepository = DefaultGitRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `openRepository success updates currentRepository and status`() = runTest {
        // Arrange
        val testPath = "/test/repo"
        val expectedRepo = GitRepository(
            path = testPath,
            name = "repo",
            currentBranch = "main",
            isDetachedHead = false,
            remoteName = "origin",
            remoteUrl = "https://github.com/test/repo.git"
        )
        val expectedStatus = GitStatus(
            branch = "main",
            trackingBranch = "origin/main",
            ahead = 0,
            behind = 0,
            stagedChanges = emptyList(),
            unstagedChanges = emptyList(),
            untrackedFiles = emptyList(),
            hasConflicts = false,
            conflictedFiles = emptyList()
        )

        val mockRepository = mockk<Repository>()

        coEvery { anyConstructed<JGitLibrary>().openRepository(any()) } returns JGitResult.Success(mockRepository)
        coEvery { anyConstructed<JGitLibrary>().getCurrentBranch() } returns "main"
        coEvery { anyConstructed<JGitLibrary>().getRepositoryRoot() } returns testPath
        coEvery { anyConstructed<JGitLibrary>().getRemotes() } returns JGitResult.Success(listOf(GitRemote("origin", "https://github.com/test/repo.git", "https://github.com/test/repo.git")))
        coEvery { anyConstructed<JGitLibrary>().getStatus() } returns JGitResult.Success(expectedStatus)

        // Act
        val result = defaultGitRepository.openRepository(testPath)

        // Assert
        assertTrue(result is GitOperationResult.Success)
        assertEquals(expectedRepo, (result as GitOperationResult.Success).data)
        assertEquals(expectedRepo, defaultGitRepository.currentRepository.first())
        assertEquals(expectedStatus, defaultGitRepository.status.first())
    }

    @Test
    fun `openRepository error returns GitOperationResult Error`() = runTest {
        // Arrange
        val testPath = "/test/repo"
        val errorMessage = "Failed to open"
        coEvery { anyConstructed<JGitLibrary>().openRepository(any()) } returns JGitResult.Error(errorMessage)

        // Act
        val result = defaultGitRepository.openRepository(testPath)

        // Assert
        assertTrue(result is GitOperationResult.Error)
        assertEquals(errorMessage, (result as GitOperationResult.Error).message)
    }

    @Test
    fun `cloneRepository success opens repository`() = runTest {
        // Arrange
        val options = GitCloneOptions(url = "https://github.com/test/repo.git", directory = "/test/repo")
        val expectedRepo = GitRepository(
            path = "/test/repo",
            name = "repo",
            currentBranch = "main",
            isDetachedHead = false,
            remoteName = "origin",
            remoteUrl = "https://github.com/test/repo.git"
        )
        val expectedStatus = GitStatus(
            branch = "main",
            trackingBranch = "origin/main",
            ahead = 0,
            behind = 0,
            stagedChanges = emptyList(),
            unstagedChanges = emptyList(),
            untrackedFiles = emptyList(),
            hasConflicts = false,
            conflictedFiles = emptyList()
        )

        val mockRepository = mockk<Repository>()

        coEvery { anyConstructed<JGitLibrary>().cloneRepository(any(), any(), any(), any(), any(), any()) } returns JGitResult.Success(mockRepository)
        coEvery { anyConstructed<JGitLibrary>().openRepository(any()) } returns JGitResult.Success(mockRepository)
        coEvery { anyConstructed<JGitLibrary>().getCurrentBranch() } returns "main"
        coEvery { anyConstructed<JGitLibrary>().getRepositoryRoot() } returns "/test/repo"
        coEvery { anyConstructed<JGitLibrary>().getRemotes() } returns JGitResult.Success(listOf(GitRemote("origin", "https://github.com/test/repo.git", "https://github.com/test/repo.git")))
        coEvery { anyConstructed<JGitLibrary>().getStatus() } returns JGitResult.Success(expectedStatus)

        // Act
        val result = defaultGitRepository.cloneRepository(options)

        // Assert
        assertTrue(result is GitOperationResult.Success)
        assertEquals(expectedRepo, (result as GitOperationResult.Success).data)
    }

    @Test
    fun `cloneRepository error returns GitOperationResult Error`() = runTest {
        // Arrange
        val options = GitCloneOptions(url = "https://github.com/test/repo.git", directory = "/test/repo")
        val errorMessage = "Failed to clone"

        coEvery { anyConstructed<JGitLibrary>().cloneRepository(any(), any(), any(), any(), any(), any()) } returns JGitResult.Error(errorMessage)

        // Act
        val result = defaultGitRepository.cloneRepository(options)

        // Assert
        assertTrue(result is GitOperationResult.Error)
        assertEquals(errorMessage, (result as GitOperationResult.Error).message)
    }

    @Test
    fun `stage updates status`() = runTest {
        // Arrange
        val paths = listOf("file.txt")
        val expectedStatus = GitStatus(
            branch = "main",
            trackingBranch = "origin/main",
            ahead = 0,
            behind = 0,
            stagedChanges = listOf(GitFileChange("file.txt", "file.txt", GitFileStatus.ADDED, true)),
            unstagedChanges = emptyList(),
            untrackedFiles = emptyList(),
            hasConflicts = false,
            conflictedFiles = emptyList()
        )
        coEvery { anyConstructed<JGitLibrary>().stage(any()) } returns JGitResult.Success(Unit)
        coEvery { anyConstructed<JGitLibrary>().getStatus() } returns JGitResult.Success(expectedStatus)

        // Act
        val result = defaultGitRepository.stage(paths)

        // Assert
        assertTrue(result is GitOperationResult.Success)
        assertEquals(expectedStatus, defaultGitRepository.status.first())
    }

    @Test
    fun `commit calls jgitLibrary commit and refresh`() = runTest {
        // Arrange
        val options = GitCommitOptions(message = "Init commit")
        val expectedCommit = GitCommit("hash123", "hash123", "Init commit", "Author", "author@test.com", 123456789L, emptyList())
        val expectedStatus = GitStatus(
            branch = "main",
            trackingBranch = "origin/main",
            ahead = 0,
            behind = 0,
            stagedChanges = emptyList(),
            unstagedChanges = emptyList(),
            untrackedFiles = emptyList(),
            hasConflicts = false,
            conflictedFiles = emptyList()
        )

        coEvery { anyConstructed<JGitLibrary>().commit(any(), any()) } returns JGitResult.Success(expectedCommit)
        coEvery { anyConstructed<JGitLibrary>().getStatus() } returns JGitResult.Success(expectedStatus)

        // Act
        val result = defaultGitRepository.commit(options)

        // Assert
        assertTrue(result is GitOperationResult.Success)
        assertEquals(expectedCommit, (result as GitOperationResult.Success).data)
        assertEquals(expectedStatus, defaultGitRepository.status.first())
    }
}
