package com.scto.codelikebastimove.feature.git.library

import com.scto.codelikebastimove.feature.git.model.GitFileStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JGitLibraryTest {

    private lateinit var jGitLibrary: JGitLibrary
    private lateinit var tempDir: File

    @Before
    fun setup() {
        jGitLibrary = JGitLibrary()
        tempDir = kotlin.io.path.createTempDirectory("jgit_test_repo").toFile()
    }

    @After
    fun teardown() {
        jGitLibrary.close()
        tempDir.deleteRecursively()
    }

    @Test
    fun testInitRepository() = runBlocking {
        val path = tempDir.absolutePath
        val result = jGitLibrary.initRepository(path)

        assertTrue(result is JGitResult.Success, "Expected initRepository to succeed")
        assertTrue(File(tempDir, ".git").exists(), "Expected .git directory to be created")
        assertTrue(jGitLibrary.isRepositoryOpen(), "Expected repository to be open")
    }

    @Test
    fun testOpenRepository() = runBlocking {
        val path = tempDir.absolutePath
        jGitLibrary.initRepository(path)
        jGitLibrary.close()
        assertFalse(jGitLibrary.isRepositoryOpen(), "Repository should be closed")

        val result = jGitLibrary.openRepository(path)
        assertTrue(result is JGitResult.Success, "Expected openRepository to succeed")
        assertTrue(jGitLibrary.isRepositoryOpen(), "Expected repository to be open")
    }

    @Test
    fun testSetAndClearCredentials() {
        jGitLibrary.setCredentials("user", "pass")
        jGitLibrary.clearCredentials()
        // Simple call verification since credentialsProvider is private.
        // As long as no exception is thrown, we pass.
        assertTrue(true)
    }

    @Test
    fun testStageAndCommit() = runBlocking {
        val path = tempDir.absolutePath
        jGitLibrary.initRepository(path)

        // Create a test file
        val testFile = File(tempDir, "test.txt")
        testFile.writeText("Hello JGit")

        // Stage the file
        val stageResult = jGitLibrary.stage(listOf("test.txt"))
        assertTrue(stageResult is JGitResult.Success, "Expected stage to succeed")

        // Commit the file
        val commitResult = jGitLibrary.commit("Initial commit", author = "Test Author", email = "test@example.com")
        assertTrue(commitResult is JGitResult.Success, "Expected commit to succeed")
        assertEquals("Initial commit", (commitResult as JGitResult.Success).data.message)
    }

    @Test
    fun testGetStatus() = runBlocking {
        val path = tempDir.absolutePath
        jGitLibrary.initRepository(path)

        val testFile = File(tempDir, "untracked.txt")
        testFile.writeText("Hello untracked")

        val statusResult = jGitLibrary.getStatus()
        assertTrue(statusResult is JGitResult.Success, "Expected getStatus to succeed")

        val status = statusResult.data
        assertTrue(status.untrackedFiles.contains("untracked.txt"), "Expected file to be untracked")
        assertEquals(0, status.stagedChanges.size)
        assertEquals(0, status.unstagedChanges.size)

        // Now stage it
        jGitLibrary.stage(listOf("untracked.txt"))
        val stagedStatusResult = jGitLibrary.getStatus()
        assertTrue(stagedStatusResult is JGitResult.Success)
        val stagedStatus = stagedStatusResult.data
        assertEquals(1, stagedStatus.stagedChanges.size)
        assertEquals("untracked.txt", stagedStatus.stagedChanges.first().path)
        assertEquals(GitFileStatus.ADDED, stagedStatus.stagedChanges.first().status)
    }

    @Test
    fun testCreateBranchAndGetBranches() = runBlocking {
        val path = tempDir.absolutePath
        jGitLibrary.initRepository(path)

        // Need an initial commit to create branches safely
        val testFile = File(tempDir, "test.txt")
        testFile.writeText("Hello")
        jGitLibrary.stage(listOf("test.txt"))
        jGitLibrary.commit("Initial", author = "Test Author", email = "test@example.com")

        val branchResult = jGitLibrary.createBranch("feature-branch", checkout = false)
        assertTrue(branchResult is JGitResult.Success, "Expected createBranch to succeed")
        assertEquals("feature-branch", branchResult.data.name)

        val getBranchesResult = jGitLibrary.getBranches()
        assertTrue(getBranchesResult is JGitResult.Success, "Expected getBranches to succeed")
        val branches = getBranchesResult.data
        assertTrue(branches.any { it.name == "master" || it.name == "main" }, "Expected master/main branch to exist")
        assertTrue(branches.any { it.name == "feature-branch" }, "Expected feature-branch to exist")
    }

    @Test
    fun testCloneRepositoryWithExistingNonEmptyDir() = runBlocking {
        val file = File(tempDir, "existing.txt")
        file.writeText("some content")

        val result = jGitLibrary.cloneRepository("https://github.com/example/repo.git", tempDir.absolutePath)
        assertTrue(result is JGitResult.Error, "Expected clone to fail because directory is not empty")
        assertTrue(result.message.contains("Directory already exists and is not empty"))
    }
}
