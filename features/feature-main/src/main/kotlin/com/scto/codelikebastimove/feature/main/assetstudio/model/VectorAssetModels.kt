package com.scto.codelikebastimove.feature.main.assetstudio.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class VectorAsset(
    val id: String,
    val name: String,
    val category: String,
    val tags: List<String> = emptyList(),
    val svgContent: String? = null,
    val avdContent: String? = null,
    val imageVector: ImageVector? = null,
    val provider: IconProvider,
    val downloadUrl: String? = null,
    val previewColor: Color = Color.Black
)

data class VectorPath(
    val id: String,
    val pathData: String,
    val fillColor: Color = Color.Black,
    val strokeColor: Color? = null,
    val strokeWidth: Float = 0f,
    val fillAlpha: Float = 1f,
    val strokeAlpha: Float = 1f,
    val trimPathStart: Float = 0f,
    val trimPathEnd: Float = 1f,
    val trimPathOffset: Float = 0f
)

data class VectorGroup(
    val id: String,
    val name: String = "",
    val rotation: Float = 0f,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
    val paths: List<VectorPath> = emptyList(),
    val groups: List<VectorGroup> = emptyList()
)

data class AVDDocument(
    val name: String,
    val width: Float = 24f,
    val height: Float = 24f,
    val viewportWidth: Float = 24f,
    val viewportHeight: Float = 24f,
    val tint: Color? = null,
    val tintMode: String = "src_atop",
    val autoMirrored: Boolean = false,
    val rootGroup: VectorGroup = VectorGroup(id = "root")
)

enum class IconProvider(
    val displayName: String,
    val baseUrl: String,
    val supportsDownload: Boolean = true
) {
    MATERIAL_ICONS("Material Icons", "https://fonts.google.com/icons", true),
    MATERIAL_SYMBOLS("Material Symbols", "https://fonts.google.com/icons", true),
    FONT_AWESOME("Font Awesome", "https://fontawesome.com", true),
    FEATHER_ICONS("Feather Icons", "https://feathericons.com", true),
    HEROICONS("Heroicons", "https://heroicons.com", true),
    TABLER_ICONS("Tabler Icons", "https://tabler-icons.io", true),
    PHOSPHOR_ICONS("Phosphor Icons", "https://phosphoricons.com", true),
    LUCIDE_ICONS("Lucide Icons", "https://lucide.dev", true),
    LOCAL("Local Assets", "", false)
}

enum class IconStyle(val displayName: String) {
    FILLED("Filled"),
    OUTLINED("Outlined"),
    ROUNDED("Rounded"),
    SHARP("Sharp"),
    TWO_TONE("Two Tone")
}

data class IconCategory(
    val id: String,
    val name: String,
    val iconCount: Int = 0
)

data class IconSearchResult(
    val icons: List<VectorAsset>,
    val totalCount: Int,
    val hasMore: Boolean,
    val nextPage: Int? = null
)

data class ExportConfig(
    val format: ExportFormat,
    val size: Int = 24,
    val color: Color = Color.Black,
    val includeDensities: Boolean = true,
    val densities: List<String> = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"),
    val outputPath: String = ""
)

enum class ExportFormat(val displayName: String, val extension: String) {
    AVD_XML("Android Vector Drawable (XML)", "xml"),
    SVG("SVG", "svg"),
    PNG("PNG", "png"),
    WEBP("WebP", "webp"),
    COMPOSE_ICON("Jetpack Compose ImageVector", "kt")
}
