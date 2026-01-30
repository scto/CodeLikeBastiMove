package com.scto.codelikebastimove.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.OnboardingConfig

@Composable
fun SummaryPage(
  onboardingConfig: OnboardingConfig,
  canComplete: Boolean = true,
  onStartInstallation: () -> Unit,
  onBackClick: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(16.dp)
          .padding(bottom = 80.dp)
          .verticalScroll(rememberScrollState())
    ) {
      Text(
        text = "Zusammenfassung",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Berechtigungen",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )

          Spacer(modifier = Modifier.height(12.dp))

          SummaryItem(
            label = "Dateizugriff",
            isEnabled = onboardingConfig.fileAccessPermissionGranted,
          )

          SummaryItem(
            label = "Nutzungsanalyse",
            isEnabled = onboardingConfig.usageAnalyticsPermissionGranted,
          )

          SummaryItem(
            label = "Akku-Optimierung deaktiviert",
            isEnabled = onboardingConfig.batteryOptimizationDisabled,
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Entwicklungsumgebung",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )

          Spacer(modifier = Modifier.height(12.dp))

          SummaryValueItem(
            label = "OpenJDK Version",
            value = onboardingConfig.selectedOpenJdkVersion.displayName,
          )

          SummaryValueItem(
            label = "Build Tools Version",
            value = onboardingConfig.selectedBuildToolsVersion.displayName,
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Optionale Tools",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )

          Spacer(modifier = Modifier.height(12.dp))

          SummaryItem(label = "git", isEnabled = onboardingConfig.gitEnabled)

          SummaryItem(label = "git-lfs", isEnabled = onboardingConfig.gitLfsEnabled)

          SummaryItem(label = "ssh", isEnabled = onboardingConfig.sshEnabled)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      if (!canComplete) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        ) {
          Text(
            text = "Bitte erteilen Sie die Berechtigung für den Dateizugriff, um fortzufahren.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp),
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      Button(
        onClick = onStartInstallation,
        modifier = Modifier.fillMaxWidth(),
        enabled = canComplete,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          ),
      ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if (canComplete) "Installation starten" else "Berechtigung erforderlich")
      }
    }

    FloatingActionButton(
      onClick = onBackClick,
      modifier = Modifier.align(Alignment.BottomStart).padding(24.dp),
      containerColor = MaterialTheme.colorScheme.secondary,
    ) {
      Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
    }
  }
}

@Composable
private fun SummaryItem(label: String, isEnabled: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Icon(
      imageVector = if (isEnabled) Icons.Default.Check else Icons.Default.Close,
      contentDescription = if (isEnabled) "Aktiviert" else "Deaktiviert",
      tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
    )
  }
}

@Composable
private fun SummaryValueItem(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.primary,
    )
  }
}
