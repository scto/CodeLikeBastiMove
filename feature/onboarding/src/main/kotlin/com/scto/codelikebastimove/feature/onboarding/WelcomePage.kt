package com.scto.codelikebastimove.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WelcomePage(onNextClick: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier.fillMaxSize().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Icon(
        imageVector = Icons.Default.Code,
        contentDescription = "App Logo",
        modifier = Modifier.size(120.dp),
        tint = MaterialTheme.colorScheme.primary,
      )

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "CodeLikeBastiMove",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text =
          "Willkommen bei CodeLikeBastiMove! Diese Applikation ermöglicht es Ihnen, Android-Projekte zu erstellen, zu bearbeiten und zu verwalten. Mit integrierten Entwicklungstools und Git-Unterstützung haben Sie alles, was Sie für die mobile Entwicklung benötigen.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Herzlich willkommen! Lassen Sie uns gemeinsam die Einrichtung durchführen.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
    }

    FloatingActionButton(
      onClick = onNextClick,
      modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
      containerColor = MaterialTheme.colorScheme.primary,
    ) {
      Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Weiter")
    }
  }
}
