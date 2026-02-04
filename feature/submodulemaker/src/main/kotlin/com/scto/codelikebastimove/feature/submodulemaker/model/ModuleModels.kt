package com.scto.codelikebastimove.feature.submodulemaker.model

enum class ProgrammingLanguage(val displayName: String, val extension: String) {
    KOTLIN("Kotlin", "kt"),
    JAVA("Java", "java"),
}

enum class ModuleType(val displayName: String, val plugin: String) {
    APPLICATION("Application", "com.android.application"),
    LIBRARY("Library", "com.android.library"),
}

enum class ModuleTemplate(
    val displayName: String,
    val description: String,
) {
    EMPTY("Empty Module", "Basic module with minimal setup"),
    FEATURE("Feature Module", "UI feature with ViewModel and Screen"),
    DATA("Data Module", "Repository pattern with local/remote sources"),
    DOMAIN("Domain Module", "Use cases and domain models"),
    CORE("Core Module", "Shared utilities and extensions"),
}

data class ModuleConfig(
    val gradlePath: String = "",
    val packageName: String = "",
    val language: ProgrammingLanguage = ProgrammingLanguage.KOTLIN,
    val moduleType: ModuleType = ModuleType.LIBRARY,
    val useCompose: Boolean = true,
    val minSdk: Int = 24,
    val targetSdk: Int = 35,
) {
    val isValid: Boolean
        get() = gradlePath.isNotBlank() && gradlePath.startsWith(":")

    val moduleName: String
        get() = gradlePath.split(":").lastOrNull { it.isNotBlank() } ?: ""

    val folderName: String
        get() {
            val parts = gradlePath.split(":").filter { it.isNotBlank() }
            return if (parts.size > 1) parts.dropLast(1).joinToString("/") else ""
        }

    val directoryPath: String
        get() = gradlePath.replace(":", "/").trimStart('/')

    fun generatePackageName(basePackage: String = "com.scto.codelikebastimove"): String {
        if (packageName.isNotBlank()) return packageName
        val cleanPath = gradlePath.replace(":", ".").replace("-", "").trim('.').lowercase()
        return "$basePackage.$cleanPath"
    }

    fun toGradleNotation(): String = gradlePath
}

data class GradleNotation(
    val segments: List<String>,
) {
    val fullPath: String
        get() = ":" + segments.joinToString(":")

    val moduleName: String
        get() = segments.lastOrNull() ?: ""

    val parentPath: String
        get() = if (segments.size > 1) {
            ":" + segments.dropLast(1).joinToString(":")
        } else ""

    val directoryPath: String
        get() = segments.joinToString("/")

    val depth: Int
        get() = segments.size

    fun toPackageName(basePackage: String): String {
        val cleanSegments = segments.map { segment ->
            segment.replace("-", "").replace("_", "").lowercase()
        }
        return "$basePackage.${cleanSegments.joinToString(".")}"
    }

    companion object {
        private val VALID_SEGMENT_REGEX = Regex("^[a-z][a-z0-9_-]*$")

        fun parse(notation: String): GradleNotation? {
            val trimmed = notation.trim()
            if (trimmed.isBlank()) return null
            if (!trimmed.startsWith(":")) return null
            if (trimmed.contains("::")) return null
            if (trimmed.endsWith(":")) return null

            val segments = trimmed.removePrefix(":").split(":")
            if (segments.isEmpty() || segments.any { it.isBlank() }) return null

            val invalidSegment = segments.find { !isValidSegment(it) }
            if (invalidSegment != null) return null

            return GradleNotation(segments)
        }

        fun isValid(notation: String): Boolean = parse(notation) != null

        fun isValidSegment(segment: String): Boolean {
            if (segment.isBlank()) return false
            if (segment.length > 64) return false
            return VALID_SEGMENT_REGEX.matches(segment)
        }

        fun suggest(prefix: String): List<String> {
            val common = listOf(
                ":app",
                ":core:common",
                ":core:ui",
                ":core:network",
                ":core:database",
                ":core:utils",
                ":core:testing",
                ":data:repository",
                ":data:local",
                ":data:remote",
                ":domain:model",
                ":domain:usecase",
                ":feature:home",
                ":feature:auth",
                ":feature:settings",
                ":feature:profile",
            )
            return common.filter { it.startsWith(prefix) && it != prefix }
        }
    }
}
