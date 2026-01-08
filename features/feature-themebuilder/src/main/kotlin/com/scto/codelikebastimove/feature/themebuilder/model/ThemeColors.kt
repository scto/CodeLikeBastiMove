package com.scto.codelikebastimove.feature.themebuilder.model

import androidx.compose.ui.graphics.Color

data class ThemeColors(
    val primary: Color = Color(0xFFD4C4A8),
    val secondary: Color = Color(0xFFB8977E),
    val tertiary: Color = Color(0xFF8B7355),
    val error: Color = Color(0xFFB3261E),
    val primaryContainer: Color = Color(0xFFF5E6D3),
    val secondaryContainer: Color = Color(0xFFE8D5C4),
    val tertiaryContainer: Color = Color(0xFFD4C4B4),
    val errorContainer: Color = Color(0xFFF9DEDC),
    val surface: Color = Color(0xFFFFFBFE),
    val surfaceVariant: Color = Color(0xFFE7E0EC),
    val background: Color = Color(0xFFFFFBFE),
    val outline: Color = Color(0xFF79747E)
)
