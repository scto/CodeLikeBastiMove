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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.ThemeMode
import com.scto.codelikebastimove.core.datastore.UserPreferences
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
    val userPreferences by userPreferencesRepository.userPreferences.collectAsState(
        initial = UserPreferences()
    )
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetOnboardingDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            AdaptiveTopAppBar(
                title = "IDE-Einstellungen",
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
            SettingsCategoryHeader(title = "Erscheinungsbild")
            
            ThemeSettingRow(
                currentTheme = userPreferences.themeMode,
                onClick = { showThemeDialog = true }
            )
            
            ToggleSettingRow(
                title = "Dynamische Farben",
                description = "Material You Farben basierend auf Hintergrundbild (Android 12+)",
                icon = Icons.Default.Palette,
                isEnabled = userPreferences.dynamicColorsEnabled,
                onToggle = {
                    scope.launch {
                        userPreferencesRepository.setDynamicColorsEnabled(!userPreferences.dynamicColorsEnabled)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            
            SettingsCategoryHeader(title = "Konfigurieren")
            
            SettingsItemRow(
                title = "Allgemein",
                description = "Allgemeine IDE-Konfiguration.",
                onClick = { }
            )
            
            SettingsItemRow(
                title = "Editor",
                description = "Konfiguriere den Editor.",
                onClick = { }
            )
            
            SettingsItemRow(
                title = "AI Agent",
                description = "KI-gestützte Codegenerierung mit AI Agent",
                onClick = onNavigateToAIAgent
            )
            
            SettingsItemRow(
                title = "Build & Run",
                description = "Gradle-Build konfigurieren.",
                onClick = { }
            )
            
            SettingsItemRow(
                title = "Termux",
                description = "Einstellungen für das Termux-Terminal.",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            
            SettingsCategoryHeader(title = "Datenschutz")
            
            SettingsItemRow(
                title = "CLBM Statistiken",
                description = "Anonyme Nutzungsdaten",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            
            SettingsCategoryHeader(title = "Entwickleroptionen")
            
            SettingsItemRow(
                title = "Entwickleroptionen",
                description = "Experimentelle/Debugging Optionen für CLBM",
                onClick = { }
            )
            
            SettingsItemRow(
                title = "Onboarding zurücksetzen",
                description = "Zeigt den Einrichtungsassistenten beim nächsten Start",
                onClick = { showResetOnboardingDialog = true }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            
            SettingsCategoryHeader(title = "Über")
            
            SettingsItemRow(
                title = "Über",
                description = "App-Version und Informationen",
                onClick = { }
            )
        }
    }
    
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = userPreferences.themeMode,
            onThemeSelected = { theme ->
                scope.launch {
                    userPreferencesRepository.setThemeMode(theme)
                }
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    
    if (showResetOnboardingDialog) {
        AlertDialog(
            onDismissRequest = { showResetOnboardingDialog = false },
            title = { Text("Onboarding zurücksetzen?") },
            text = { 
                Text("Der Einrichtungsassistent wird beim nächsten Start der App angezeigt. Alle Berechtigungen bleiben erhalten.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            userPreferencesRepository.setOnboardingCompleted(false)
                        }
                        showResetOnboardingDialog = false
                    }
                ) {
                    Text("Zurücksetzen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetOnboardingDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
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

@Composable
private fun ThemeSettingRow(
    currentTheme: ThemeMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeName = when (currentTheme) {
        ThemeMode.LIGHT -> "Hell"
        ThemeMode.DARK -> "Dunkel"
        ThemeMode.FOLLOW_SYSTEM -> "System"
    }
    
    val themeIcon = when (currentTheme) {
        ThemeMode.LIGHT -> Icons.Default.LightMode
        ThemeMode.DARK -> Icons.Default.DarkMode
        ThemeMode.FOLLOW_SYSTEM -> Icons.Default.Settings
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = themeIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Design",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = themeName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    description: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
        
        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() }
        )
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Design auswählen") },
        text = {
            Column {
                ThemeOption(
                    name = "Hell",
                    icon = Icons.Default.LightMode,
                    isSelected = currentTheme == ThemeMode.LIGHT,
                    onClick = { onThemeSelected(ThemeMode.LIGHT) }
                )
                
                ThemeOption(
                    name = "Dunkel",
                    icon = Icons.Default.DarkMode,
                    isSelected = currentTheme == ThemeMode.DARK,
                    onClick = { onThemeSelected(ThemeMode.DARK) }
                )
                
                ThemeOption(
                    name = "System (automatisch)",
                    icon = Icons.Default.Settings,
                    isSelected = currentTheme == ThemeMode.FOLLOW_SYSTEM,
                    onClick = { onThemeSelected(ThemeMode.FOLLOW_SYSTEM) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Schließen")
            }
        }
    )
}

@Composable
private fun ThemeOption(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
