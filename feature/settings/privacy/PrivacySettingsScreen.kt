package com.scto.codelikebastimove.feature.settings.privacy

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun PrivacySettingsScreen(
  onBackClick: () -> Unit,
  onNavigateToStatistics: () -> Unit,
  modifier: Modifier = Modifier,
) {
  PreferenceLayout(
    label = stringResource(R.string.privacy),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = stringResource(R.string.privacy)) {
      NavigationItem(
        label = stringResource(R.string.clbm_statistics),
        description = stringResource(R.string.pref_configure_general_summary),
        onClick = onNavigateToStatistics,
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
