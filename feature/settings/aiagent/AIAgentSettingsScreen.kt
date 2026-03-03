package com.scto.codelikebastimove.feature.settings.aiagent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
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
fun AIAgentSettingsScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  PreferenceLayout(
    label = stringResource(R.string.ai_agent),
    onBack = onBackClick,
    modifier = modifier,
  ) {
    PreferenceGroup(heading = "AI Model") {
      SettingsToggle(
        label = "Preferred AI Model",
        description = "Configure your preferred AI model for code generation and assistance",
        checked = false, // Placeholder
        onCheckedChange = { /* TODO */ },
        startWidget = {
          Icon(
            imageVector = Icons.Default.Memory,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
          )
        },
      )
    }
    // Add more AI agent specific settings here
  }
}
