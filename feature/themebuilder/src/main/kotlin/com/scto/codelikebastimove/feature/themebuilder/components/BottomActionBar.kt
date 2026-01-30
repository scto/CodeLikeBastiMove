package com.scto.codelikebastimove.feature.themebuilder.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomActionBar(
  currentPage: Int,
  totalPages: Int,
  showExportMenu: Boolean,
  onExportMenuToggle: (Boolean) -> Unit,
  onBack: () -> Unit,
  onNext: () -> Unit,
  onExport: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "$currentPage of $totalPages",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
          onClick = onBack,
          enabled = currentPage > 1,
          modifier = Modifier.height(36.dp),
        ) {
          Text("Back", fontSize = 13.sp)
        }

        Box {
          Button(
            onClick = { if (currentPage < totalPages) onNext() else onExportMenuToggle(true) },
            modifier = Modifier.height(36.dp),
          ) {
            Text(
              if (currentPage < totalPages) "Pick your fonts" else "Export theme",
              fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector =
                if (currentPage < totalPages) Icons.AutoMirrored.Filled.ArrowForward
                else Icons.Default.ArrowDropDown,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
            )
          }

          DropdownMenu(
            expanded = showExportMenu,
            onDismissRequest = { onExportMenuToggle(false) },
          ) {
            DropdownMenuItem(
              text = { Text("Export as ZIP (Jetpack Compose)") },
              onClick = { onExport("compose") },
            )
            DropdownMenuItem(
              text = { Text("Export as ZIP (Android XML)") },
              onClick = { onExport("android") },
            )
            DropdownMenuItem(
              text = { Text("Export as ZIP (Web/CSS)") },
              onClick = { onExport("web") },
            )
            DropdownMenuItem(text = { Text("Export as JSON") }, onClick = { onExport("json") })
          }
        }
      }
    }
  }
}
