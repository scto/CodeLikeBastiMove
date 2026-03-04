package com.scto.codelikebastimove.feature.settings.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun SettingsAppScreen(
  onBackClick: () -> Unit,
  onNavigateToAppearance: () -> Unit,
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
  PreferenceLayout(
    label = stringResource(R.string.ide_settings),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = stringResource(R.string.appearance)) {
      NavigationItem(
        label = stringResource(R.string.appearance),
        description = "Themes, dynamic colors",
        onClick = onNavigateToAppearance,
        startIcon = Icons.Default.Palette,
      )
    }

    PreferenceGroup(heading = stringResource(R.string.configure)) {
      NavigationItem(
        label = stringResource(R.string.general_settings),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToGeneral,
        startIcon = Icons.Default.Settings,
      )

      HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

      NavigationItem(
        label = stringResource(R.string.editor),
        description = stringResource(R.string.pref_configure_editor_summary),
        onClick = onNavigateToEditorSettings,
        startIcon = Icons.Default.Code,
      )

      HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

      NavigationItem(
        label = stringResource(R.string.ai_agent),
        description = stringResource(R.string.pref_configure_editor_summary),
        onClick = onNavigateToAIAgent,
        startIcon = Icons.Default.Memory,
      )

      HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

      NavigationItem(
        label = stringResource(R.string.build_and_run),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToBuildAndRun,
        startIcon = Icons.Default.Build,
      )

      HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

      NavigationItem(
        label = stringResource(R.string.termux),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToTermux,
        startIcon = Icons.Default.Terminal,
      )
    }

    PreferenceGroup(heading = stringResource(R.string.privacy)) {
      NavigationItem(
        label = stringResource(R.string.clbm_statistics),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToStatistics,
        startIcon = Icons.Default.Analytics,
      )
    }

    PreferenceGroup(heading = stringResource(R.string.developer_options)) {
      NavigationItem(
        label = "Entwickleroptionen",
        description = "Experimentelle/Debugging Optionen für CLBM",
        onClick = onNavigateToDeveloperOptions,
        startIcon = Icons.Default.BugReport,
      )
    }

    PreferenceGroup(heading = "Über") {
      NavigationItem(
        label = "Über",
        description = "App-Version und Informationen",
        onClick = onNavigateToAbout,
        startIcon = Icons.Default.Settings, // Placeholder icon
      )
    }
  }
}

@Composable
private fun NavigationItem(
  label: String,
  description: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  startIcon: ImageVector? = null,
) {
  SettingsToggle(
    label = label,
    description = description,
    showSwitch = false,
    onClick = onClick,
    modifier = modifier,
    startWidget = {
      if (startIcon != null) {
        Icon(
          imageVector = startIcon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp),
        )
      }
    },
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
