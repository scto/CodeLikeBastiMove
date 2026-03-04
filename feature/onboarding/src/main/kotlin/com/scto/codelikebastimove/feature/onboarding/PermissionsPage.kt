package com.scto.codelikebastimove.feature.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.OnboardingConfig
import com.scto.codelikebastimove.core.utils.PermissionUtils

@Composable
fun PermissionsPage(
    config: OnboardingConfig,
    onPermissionChange: (PermissionUtils.PermissionType, Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        onPermissionChange(PermissionUtils.PermissionType.NOTIFICATIONS, it)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 80.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Berechtigungen", style = MaterialTheme.typography.headlineMedium)

            PermissionItem(
                title = "Dateizugriff (Alle Dateien)",
                desc = "Erforderlich für das Management deiner Projekte.",
                isGranted = config.fileAccessPermissionGranted,
                onGrant = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${context.packageName}"))
                        context.startActivity(intent)
                    }
                }
            )

            PermissionItem(
                title = "Benachrichtigungen",
                desc = "Status-Infos zu deinen Builds.",
                isGranted = config.notificationPermissionGranted,
                onGrant = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )

            PermissionItem(
                title = "Apps installieren",
                desc = "APKs direkt aus der IDE testen.",
                isGranted = config.installPackagesPermissionGranted,
                onGrant = {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                    context.startActivity(intent)
                }
            )
        }

        Row(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            FloatingActionButton(onClick = onBack, containerColor = MaterialTheme.colorScheme.secondary) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            FloatingActionButton(onClick = onNext, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next")
            }
        }
    }
}

@Composable
fun PermissionItem(title: String, desc: String, isGranted: Boolean, onGrant: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = isGranted, onCheckedChange = { onGrant() })
        }
    }
}