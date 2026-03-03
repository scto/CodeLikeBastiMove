package com.scto.codelikebastimove.feature.settings.appearance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.settings.SettingsViewModel
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun AppearanceSettingsScreen(
  viewModel: SettingsViewModel = viewModel(),
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userPreferences by viewModel.userPreferences.collectAsState()
  val selectedThemeMode = userPreferences.themeMode
  val dynamicColorsEnabled = userPreferences.dynamicColorsEnabled

  var showThemeDialog by remember { mutableStateOf(false) }

  val themeName = when (selectedThemeMode) {
    ThemeMode.LIGHT -> "Hell"
    ThemeMode.DARK -> "Dunkel"
    ThemeMode.FOLLOW_SYSTEM -> "System"
  }

  val themeIcon = when (selectedThemeMode) {
    ThemeMode.LIGHT -> Icons.Default.LightMode
    ThemeMode.DARK -> Icons.Default.DarkMode
    ThemeMode.FOLLOW_SYSTEM -> Icons.Default.Settings
  }

  PreferenceLayout(
    label = stringResource(R.string.appearance),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = stringResource(R.string.settings_theme)) {
      SettingsToggle(
        label = "Design",
        description = themeName,
        showSwitch = false,
        onClick = { showThemeDialog = true },
        startWidget = {
          Icon(
            imageVector = themeIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }

    PreferenceGroup(heading = stringResource(R.string.settings_appearance)) {
      SettingsToggle(
        label = stringResource(R.string.settings_dynamic_colors),
        description = stringResource(R.string.settings_dynamic_colors_subtitle),
        checked = dynamicColorsEnabled,
        onCheckedChange = { viewModel.setDynamicColorsEnabled(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }
  }

  if (showThemeDialog) {
    ThemeSelectionDialog(
      currentTheme = selectedThemeMode,
      onThemeSelected = { theme ->
        viewModel.setThemeMode(theme)
        showThemeDialog = false
      },
      onDismiss = { showThemeDialog = false },
    )
  }
}

@Composable
private fun ThemeSelectionDialog(
  currentTheme: ThemeMode,
  onThemeSelected: (ThemeMode) -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Design auswählen") },
    text = {
      Column {
        ThemeOption(
          name = "Hell",
          icon = Icons.Default.LightMode,
          isSelected = currentTheme == ThemeMode.LIGHT,
          onClick = { onThemeSelected(ThemeMode.LIGHT) },
        )

        ThemeOption(
          name = "Dunkel",
          icon = Icons.Default.DarkMode,
          isSelected = currentTheme == ThemeMode.DARK,
          onClick = { onThemeSelected(ThemeMode.DARK) },
        )

        ThemeOption(
          name = "System (automatisch)",
          icon = Icons.Default.Settings,
          isSelected = currentTheme == ThemeMode.FOLLOW_SYSTEM,
          onClick = { onThemeSelected(ThemeMode.FOLLOW_SYSTEM) },
        )
      }
    },
    confirmButton = { TextButton(onClick = onDismiss) { Text("Schließen") } },
  )
}

@Composable
private fun ThemeOption(
  name: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(selected = isSelected, onClick = onClick)

    Spacer(modifier = Modifier.width(8.dp))

    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (isSelected) MaterialTheme.colorScheme.primary
      else MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = name,
      style = MaterialTheme.typography.bodyLarge,
      color = if (isSelected) MaterialTheme.colorScheme.primary
      else MaterialTheme.colorScheme.onSurface,
    )
  }
}
