package com.scto.codelikebastimove.feature.themebuilder.export

import com.scto.codelikebastimove.feature.themebuilder.model.ThemeColors
import com.scto.codelikebastimove.feature.themebuilder.util.colorToHex

fun generateAndroidColorsXml(colors: ThemeColors): String {
  return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="md_theme_primary">${colorToHex(colors.primary)}</color>
    <color name="md_theme_secondary">${colorToHex(colors.secondary)}</color>
    <color name="md_theme_tertiary">${colorToHex(colors.tertiary)}</color>
    <color name="md_theme_error">${colorToHex(colors.error)}</color>
    <color name="md_theme_primaryContainer">${colorToHex(colors.primaryContainer)}</color>
    <color name="md_theme_secondaryContainer">${colorToHex(colors.secondaryContainer)}</color>
    <color name="md_theme_tertiaryContainer">${colorToHex(colors.tertiaryContainer)}</color>
    <color name="md_theme_errorContainer">${colorToHex(colors.errorContainer)}</color>
    <color name="md_theme_surface">${colorToHex(colors.surface)}</color>
    <color name="md_theme_surfaceVariant">${colorToHex(colors.surfaceVariant)}</color>
    <color name="md_theme_background">${colorToHex(colors.background)}</color>
    <color name="md_theme_outline">${colorToHex(colors.outline)}</color>
</resources>"""
}

fun generateAndroidThemeXml(themeName: String): String {
  val safeName = themeName.replace(" ", "").replace("-", "")
  return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.$safeName" parent="Theme.Material3.DayNight">
        <item name="colorPrimary">@color/md_theme_primary</item>
        <item name="colorSecondary">@color/md_theme_secondary</item>
        <item name="colorTertiary">@color/md_theme_tertiary</item>
        <item name="colorError">@color/md_theme_error</item>
        <item name="colorPrimaryContainer">@color/md_theme_primaryContainer</item>
        <item name="colorSecondaryContainer">@color/md_theme_secondaryContainer</item>
        <item name="colorTertiaryContainer">@color/md_theme_tertiaryContainer</item>
        <item name="colorErrorContainer">@color/md_theme_errorContainer</item>
        <item name="colorSurface">@color/md_theme_surface</item>
        <item name="colorSurfaceVariant">@color/md_theme_surfaceVariant</item>
        <item name="android:colorBackground">@color/md_theme_background</item>
        <item name="colorOutline">@color/md_theme_outline</item>
    </style>
</resources>"""
}

fun generateAndroidTypeXml(displayFont: String, bodyFont: String): String {
  val displayFontFamily =
    if (displayFont == "-- System Default --") "sans-serif"
    else displayFont.lowercase().replace(" ", "_")
  val bodyFontFamily =
    if (bodyFont == "-- System Default --") "sans-serif" else bodyFont.lowercase().replace(" ", "_")

  return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="TextAppearance.Display" parent="TextAppearance.Material3.DisplayLarge">
        <item name="fontFamily">$displayFontFamily</item>
    </style>
    <style name="TextAppearance.Headline" parent="TextAppearance.Material3.HeadlineLarge">
        <item name="fontFamily">$displayFontFamily</item>
    </style>
    <style name="TextAppearance.Title" parent="TextAppearance.Material3.TitleLarge">
        <item name="fontFamily">$displayFontFamily</item>
    </style>
    <style name="TextAppearance.Body" parent="TextAppearance.Material3.BodyLarge">
        <item name="fontFamily">$bodyFontFamily</item>
    </style>
    <style name="TextAppearance.Label" parent="TextAppearance.Material3.LabelLarge">
        <item name="fontFamily">$bodyFontFamily</item>
    </style>
</resources>"""
}
