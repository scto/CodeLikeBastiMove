package com.scto.codelikebastimove.feature.buildvariants

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BuildVariantsScreen(
  projectPath: String,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var selectedModule by remember { mutableStateOf("app") }
  val modules = listOf("app", "core:core-ui", "core:core-datastore", "features:feature-main")
  val variants = listOf("debug", "release")
  var selectedVariant by remember { mutableStateOf("debug") }

  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Build Varianten",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Text(
      text = "Modul auswählen",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      modules.forEach { module ->
        FilterChip(
          selected = selectedModule == module,
          onClick = { selectedModule = module },
          label = { Text(module, fontSize = 11.sp) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }

    Text(
      text = "Variante",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      variants.forEach { variant ->
        FilterChip(
          selected = selectedVariant == variant,
          onClick = { selectedVariant = variant },
          label = { Text(variant) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
      Icon(Icons.Default.PlayArrow, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Build starten")
    }
  }
}
