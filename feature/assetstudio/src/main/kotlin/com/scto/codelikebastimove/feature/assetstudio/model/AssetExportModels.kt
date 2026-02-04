package com.scto.codelikebastimove.feature.assetstudio.model

import androidx.compose.ui.graphics.Color

enum class AssetType(val displayName: String) {
    VECTOR_DRAWABLE("Vector Drawable"),
    LAUNCHER_ICON("Launcher Icon"),
    ADAPTIVE_ICON("Adaptive Icon"),
    NOTIFICATION_ICON("Notification Icon"),
    ACTION_BAR_ICON("Action Bar Icon"),
    TAB_ICON("Tab Icon"),
}

enum class IconDensity(
    val qualifier: String,
    val scale: Float,
    val launcherIconSize: Int,
) {
    MDPI("mdpi", 1.0f, 48),
    HDPI("hdpi", 1.5f, 72),
    XHDPI("xhdpi", 2.0f, 96),
    XXHDPI("xxhdpi", 3.0f, 144),
    XXXHDPI("xxxhdpi", 4.0f, 192),
}

data class DensityVariant(
    val density: IconDensity,
    val sizeDp: Int,
    val enabled: Boolean = true,
)

data class LauncherIconConfig(
    val name: String = "ic_launcher",
    val foregroundDocument: AVDDocument? = null,
    val backgroundColor: Color = Color.White,
    val foregroundScale: Float = 1.0f,
    val shape: IconShape = IconShape.ADAPTIVE,
    val generateRound: Boolean = true,
    val legacyIconSize: Int = 48,
)

data class AdaptiveIconConfig(
    val name: String = "ic_launcher",
    val foregroundDocument: AVDDocument? = null,
    val backgroundDocument: AVDDocument? = null,
    val backgroundColor: Color = Color.White,
    val foregroundScale: Float = 0.6f,
    val foregroundPadding: Int = 18,
    val generateRound: Boolean = true,
    val generateMonochrome: Boolean = true,
)

enum class IconShape(val displayName: String) {
    ADAPTIVE("Adaptive"),
    CIRCLE("Circle"),
    ROUNDED_SQUARE("Rounded Square"),
    SQUIRCLE("Squircle"),
    SQUARE("Square"),
}

data class NotificationIconConfig(
    val name: String = "ic_notification",
    val document: AVDDocument? = null,
    val size: Int = 24,
)

data class ActionBarIconConfig(
    val name: String = "ic_action",
    val document: AVDDocument? = null,
    val size: Int = 24,
    val tintColor: Color = Color.White,
)
