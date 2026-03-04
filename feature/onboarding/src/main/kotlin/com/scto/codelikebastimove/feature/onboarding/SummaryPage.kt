package com.scto.codelikebastimove.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.OnboardingConfig

@Composable
fun SummaryPage(
    config: OnboardingConfig,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val canComplete = config.fileAccessPermissionGranted

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 80.dp).verticalScroll(rememberScrollState())) {
            Text("Zusammenfassung", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(modifier = Modifier.height(16.dp))

            SummarySection("Berechtigungen") {
                SummaryRow("Dateizugriff", config.fileAccessPermissionGranted)
                SummaryRow("Benachrichtigungen", config.notificationPermissionGranted)
                SummaryRow("Apps installieren", config.installPackagesPermissionGranted)
            }

            SummarySection("Konfiguration") {
                Text("JDK: ${config.selectedOpenJdkVersion.displayName}")
                Text("Build Tools: ${config.selectedBuildToolsVersion.displayName}")
                SummaryRow("Git Support", config.gitEnabled)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onFinish, enabled = canComplete, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Icon(Icons.Default.PlayArrow, null)
                Text("Einrichtung abschließen")
            }
        }

        FloatingActionButton(onClick = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }
    }
}

@Composable
fun SummarySection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun SummaryRow(label: String, active: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Icon(if (active) Icons.Default.Check else Icons.Default.Close, null, tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
    }
}