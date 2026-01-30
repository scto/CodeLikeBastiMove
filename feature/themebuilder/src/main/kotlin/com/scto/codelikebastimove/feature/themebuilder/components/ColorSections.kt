package com.scto.codelikebastimove.feature.themebuilder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.themebuilder.model.ThemeColors
import com.scto.codelikebastimove.feature.themebuilder.util.seedColors

@Composable
fun ColorSchemePreviewSection(
  title: String,
  isDark: Boolean,
  themeColors: ThemeColors,
  onColorClick: (String, Color) -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor = if (isDark) Color(0xFF1C1B1F) else themeColors.background
  val textColor = if (isDark) Color.White else Color.Black

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = textColor,
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorChip("Primary", themeColors.primary, Modifier.weight(1f)) {
          onColorClick("Primary", themeColors.primary)
        }
        ColorChip("Secondary", themeColors.secondary, Modifier.weight(1f)) {
          onColorClick("Secondary", themeColors.secondary)
        }
        ColorChip("Tertiary", themeColors.tertiary, Modifier.weight(1f)) {
          onColorClick("Tertiary", themeColors.tertiary)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorChip("Primary Container", themeColors.primaryContainer, Modifier.weight(1f)) {
          onColorClick("Primary Container", themeColors.primaryContainer)
        }
        ColorChip("Secondary Container", themeColors.secondaryContainer, Modifier.weight(1f)) {
          onColorClick("Secondary Container", themeColors.secondaryContainer)
        }
        ColorChip("Tertiary Container", themeColors.tertiaryContainer, Modifier.weight(1f)) {
          onColorClick("Tertiary Container", themeColors.tertiaryContainer)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorChip("Surface", themeColors.surface, Modifier.weight(1f)) {
          onColorClick("Surface", themeColors.surface)
        }
        ColorChip("Error", themeColors.error, Modifier.weight(1f)) {
          onColorClick("Error", themeColors.error)
        }
      }
    }
  }
}

@Composable
fun ColorChip(name: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Column(
    modifier =
      modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable(onClick = onClick)
        .background(color)
        .padding(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = name,
      fontSize = 9.sp,
      color = if (color.luminance() > 0.5f) Color.Black else Color.White,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      lineHeight = 10.sp,
    )
  }
}

private fun Color.luminance(): Float {
  val r = red
  val g = green
  val b = blue
  return 0.299f * r + 0.587f * g + 0.114f * b
}

@Composable
fun TonalPaletteSection(themeColors: ThemeColors, modifier: Modifier = Modifier) {
  val tones = listOf(100, 99, 95, 90, 80, 70, 60, 50, 40, 35, 30, 25, 20, 15, 10, 5, 0)

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    TonalPaletteRow("Primary", themeColors.primary, tones)
    TonalPaletteRow("Secondary", themeColors.secondary, tones)
    TonalPaletteRow("Tertiary", themeColors.tertiary, tones)
    TonalPaletteRow("Neutral", Color(0xFF9E9E9E), tones)
    TonalPaletteRow("Neutral Variant", Color(0xFF8D8D8D), tones)
  }
}

@Composable
fun TonalPaletteRow(
  name: String,
  baseColor: Color,
  tones: List<Int>,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      text = name,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 10.sp,
    )

    Spacer(modifier = Modifier.height(2.dp))

    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
      tones.forEach { tone ->
        val toneColor =
          baseColor.copy(
            red = (baseColor.red * tone / 100f).coerceIn(0f, 1f),
            green = (baseColor.green * tone / 100f).coerceIn(0f, 1f),
            blue = (baseColor.blue * tone / 100f).coerceIn(0f, 1f),
          )

        Box(
          modifier = Modifier.size(20.dp, 28.dp).background(toneColor),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = tone.toString(),
            fontSize = 5.sp,
            color = if (tone > 50) Color.Black else Color.White,
          )
        }
      }
    }
  }
}

@Composable
fun SeedColorRow(
  themeColors: ThemeColors,
  onColorSelected: (Color) -> Unit,
  onPickColor: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors =
      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      seedColors.forEach { color ->
        Box(
          modifier =
            Modifier.size(44.dp)
              .clip(CircleShape)
              .background(color)
              .border(
                width = if (color == themeColors.primary) 3.dp else 1.dp,
                color =
                  if (color == themeColors.primary) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
              )
              .clickable { onColorSelected(color) }
        )
      }

      Box(
        modifier =
          Modifier.size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(onClick = onPickColor),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Outlined.Colorize,
          contentDescription = "Pick color",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}
