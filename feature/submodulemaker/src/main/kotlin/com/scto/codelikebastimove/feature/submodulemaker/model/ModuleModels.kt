package com.scto.codelikebastimove.feature.submodulemaker.model

enum class ProgrammingLanguage(val displayName: String, val extension: String) {
    KOTLIN("Kotlin", "kt"),
    JAVA("Java", "java")
}

enum class ModuleType(val displayName: String, val plugin: String) {
    APPLICATION("Application", "com.android.application"),
    LIBRARY("Library", "com.android.library")
}

data class ModuleConfig(
    val gradlePath: String = "",
    val packageName: String = "",
    val language: ProgrammingLanguage = ProgrammingLanguage.KOTLIN,
    val moduleType: ModuleType = ModuleType.LIBRARY,
    val useCompose: Boolean = true,
    val minSdk: Int = 29,
    val targetSdk: Int = 35
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
        val cleanPath = gradlePath.replace(":", ".").replace("-", ".").trim('.').lowercase()
        return "$basePackage.$cleanPath"
    }

    fun toGradleNotation(): String = gradlePath
}
