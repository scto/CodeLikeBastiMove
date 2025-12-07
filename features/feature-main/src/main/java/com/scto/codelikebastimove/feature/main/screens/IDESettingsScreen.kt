package com.scto.codelikebastimove.feature.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SettingsCategory(
    val title: String,
    val items: List<SettingsItem>
)

data class SettingsItem(
    val title: String,
    val description: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDESettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToAIAgent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        SettingsCategory(
            title = "Konfigurieren",
            items = listOf(
                SettingsItem(
                    title = "Allgemein",
                    description = "Allgemeine IDE-Konfiguration.",
                    icon = Icons.Default.Settings
                ),
                SettingsItem(
                    title = "Editor",
                    description = "Konfiguriere den Editor.",
                    icon = Icons.Default.Code
                ),
                SettingsItem(
                    title = "AI Agent",
                    description = "Get AI-powered code generation using AI Agent",
                    icon = Icons.Default.Memory,
                    onClick = onNavigateToAIAgent
                ),
                SettingsItem(
                    title = "Build & Run",
                    description = "Gradle-Build konfigurieren.",
                    icon = Icons.Default.Build
                ),
                SettingsItem(
                    title = "Termux",
                    description = "Preferences for the Termux terminal.",
                    icon = Icons.Default.Terminal
                )
            )
        ),
        SettingsCategory(
            title = "Datenschutz",
            items = listOf(
                SettingsItem(
                    title = "AndroidIDE Statistiken",
                    description = "Anonyme Nutzungsdaten",
                    icon = Icons.Outlined.Analytics
                )
            )
        ),
        SettingsCategory(
            title = "Entwickleroptionen",
            items = listOf(
                SettingsItem(
                    title = "Entwickleroptionen",
                    description = "Experimentelle/Debugging Optionen für AndroidIDE",
                    icon = Icons.Default.BugReport
                )
            )
        ),
        SettingsCategory(
            title = "Über",
            items = listOf(
                SettingsItem(
                    title = "Über",
                    description = "App-Version und Informationen",
                    icon = Icons.Outlined.Info
                )
            )
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IDE-Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            categories.forEach { category ->
                SettingsCategoryHeader(title = category.title)
                
                category.items.forEach { item ->
                    SettingsItemRow(
                        title = item.title,
                        description = item.description,
                        onClick = item.onClick
                    )
                }
                
                if (category != categories.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsCategoryHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsItemRow(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
