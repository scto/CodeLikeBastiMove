package com.scto.codelikebastimove.feature.settings.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.core.updater.UpdateCheckInterval
import com.scto.codelikebastimove.feature.settings.SettingsViewModel
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun DebugSettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    val resetOnboarding = userPreferences.resetOnboarding
    val loggingEnabled = userPreferences.loggingEnabled
    val updateCheckInterval = UpdateCheckInterval.fromHours(userPreferences.updateCheckIntervalHours)

    PreferenceLayout(
        label = stringResource(R.string.settings_debug),
        onBack = onBack,
        modifier = modifier,
    ) {
        PreferenceGroup(heading = stringResource(R.string.settings_debug_logging)) {
            SettingsToggle(
                label = stringResource(R.string.settings_enable_logging),
                description = stringResource(R.string.settings_enable_logging_subtitle),
                checked = loggingEnabled,
                onCheckedChange = { viewModel.setLoggingEnabled(it) },
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

        PreferenceGroup(heading = stringResource(R.string.settings_debug_actions)) {
            SettingsToggle(
                label = stringResource(R.string.settings_reset_onboarding),
                description = stringResource(R.string.settings_reset_onboarding_subtitle),
                showSwitch = false,
                onClick = { viewModel.resetOnboarding() },
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
        
        PreferenceGroup(heading = stringResource(R.string.settings_update_check_interval)) {
            UpdateCheckInterval.entries.forEachIndexed { index, interval ->
                UpdateIntervalOption(
                    title = getIntervalLabel(interval),
                    isSelected = updateCheckInterval == interval,
                    onClick = { viewModel.setUpdateCheckInterval(interval) },
                )
                if (index < UpdateCheckInterval.entries.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun UpdateIntervalOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun getIntervalLabel(interval: UpdateCheckInterval): String {
    return when (interval) {
        UpdateCheckInterval.NEVER -> stringResource(R.string.settings_interval_never)
        UpdateCheckInterval.HOURLY -> stringResource(R.string.settings_interval_hourly)
        UpdateCheckInterval.EVERY_6_HOURS -> stringResource(R.string.settings_interval_6_hours)
        UpdateCheckInterval.EVERY_12_HOURS -> stringResource(R.string.settings_interval_12_hours)
        UpdateCheckInterval.DAILY -> stringResource(R.string.settings_interval_daily)
        UpdateCheckInterval.WEEKLY -> stringResource(R.string.settings_interval_weekly)
    }
}

}