package com.scto.codelikebastimove.feature.submodulemaker.generator

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleType
import com.scto.codelikebastimove.feature.submodulemaker.model.ProgrammingLanguage
import java.io.File

class ModuleGenerator(private val context: Context? = null) {

    fun generateModuleWithDocumentFile(
        projectDir: DocumentFile,
        config: ModuleConfig,
        basePackage: String = "com.scto.codelikebastimove",
    ): Result<List<String>> = runCatching {
        val createdFiles = mutableListOf<String>()

        val moduleDir = createDirectoryPath(projectDir, config.directoryPath)
            ?: throw IllegalStateException("Failed to create module directory: ${config.directoryPath}")

        createdFiles.add("${config.directoryPath}/build.gradle.kts")
        createBuildGradleKts(moduleDir, config, basePackage)

        createdFiles.add("${config.directoryPath}/src/main/AndroidManifest.xml")
        createAndroidManifest(moduleDir, config, basePackage)

        createdFiles.addAll(createSourceDirectories(moduleDir, config, basePackage))

        createdFiles.add("${config.directoryPath}/consumer-rules.pro")
        createConsumerRules(moduleDir)

        createdFiles.add("${config.directoryPath}/proguard-rules.pro")
        createProguardRules(moduleDir)

        updateSettingsGradle(projectDir, config.toGradleNotation())

        createdFiles
    }

    fun generateModule(projectRoot: File, config: ModuleConfig): Result<Unit> = runCatching {
        val moduleDir = File(projectRoot, config.directoryPath)
        moduleDir.mkdirs()

        generateBuildGradleKtsFile(moduleDir, config)
        generateAndroidManifestFile(moduleDir, config)
        generateSourceDirectoriesFile(moduleDir, config)
        generateConsumerRulesFile(moduleDir)

        updateSettingsGradleFile(projectRoot, config.toGradleNotation())
    }

    private fun createDirectoryPath(parent: DocumentFile, path: String): DocumentFile? {
        var current = parent
        path.split("/").filter { it.isNotBlank() }.forEach { segment ->
            current = current.findFile(segment) ?: current.createDirectory(segment) ?: return null
        }
        return current
    }

    private fun createBuildGradleKts(moduleDir: DocumentFile, config: ModuleConfig, basePackage: String) {
        val content = generateBuildGradleContent(config, basePackage)
        writeTextFile(moduleDir, "build.gradle.kts", content)
    }

    private fun generateBuildGradleContent(config: ModuleConfig, basePackage: String): String {
        return buildString {
            appendLine("plugins {")
            when (config.moduleType) {
                ModuleType.APPLICATION -> {
                    if (config.language == ProgrammingLanguage.KOTLIN) {
                        appendLine("    id(\"clbm.android.application\")")
                        if (config.useCompose) {
                            appendLine("    id(\"clbm.android.application.compose\")")
                        }
                    } else {
                        appendLine("    id(\"${config.moduleType.plugin}\")")
                    }
                }
                ModuleType.LIBRARY -> {
                    if (config.language == ProgrammingLanguage.KOTLIN) {
                        appendLine("    id(\"clbm.android.library\")")
                        if (config.useCompose) {
                            appendLine("    id(\"clbm.android.library.compose\")")
                        }
                    } else {
                        appendLine("    id(\"${config.moduleType.plugin}\")")
                    }
                }
            }
            appendLine("}")
            appendLine()
            appendLine("android {")
            appendLine("    namespace = \"${config.generatePackageName(basePackage)}\"")
            appendLine()
            appendLine("    defaultConfig {")
            appendLine("        minSdk = ${config.minSdk}")
            if (config.moduleType == ModuleType.APPLICATION) {
                appendLine("        targetSdk = ${config.targetSdk}")
                appendLine("        versionCode = 1")
                appendLine("        versionName = \"1.0\"")
            }
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("dependencies {")
            appendLine("    // Add your dependencies here")
            if (config.useCompose && config.language == ProgrammingLanguage.KOTLIN) {
                appendLine("    // implementation(project(\":core:ui\"))")
            }
            appendLine("}")
        }
    }

    private fun createAndroidManifest(moduleDir: DocumentFile, config: ModuleConfig, basePackage: String) {
        val srcMainDir = createDirectoryPath(moduleDir, "src/main")
            ?: throw IllegalStateException("Failed to create src/main directory")

        val content = buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            appendLine("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">")
            appendLine()
            if (config.moduleType == ModuleType.APPLICATION) {
                appendLine("    <application")
                appendLine("        android:allowBackup=\"true\"")
                appendLine("        android:label=\"${config.moduleName}\"")
                appendLine("        android:supportsRtl=\"true\"")
                appendLine("        android:theme=\"@style/Theme.Material3.DayNight.NoActionBar\">")
                appendLine("    </application>")
            }
            appendLine()
            appendLine("</manifest>")
        }
        writeTextFile(srcMainDir, "AndroidManifest.xml", content)
    }

    private fun createSourceDirectories(
        moduleDir: DocumentFile,
        config: ModuleConfig,
        basePackage: String,
    ): List<String> {
        val createdFiles = mutableListOf<String>()
        val packagePath = config.generatePackageName(basePackage).replace(".", "/")
        val srcLang = if (config.language == ProgrammingLanguage.KOTLIN) "kotlin" else "java"

        val srcDir = createDirectoryPath(moduleDir, "src/main/$srcLang/$packagePath")
            ?: throw IllegalStateException("Failed to create source directory")

        val resValuesDir = createDirectoryPath(moduleDir, "src/main/res/values")
            ?: throw IllegalStateException("Failed to create res/values directory")

        val stringsContent = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="module_name">${config.moduleName}</string>
</resources>"""
        writeTextFile(resValuesDir, "strings.xml", stringsContent)
        createdFiles.add("${config.directoryPath}/src/main/res/values/strings.xml")

        if (config.language == ProgrammingLanguage.KOTLIN) {
            createdFiles.addAll(generateKotlinFiles(srcDir, config, basePackage))
        } else {
            createdFiles.addAll(generateJavaFiles(srcDir, config, basePackage))
        }

        return createdFiles
    }

    private fun generateKotlinFiles(
        srcDir: DocumentFile,
        config: ModuleConfig,
        basePackage: String,
    ): List<String> {
        val createdFiles = mutableListOf<String>()
        val className = config.moduleName.toPascalCase()
        val packageName = config.generatePackageName(basePackage)
        val packagePath = packageName.replace(".", "/")

        val moduleContent = """package $packageName

/**
 * ${config.moduleName} module
 * 
 * Generated by SubModule Maker
 * Gradle path: ${config.toGradleNotation()}
 */
object ${className}Module {
    const val MODULE_NAME = "${config.moduleName}"
    const val GRADLE_PATH = "${config.toGradleNotation()}"
}
"""
        writeTextFile(srcDir, "${className}Module.kt", moduleContent)
        createdFiles.add("${config.directoryPath}/src/main/kotlin/$packagePath/${className}Module.kt")

        if (config.useCompose) {
            val screenContent = """package $packageName

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ${className}Screen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${config.moduleName}",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
"""
            writeTextFile(srcDir, "${className}Screen.kt", screenContent)
            createdFiles.add("${config.directoryPath}/src/main/kotlin/$packagePath/${className}Screen.kt")
        }

        return createdFiles
    }

    private fun generateJavaFiles(
        srcDir: DocumentFile,
        config: ModuleConfig,
        basePackage: String,
    ): List<String> {
        val createdFiles = mutableListOf<String>()
        val className = config.moduleName.toPascalCase() + "Module"
        val packageName = config.generatePackageName(basePackage)
        val packagePath = packageName.replace(".", "/")

        val content = """package $packageName;

/**
 * ${config.moduleName} module
 * 
 * Generated by SubModule Maker
 * Gradle path: ${config.toGradleNotation()}
 */
public final class $className {
    public static final String MODULE_NAME = "${config.moduleName}";
    public static final String GRADLE_PATH = "${config.toGradleNotation()}";

    private $className() {
        // Utility class
    }
}
"""
        writeTextFile(srcDir, "$className.java", content)
        createdFiles.add("${config.directoryPath}/src/main/java/$packagePath/$className.java")

        return createdFiles
    }

    private fun createConsumerRules(moduleDir: DocumentFile) {
        writeTextFile(moduleDir, "consumer-rules.pro", "# Consumer rules for this module\n")
    }

    private fun createProguardRules(moduleDir: DocumentFile) {
        val content = """# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard-rules.pro file.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
"""
        writeTextFile(moduleDir, "proguard-rules.pro", content)
    }

    private fun updateSettingsGradle(projectDir: DocumentFile, gradlePath: String) {
        val settingsFile = projectDir.findFile("settings.gradle.kts")
        if (settingsFile != null && context != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(settingsFile.uri)
                val content = inputStream?.bufferedReader()?.readText() ?: return
                inputStream.close()

                val includeStatement = "include(\"$gradlePath\")"
                if (!content.contains(includeStatement)) {
                    val updatedContent = content.trimEnd() + "\n$includeStatement\n"

                    context.contentResolver.openOutputStream(settingsFile.uri, "wt")?.use { output ->
                        output.write(updatedContent.toByteArray())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun writeTextFile(parent: DocumentFile, fileName: String, content: String) {
        val mimeType = when {
            fileName.endsWith(".kt") -> "text/x-kotlin"
            fileName.endsWith(".java") -> "text/x-java"
            fileName.endsWith(".xml") -> "text/xml"
            fileName.endsWith(".pro") -> "text/plain"
            fileName.endsWith(".kts") -> "text/plain"
            else -> "text/plain"
        }

        val file = parent.findFile(fileName)?.also {
            context?.contentResolver?.openOutputStream(it.uri, "wt")?.use { output ->
                output.write(content.toByteArray())
            }
        } ?: parent.createFile(mimeType, fileName)?.also {
            context?.contentResolver?.openOutputStream(it.uri)?.use { output ->
                output.write(content.toByteArray())
            }
        }
    }

    private fun generateBuildGradleKtsFile(moduleDir: File, config: ModuleConfig) {
        val content = generateBuildGradleContent(config, "com.scto.codelikebastimove")
        File(moduleDir, "build.gradle.kts").writeText(content)
    }

    private fun generateAndroidManifestFile(moduleDir: File, config: ModuleConfig) {
        val srcMainDir = File(moduleDir, "src/main")
        srcMainDir.mkdirs()

        val manifestContent = buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            appendLine("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">")
            if (config.moduleType == ModuleType.APPLICATION) {
                appendLine("    <application")
                appendLine("        android:allowBackup=\"true\"")
                appendLine("        android:label=\"${config.moduleName}\"")
                appendLine("        android:supportsRtl=\"true\">")
                appendLine("    </application>")
            }
            appendLine("</manifest>")
        }
        File(srcMainDir, "AndroidManifest.xml").writeText(manifestContent)
    }

    private fun generateSourceDirectoriesFile(moduleDir: File, config: ModuleConfig) {
        val packagePath = config.generatePackageName().replace(".", "/")
        val srcLang = if (config.language == ProgrammingLanguage.KOTLIN) "kotlin" else "java"

        val srcDir = File(moduleDir, "src/main/$srcLang/$packagePath")
        srcDir.mkdirs()

        val resDir = File(moduleDir, "src/main/res/values")
        resDir.mkdirs()

        val stringsContent = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="module_name">${config.moduleName}</string>
</resources>"""
        File(resDir, "strings.xml").writeText(stringsContent)

        if (config.language == ProgrammingLanguage.KOTLIN) {
            generateKotlinPlaceholderFile(srcDir, config)
        } else {
            generateJavaPlaceholderFile(srcDir, config)
        }
    }

    private fun generateKotlinPlaceholderFile(srcDir: File, config: ModuleConfig) {
        val className = config.moduleName.toPascalCase()
        val content = """package ${config.generatePackageName()}

/**
 * ${config.moduleName} module
 * Generated by SubModule Maker
 * Gradle path: ${config.toGradleNotation()}
 */
object ${className}Module {
    const val MODULE_NAME = "${config.moduleName}"
    const val GRADLE_PATH = "${config.toGradleNotation()}"
}
"""
        File(srcDir, "${className}Module.kt").writeText(content)
    }

    private fun generateJavaPlaceholderFile(srcDir: File, config: ModuleConfig) {
        val className = config.moduleName.toPascalCase() + "Module"
        val content = """package ${config.generatePackageName()};

/**
 * ${config.moduleName} module
 * Generated by SubModule Maker
 * Gradle path: ${config.toGradleNotation()}
 */
public final class $className {
    public static final String MODULE_NAME = "${config.moduleName}";
    public static final String GRADLE_PATH = "${config.toGradleNotation()}";

    private $className() {
        // Utility class
    }
}
"""
        File(srcDir, "$className.java").writeText(content)
    }

    private fun generateConsumerRulesFile(moduleDir: File) {
        File(moduleDir, "consumer-rules.pro").writeText("# Consumer rules for this module\n")
    }

    private fun updateSettingsGradleFile(projectRoot: File, gradlePath: String) {
        val settingsFile = File(projectRoot, "settings.gradle.kts")
        if (settingsFile.exists()) {
            val content = settingsFile.readText()
            val includeStatement = "include(\"$gradlePath\")"
            if (!content.contains(includeStatement)) {
                val updatedContent = content.trimEnd() + "\n$includeStatement\n"
                settingsFile.writeText(updatedContent)
            }
        }
    }

    private fun String.toPascalCase(): String {
        return split("-", "_", " ").joinToString("") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}
