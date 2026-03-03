package com.scto.codelikebastimove.feature.settings.statistics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
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
fun StatisticsSettingsScreen(
  viewModel: SettingsViewModel = viewModel(),
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userPreferences by viewModel.userPreferences.collectAsState()
  val usageAnalyticsPermissionGranted = userPreferences.onboardingConfig.usageAnalyticsPermissionGranted

  PreferenceLayout(
    label = stringResource(R.string.clbm_statistics),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = "Usage Data") {
      SettingsToggle(
        label = "Send Usage Statistics",
        description = "Help improve CLBM by sending anonymous usage data",
        checked = usageAnalyticsPermissionGranted,
        onCheckedChange = { viewModel.setUsageAnalyticsPermissionGranted(it) },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Analytics,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }
    // Add more statistics specific settings here
  }
}
