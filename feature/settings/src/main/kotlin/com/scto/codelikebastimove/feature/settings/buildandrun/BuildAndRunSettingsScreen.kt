package com.scto.codelikebastimove.feature.settings.buildandrun

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun BuildAndRunSettingsScreen(
  viewModel: SettingsViewModel = viewModel(),
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userPreferences by viewModel.userPreferences.collectAsState()
  val buildSettings = userPreferences.buildSettings

  PreferenceLayout(
    label = stringResource(R.string.build_and_run),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = "Build") {
      SettingsToggle(
        label = "Parallel Build",
        description = "Allow Gradle to build projects in parallel",
        checked = buildSettings.parallelBuildEnabled,
        onCheckedChange = { viewModel.setParallelBuildEnabled(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )

      SettingsToggle(
        label = "Clean Before Build",
        description = "Run 'clean' task before every build",
        checked = buildSettings.cleanBeforeBuildEnabled,
        onCheckedChange = { viewModel.setCleanBeforeBuildEnabled(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )

      SettingsToggle(
        label = "Offline Mode",
        description = "Force Gradle to use cached dependencies",
        checked = buildSettings.offlineModeEnabled,
        onCheckedChange = { viewModel.setOfflineModeEnabled(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }

    PreferenceGroup(heading = "Run") {
      SettingsToggle(
        label = "Auto-Run on Build Success",
        description = "Automatically launch app after successful build",
        checked = buildSettings.autoRunEnabled,
        onCheckedChange = { viewModel.setAutoRunEnabled(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }
  }
}
