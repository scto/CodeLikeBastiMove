package com.scto.codelikebastimove.feature.submodulemaker.generator

import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ModuleGeneratorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `generateModule successfully creates files when directory creation succeeds`() {
        val generator = ModuleGenerator()
        val projectRoot = tempFolder.newFolder("project")
        val config = ModuleConfig(gradlePath = ":feature:test")

        val result = generator.generateModule(projectRoot, config)

        assertTrue("Expected success when directory creation succeeds", result.isSuccess)
        val moduleDir = File(projectRoot, "feature/test")
        assertTrue(moduleDir.exists())
        assertTrue(moduleDir.isDirectory)
        assertTrue(File(moduleDir, "build.gradle.kts").exists())
        assertTrue(File(moduleDir, "src/main/AndroidManifest.xml").exists())
    }

    @Test
    fun `generateModule returns failure when directory creation fails`() {
        val generator = ModuleGenerator()
        val projectRoot = tempFolder.newFolder("project")
        val config = ModuleConfig(gradlePath = ":feature:test")

        // Create a file with the same name as the target directory to cause mkdirs() to fail
        val featureDir = File(projectRoot, "feature")
        featureDir.mkdirs()
        val testFile = File(featureDir, "test")
        testFile.createNewFile()

        val result = generator.generateModule(projectRoot, config)

        assertTrue("Expected failure when directory creation fails", result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue("Exception should be IllegalStateException but was ${exception?.let { it::class.java.name }}", exception is IllegalStateException)
        assertEquals("Failed to create module directory: feature/test", exception?.message)
    }
}
