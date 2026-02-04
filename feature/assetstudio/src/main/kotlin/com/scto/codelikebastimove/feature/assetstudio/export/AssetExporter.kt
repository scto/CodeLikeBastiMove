package com.scto.codelikebastimove.feature.assetstudio.export

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.documentfile.provider.DocumentFile
import com.scto.codelikebastimove.feature.assetstudio.model.AVDDocument
import com.scto.codelikebastimove.feature.assetstudio.model.AdaptiveIconConfig
import com.scto.codelikebastimove.feature.assetstudio.model.AssetType
import com.scto.codelikebastimove.feature.assetstudio.model.DensityVariant
import com.scto.codelikebastimove.feature.assetstudio.model.IconDensity
import com.scto.codelikebastimove.feature.assetstudio.model.LauncherIconConfig

class AssetExporter(private val context: Context) {

    data class ExportResult(
        val success: Boolean,
        val exportedFiles: List<String> = emptyList(),
        val errors: List<String> = emptyList(),
    )

    fun exportVectorAsset(
        document: AVDDocument,
        projectUri: Uri,
        assetType: AssetType,
        fileName: String,
    ): ExportResult {
        val projectDir = DocumentFile.fromTreeUri(context, projectUri)
            ?: return ExportResult(false, errors = listOf("Cannot access project directory"))

        val resDir = findOrCreateResDirectory(projectDir)
            ?: return ExportResult(false, errors = listOf("Cannot find/create res directory"))

        val exportedFiles = mutableListOf<String>()
        val errors = mutableListOf<String>()

        when (assetType) {
            AssetType.VECTOR_DRAWABLE -> {
                val drawableDir = findOrCreateDirectory(resDir, "drawable")
                if (drawableDir != null) {
                    val xmlContent = generateVectorDrawableXml(document)
                    val file = drawableDir.createFile("text/xml", "$fileName.xml")
                    if (file != null) {
                        context.contentResolver.openOutputStream(file.uri)?.use { output ->
                            output.write(xmlContent.toByteArray())
                            exportedFiles.add("drawable/$fileName.xml")
                        }
                    } else {
                        errors.add("Failed to create $fileName.xml")
                    }
                }
            }
            AssetType.NOTIFICATION_ICON -> {
                exportNotificationIcon(document, resDir, fileName, exportedFiles, errors)
            }
            AssetType.ACTION_BAR_ICON -> {
                exportActionBarIcon(document, resDir, fileName, exportedFiles, errors)
            }
            else -> {
                errors.add("Unsupported asset type: $assetType")
            }
        }

        return ExportResult(
            success = errors.isEmpty(),
            exportedFiles = exportedFiles,
            errors = errors,
        )
    }

    fun exportLauncherIcon(
        config: LauncherIconConfig,
        projectUri: Uri,
    ): ExportResult {
        val projectDir = DocumentFile.fromTreeUri(context, projectUri)
            ?: return ExportResult(false, errors = listOf("Cannot access project directory"))

        val resDir = findOrCreateResDirectory(projectDir)
            ?: return ExportResult(false, errors = listOf("Cannot find/create res directory"))

        val exportedFiles = mutableListOf<String>()
        val errors = mutableListOf<String>()

        IconDensity.entries.forEach { density ->
            val sizePx = density.launcherIconSize

            val mipmapDir = findOrCreateDirectory(resDir, "mipmap-${density.qualifier}")
            if (mipmapDir != null) {
                val launcherXml = generateLauncherIconXml(config, sizePx)
                val file = mipmapDir.createFile("text/xml", "${config.name}.xml")
                if (file != null) {
                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                        output.write(launcherXml.toByteArray())
                        exportedFiles.add("mipmap-${density.qualifier}/${config.name}.xml")
                    }
                }
            }
        }

        if (config.generateRound) {
            IconDensity.entries.forEach { density ->
                val mipmapDir = findOrCreateDirectory(resDir, "mipmap-${density.qualifier}")
                if (mipmapDir != null) {
                    val roundXml = generateRoundLauncherIconXml(config, density.launcherIconSize)
                    val file = mipmapDir.createFile("text/xml", "${config.name}_round.xml")
                    if (file != null) {
                        context.contentResolver.openOutputStream(file.uri)?.use { output ->
                            output.write(roundXml.toByteArray())
                            exportedFiles.add("mipmap-${density.qualifier}/${config.name}_round.xml")
                        }
                    }
                }
            }
        }

        return ExportResult(
            success = errors.isEmpty(),
            exportedFiles = exportedFiles,
            errors = errors,
        )
    }

    fun exportAdaptiveIcon(
        config: AdaptiveIconConfig,
        projectUri: Uri,
    ): ExportResult {
        val projectDir = DocumentFile.fromTreeUri(context, projectUri)
            ?: return ExportResult(false, errors = listOf("Cannot access project directory"))

        val resDir = findOrCreateResDirectory(projectDir)
            ?: return ExportResult(false, errors = listOf("Cannot find/create res directory"))

        val exportedFiles = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val mipmapAnydpiDir = findOrCreateDirectory(resDir, "mipmap-anydpi-v26")
        if (mipmapAnydpiDir != null) {
            val adaptiveXml = generateAdaptiveIconXml(config)
            val file = mipmapAnydpiDir.createFile("text/xml", "${config.name}.xml")
            if (file != null) {
                context.contentResolver.openOutputStream(file.uri)?.use { output ->
                    output.write(adaptiveXml.toByteArray())
                    exportedFiles.add("mipmap-anydpi-v26/${config.name}.xml")
                }
            }

            if (config.generateRound) {
                val roundFile = mipmapAnydpiDir.createFile("text/xml", "${config.name}_round.xml")
                if (roundFile != null) {
                    context.contentResolver.openOutputStream(roundFile.uri)?.use { output ->
                        output.write(adaptiveXml.toByteArray())
                        exportedFiles.add("mipmap-anydpi-v26/${config.name}_round.xml")
                    }
                }
            }
        }

        val drawableDir = findOrCreateDirectory(resDir, "drawable")
        if (drawableDir != null) {
            if (config.foregroundDocument != null) {
                val fgXml = generateVectorDrawableXml(config.foregroundDocument)
                val fgFile = drawableDir.createFile("text/xml", "${config.name}_foreground.xml")
                if (fgFile != null) {
                    context.contentResolver.openOutputStream(fgFile.uri)?.use { output ->
                        output.write(fgXml.toByteArray())
                        exportedFiles.add("drawable/${config.name}_foreground.xml")
                    }
                }
            }

            val bgXml = if (config.backgroundDocument != null) {
                generateVectorDrawableXml(config.backgroundDocument)
            } else {
                generateColorBackgroundXml(config.backgroundColor)
            }
            val bgFile = drawableDir.createFile("text/xml", "${config.name}_background.xml")
            if (bgFile != null) {
                context.contentResolver.openOutputStream(bgFile.uri)?.use { output ->
                    output.write(bgXml.toByteArray())
                    exportedFiles.add("drawable/${config.name}_background.xml")
                }
            }
        }

        IconDensity.entries.forEach { density ->
            val mipmapDir = findOrCreateDirectory(resDir, "mipmap-${density.qualifier}")
            if (mipmapDir != null) {
                val legacyXml = generateLegacyLauncherIconXml(config, density.launcherIconSize)
                val file = mipmapDir.createFile("text/xml", "${config.name}.xml")
                if (file != null) {
                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                        output.write(legacyXml.toByteArray())
                        exportedFiles.add("mipmap-${density.qualifier}/${config.name}.xml")
                    }
                }
            }
        }

        return ExportResult(
            success = errors.isEmpty(),
            exportedFiles = exportedFiles,
            errors = errors,
        )
    }

    fun exportDensityVariants(
        document: AVDDocument,
        projectUri: Uri,
        fileName: String,
        densities: List<DensityVariant>,
    ): ExportResult {
        val projectDir = DocumentFile.fromTreeUri(context, projectUri)
            ?: return ExportResult(false, errors = listOf("Cannot access project directory"))

        val resDir = findOrCreateResDirectory(projectDir)
            ?: return ExportResult(false, errors = listOf("Cannot find/create res directory"))

        val exportedFiles = mutableListOf<String>()
        val errors = mutableListOf<String>()

        densities.forEach { variant ->
            val drawableDir = findOrCreateDirectory(resDir, "drawable-${variant.density.qualifier}")
            if (drawableDir != null) {
                val xmlContent = generateVectorDrawableXml(document, variant.sizeDp)
                val file = drawableDir.createFile("text/xml", "$fileName.xml")
                if (file != null) {
                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                        output.write(xmlContent.toByteArray())
                        exportedFiles.add("drawable-${variant.density.qualifier}/$fileName.xml")
                    }
                } else {
                    errors.add("Failed to create drawable-${variant.density.qualifier}/$fileName.xml")
                }
            }
        }

        return ExportResult(
            success = errors.isEmpty(),
            exportedFiles = exportedFiles,
            errors = errors,
        )
    }

    private fun exportNotificationIcon(
        document: AVDDocument,
        resDir: DocumentFile,
        fileName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val notificationSizes = mapOf(
            "drawable-mdpi" to 24,
            "drawable-hdpi" to 36,
            "drawable-xhdpi" to 48,
            "drawable-xxhdpi" to 72,
            "drawable-xxxhdpi" to 96,
        )

        notificationSizes.forEach { (folder, size) ->
            val dir = findOrCreateDirectory(resDir, folder)
            if (dir != null) {
                val whiteDoc = document.copy(
                    rootGroup = document.rootGroup.copy(
                        paths = document.rootGroup.paths.map { it.copy(fillColor = Color.White) }
                    )
                )
                val xmlContent = generateVectorDrawableXml(whiteDoc, size)
                val file = dir.createFile("text/xml", "$fileName.xml")
                if (file != null) {
                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                        output.write(xmlContent.toByteArray())
                        exportedFiles.add("$folder/$fileName.xml")
                    }
                } else {
                    errors.add("Failed to create $folder/$fileName.xml")
                }
            }
        }
    }

    private fun exportActionBarIcon(
        document: AVDDocument,
        resDir: DocumentFile,
        fileName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val actionBarSizes = mapOf(
            "drawable-mdpi" to 24,
            "drawable-hdpi" to 36,
            "drawable-xhdpi" to 48,
            "drawable-xxhdpi" to 72,
            "drawable-xxxhdpi" to 96,
        )

        actionBarSizes.forEach { (folder, size) ->
            val dir = findOrCreateDirectory(resDir, folder)
            if (dir != null) {
                val xmlContent = generateVectorDrawableXml(document, size)
                val file = dir.createFile("text/xml", "$fileName.xml")
                if (file != null) {
                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                        output.write(xmlContent.toByteArray())
                        exportedFiles.add("$folder/$fileName.xml")
                    }
                } else {
                    errors.add("Failed to create $folder/$fileName.xml")
                }
            }
        }
    }

    private fun findOrCreateResDirectory(projectDir: DocumentFile): DocumentFile? {
        var current = projectDir

        val pathsToCheck = listOf(
            listOf("app", "src", "main", "res"),
            listOf("src", "main", "res"),
            listOf("res"),
        )

        for (path in pathsToCheck) {
            var found = projectDir
            var valid = true
            for (segment in path) {
                val next = found.findFile(segment)
                if (next != null && next.isDirectory) {
                    found = next
                } else {
                    valid = false
                    break
                }
            }
            if (valid) return found
        }

        val appDir = projectDir.findFile("app") ?: projectDir.createDirectory("app")
        val srcDir = appDir?.findFile("src") ?: appDir?.createDirectory("src")
        val mainDir = srcDir?.findFile("main") ?: srcDir?.createDirectory("main")
        return mainDir?.findFile("res") ?: mainDir?.createDirectory("res")
    }

    private fun findOrCreateDirectory(parent: DocumentFile, name: String): DocumentFile? {
        return parent.findFile(name) ?: parent.createDirectory(name)
    }

    private fun generateVectorDrawableXml(document: AVDDocument, size: Int = 24): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<vector xmlns:android="http://schemas.android.com/apk/res/android"""")
            appendLine("""    android:width="${size}dp"""")
            appendLine("""    android:height="${size}dp"""")
            appendLine("""    android:viewportWidth="${document.viewportWidth}"""")
            appendLine("""    android:viewportHeight="${document.viewportHeight}">""")

            document.rootGroup.paths.forEach { path ->
                val fillColor = String.format("#%08X", path.fillColor.toArgb())
                appendLine("""    <path""")
                appendLine("""        android:pathData="${path.pathData}"""")
                appendLine("""        android:fillColor="$fillColor"/>""")
            }

            appendLine("""</vector>""")
        }
    }

    private fun generateLauncherIconXml(config: LauncherIconConfig, sizePx: Int): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<vector xmlns:android="http://schemas.android.com/apk/res/android"""")
            appendLine("""    android:width="${sizePx}dp"""")
            appendLine("""    android:height="${sizePx}dp"""")
            appendLine("""    android:viewportWidth="108"""")
            appendLine("""    android:viewportHeight="108">""")
            appendLine("""    <path""")
            appendLine("""        android:pathData="M0,0h108v108h-108z"""")
            appendLine("""        android:fillColor="${String.format("#%08X", config.backgroundColor.toArgb())}"/>""")
            if (config.foregroundDocument != null) {
                config.foregroundDocument.rootGroup.paths.forEach { path ->
                    appendLine("""    <path""")
                    appendLine("""        android:pathData="${path.pathData}"""")
                    appendLine("""        android:fillColor="${String.format("#%08X", path.fillColor.toArgb())}"/>""")
                }
            }
            appendLine("""</vector>""")
        }
    }

    private fun generateRoundLauncherIconXml(config: LauncherIconConfig, sizePx: Int): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<vector xmlns:android="http://schemas.android.com/apk/res/android"""")
            appendLine("""    android:width="${sizePx}dp"""")
            appendLine("""    android:height="${sizePx}dp"""")
            appendLine("""    android:viewportWidth="108"""")
            appendLine("""    android:viewportHeight="108">""")
            appendLine("""    <path""")
            appendLine("""        android:pathData="M54,54m-54,0a54,54 0,1 1,108 0a54,54 0,1 1,-108 0"""")
            appendLine("""        android:fillColor="${String.format("#%08X", config.backgroundColor.toArgb())}"/>""")
            appendLine("""</vector>""")
        }
    }

    private fun generateAdaptiveIconXml(config: AdaptiveIconConfig): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">""")
            appendLine("""    <background android:drawable="@drawable/${config.name}_background"/>""")
            appendLine("""    <foreground android:drawable="@drawable/${config.name}_foreground"/>""")
            if (config.generateMonochrome) {
                appendLine("""    <monochrome android:drawable="@drawable/${config.name}_foreground"/>""")
            }
            appendLine("""</adaptive-icon>""")
        }
    }

    private fun generateColorBackgroundXml(color: Color): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<vector xmlns:android="http://schemas.android.com/apk/res/android"""")
            appendLine("""    android:width="108dp"""")
            appendLine("""    android:height="108dp"""")
            appendLine("""    android:viewportWidth="108"""")
            appendLine("""    android:viewportHeight="108">""")
            appendLine("""    <path""")
            appendLine("""        android:pathData="M0,0h108v108h-108z"""")
            appendLine("""        android:fillColor="${String.format("#%08X", color.toArgb())}"/>""")
            appendLine("""</vector>""")
        }
    }

    private fun generateLegacyLauncherIconXml(config: AdaptiveIconConfig, sizePx: Int): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<layer-list xmlns:android="http://schemas.android.com/apk/res/android">""")
            appendLine("""    <item android:drawable="@drawable/${config.name}_background"/>""")
            appendLine("""    <item android:drawable="@drawable/${config.name}_foreground"/>""")
            appendLine("""</layer-list>""")
        }
    }
}
