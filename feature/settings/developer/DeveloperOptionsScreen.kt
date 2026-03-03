package com.scto.codelikebastimove.feature.settings.developer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.settings.SettingsViewModel
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun DeveloperOptionsScreen(
  viewModel: SettingsViewModel = viewModel(),
  onBackClick: () -> Unit,
  onNavigateToDebugSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userPreferences by viewModel.userPreferences.collectAsState()
  val loggingEnabled = userPreferences.loggingEnabled

  var showResetOnboardingDialog by remember { mutableStateOf(false) }

  PreferenceLayout(
    label = "Entwickleroptionen",
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = stringResource(R.string.settings_debug_logging)) {
      SettingsToggle(
        label = stringResource(R.string.debug_logging),
        description = stringResource(R.string.debug_logging_description),
        checked = loggingEnabled,
        onCheckedChange = { viewModel.setLoggingEnabled(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.BugReport,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }

    PreferenceGroup(heading = "Experimentelle Funktionen") {
      NavigationItem(
        label = "Debugging Einstellungen",
        description = "Configure various debugging options",
        onClick = onNavigateToDebugSettings,
      )

      HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

      SettingsToggle(
        label = "Onboarding zurücksetzen",
        description = "Zeigt den Einrichtungsassistenten beim nächsten Start",
        showSwitch = false,
        onClick = { showResetOnboardingDialog = true },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }
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
private fun NavigationItem(
  label: String,
  description: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  SettingsToggle(
    label = label,
    description = description,
    showSwitch = false,
    onClick = onClick,
    modifier = modifier,
    endWidget = {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(24.dp),
      )
    },
  )
}
