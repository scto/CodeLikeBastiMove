package com.scto.codelikebastimove.feature.settings.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.core.updater.UpdateState
import com.scto.codelikebastimove.feature.settings.SettingsViewModel
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.PreferenceTemplate
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun AboutScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val updateState by viewModel.updateState.collectAsState()
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0"
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    PreferenceLayout(
        label = stringResource(R.string.settings_about),
        onBack = onBack,
        modifier = modifier,
    ) {
        PreferenceGroup(heading = "App") {
            PreferenceTemplate(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(8.dp))

                        AppLogo()

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = stringResource(R.string.settings_version, versionName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.settings_app_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                },
            )
        }

        PreferenceGroup(heading = stringResource(R.string.settings_updates)) {
            PreferenceTemplate(
                title = {
                    Text(
                        text = stringResource(R.string.settings_updates),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                description = {
                    Text(
                        text = when (updateState) {
                            is UpdateState.Idle -> stringResource(R.string.settings_check_for_updates)
                            is UpdateState.Checking -> stringResource(R.string.settings_checking_updates)
                            is UpdateState.UpToDate -> stringResource(R.string.settings_up_to_date)
                            is UpdateState.UpdateAvailable -> stringResource(
                                R.string.settings_update_available,
                                (updateState as UpdateState.UpdateAvailable).info.latestVersion
                            )
                            is UpdateState.Error -> stringResource(R.string.settings_update_error)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
                endWidget = {
                    when (updateState) {
                        is UpdateState.Checking -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        }
                        else -> {
                            Button(onClick = { viewModel.checkForUpdates() }) {
                                Text(stringResource(R.string.settings_check))
                            }
                        }
                    }
                },
            )
        }

        PreferenceGroup(heading = stringResource(R.string.settings_links)) {
            SettingsToggle(
                label = stringResource(R.string.settings_github_project),
                description = "github.com/AbandonedCart/CodeLikeBastiMove",
                showSwitch = false,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AbandonedCart/CodeLikeBastiMove"))
                    context.startActivity(intent)
                },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
                endWidget = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = stringResource(R.string.settings_github_releases),
                description = stringResource(R.string.settings_github_releases_subtitle),
                showSwitch = false,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AbandonedCart/CodeLikeBastiMove/releases"))
                    context.startActivity(intent)
                },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
                endWidget = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00D9FF),
                        Color(0xFF00B4D8),
                        Color(0xFF7C3AED),
                        Color(0xFFA855F7),
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CLBM",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
            )
            Text(
                text = "</>",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
            )
        }
    }
}
