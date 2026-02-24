package com.scto.codelikebastimove.feature.settings.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.settings.SettingsViewModel
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun SettingsAppScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNavigateToAboutSettings: () -> Unit,
    onNavigateToAIAgentSettings: () -> Unit,
    onNavigateToBuildAndRunSettings: () -> Unit,
    onNavigateToDeveloperOptions: () -> Unit,
    onNavigateToEditorSettings: () -> Unit,
    onNavigateToGeneralSetting: () -> Unit,
    onNavigateToGitSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToTermux: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    
    /*
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetOnboardingDialog by remember { mutableStateOf(false) }

    val themeName = when (userPreferences.themeMode) {
        ThemeMode.LIGHT -> "Hell"
        ThemeMode.DARK -> "Dunkel"
        ThemeMode.FOLLOW_SYSTEM -> "System"
    }

    val themeIcon = when (userPreferences.themeMode) {
        ThemeMode.LIGHT -> Icons.Default.LightMode
        ThemeMode.DARK -> Icons.Default.DarkMode
        ThemeMode.FOLLOW_SYSTEM -> Icons.Default.Settings
    }
    */
    
    PreferenceLayout(
        label = stringResource(R.string.ide_settings),
        onBack = onBackClick,
        modifier = modifier,
    ) {
        /*
        PreferenceGroup(heading = stringResource(R.string.appearance)) {
            SettingsToggle(
                label = "Design",
                description = themeName,
                showSwitch = false,
                onClick = { showThemeDialog = true },
                startWidget = {
                    Icon(
                        imageVector = themeIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = stringResource(R.string.dynamic_colors),
                description = stringResource(R.string.dynamic_colors_summary_enabled),
                checked = userPreferences.dynamicColorsEnabled,
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
        */
        
        PreferenceGroup(heading = stringResource(R.string.configure)) {
            NavigationItem(
                label = stringResource(R.string.general_settings),
                description = stringResource(R.string.pref_configure_general_summary),
                onClick = onNavigateToGeneral,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            NavigationItem(
                label = stringResource(R.string.editor),
                description = stringResource(R.string.pref_configure_editor_summary),
                onClick = onNavigateToEditorSettings,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            NavigationItem(
                label = stringResource(R.string.ai_agent),
                description = stringResource(R.string.pref_configure_editor_summary),
                onClick = onNavigateToAIAgent,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            NavigationItem(
                label = stringResource(R.string.build_and_run),
                description = stringResource(R.string.pref_configure_general_summary),
                onClick = onNavigateToBuildAndRun,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            NavigationItem(
                label = stringResource(R.string.termux),
                description = stringResource(R.string.pref_configure_general_summary),
                onClick = onNavigateToTermux,
            )
        }

        PreferenceGroup(heading = stringResource(R.string.privacy)) {
            NavigationItem(
                label = stringResource(R.string.clbm_statistics),
                description = stringResource(R.string.pref_configure_general_summary),
                onClick = onNavigateToStatistics,
            )
        }

        PreferenceGroup(heading = stringResource(R.string.developer_options)) {
            /*
            SettingsToggle(
                label = stringResource(R.string.debug_logging),
                description = stringResource(R.string.debug_logging_description),
                checked = userPreferences.loggingEnabled,
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
            */

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            NavigationItem(
                label = "Entwickleroptionen",
                description = "Experimentelle/Debugging Optionen für CLBM",
                onClick = onNavigateToDeveloperOptions,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            /*
            NavigationItem(
                label = "Onboarding zurücksetzen",
                description = "Zeigt den Einrichtungsassistenten beim nächsten Start",
                onClick = { showResetOnboardingDialog = true },
            )
            */
        }

        PreferenceGroup(heading = "Über") {
            NavigationItem(
                label = "Über",
                description = "App-Version und Informationen",
                onClick = onNavigateToAbout,
            )
        }
    }

    /*
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
    */
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

/*
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
            androidx.compose.foundation.layout.Column {
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
*/