package com.scto.codelikebastimove.feature.submodulemaker.generator

import androidx.documentfile.provider.DocumentFile
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleType
import com.scto.codelikebastimove.feature.submodulemaker.model.ProgrammingLanguage
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModuleGeneratorTest {

    @Test
    fun `generateModuleWithDocumentFile returns failure when directory creation fails`() {
        val generator = ModuleGenerator()
        val mockDocumentFile = mockk<DocumentFile>()

        every { mockDocumentFile.findFile(any()) } returns null
        every { mockDocumentFile.createDirectory(any()) } returns null

        val config = ModuleConfig(
            gradlePath = ":feature:test",
            moduleType = ModuleType.LIBRARY,
            language = ProgrammingLanguage.KOTLIN,
            useCompose = true,
            minSdk = 24,
            targetSdk = 34
        )

        val result = generator.generateModuleWithDocumentFile(mockDocumentFile, config)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Failed to create module directory: feature/test", exception?.message)
    }
}
