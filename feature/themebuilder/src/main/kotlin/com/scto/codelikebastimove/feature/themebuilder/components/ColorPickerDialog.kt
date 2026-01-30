package com.scto.codelikebastimove.feature.themebuilder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.themebuilder.util.colorToHex
import com.scto.codelikebastimove.feature.themebuilder.util.colorToHsl
import com.scto.codelikebastimove.feature.themebuilder.util.presetColors

@Composable
fun ColorPickerDialog(
  colorName: String,
  initialColor: Color,
  onDismiss: () -> Unit,
  onColorSelected: (Color) -> Unit,
) {
  val initialHsl = colorToHsl(initialColor)
  var hue by remember { mutableFloatStateOf(initialHsl.first) }
  var saturation by remember { mutableFloatStateOf(initialHsl.second) }
  var lightness by remember { mutableFloatStateOf(initialHsl.third) }
  var hexInput by remember { mutableStateOf(colorToHex(initialColor)) }

  val currentColor = Color.hsl(hue, saturation, lightness)

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = "Edit $colorName", style = MaterialTheme.typography.titleMedium) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .height(60.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(currentColor)
              .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
        )

        Column {
          Text("Hue: ${hue.toInt()}°", fontSize = 12.sp)
          Slider(
            value = hue,
            onValueChange = {
              hue = it
              hexInput = colorToHex(Color.hsl(hue, saturation, lightness))
            },
            valueRange = 0f..360f,
            colors =
              SliderDefaults.colors(thumbColor = currentColor, activeTrackColor = currentColor),
          )
        }

        Column {
          Text("Saturation: ${(saturation * 100).toInt()}%", fontSize = 12.sp)
          Slider(
            value = saturation,
            onValueChange = {
              saturation = it
              hexInput = colorToHex(Color.hsl(hue, saturation, lightness))
            },
            valueRange = 0f..1f,
          )
        }

        Column {
          Text("Lightness: ${(lightness * 100).toInt()}%", fontSize = 12.sp)
          Slider(
            value = lightness,
            onValueChange = {
              lightness = it
              hexInput = colorToHex(Color.hsl(hue, saturation, lightness))
            },
            valueRange = 0f..1f,
          )
        }

        OutlinedTextField(
          value = hexInput,
          onValueChange = { input ->
            hexInput = input
            if (input.length == 7 && input.startsWith("#")) {
              try {
                val parsed = android.graphics.Color.parseColor(input)
                val r = android.graphics.Color.red(parsed) / 255f
                val g = android.graphics.Color.green(parsed) / 255f
                val b = android.graphics.Color.blue(parsed) / 255f
                val max = maxOf(r, g, b)
                val min = minOf(r, g, b)
                lightness = (max + min) / 2f
                saturation =
                  if (max == min) 0f
                  else {
                    if (lightness > 0.5f) (max - min) / (2f - max - min)
                    else (max - min) / (max + min)
                  }
                hue =
                  when {
                    max == min -> 0f
                    max == r -> (60f * ((g - b) / (max - min)) + 360f) % 360f
                    max == g -> 60f * ((b - r) / (max - min)) + 120f
                    else -> 60f * ((r - g) / (max - min)) + 240f
                  }
              } catch (_: Exception) {}
            }
          },
          label = { Text("Hex Color") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(presetColors) { color ->
            Box(
              modifier =
                Modifier.size(40.dp)
                  .clip(CircleShape)
                  .background(color)
                  .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                  .clickable {
                    hexInput = colorToHex(color)
                    val hsl = colorToHsl(color)
                    hue = hsl.first
                    saturation = hsl.second
                    lightness = hsl.third
                  }
            )
          }
        }
      }
    },
    confirmButton = { Button(onClick = { onColorSelected(currentColor) }) { Text("Apply") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}
