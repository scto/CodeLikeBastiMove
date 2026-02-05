package com.scto.codelikebastimove.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDESettingsScreen(
  viewModel: SettingsViewModel = viewModel(),
  onBackClick: () -> Unit,
  onNavigateToGeneral: () -> Unit,
  onNavigateToEditorSettings: () -> Unit,
  onNavigateToAIAgent: () -> Unit,
  onNavigateToBuildAndRun: () -> Unit,
  onNavigateToTermux: () -> Unit,
  onNavigateToStatistics: () -> Unit,
  onNavigateToDeveloperOptions: () -> Unit,
  onNavigateToAbout: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userPreferences by viewModel.userPreferences.collectAsState()

  var showThemeDialog by remember { mutableStateOf(false) }
  var showResetOnboardingDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      AdaptiveTopAppBar(
        title = stringResource(R.string.ide_settings),
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.statusBarsPadding(),
      )
    },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    modifier = modifier,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
    ) {
      SettingsCategoryHeader(title = stringResource(R.string.appearance))

      ThemeSettingRow(
        currentTheme = userPreferences.themeMode,
        onClick = { showThemeDialog = true },
      )

      ToggleSettingRow(
        title = stringResource(R.string.dynamic_colors),
        description = stringResource(R.string.dynamic_colors_summary_enabled),
        icon = Icons.Default.Palette,
        isEnabled = userPreferences.dynamicColorsEnabled,
        onToggle = {
          viewModel.setDynamicColorsEnabled(!userPreferences.dynamicColorsEnabled)
        },
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = stringResource(R.string.configure))

      SettingsItemRow(
        title = stringResource(R.string.general_settings),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToGeneral,
      )

      SettingsItemRow(
        title = stringResource(R.string.editor),
        description = stringResource(R.string.pref_configure_editor_summary),
        onClick = onNavigateToEditorSettings,
      )

      SettingsItemRow(
        title = stringResource(R.string.ai_agent),
        description = stringResource(R.string.pref_configure_editor_summary),
        onClick = onNavigateToAIAgent,
      )

      SettingsItemRow(
        title = stringResource(R.string.build_and_run),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToBuildAndRun,
      )

      SettingsItemRow(
        title = stringResource(R.string.termux),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToTermux,
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = stringResource(R.string.privacy))

      SettingsItemRow(
        title = stringResource(R.string.clbm_statistics),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToStatistics,
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = stringResource(R.string.developer_options))

      ToggleSettingRow(
        title = stringResource(R.string.debug_logging),
        description = stringResource(R.string.debug_logging_description),
        icon = Icons.Default.BugReport,
        isEnabled = userPreferences.loggingEnabled,
        onToggle = {
          viewModel.setLoggingEnabled(!userPreferences.loggingEnabled)
        },
      )

      SettingsItemRow(
        title = "Entwickleroptionen",
        description = "Experimentelle/Debugging Optionen für CLBM",
        onClick = onNavigateToDeveloperOptions,
      )

      SettingsItemRow(
        title = "Onboarding zurücksetzen",
        description = "Zeigt den Einrichtungsassistenten beim nächsten Start",
        onClick = { showResetOnboardingDialog = true },
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = "Über")

      SettingsItemRow(
        title = "Über",
        description = "App-Version und Informationen",
        onClick = onNavigateToAbout,
      )
    }
  }

  if (showThemeDialog) {
    ThemeSelectionDialog(
      currentTheme = userPreferences.themeMode,
      onThemeSelected = { theme ->
        viewModel.setThemeMode(theme)
        showThemeDialog = false
      },
      onDismiss = { showThemeDialog = false },
    )
  }

  if (showResetOnboardingDialog) {
    AlertDialog(
      onDismissRequest = { showResetOnboardingDialog = false },
      title = { Text("Onboarding zurücksetzen?") },
      text = {
        Text(
          "Der Einrichtungsassistent wird beim nächsten Start der App angezeigt. Berechtigungen werden erneut geprüft."
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.resetOnboarding()
            showResetOnboardingDialog = false
          }
        ) {
          Text("Zurücksetzen")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetOnboardingDialog = false }) { Text("Abbrechen") }
      },
    )
  }
}

@Composable
private fun SettingsCategoryHeader(title: String, modifier: Modifier = Modifier) {
  Text(
    text = title,
    style = MaterialTheme.typography.labelLarge,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Medium,
    modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
  )
}

@Composable
private fun SettingsItemRow(
  title: String,
  description: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onBackground,
    )

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
    )
  }
}

@Composable
private fun ThemeSettingRow(
  currentTheme: ThemeMode,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val themeName = when (currentTheme) {
    ThemeMode.LIGHT -> "Hell"
    ThemeMode.DARK -> "Dunkel"
    ThemeMode.FOLLOW_SYSTEM -> "System"
  }

  val themeIcon = when (currentTheme) {
    ThemeMode.LIGHT -> Icons.Default.LightMode
    ThemeMode.DARK -> Icons.Default.DarkMode
    ThemeMode.FOLLOW_SYSTEM -> Icons.Default.Settings
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = themeIcon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(24.dp),
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "Design",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = themeName,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      )
    }
  }
}

@Composable
private fun ToggleSettingRow(
  title: String,
  description: String,
  icon: ImageVector,
  isEnabled: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onToggle)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(24.dp),
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      )
    }

    Switch(checked = isEnabled, onCheckedChange = { onToggle() })
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
