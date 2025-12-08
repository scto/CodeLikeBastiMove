package com.scto.codelikebastimove.feature.designer.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.scto.codelikebastimove.feature.designer.data.model.BlockTree
import com.scto.codelikebastimove.feature.designer.data.model.ComponentDefinition
import com.scto.codelikebastimove.feature.designer.data.model.ExportConfig
import com.scto.codelikebastimove.feature.designer.data.model.ExportResult
import com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor
import com.scto.codelikebastimove.feature.designer.domain.codegen.CodeEmitter
import java.io.OutputStream

class ExportManager(
    private val codeEmitter: CodeEmitter = CodeEmitter()
) {
    
    fun exportToUri(
        context: Context,
        uri: Uri,
        content: String
    ): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun exportToDirectory(
        context: Context,
        directoryUri: Uri,
        fileName: String,
        content: String
    ): ExportResult {
        return try {
            val directory = DocumentFile.fromTreeUri(context, directoryUri)
            if (directory == null || !directory.canWrite()) {
                return ExportResult(
                    success = false,
                    generatedCode = content,
                    filePath = null,
                    errors = listOf("Cannot write to selected directory")
                )
            }
            
            val existingFile = directory.findFile(fileName)
            val file = existingFile ?: directory.createFile("text/x-kotlin", fileName)
            
            if (file == null) {
                return ExportResult(
                    success = false,
                    generatedCode = content,
                    filePath = null,
                    errors = listOf("Failed to create file: $fileName")
                )
            }
            
            context.contentResolver.openOutputStream(file.uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
                outputStream.flush()
            }
            
            ExportResult(
                success = true,
                generatedCode = content,
                filePath = file.uri.toString()
            )
        } catch (e: Exception) {
            ExportResult(
                success = false,
                generatedCode = content,
                filePath = null,
                errors = listOf("Export failed: ${e.message}")
            )
        }
    }
    
    fun prepareExport(
        blockTree: BlockTree,
        config: ExportConfig,
        themeDescriptor: ThemeDescriptor? = null
    ): ExportPreview {
        val generatedCode = codeEmitter.generateCode(blockTree, config, themeDescriptor)
        val validation = codeEmitter.validateCode(generatedCode.code)
        
        val formattedCode = if (config.formatCode) {
            formatCode(generatedCode.code)
        } else {
            generatedCode.code
        }
        
        return ExportPreview(
            code = formattedCode,
            validation = validation,
            imports = generatedCode.imports,
            functionName = generatedCode.functionName,
            targetPath = buildTargetPath(config)
        )
    }
    
    fun export(
        blockTree: BlockTree,
        config: ExportConfig,
        themeDescriptor: ThemeDescriptor? = null,
        customComponents: List<ComponentDefinition> = emptyList()
    ): ExportResult {
        val preview = prepareExport(blockTree, config, themeDescriptor)
        
        if (!preview.validation.isValid) {
            return ExportResult(
                success = false,
                generatedCode = preview.code,
                filePath = null,
                errors = preview.validation.errors.map { it.message }
            )
        }
        
        val results = mutableListOf<String>()
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        try {
            val mainFilePath = "${config.exportPath}/${preview.functionName}.kt"
            val mainFileContent = buildFileContent(
                packageName = extractPackageName(config.exportPath),
                imports = preview.imports,
                code = preview.code
            )
            
            results.add("Main composable: $mainFilePath")
            
            if (themeDescriptor != null) {
                val themeResult = exportTheme(themeDescriptor, config)
                if (themeResult.success) {
                    results.add("Theme exported: ${themeResult.filePath}")
                } else {
                    warnings.addAll(themeResult.warnings)
                }
            }
            
            if (customComponents.isNotEmpty()) {
                customComponents.forEach { component ->
                    val componentResult = exportComponent(component, config)
                    if (componentResult.success) {
                        results.add("Component exported: ${componentResult.filePath}")
                    } else {
                        warnings.addAll(componentResult.warnings)
                    }
                }
            }
            
            return ExportResult(
                success = true,
                generatedCode = mainFileContent,
                filePath = mainFilePath,
                warnings = warnings
            )
        } catch (e: Exception) {
            return ExportResult(
                success = false,
                generatedCode = preview.code,
                filePath = null,
                errors = listOf("Export failed: ${e.message}")
            )
        }
    }
    
    fun exportTheme(
        themeDescriptor: ThemeDescriptor,
        config: ExportConfig
    ): ExportResult {
        val themeName = if (config.exportThemeToRepo) {
            config.themeRepoName.ifEmpty { themeDescriptor.name }
        } else {
            config.themeName
        }
        
        val themeCode = generateThemeCode(themeDescriptor, themeName)
        
        val targetPath = if (config.exportThemeToRepo) {
            "extensions/themes/${themeName}"
        } else {
            "${config.exportPath}/${config.themePath}"
        }
        
        return ExportResult(
            success = true,
            generatedCode = themeCode,
            filePath = "$targetPath/Theme.kt"
        )
    }
    
    fun exportComponent(
        component: ComponentDefinition,
        config: ExportConfig
    ): ExportResult {
        val prefixedCode = component.template.replace("{{PREFIX}}", config.componentPrefix)
        val componentName = "${config.componentPrefix}${component.name}"
        
        val fullCode = buildFileContent(
            packageName = extractPackageName("${config.exportPath}/${config.componentsPath}"),
            imports = listOf(
                "androidx.compose.runtime.Composable",
                "androidx.compose.ui.Modifier",
                "androidx.compose.foundation.layout.*",
                "androidx.compose.material3.*",
                "androidx.compose.ui.unit.dp"
            ),
            code = prefixedCode
        )
        
        return ExportResult(
            success = true,
            generatedCode = fullCode,
            filePath = "${config.exportPath}/${config.componentsPath}/$componentName.kt"
        )
    }
    
    private fun generateThemeCode(theme: ThemeDescriptor, themeName: String): String {
        return buildString {
            appendLine("package ui.theme")
            appendLine()
            appendLine("import androidx.compose.foundation.isSystemInDarkTheme")
            appendLine("import androidx.compose.material3.MaterialTheme")
            appendLine("import androidx.compose.material3.darkColorScheme")
            appendLine("import androidx.compose.material3.lightColorScheme")
            if (theme.isDynamic) {
                appendLine("import androidx.compose.material3.dynamicDarkColorScheme")
                appendLine("import androidx.compose.material3.dynamicLightColorScheme")
                appendLine("import android.os.Build")
                appendLine("import androidx.compose.ui.platform.LocalContext")
            }
            appendLine("import androidx.compose.runtime.Composable")
            appendLine()
            appendLine("private val LightColorScheme = lightColorScheme(")
            theme.customColors.forEach { (key, value) ->
                appendLine("    $key = $value,")
            }
            appendLine(")")
            appendLine()
            appendLine("private val DarkColorScheme = darkColorScheme(")
            theme.customColors.forEach { (key, value) ->
                appendLine("    $key = $value,")
            }
            appendLine(")")
            appendLine()
            appendLine("@Composable")
            appendLine("fun ${themeName}Theme(")
            appendLine("    darkTheme: Boolean = isSystemInDarkTheme(),")
            if (theme.isDynamic) {
                appendLine("    dynamicColor: Boolean = true,")
            }
            appendLine("    content: @Composable () -> Unit")
            appendLine(") {")
            if (theme.isDynamic) {
                appendLine("    val colorScheme = when {")
                appendLine("        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {")
                appendLine("            val context = LocalContext.current")
                appendLine("            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)")
                appendLine("        }")
                appendLine("        darkTheme -> DarkColorScheme")
                appendLine("        else -> LightColorScheme")
                appendLine("    }")
            } else {
                appendLine("    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme")
            }
            appendLine()
            appendLine("    MaterialTheme(")
            appendLine("        colorScheme = colorScheme,")
            appendLine("        content = content")
            appendLine("    )")
            appendLine("}")
        }
    }
    
    private fun buildFileContent(
        packageName: String,
        imports: List<String>,
        code: String
    ): String {
        return buildString {
            if (packageName.isNotEmpty()) {
                appendLine("package $packageName")
                appendLine()
            }
            imports.sorted().distinct().forEach { import ->
                appendLine("import $import")
            }
            appendLine()
            append(code)
        }
    }
    
    private fun extractPackageName(path: String): String {
        val srcMainJava = "src/main/java/"
        val srcMainKotlin = "src/main/kotlin/"
        
        val normalizedPath = path.replace("\\", "/")
        
        val packagePath = when {
            normalizedPath.contains(srcMainJava) -> {
                normalizedPath.substringAfter(srcMainJava)
            }
            normalizedPath.contains(srcMainKotlin) -> {
                normalizedPath.substringAfter(srcMainKotlin)
            }
            else -> normalizedPath
        }
        
        return packagePath
            .replace("/", ".")
            .trimEnd('.')
    }
    
    private fun buildTargetPath(config: ExportConfig): String {
        return config.exportPath.ifEmpty { "generated" }
    }
    
    private fun formatCode(code: String): String {
        return code
    }
}

data class ExportPreview(
    val code: String,
    val validation: ValidationResult,
    val imports: List<String>,
    val functionName: String,
    val targetPath: String
)
