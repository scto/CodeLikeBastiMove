package com.scto.codelikebastimove.feature.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar

enum class ProgrammingLanguage(val displayName: String) {
    KOTLIN("Kotlin"),
    JAVA("Java")
}

enum class ModuleType(val displayName: String) {
    LIBRARY("Library"),
    FEATURE("Feature"),
    CORE("Core")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubModuleMakerScreen(
    onBackClick: () -> Unit,
    onCreateModule: (modulePath: String, packageName: String, language: ProgrammingLanguage, type: ModuleType) -> Unit,
    modifier: Modifier = Modifier
) {
    // Default to a common pattern like :core:model to show user the expected format
    var modulePath by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(ProgrammingLanguage.KOTLIN) }
    var selectedType by remember { mutableStateOf(ModuleType.LIBRARY) }
    
    Scaffold(
        topBar = {
            AdaptiveTopAppBar(
                title = "Sub-Module Maker",
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
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Neues Modul erstellen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Erstelle neue Sub-Module mit Gradle Notation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Konfiguration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // --- Language Selection ---
                    Text(
                        text = "Sprache",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProgrammingLanguage.entries.forEach { language ->
                            FilterChip(
                                selected = selectedLanguage == language,
                                onClick = { selectedLanguage = language },
                                label = { Text(language.displayName) },
                                leadingIcon = if (selectedLanguage == language) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Type Selection ---
                    Text(
                        text = "Modultyp",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModuleType.entries.forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.displayName) },
                                leadingIcon = if (selectedType == type) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // --- Module Path Input ---
                    OutlinedTextField(
                        value = modulePath,
                        onValueChange = { 
                            modulePath = it 
                            // Einfache Auto-Vervollständigung des Package-Namens, wenn dieser leer ist
                            if (packageName.isEmpty() && it.isNotEmpty()) {
                                val cleanPath = it.replace(":", ".").trim('.')
                                packageName = "com.example.app.$cleanPath"
                            }
                        },
                        label = { Text("Gradle Pfad (z.B. :features:login)") },
                        placeholder = { Text(":core:network") },
                        leadingIcon = {
                            Icon(Icons.Default.Folder, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Text(
                        text = "Verwende ':' um Ordner zu verschachteln.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // --- Package Name Input ---
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Paketname (Optional)") },
                        placeholder = { Text("com.example.app.features.login") },
                        leadingIcon = {
                            Icon(Icons.Default.Domain, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (modulePath.isNotBlank()) {
                                onCreateModule(modulePath, packageName, selectedLanguage, selectedType)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = modulePath.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Modul erstellen")
                    }
                }
            }
        }
    }
}