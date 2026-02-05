package com.scto.codelikebastimove.core.utils

import com.scto.codelikebastimove.core.logger.CLBMLogger
import java.io.File

object ProjectUtils {

    private const val TAG = "ProjectUtils"

    fun isProjectDirectory(file: File): Boolean {
        if (!file.isDirectory) return false
        return File(file, "build.gradle.kts").exists() ||
            File(file, "build.gradle").exists() ||
            File(file, "settings.gradle.kts").exists() ||
            File(file, "settings.gradle").exists()
    }

    fun isAndroidProject(file: File): Boolean {
        if (!isProjectDirectory(file)) return false
        val appDir = File(file, "app")
        if (!appDir.exists()) return false
        return File(appDir, "build.gradle.kts").exists() ||
            File(appDir, "build.gradle").exists()
    }

    fun detectPackageName(projectDir: File): String {
        val manifestFile = File(projectDir, "app/src/main/AndroidManifest.xml")
        if (manifestFile.exists()) {
            try {
                val content = manifestFile.readText()
                val packageRegex = """package\s*=\s*["']([^"']+)["']""".toRegex()
                val match = packageRegex.find(content)
                if (match != null) {
                    return match.groupValues[1]
                }
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Failed to detect package name", e)
            }
        }
        return "com.example.${projectDir.name.lowercase().replace("-", "").replace(" ", "")}"
    }

    fun extractRepoName(url: String): String {
        return url
            .removeSuffix(".git")
            .removeSuffix("/")
            .substringAfterLast("/")
            .ifBlank { "cloned_repo" }
    }

    fun getProjectType(projectDir: File): ProjectType {
        if (!isProjectDirectory(projectDir)) return ProjectType.UNKNOWN

        val hasApp = File(projectDir, "app").exists()
        val hasWear = File(projectDir, "wear").exists()
        val hasTv = File(projectDir, "tv").exists()

        val buildFile = File(projectDir, "app/build.gradle.kts").takeIf { it.exists() }
            ?: File(projectDir, "app/build.gradle").takeIf { it.exists() }

        buildFile?.let {
            val content = it.readText()
            if (content.contains("compose") || content.contains("Compose")) {
                return if (hasWear) ProjectType.WEAR_COMPOSE else ProjectType.COMPOSE
            }
        }

        return when {
            hasWear -> ProjectType.WEAR
            hasTv -> ProjectType.TV
            hasApp -> ProjectType.ANDROID
            else -> ProjectType.LIBRARY
        }
    }

    fun getModulePaths(projectDir: File): List<String> {
        val modules = mutableListOf<String>()
        val settingsFile = File(projectDir, "settings.gradle.kts").takeIf { it.exists() }
            ?: File(projectDir, "settings.gradle").takeIf { it.exists() }

        settingsFile?.let {
            val content = it.readText()
            val includeRegex = """include\s*\(\s*["']([^"']+)["']\s*\)""".toRegex()
            includeRegex.findAll(content).forEach { match ->
                modules.add(match.groupValues[1])
            }
        }

        return modules
    }

    fun validatePackageName(packageName: String): Boolean {
        if (packageName.isBlank()) return false
        val parts = packageName.split(".")
        if (parts.size < 2) return false
        val validPartRegex = """^[a-z][a-z0-9_]*$""".toRegex()
        return parts.all { it.matches(validPartRegex) }
    }

    fun validateProjectName(name: String): Boolean {
        if (name.isBlank()) return false
        val validNameRegex = """^[a-zA-Z][a-zA-Z0-9_-]*$""".toRegex()
        return name.matches(validNameRegex)
    }
}

enum class ProjectType {
    ANDROID,
    COMPOSE,
    WEAR,
    WEAR_COMPOSE,
    TV,
    LIBRARY,
    UNKNOWN
}
