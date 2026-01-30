package com.scto.codelikebastimove.feature.themebuilder.export

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.scto.codelikebastimove.feature.themebuilder.model.ThemeColors
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun exportTheme(
  context: Context,
  themeName: String,
  colors: ThemeColors,
  displayFont: String,
  bodyFont: String,
  format: String,
) {
  try {
    val cacheDir = context.cacheDir
    val themeDir = File(cacheDir, "theme_export")
    themeDir.mkdirs()

    when (format) {
      "compose" -> {
        val colorKt = generateComposeColorKt(themeName, colors)
        val themeKt = generateComposeThemeKt(themeName)
        val typeKt = generateComposeTypeKt(themeName, displayFont, bodyFont)

        File(themeDir, "Color.kt").writeText(colorKt)
        File(themeDir, "Theme.kt").writeText(themeKt)
        File(themeDir, "Type.kt").writeText(typeKt)
      }
      "android" -> {
        val colorsXml = generateAndroidColorsXml(colors)
        val themeXml = generateAndroidThemeXml(themeName)
        val typeXml = generateAndroidTypeXml(displayFont, bodyFont)

        File(themeDir, "colors.xml").writeText(colorsXml)
        File(themeDir, "themes.xml").writeText(themeXml)
        File(themeDir, "type.xml").writeText(typeXml)
      }
      "web" -> {
        val cssContent = generateWebCss(themeName, colors, displayFont, bodyFont)
        File(themeDir, "theme.css").writeText(cssContent)
      }
      "json" -> {
        val jsonContent = generateThemeJson(themeName, colors, displayFont, bodyFont)
        File(themeDir, "theme.json").writeText(jsonContent)
      }
    }

    val zipFile = File(cacheDir, "${themeName.replace(" ", "_")}_${format}.zip")
    createZip(themeDir, zipFile)

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zipFile)

    val shareIntent =
      Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

    context.startActivity(Intent.createChooser(shareIntent, "Export Theme"))

    Toast.makeText(context, "Theme exported successfully!", Toast.LENGTH_SHORT).show()
  } catch (e: Exception) {
    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
  }
}

private fun createZip(sourceDir: File, zipFile: File) {
  ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
    sourceDir.listFiles()?.forEach { file ->
      zos.putNextEntry(ZipEntry(file.name))
      file.inputStream().use { it.copyTo(zos) }
      zos.closeEntry()
    }
  }
}
