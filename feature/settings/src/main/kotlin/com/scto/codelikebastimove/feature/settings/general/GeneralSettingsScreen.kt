package com.scto.codelikebastimove.feature.settings.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun GeneralSettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    val selectedThemeMode = userPreferences.themeMode
    val dynamicColorsEnabled = userPreferences.dynamicColorsEnabled

    PreferenceLayout(
        label = stringResource(R.string.settings_general),
        onBack = onBack,
        modifier = modifier,
    ) {
        PreferenceGroup(heading = stringResource(R.string.settings_theme)) {
            ThemeOption(
                icon = Icons.Default.LightMode,
                title = stringResource(R.string.settings_theme_light),
                isSelected = selectedThemeMode == ThemeMode.LIGHT,
                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
            )

            ThemeOption(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.settings_theme_dark),
                isSelected = selectedThemeMode == ThemeMode.DARK,
                onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
            )

            ThemeOption(
                icon = Icons.Default.BrightnessAuto,
                title = stringResource(R.string.settings_theme_system),
                isSelected = selectedThemeMode == ThemeMode.FOLLOW_SYSTEM,
                onClick = { viewModel.setThemeMode(ThemeMode.FOLLOW_SYSTEM) },
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
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
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
            imageVector = icon,
            contentDescription = title,
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
