package com.scto.codelikebastimove.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.datastore.ThemeMode

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
  val userPreferences by viewModel.userPreferences.collectAsState()

  GeneralSettings(
    selectedThemeMode = userPreferences.themeMode,
    dynamicColorsEnabled = userPreferences.dynamicColorsEnabled,
    onThemeModeSelected = { viewModel.setThemeMode(it) },
    onDynamicColorsChanged = { viewModel.setDynamicColorsEnabled(it) },
  )
}

@Composable
fun GeneralSettings(
  selectedThemeMode: ThemeMode,
  dynamicColorsEnabled: Boolean,
  onThemeModeSelected: (ThemeMode) -> Unit,
  onDynamicColorsChanged: (Boolean) -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
    Text(
      text = "Allgemeine Einstellungen",
      style = MaterialTheme.typography.headlineMedium,
      color = MaterialTheme.colorScheme.onBackground,
    )

    Spacer(modifier = Modifier.height(24.dp))

    ThemeSection(selectedThemeMode = selectedThemeMode, onThemeModeSelected = onThemeModeSelected)

    Spacer(modifier = Modifier.height(24.dp))

    DynamicColorsSection(
      dynamicColorsEnabled = dynamicColorsEnabled,
      onDynamicColorsChanged = onDynamicColorsChanged,
    )
  }
}

@Composable
private fun ThemeSection(selectedThemeMode: ThemeMode, onThemeModeSelected: (ThemeMode) -> Unit) {
  Column {
    Text(
      text = "Design",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(12.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
      Column(modifier = Modifier.padding(8.dp)) {
        ThemeOption(
          icon = Icons.Default.LightMode,
          title = "Hell",
          isSelected = selectedThemeMode == ThemeMode.LIGHT,
          onClick = { onThemeModeSelected(ThemeMode.LIGHT) },
        )

        ThemeOption(
          icon = Icons.Default.DarkMode,
          title = "Dunkel",
          isSelected = selectedThemeMode == ThemeMode.DARK,
          onClick = { onThemeModeSelected(ThemeMode.DARK) },
        )

        ThemeOption(
          icon = Icons.Default.BrightnessAuto,
          title = "System folgen",
          isSelected = selectedThemeMode == ThemeMode.FOLLOW_SYSTEM,
          onClick = { onThemeModeSelected(ThemeMode.FOLLOW_SYSTEM) },
        )
      }
    }
  }
}

@Composable
private fun ThemeOption(
  icon: ImageVector,
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(24.dp),
    )

    Spacer(modifier = Modifier.width(16.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.bodyLarge,
      color =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1f),
    )

    if (isSelected) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Ausgewählt",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
private fun DynamicColorsSection(
  dynamicColorsEnabled: Boolean,
  onDynamicColorsChanged: (Boolean) -> Unit,
) {
  Column {
    Text(
      text = "Dynamische Farben",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(12.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = "Dynamische Farben",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Dynamische Farben verwenden",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = "Farben aus Hintergrundbild verwenden",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
          )
        }

        Switch(checked = dynamicColorsEnabled, onCheckedChange = onDynamicColorsChanged)
      }
    }
  }
}
