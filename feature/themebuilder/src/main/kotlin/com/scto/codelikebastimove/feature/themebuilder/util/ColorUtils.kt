package com.scto.codelikebastimove.feature.themebuilder.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun colorToHex(color: Color): String {
  val argb = color.toArgb()
  return String.format("#%06X", 0xFFFFFF and argb)
}

fun colorToArgbHex(color: Color): String {
  val argb = color.toArgb()
  return String.format("0x%08X", argb)
}

fun hexToColor(hex: String): Color? {
  return try {
    if (hex.length == 7 && hex.startsWith("#")) {
      val parsed = android.graphics.Color.parseColor(hex)
      Color(parsed)
    } else null
  } catch (_: Exception) {
    null
  }
}

fun rgbToHsl(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
  val max = maxOf(r, g, b)
  val min = minOf(r, g, b)
  val lightness = (max + min) / 2f
  val saturation =
    if (max == min) 0f
    else {
      if (lightness > 0.5f) (max - min) / (2f - max - min) else (max - min) / (max + min)
    }
  val hue =
    when {
      max == min -> 0f
      max == r -> (60f * ((g - b) / (max - min)) + 360f) % 360f
      max == g -> 60f * ((b - r) / (max - min)) + 120f
      else -> 60f * ((r - g) / (max - min)) + 240f
    }
  return Triple(hue, saturation, lightness)
}

fun colorToHsl(color: Color): Triple<Float, Float, Float> {
  val argb = color.toArgb()
  val r = android.graphics.Color.red(argb) / 255f
  val g = android.graphics.Color.green(argb) / 255f
  val b = android.graphics.Color.blue(argb) / 255f
  return rgbToHsl(r, g, b)
}

val presetColors =
  listOf(
    Color(0xFFD4C4A8),
    Color(0xFFB8977E),
    Color(0xFF8B7355),
    Color(0xFF6750A4),
    Color(0xFF2196F3),
    Color(0xFF4CAF50),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF00BCD4),
    Color(0xFFFF5722),
    Color(0xFF795548),
  )

val seedColors =
  listOf(
    Color(0xFFE8B896),
    Color(0xFFD4A8A8),
    Color(0xFFC4C4A8),
    Color(0xFFE8A8A8),
    Color(0xFFA8C4D4),
  )

val availableFonts =
  listOf(
    "-- System Default --",
    "Roboto",
    "Inter",
    "Open Sans",
    "Montserrat",
    "Poppins",
    "Lato",
    "Noto Sans",
    "Source Sans Pro",
  )

val schemeOptions =
  listOf(
    "Tonal Spot",
    "Neutral",
    "Vibrant",
    "Expressive",
    "Fidelity",
    "Content",
    "Monochromatic",
    "Rainbow",
    "Fruit Salad",
  )

fun getSchemeDescription(scheme: String): String =
  when (scheme) {
    "Tonal Spot" -> "A balanced scheme with harmonious tones"
    "Neutral" -> "Muted colors with minimal saturation for subtle themes"
    "Vibrant" -> "High saturation colors for bold, energetic themes"
    "Expressive" -> "Playful colors with varied hues for creative themes"
    "Fidelity" -> "Colors stay close to the source for brand accuracy"
    "Content" -> "Optimized for content-heavy apps with readable contrast"
    "Monochromatic" -> "Single hue with varying lightness for minimal themes"
    "Rainbow" -> "Full spectrum of colors for colorful, diverse themes"
    "Fruit Salad" -> "Warm, fruity colors for playful, organic themes"
    else -> "Select a color scheme style"
  }
