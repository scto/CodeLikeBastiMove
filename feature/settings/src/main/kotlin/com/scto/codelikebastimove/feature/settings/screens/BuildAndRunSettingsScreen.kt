package com.scto.codelikebastimove.feature.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.BuildSettings
import com.scto.codelikebastimove.core.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildAndRunSettingsScreen(
    buildSettings: BuildSettings,
    onParallelBuildChanged: (Boolean) -> Unit,
    onCleanBeforeBuildChanged: (Boolean) -> Unit,
    onOfflineModeChanged: (Boolean) -> Unit,
    onAutoRunChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings_build_and_run)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_build_options),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    BuildSwitchSetting(
                        icon = Icons.Default.Speed,
                        title = stringResource(R.string.settings_parallel_build),
                        subtitle = stringResource(R.string.settings_parallel_build_subtitle),
                        checked = buildSettings.parallelBuildEnabled,
                        onCheckedChange = onParallelBuildChanged
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    BuildSwitchSetting(
                        icon = Icons.Default.CleaningServices,
                        title = stringResource(R.string.settings_clean_before_build),
                        subtitle = stringResource(R.string.settings_clean_before_build_subtitle),
                        checked = buildSettings.cleanBeforeBuildEnabled,
                        onCheckedChange = onCleanBeforeBuildChanged
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    BuildSwitchSetting(
                        icon = Icons.Default.Memory,
                        title = stringResource(R.string.settings_offline_mode),
                        subtitle = stringResource(R.string.settings_offline_mode_subtitle),
                        checked = buildSettings.offlineModeEnabled,
                        onCheckedChange = onOfflineModeChanged
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_run_options),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                BuildSwitchSetting(
                    icon = Icons.Default.Build,
                    title = stringResource(R.string.settings_auto_run),
                    subtitle = stringResource(R.string.settings_auto_run_subtitle),
                    checked = buildSettings.autoRunEnabled,
                    onCheckedChange = onAutoRunChanged
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BuildSwitchSetting(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
