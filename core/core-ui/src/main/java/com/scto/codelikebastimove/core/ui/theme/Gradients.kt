package com.scto.codelikebastimove.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppGradients {
    
    val PrimaryGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(Purple40, Purple80),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    
    val SecondaryGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(PurpleGrey40, PurpleGrey80),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    
    val TertiaryGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(Pink40, Pink80),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    
    val SurfaceGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFBFE),
                Color(0xFFF6F2FA)
            )
        )
    
    val DarkSurfaceGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1C1B1F),
                Color(0xFF2D2B33)
            )
        )
    
    val CardGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                Color(0xFFEADDFF),
                Color(0xFFE8DEF8)
            )
        )
    
    val DarkCardGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                Color(0xFF4F378B),
                Color(0xFF4A4458)
            )
        )
    
    val AccentGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                Primary,
                Tertiary
            )
        )
    
    val SuccessGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                Color(0xFF4CAF50),
                Color(0xFF81C784)
            )
        )
    
    val WarningGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF9800),
                Color(0xFFFFB74D)
            )
        )
    
    val ErrorGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB3261E),
                Color(0xFFF2B8B5)
            )
        )
    
    @Composable
    fun shimmerGradient(
        startOffset: Float,
        colors: List<Color> = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        )
    ): Brush {
        return Brush.linearGradient(
            colors = colors,
            start = Offset(startOffset, 0f),
            end = Offset(startOffset + 1000f, 0f)
        )
    }
}
