package com.scto.codelikebastimove.feature.themebuilder.export

import com.scto.codelikebastimove.feature.themebuilder.model.ThemeColors
import com.scto.codelikebastimove.feature.themebuilder.util.colorToHex

fun generateWebCss(
  themeName: String,
  colors: ThemeColors,
  displayFont: String,
  bodyFont: String,
): String {
  val displayFontStack =
    if (displayFont == "-- System Default --") "system-ui, sans-serif"
    else "'$displayFont', sans-serif"
  val bodyFontStack =
    if (bodyFont == "-- System Default --") "system-ui, sans-serif" else "'$bodyFont', sans-serif"

  return """:root {
  /* ${themeName} - Material Theme */
  
  /* Colors */
  --md-sys-color-primary: ${colorToHex(colors.primary)};
  --md-sys-color-secondary: ${colorToHex(colors.secondary)};
  --md-sys-color-tertiary: ${colorToHex(colors.tertiary)};
  --md-sys-color-error: ${colorToHex(colors.error)};
  --md-sys-color-primary-container: ${colorToHex(colors.primaryContainer)};
  --md-sys-color-secondary-container: ${colorToHex(colors.secondaryContainer)};
  --md-sys-color-tertiary-container: ${colorToHex(colors.tertiaryContainer)};
  --md-sys-color-error-container: ${colorToHex(colors.errorContainer)};
  --md-sys-color-surface: ${colorToHex(colors.surface)};
  --md-sys-color-surface-variant: ${colorToHex(colors.surfaceVariant)};
  --md-sys-color-background: ${colorToHex(colors.background)};
  --md-sys-color-outline: ${colorToHex(colors.outline)};
  
  /* Typography */
  --md-sys-typescale-display-font: $displayFontStack;
  --md-sys-typescale-headline-font: $displayFontStack;
  --md-sys-typescale-title-font: $displayFontStack;
  --md-sys-typescale-body-font: $bodyFontStack;
  --md-sys-typescale-label-font: $bodyFontStack;
}

body {
  font-family: var(--md-sys-typescale-body-font);
  background-color: var(--md-sys-color-background);
  color: var(--md-sys-color-on-surface, #1C1B1F);
}

h1, h2, h3 {
  font-family: var(--md-sys-typescale-display-font);
}

.primary {
  background-color: var(--md-sys-color-primary);
}

.secondary {
  background-color: var(--md-sys-color-secondary);
}

.tertiary {
  background-color: var(--md-sys-color-tertiary);
}
"""
}

fun generateThemeJson(
  themeName: String,
  colors: ThemeColors,
  displayFont: String,
  bodyFont: String,
): String {
  return """{
  "name": "$themeName",
  "colors": {
    "primary": "${colorToHex(colors.primary)}",
    "secondary": "${colorToHex(colors.secondary)}",
    "tertiary": "${colorToHex(colors.tertiary)}",
    "error": "${colorToHex(colors.error)}",
    "primaryContainer": "${colorToHex(colors.primaryContainer)}",
    "secondaryContainer": "${colorToHex(colors.secondaryContainer)}",
    "tertiaryContainer": "${colorToHex(colors.tertiaryContainer)}",
    "errorContainer": "${colorToHex(colors.errorContainer)}",
    "surface": "${colorToHex(colors.surface)}",
    "surfaceVariant": "${colorToHex(colors.surfaceVariant)}",
    "background": "${colorToHex(colors.background)}",
    "outline": "${colorToHex(colors.outline)}"
  },
  "typography": {
    "displayFont": "$displayFont",
    "bodyFont": "$bodyFont"
  }
}"""
}
