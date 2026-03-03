package com.scto.codelikebastimove.feature.settings.termux

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
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
fun TermuxSettingsScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  PreferenceLayout(
    label = stringResource(R.string.termux),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = "Terminal") {
      SettingsToggle(
        label = "Enable Termux Integration",
        description = "Allow CLBM to interact with Termux environment",
        checked = true, // Placeholder
        onCheckedChange = { /* TODO */ },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }
    // Add more Termux specific settings here
  }
}
