package com.scto.codelikebastimove.feature.themebuilder.generator

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

data class TonalPalette(
    val tone0: Color,
    val tone10: Color,
    val tone20: Color,
    val tone30: Color,
    val tone40: Color,
    val tone50: Color,
    val tone60: Color,
    val tone70: Color,
    val tone80: Color,
    val tone90: Color,
    val tone95: Color,
    val tone99: Color,
    val tone100: Color,
) {
    fun tone(value: Int): Color {
        return when (value) {
            0 -> tone0
            10 -> tone10
            20 -> tone20
            30 -> tone30
            40 -> tone40
            50 -> tone50
            60 -> tone60
            70 -> tone70
            80 -> tone80
            90 -> tone90
            95 -> tone95
            99 -> tone99
            100 -> tone100
            else -> interpolateTone(value)
        }
    }

    private fun interpolateTone(value: Int): Color {
        val tones = listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 95, 99, 100)
        val colors = listOf(tone0, tone10, tone20, tone30, tone40, tone50, tone60, tone70, tone80, tone90, tone95, tone99, tone100)

        val lowerIndex = tones.indexOfLast { it <= value }.coerceAtLeast(0)
        val upperIndex = tones.indexOfFirst { it >= value }.coerceAtMost(tones.size - 1)

        if (lowerIndex == upperIndex) return colors[lowerIndex]

        val lowerTone = tones[lowerIndex]
        val upperTone = tones[upperIndex]
        val ratio = (value - lowerTone).toFloat() / (upperTone - lowerTone)

        return lerpColor(colors[lowerIndex], colors[upperIndex], ratio)
    }

    private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
        return Color(
            red = start.red + (end.red - start.red) * fraction,
            green = start.green + (end.green - start.green) * fraction,
            blue = start.blue + (end.blue - start.blue) * fraction,
            alpha = 1f,
        )
    }
}

data class Material3ColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val scrim: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val surfaceDim: Color,
    val surfaceBright: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
)

data class GeneratedTheme(
    val seedColor: Color,
    val lightScheme: Material3ColorScheme,
    val darkScheme: Material3ColorScheme,
    val primaryPalette: TonalPalette,
    val secondaryPalette: TonalPalette,
    val tertiaryPalette: TonalPalette,
    val neutralPalette: TonalPalette,
    val neutralVariantPalette: TonalPalette,
    val errorPalette: TonalPalette,
)

enum class SchemeStyle(val displayName: String) {
    TONAL_SPOT("Tonal Spot"),
    VIBRANT("Vibrant"),
    EXPRESSIVE("Expressive"),
    FIDELITY("Fidelity"),
    MONOCHROME("Monochrome"),
    NEUTRAL("Neutral"),
    CONTENT("Content"),
}

class MaterialColorGenerator {

    fun generateTheme(seedColor: Color, style: SchemeStyle = SchemeStyle.TONAL_SPOT): GeneratedTheme {
        val hct = rgbToHct(seedColor)
        val hue = hct[0]
        val chroma = hct[1]

        val primaryPalette = generateTonalPalette(hue, adjustChroma(chroma, style, isPrimary = true))
        val secondaryPalette = generateTonalPalette(hue, adjustChroma(chroma * 0.33f, style, isPrimary = false))
        val tertiaryPalette = generateTonalPalette(hue + 60f, adjustChroma(chroma * 0.5f, style, isPrimary = false))
        val neutralPalette = generateTonalPalette(hue, adjustChroma(chroma * 0.04f, style, isPrimary = false))
        val neutralVariantPalette = generateTonalPalette(hue, adjustChroma(chroma * 0.08f, style, isPrimary = false))
        val errorPalette = generateTonalPalette(25f, 84f)

        val lightScheme = generateLightScheme(
            primaryPalette, secondaryPalette, tertiaryPalette,
            neutralPalette, neutralVariantPalette, errorPalette
        )

        val darkScheme = generateDarkScheme(
            primaryPalette, secondaryPalette, tertiaryPalette,
            neutralPalette, neutralVariantPalette, errorPalette
        )

        return GeneratedTheme(
            seedColor = seedColor,
            lightScheme = lightScheme,
            darkScheme = darkScheme,
            primaryPalette = primaryPalette,
            secondaryPalette = secondaryPalette,
            tertiaryPalette = tertiaryPalette,
            neutralPalette = neutralPalette,
            neutralVariantPalette = neutralVariantPalette,
            errorPalette = errorPalette,
        )
    }

    private fun adjustChroma(baseChroma: Float, style: SchemeStyle, isPrimary: Boolean): Float {
        return when (style) {
            SchemeStyle.VIBRANT -> if (isPrimary) max(baseChroma, 48f) else baseChroma * 1.2f
            SchemeStyle.EXPRESSIVE -> if (isPrimary) max(baseChroma, 64f) else baseChroma * 1.5f
            SchemeStyle.FIDELITY -> baseChroma
            SchemeStyle.MONOCHROME -> 0f
            SchemeStyle.NEUTRAL -> baseChroma * 0.12f
            SchemeStyle.CONTENT -> baseChroma * 0.8f
            SchemeStyle.TONAL_SPOT -> if (isPrimary) max(baseChroma, 36f) else baseChroma
        }
    }

    private fun generateTonalPalette(hue: Float, chroma: Float): TonalPalette {
        return TonalPalette(
            tone0 = hctToRgb(hue, chroma, 0f),
            tone10 = hctToRgb(hue, chroma, 10f),
            tone20 = hctToRgb(hue, chroma, 20f),
            tone30 = hctToRgb(hue, chroma, 30f),
            tone40 = hctToRgb(hue, chroma, 40f),
            tone50 = hctToRgb(hue, chroma, 50f),
            tone60 = hctToRgb(hue, chroma, 60f),
            tone70 = hctToRgb(hue, chroma, 70f),
            tone80 = hctToRgb(hue, chroma, 80f),
            tone90 = hctToRgb(hue, chroma, 90f),
            tone95 = hctToRgb(hue, chroma, 95f),
            tone99 = hctToRgb(hue, chroma, 99f),
            tone100 = hctToRgb(hue, chroma, 100f),
        )
    }

    private fun generateLightScheme(
        primary: TonalPalette,
        secondary: TonalPalette,
        tertiary: TonalPalette,
        neutral: TonalPalette,
        neutralVariant: TonalPalette,
        error: TonalPalette,
    ): Material3ColorScheme {
        return Material3ColorScheme(
            primary = primary.tone40,
            onPrimary = primary.tone100,
            primaryContainer = primary.tone90,
            onPrimaryContainer = primary.tone10,
            secondary = secondary.tone40,
            onSecondary = secondary.tone100,
            secondaryContainer = secondary.tone90,
            onSecondaryContainer = secondary.tone10,
            tertiary = tertiary.tone40,
            onTertiary = tertiary.tone100,
            tertiaryContainer = tertiary.tone90,
            onTertiaryContainer = tertiary.tone10,
            error = error.tone40,
            onError = error.tone100,
            errorContainer = error.tone90,
            onErrorContainer = error.tone10,
            background = neutral.tone99,
            onBackground = neutral.tone10,
            surface = neutral.tone99,
            onSurface = neutral.tone10,
            surfaceVariant = neutralVariant.tone90,
            onSurfaceVariant = neutralVariant.tone30,
            outline = neutralVariant.tone50,
            outlineVariant = neutralVariant.tone80,
            scrim = neutral.tone0,
            inverseSurface = neutral.tone20,
            inverseOnSurface = neutral.tone95,
            inversePrimary = primary.tone80,
            surfaceDim = neutral.tone(87),
            surfaceBright = neutral.tone(98),
            surfaceContainerLowest = neutral.tone100,
            surfaceContainerLow = neutral.tone(96),
            surfaceContainer = neutral.tone(94),
            surfaceContainerHigh = neutral.tone(92),
            surfaceContainerHighest = neutral.tone90,
        )
    }

    private fun generateDarkScheme(
        primary: TonalPalette,
        secondary: TonalPalette,
        tertiary: TonalPalette,
        neutral: TonalPalette,
        neutralVariant: TonalPalette,
        error: TonalPalette,
    ): Material3ColorScheme {
        return Material3ColorScheme(
            primary = primary.tone80,
            onPrimary = primary.tone20,
            primaryContainer = primary.tone30,
            onPrimaryContainer = primary.tone90,
            secondary = secondary.tone80,
            onSecondary = secondary.tone20,
            secondaryContainer = secondary.tone30,
            onSecondaryContainer = secondary.tone90,
            tertiary = tertiary.tone80,
            onTertiary = tertiary.tone20,
            tertiaryContainer = tertiary.tone30,
            onTertiaryContainer = tertiary.tone90,
            error = error.tone80,
            onError = error.tone20,
            errorContainer = error.tone30,
            onErrorContainer = error.tone90,
            background = neutral.tone10,
            onBackground = neutral.tone90,
            surface = neutral.tone10,
            onSurface = neutral.tone90,
            surfaceVariant = neutralVariant.tone30,
            onSurfaceVariant = neutralVariant.tone80,
            outline = neutralVariant.tone60,
            outlineVariant = neutralVariant.tone30,
            scrim = neutral.tone0,
            inverseSurface = neutral.tone90,
            inverseOnSurface = neutral.tone20,
            inversePrimary = primary.tone40,
            surfaceDim = neutral.tone(6),
            surfaceBright = neutral.tone(24),
            surfaceContainerLowest = neutral.tone(4),
            surfaceContainerLow = neutral.tone10,
            surfaceContainer = neutral.tone(12),
            surfaceContainerHigh = neutral.tone(17),
            surfaceContainerHighest = neutral.tone(22),
        )
    }

    private fun rgbToHct(color: Color): FloatArray {
        val r = color.red
        val g = color.green
        val b = color.blue

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        var hue = when {
            delta == 0f -> 0f
            max == r -> 60f * (((g - b) / delta) % 6)
            max == g -> 60f * (((b - r) / delta) + 2)
            else -> 60f * (((r - g) / delta) + 4)
        }

        if (hue < 0) hue += 360f

        val lightness = (max + min) / 2f * 100f

        val chroma = if (delta == 0f) 0f else {
            delta / (1 - abs(2 * lightness / 100f - 1)) * 100f
        }

        return floatArrayOf(hue, chroma.coerceIn(0f, 120f), lightness)
    }

    private fun hctToRgb(hue: Float, chroma: Float, tone: Float): Color {
        val l = tone / 100f
        val c = chroma / 100f * min(l, 1 - l)

        fun f(n: Float): Float {
            val k = (n + hue / 30f) % 12
            return l - c * maxOf(-1f, minOf(k - 3, 9 - k, 1f))
        }

        return Color(
            red = f(0f).coerceIn(0f, 1f),
            green = f(8f).coerceIn(0f, 1f),
            blue = f(4f).coerceIn(0f, 1f),
        )
    }

    fun extractColorsFromImage(pixels: IntArray, width: Int, height: Int): List<Color> {
        val colorCounts = mutableMapOf<Int, Int>()

        val step = maxOf(1, (width * height) / 10000)
        for (i in pixels.indices step step) {
            val pixel = pixels[i]
            val quantized = quantizeColor(pixel)
            colorCounts[quantized] = (colorCounts[quantized] ?: 0) + 1
        }

        return colorCounts.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { entry ->
                Color(
                    red = ((entry.key shr 16) and 0xFF) / 255f,
                    green = ((entry.key shr 8) and 0xFF) / 255f,
                    blue = (entry.key and 0xFF) / 255f,
                )
            }
    }

    private fun quantizeColor(argb: Int): Int {
        val r = ((argb shr 16) and 0xFF) / 32 * 32
        val g = ((argb shr 8) and 0xFF) / 32 * 32
        val b = (argb and 0xFF) / 32 * 32
        return (r shl 16) or (g shl 8) or b
    }
}
