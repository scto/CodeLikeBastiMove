package com.scto.codelikebastimove.feature.themebuilder.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.themebuilder.util.availableFonts

@Composable
fun FontSelectionSection(
  displayFont: String,
  bodyFont: String,
  onDisplayFontChange: (String) -> Unit,
  onBodyFontChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
    Text(
      text = "Choose fonts",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Medium,
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Display, headlines, & titles",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
      )

      Text(
        text =
          "As the largest text on the screen, these styles are reserved for short, important text, and high-emphasis text.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      FontDropdown(selectedFont = displayFont, onFontSelected = onDisplayFontChange)
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = "Body & Labels",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
      )

      Text(
        text =
          "Typefaces intended for body and label which are readable at smaller sizes and comfortably read in longer passages.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      FontDropdown(selectedFont = bodyFont, onFontSelected = onBodyFontChange)
    }
  }
}

@Composable
fun FontDropdown(
  selectedFont: String,
  onFontSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = selectedFont, fontSize = 14.sp)
        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
      }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      availableFonts.forEach { font ->
        DropdownMenuItem(
          text = { Text(font) },
          onClick = {
            onFontSelected(font)
            expanded = false
          },
        )
      }
    }
  }
}
