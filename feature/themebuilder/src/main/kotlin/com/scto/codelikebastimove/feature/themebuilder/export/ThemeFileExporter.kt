package com.scto.codelikebastimove.feature.themebuilder.export

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.documentfile.provider.DocumentFile
import com.scto.codelikebastimove.feature.themebuilder.generator.GeneratedTheme
import com.scto.codelikebastimove.feature.themebuilder.generator.Material3ColorScheme

class ThemeFileExporter(private val context: Context) {

    data class ExportResult(
        val success: Boolean,
        val exportedFiles: List<String> = emptyList(),
        val errors: List<String> = emptyList(),
    )

    fun exportToAndroidProject(
        theme: GeneratedTheme,
        projectUri: Uri,
        themeName: String = "AppTheme",
        packageName: String = "com.example.app",
    ): ExportResult {
        val projectDir = DocumentFile.fromTreeUri(context, projectUri)
            ?: return ExportResult(false, errors = listOf("Cannot access project directory"))

        val resDir = findOrCreateResDirectory(projectDir)
            ?: return ExportResult(false, errors = listOf("Cannot find/create res directory"))

        val exportedFiles = mutableListOf<String>()
        val errors = mutableListOf<String>()

        exportColorsXml(resDir, theme, exportedFiles, errors)
        exportThemesXml(resDir, theme, themeName, exportedFiles, errors)
        exportNightThemesXml(resDir, theme, themeName, exportedFiles, errors)

        return ExportResult(
            success = errors.isEmpty(),
            exportedFiles = exportedFiles,
            errors = errors,
        )
    }

    fun exportComposeThemeFiles(
        theme: GeneratedTheme,
        projectUri: Uri,
        packageName: String = "com.example.app.ui.theme",
    ): ExportResult {
        val projectDir = DocumentFile.fromTreeUri(context, projectUri)
            ?: return ExportResult(false, errors = listOf("Cannot access project directory"))

        val themeDir = findOrCreateThemeDirectory(projectDir, packageName)
            ?: return ExportResult(false, errors = listOf("Cannot find/create theme directory"))

        val exportedFiles = mutableListOf<String>()
        val errors = mutableListOf<String>()

        exportColorKt(themeDir, theme, packageName, exportedFiles, errors)
        exportThemeKt(themeDir, theme, packageName, exportedFiles, errors)
        exportTypeKt(themeDir, packageName, exportedFiles, errors)

        return ExportResult(
            success = errors.isEmpty(),
            exportedFiles = exportedFiles,
            errors = errors,
        )
    }

    fun generateColorsXml(theme: GeneratedTheme): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<resources>""")
            appendLine()
            appendLine("""    <!-- Seed Color -->""")
            appendLine("""    <color name="seed">${colorToHex(theme.seedColor)}</color>""")
            appendLine()
            appendLine("""    <!-- Light Theme Colors -->""")
            appendSchemeColors(theme.lightScheme, "md_theme_light_")
            appendLine()
            appendLine("""    <!-- Dark Theme Colors -->""")
            appendSchemeColors(theme.darkScheme, "md_theme_dark_")
            appendLine()
            appendLine("""</resources>""")
        }
    }

    fun generateThemesXml(theme: GeneratedTheme, themeName: String): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<resources>""")
            appendLine()
            appendLine("""    <style name="Theme.$themeName" parent="Theme.Material3.DayNight.NoActionBar">""")
            appendLine("""        <item name="colorPrimary">@color/md_theme_light_primary</item>""")
            appendLine("""        <item name="colorOnPrimary">@color/md_theme_light_onPrimary</item>""")
            appendLine("""        <item name="colorPrimaryContainer">@color/md_theme_light_primaryContainer</item>""")
            appendLine("""        <item name="colorOnPrimaryContainer">@color/md_theme_light_onPrimaryContainer</item>""")
            appendLine("""        <item name="colorSecondary">@color/md_theme_light_secondary</item>""")
            appendLine("""        <item name="colorOnSecondary">@color/md_theme_light_onSecondary</item>""")
            appendLine("""        <item name="colorSecondaryContainer">@color/md_theme_light_secondaryContainer</item>""")
            appendLine("""        <item name="colorOnSecondaryContainer">@color/md_theme_light_onSecondaryContainer</item>""")
            appendLine("""        <item name="colorTertiary">@color/md_theme_light_tertiary</item>""")
            appendLine("""        <item name="colorOnTertiary">@color/md_theme_light_onTertiary</item>""")
            appendLine("""        <item name="colorTertiaryContainer">@color/md_theme_light_tertiaryContainer</item>""")
            appendLine("""        <item name="colorOnTertiaryContainer">@color/md_theme_light_onTertiaryContainer</item>""")
            appendLine("""        <item name="colorError">@color/md_theme_light_error</item>""")
            appendLine("""        <item name="colorOnError">@color/md_theme_light_onError</item>""")
            appendLine("""        <item name="colorErrorContainer">@color/md_theme_light_errorContainer</item>""")
            appendLine("""        <item name="colorOnErrorContainer">@color/md_theme_light_onErrorContainer</item>""")
            appendLine("""        <item name="android:colorBackground">@color/md_theme_light_background</item>""")
            appendLine("""        <item name="colorOnBackground">@color/md_theme_light_onBackground</item>""")
            appendLine("""        <item name="colorSurface">@color/md_theme_light_surface</item>""")
            appendLine("""        <item name="colorOnSurface">@color/md_theme_light_onSurface</item>""")
            appendLine("""        <item name="colorSurfaceVariant">@color/md_theme_light_surfaceVariant</item>""")
            appendLine("""        <item name="colorOnSurfaceVariant">@color/md_theme_light_onSurfaceVariant</item>""")
            appendLine("""        <item name="colorOutline">@color/md_theme_light_outline</item>""")
            appendLine("""    </style>""")
            appendLine()
            appendLine("""</resources>""")
        }
    }

    fun generateNightThemesXml(theme: GeneratedTheme, themeName: String): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>""")
            appendLine("""<resources>""")
            appendLine()
            appendLine("""    <style name="Theme.$themeName" parent="Theme.Material3.DayNight.NoActionBar">""")
            appendLine("""        <item name="colorPrimary">@color/md_theme_dark_primary</item>""")
            appendLine("""        <item name="colorOnPrimary">@color/md_theme_dark_onPrimary</item>""")
            appendLine("""        <item name="colorPrimaryContainer">@color/md_theme_dark_primaryContainer</item>""")
            appendLine("""        <item name="colorOnPrimaryContainer">@color/md_theme_dark_onPrimaryContainer</item>""")
            appendLine("""        <item name="colorSecondary">@color/md_theme_dark_secondary</item>""")
            appendLine("""        <item name="colorOnSecondary">@color/md_theme_dark_onSecondary</item>""")
            appendLine("""        <item name="colorSecondaryContainer">@color/md_theme_dark_secondaryContainer</item>""")
            appendLine("""        <item name="colorOnSecondaryContainer">@color/md_theme_dark_onSecondaryContainer</item>""")
            appendLine("""        <item name="colorTertiary">@color/md_theme_dark_tertiary</item>""")
            appendLine("""        <item name="colorOnTertiary">@color/md_theme_dark_onTertiary</item>""")
            appendLine("""        <item name="colorTertiaryContainer">@color/md_theme_dark_tertiaryContainer</item>""")
            appendLine("""        <item name="colorOnTertiaryContainer">@color/md_theme_dark_onTertiaryContainer</item>""")
            appendLine("""        <item name="colorError">@color/md_theme_dark_error</item>""")
            appendLine("""        <item name="colorOnError">@color/md_theme_dark_onError</item>""")
            appendLine("""        <item name="colorErrorContainer">@color/md_theme_dark_errorContainer</item>""")
            appendLine("""        <item name="colorOnErrorContainer">@color/md_theme_dark_onErrorContainer</item>""")
            appendLine("""        <item name="android:colorBackground">@color/md_theme_dark_background</item>""")
            appendLine("""        <item name="colorOnBackground">@color/md_theme_dark_onBackground</item>""")
            appendLine("""        <item name="colorSurface">@color/md_theme_dark_surface</item>""")
            appendLine("""        <item name="colorOnSurface">@color/md_theme_dark_onSurface</item>""")
            appendLine("""        <item name="colorSurfaceVariant">@color/md_theme_dark_surfaceVariant</item>""")
            appendLine("""        <item name="colorOnSurfaceVariant">@color/md_theme_dark_onSurfaceVariant</item>""")
            appendLine("""        <item name="colorOutline">@color/md_theme_dark_outline</item>""")
            appendLine("""    </style>""")
            appendLine()
            appendLine("""</resources>""")
        }
    }

    fun generateColorKt(theme: GeneratedTheme, packageName: String): String {
        return buildString {
            appendLine("""package $packageName""")
            appendLine()
            appendLine("""import androidx.compose.ui.graphics.Color""")
            appendLine()
            appendLine("""// Seed color: ${colorToHex(theme.seedColor)}""")
            appendLine()
            appendLine("""// Light Theme""")
            appendColorValues(theme.lightScheme, "light")
            appendLine()
            appendLine("""// Dark Theme""")
            appendColorValues(theme.darkScheme, "dark")
        }
    }

    fun generateThemeKt(packageName: String): String {
        return buildString {
            appendLine("""package $packageName""")
            appendLine()
            appendLine("""import android.app.Activity""")
            appendLine("""import android.os.Build""")
            appendLine("""import androidx.compose.foundation.isSystemInDarkTheme""")
            appendLine("""import androidx.compose.material3.MaterialTheme""")
            appendLine("""import androidx.compose.material3.darkColorScheme""")
            appendLine("""import androidx.compose.material3.dynamicDarkColorScheme""")
            appendLine("""import androidx.compose.material3.dynamicLightColorScheme""")
            appendLine("""import androidx.compose.material3.lightColorScheme""")
            appendLine("""import androidx.compose.runtime.Composable""")
            appendLine("""import androidx.compose.runtime.SideEffect""")
            appendLine("""import androidx.compose.ui.graphics.toArgb""")
            appendLine("""import androidx.compose.ui.platform.LocalContext""")
            appendLine("""import androidx.compose.ui.platform.LocalView""")
            appendLine("""import androidx.core.view.WindowCompat""")
            appendLine()
            appendLine("""private val LightColorScheme = lightColorScheme(""")
            appendLine("""    primary = lightPrimary,""")
            appendLine("""    onPrimary = lightOnPrimary,""")
            appendLine("""    primaryContainer = lightPrimaryContainer,""")
            appendLine("""    onPrimaryContainer = lightOnPrimaryContainer,""")
            appendLine("""    secondary = lightSecondary,""")
            appendLine("""    onSecondary = lightOnSecondary,""")
            appendLine("""    secondaryContainer = lightSecondaryContainer,""")
            appendLine("""    onSecondaryContainer = lightOnSecondaryContainer,""")
            appendLine("""    tertiary = lightTertiary,""")
            appendLine("""    onTertiary = lightOnTertiary,""")
            appendLine("""    tertiaryContainer = lightTertiaryContainer,""")
            appendLine("""    onTertiaryContainer = lightOnTertiaryContainer,""")
            appendLine("""    error = lightError,""")
            appendLine("""    onError = lightOnError,""")
            appendLine("""    errorContainer = lightErrorContainer,""")
            appendLine("""    onErrorContainer = lightOnErrorContainer,""")
            appendLine("""    background = lightBackground,""")
            appendLine("""    onBackground = lightOnBackground,""")
            appendLine("""    surface = lightSurface,""")
            appendLine("""    onSurface = lightOnSurface,""")
            appendLine("""    surfaceVariant = lightSurfaceVariant,""")
            appendLine("""    onSurfaceVariant = lightOnSurfaceVariant,""")
            appendLine("""    outline = lightOutline,""")
            appendLine("""    outlineVariant = lightOutlineVariant,""")
            appendLine("""    scrim = lightScrim,""")
            appendLine("""    inverseSurface = lightInverseSurface,""")
            appendLine("""    inverseOnSurface = lightInverseOnSurface,""")
            appendLine("""    inversePrimary = lightInversePrimary,""")
            appendLine(""")""")
            appendLine()
            appendLine("""private val DarkColorScheme = darkColorScheme(""")
            appendLine("""    primary = darkPrimary,""")
            appendLine("""    onPrimary = darkOnPrimary,""")
            appendLine("""    primaryContainer = darkPrimaryContainer,""")
            appendLine("""    onPrimaryContainer = darkOnPrimaryContainer,""")
            appendLine("""    secondary = darkSecondary,""")
            appendLine("""    onSecondary = darkOnSecondary,""")
            appendLine("""    secondaryContainer = darkSecondaryContainer,""")
            appendLine("""    onSecondaryContainer = darkOnSecondaryContainer,""")
            appendLine("""    tertiary = darkTertiary,""")
            appendLine("""    onTertiary = darkOnTertiary,""")
            appendLine("""    tertiaryContainer = darkTertiaryContainer,""")
            appendLine("""    onTertiaryContainer = darkOnTertiaryContainer,""")
            appendLine("""    error = darkError,""")
            appendLine("""    onError = darkOnError,""")
            appendLine("""    errorContainer = darkErrorContainer,""")
            appendLine("""    onErrorContainer = darkOnErrorContainer,""")
            appendLine("""    background = darkBackground,""")
            appendLine("""    onBackground = darkOnBackground,""")
            appendLine("""    surface = darkSurface,""")
            appendLine("""    onSurface = darkOnSurface,""")
            appendLine("""    surfaceVariant = darkSurfaceVariant,""")
            appendLine("""    onSurfaceVariant = darkOnSurfaceVariant,""")
            appendLine("""    outline = darkOutline,""")
            appendLine("""    outlineVariant = darkOutlineVariant,""")
            appendLine("""    scrim = darkScrim,""")
            appendLine("""    inverseSurface = darkInverseSurface,""")
            appendLine("""    inverseOnSurface = darkInverseOnSurface,""")
            appendLine("""    inversePrimary = darkInversePrimary,""")
            appendLine(""")""")
            appendLine()
            appendLine("""@Composable""")
            appendLine("""fun AppTheme(""")
            appendLine("""    darkTheme: Boolean = isSystemInDarkTheme(),""")
            appendLine("""    dynamicColor: Boolean = true,""")
            appendLine("""    content: @Composable () -> Unit""")
            appendLine(""") {""")
            appendLine("""    val colorScheme = when {""")
            appendLine("""        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {""")
            appendLine("""            val context = LocalContext.current""")
            appendLine("""            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)""")
            appendLine("""        }""")
            appendLine("""        darkTheme -> DarkColorScheme""")
            appendLine("""        else -> LightColorScheme""")
            appendLine("""    }""")
            appendLine()
            appendLine("""    val view = LocalView.current""")
            appendLine("""    if (!view.isInEditMode) {""")
            appendLine("""        SideEffect {""")
            appendLine("""            val window = (view.context as Activity).window""")
            appendLine("""            window.statusBarColor = colorScheme.primary.toArgb()""")
            appendLine("""            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme""")
            appendLine("""        }""")
            appendLine("""    }""")
            appendLine()
            appendLine("""    MaterialTheme(""")
            appendLine("""        colorScheme = colorScheme,""")
            appendLine("""        typography = Typography,""")
            appendLine("""        content = content""")
            appendLine("""    )""")
            appendLine("""}""")
        }
    }

    private fun StringBuilder.appendSchemeColors(scheme: Material3ColorScheme, prefix: String) {
        appendLine("""    <color name="${prefix}primary">${colorToHex(scheme.primary)}</color>""")
        appendLine("""    <color name="${prefix}onPrimary">${colorToHex(scheme.onPrimary)}</color>""")
        appendLine("""    <color name="${prefix}primaryContainer">${colorToHex(scheme.primaryContainer)}</color>""")
        appendLine("""    <color name="${prefix}onPrimaryContainer">${colorToHex(scheme.onPrimaryContainer)}</color>""")
        appendLine("""    <color name="${prefix}secondary">${colorToHex(scheme.secondary)}</color>""")
        appendLine("""    <color name="${prefix}onSecondary">${colorToHex(scheme.onSecondary)}</color>""")
        appendLine("""    <color name="${prefix}secondaryContainer">${colorToHex(scheme.secondaryContainer)}</color>""")
        appendLine("""    <color name="${prefix}onSecondaryContainer">${colorToHex(scheme.onSecondaryContainer)}</color>""")
        appendLine("""    <color name="${prefix}tertiary">${colorToHex(scheme.tertiary)}</color>""")
        appendLine("""    <color name="${prefix}onTertiary">${colorToHex(scheme.onTertiary)}</color>""")
        appendLine("""    <color name="${prefix}tertiaryContainer">${colorToHex(scheme.tertiaryContainer)}</color>""")
        appendLine("""    <color name="${prefix}onTertiaryContainer">${colorToHex(scheme.onTertiaryContainer)}</color>""")
        appendLine("""    <color name="${prefix}error">${colorToHex(scheme.error)}</color>""")
        appendLine("""    <color name="${prefix}onError">${colorToHex(scheme.onError)}</color>""")
        appendLine("""    <color name="${prefix}errorContainer">${colorToHex(scheme.errorContainer)}</color>""")
        appendLine("""    <color name="${prefix}onErrorContainer">${colorToHex(scheme.onErrorContainer)}</color>""")
        appendLine("""    <color name="${prefix}background">${colorToHex(scheme.background)}</color>""")
        appendLine("""    <color name="${prefix}onBackground">${colorToHex(scheme.onBackground)}</color>""")
        appendLine("""    <color name="${prefix}surface">${colorToHex(scheme.surface)}</color>""")
        appendLine("""    <color name="${prefix}onSurface">${colorToHex(scheme.onSurface)}</color>""")
        appendLine("""    <color name="${prefix}surfaceVariant">${colorToHex(scheme.surfaceVariant)}</color>""")
        appendLine("""    <color name="${prefix}onSurfaceVariant">${colorToHex(scheme.onSurfaceVariant)}</color>""")
        appendLine("""    <color name="${prefix}outline">${colorToHex(scheme.outline)}</color>""")
        appendLine("""    <color name="${prefix}outlineVariant">${colorToHex(scheme.outlineVariant)}</color>""")
        appendLine("""    <color name="${prefix}scrim">${colorToHex(scheme.scrim)}</color>""")
        appendLine("""    <color name="${prefix}inverseSurface">${colorToHex(scheme.inverseSurface)}</color>""")
        appendLine("""    <color name="${prefix}inverseOnSurface">${colorToHex(scheme.inverseOnSurface)}</color>""")
        appendLine("""    <color name="${prefix}inversePrimary">${colorToHex(scheme.inversePrimary)}</color>""")
    }

    private fun StringBuilder.appendColorValues(scheme: Material3ColorScheme, prefix: String) {
        appendLine("""val ${prefix}Primary = Color(${colorToArgbHex(scheme.primary)})""")
        appendLine("""val ${prefix}OnPrimary = Color(${colorToArgbHex(scheme.onPrimary)})""")
        appendLine("""val ${prefix}PrimaryContainer = Color(${colorToArgbHex(scheme.primaryContainer)})""")
        appendLine("""val ${prefix}OnPrimaryContainer = Color(${colorToArgbHex(scheme.onPrimaryContainer)})""")
        appendLine("""val ${prefix}Secondary = Color(${colorToArgbHex(scheme.secondary)})""")
        appendLine("""val ${prefix}OnSecondary = Color(${colorToArgbHex(scheme.onSecondary)})""")
        appendLine("""val ${prefix}SecondaryContainer = Color(${colorToArgbHex(scheme.secondaryContainer)})""")
        appendLine("""val ${prefix}OnSecondaryContainer = Color(${colorToArgbHex(scheme.onSecondaryContainer)})""")
        appendLine("""val ${prefix}Tertiary = Color(${colorToArgbHex(scheme.tertiary)})""")
        appendLine("""val ${prefix}OnTertiary = Color(${colorToArgbHex(scheme.onTertiary)})""")
        appendLine("""val ${prefix}TertiaryContainer = Color(${colorToArgbHex(scheme.tertiaryContainer)})""")
        appendLine("""val ${prefix}OnTertiaryContainer = Color(${colorToArgbHex(scheme.onTertiaryContainer)})""")
        appendLine("""val ${prefix}Error = Color(${colorToArgbHex(scheme.error)})""")
        appendLine("""val ${prefix}OnError = Color(${colorToArgbHex(scheme.onError)})""")
        appendLine("""val ${prefix}ErrorContainer = Color(${colorToArgbHex(scheme.errorContainer)})""")
        appendLine("""val ${prefix}OnErrorContainer = Color(${colorToArgbHex(scheme.onErrorContainer)})""")
        appendLine("""val ${prefix}Background = Color(${colorToArgbHex(scheme.background)})""")
        appendLine("""val ${prefix}OnBackground = Color(${colorToArgbHex(scheme.onBackground)})""")
        appendLine("""val ${prefix}Surface = Color(${colorToArgbHex(scheme.surface)})""")
        appendLine("""val ${prefix}OnSurface = Color(${colorToArgbHex(scheme.onSurface)})""")
        appendLine("""val ${prefix}SurfaceVariant = Color(${colorToArgbHex(scheme.surfaceVariant)})""")
        appendLine("""val ${prefix}OnSurfaceVariant = Color(${colorToArgbHex(scheme.onSurfaceVariant)})""")
        appendLine("""val ${prefix}Outline = Color(${colorToArgbHex(scheme.outline)})""")
        appendLine("""val ${prefix}OutlineVariant = Color(${colorToArgbHex(scheme.outlineVariant)})""")
        appendLine("""val ${prefix}Scrim = Color(${colorToArgbHex(scheme.scrim)})""")
        appendLine("""val ${prefix}InverseSurface = Color(${colorToArgbHex(scheme.inverseSurface)})""")
        appendLine("""val ${prefix}InverseOnSurface = Color(${colorToArgbHex(scheme.inverseOnSurface)})""")
        appendLine("""val ${prefix}InversePrimary = Color(${colorToArgbHex(scheme.inversePrimary)})""")
    }

    private fun exportColorsXml(
        resDir: DocumentFile,
        theme: GeneratedTheme,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val valuesDir = findOrCreateDirectory(resDir, "values")
        if (valuesDir != null) {
            val content = generateColorsXml(theme)
            val file = valuesDir.createFile("text/xml", "colors.xml")
            if (file != null) {
                context.contentResolver.openOutputStream(file.uri)?.use { output ->
                    output.write(content.toByteArray())
                    exportedFiles.add("values/colors.xml")
                }
            } else {
                errors.add("Failed to create colors.xml")
            }
        }
    }

    private fun exportThemesXml(
        resDir: DocumentFile,
        theme: GeneratedTheme,
        themeName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val valuesDir = findOrCreateDirectory(resDir, "values")
        if (valuesDir != null) {
            val content = generateThemesXml(theme, themeName)
            val file = valuesDir.createFile("text/xml", "themes.xml")
            if (file != null) {
                context.contentResolver.openOutputStream(file.uri)?.use { output ->
                    output.write(content.toByteArray())
                    exportedFiles.add("values/themes.xml")
                }
            } else {
                errors.add("Failed to create themes.xml")
            }
        }
    }

    private fun exportNightThemesXml(
        resDir: DocumentFile,
        theme: GeneratedTheme,
        themeName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val valuesNightDir = findOrCreateDirectory(resDir, "values-night")
        if (valuesNightDir != null) {
            val content = generateNightThemesXml(theme, themeName)
            val file = valuesNightDir.createFile("text/xml", "themes.xml")
            if (file != null) {
                context.contentResolver.openOutputStream(file.uri)?.use { output ->
                    output.write(content.toByteArray())
                    exportedFiles.add("values-night/themes.xml")
                }
            } else {
                errors.add("Failed to create values-night/themes.xml")
            }
        }
    }

    private fun exportColorKt(
        themeDir: DocumentFile,
        theme: GeneratedTheme,
        packageName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val content = generateColorKt(theme, packageName)
        val file = themeDir.createFile("text/x-kotlin", "Color.kt")
        if (file != null) {
            context.contentResolver.openOutputStream(file.uri)?.use { output ->
                output.write(content.toByteArray())
                exportedFiles.add("Color.kt")
            }
        } else {
            errors.add("Failed to create Color.kt")
        }
    }

    private fun exportThemeKt(
        themeDir: DocumentFile,
        theme: GeneratedTheme,
        packageName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val content = generateThemeKt(packageName)
        val file = themeDir.createFile("text/x-kotlin", "Theme.kt")
        if (file != null) {
            context.contentResolver.openOutputStream(file.uri)?.use { output ->
                output.write(content.toByteArray())
                exportedFiles.add("Theme.kt")
            }
        } else {
            errors.add("Failed to create Theme.kt")
        }
    }

    private fun exportTypeKt(
        themeDir: DocumentFile,
        packageName: String,
        exportedFiles: MutableList<String>,
        errors: MutableList<String>,
    ) {
        val content = buildString {
            appendLine("""package $packageName""")
            appendLine()
            appendLine("""import androidx.compose.material3.Typography""")
            appendLine("""import androidx.compose.ui.text.TextStyle""")
            appendLine("""import androidx.compose.ui.text.font.FontFamily""")
            appendLine("""import androidx.compose.ui.text.font.FontWeight""")
            appendLine("""import androidx.compose.ui.unit.sp""")
            appendLine()
            appendLine("""val Typography = Typography(""")
            appendLine("""    bodyLarge = TextStyle(""")
            appendLine("""        fontFamily = FontFamily.Default,""")
            appendLine("""        fontWeight = FontWeight.Normal,""")
            appendLine("""        fontSize = 16.sp,""")
            appendLine("""        lineHeight = 24.sp,""")
            appendLine("""        letterSpacing = 0.5.sp""")
            appendLine("""    )""")
            appendLine(""")""")
        }
        val file = themeDir.createFile("text/x-kotlin", "Type.kt")
        if (file != null) {
            context.contentResolver.openOutputStream(file.uri)?.use { output ->
                output.write(content.toByteArray())
                exportedFiles.add("Type.kt")
            }
        } else {
            errors.add("Failed to create Type.kt")
        }
    }

    private fun findOrCreateResDirectory(projectDir: DocumentFile): DocumentFile? {
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

    private fun findOrCreateThemeDirectory(projectDir: DocumentFile, packageName: String): DocumentFile? {
        val kotlinDir = findOrCreateResDirectory(projectDir)?.parentFile
            ?.findFile("kotlin")
            ?: findOrCreateResDirectory(projectDir)?.parentFile?.createDirectory("kotlin")

        var current = kotlinDir ?: return null
        packageName.split(".").forEach { segment ->
            current = current.findFile(segment) ?: current.createDirectory(segment) ?: return null
        }
        return current
    }

    private fun findOrCreateDirectory(parent: DocumentFile, name: String): DocumentFile? {
        return parent.findFile(name) ?: parent.createDirectory(name)
    }

    private fun colorToHex(color: Color): String {
        return String.format("#%06X", color.toArgb() and 0xFFFFFF)
    }

    private fun colorToArgbHex(color: Color): String {
        return String.format("0xFF%06X", color.toArgb() and 0xFFFFFF)
    }
}
