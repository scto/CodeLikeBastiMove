package com.scto.codelikebastimove.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HomeButton(
            icon = Icons.Default.Add,
            text = "Erstelle ein Projekt",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeButton(
            icon = Icons.Default.FolderOpen,
            text = "Öffne ein Projekt",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeButton(
            icon = Icons.Default.CloudDownload,
            text = "Clone ein Repository",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeButton(
            icon = Icons.Default.Settings,
            text = "Einstellungen",
            onClick = onNavigateToSettings
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeButton(
            icon = Icons.Default.Help,
            text = "Hilfe",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HomeButton(
            icon = Icons.Default.QuestionAnswer,
            text = "FAQ",
            onClick = { }
        )
    }
}

@Composable
private fun HomeButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
