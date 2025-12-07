package com.scto.codelikebastimove.feature.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate
import com.scto.codelikebastimove.core.templates.impl.ProjectManagerImpl
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectDialog(
    context: Context,
    onDismiss: () -> Unit,
    onProjectCreated: (Project) -> Unit
) {
    val projectManager = remember { ProjectManagerImpl(context) }
    val templates = remember { projectManager.getAvailableTemplates() }
    val scope = rememberCoroutineScope()
    
    var selectedTemplate by remember { mutableStateOf(templates.firstOrNull()) }
    var projectName by remember { mutableStateOf("MyApplication") }
    var packageName by remember { mutableStateOf("com.example.myapplication") }
    var minSdk by remember { mutableIntStateOf(24) }
    var selectedLanguage by remember { mutableStateOf(ProjectLanguage.KOTLIN) }
    var selectedGradleLanguage by remember { mutableStateOf(GradleLanguage.KOTLIN_DSL) }
    var isCreating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var minSdkExpanded by remember { mutableStateOf(false) }
    
    val minSdkOptions = listOf(21, 23, 24, 26, 28, 29, 30, 31, 33, 34)
    
    AlertDialog(
        onDismissRequest = { if (!isCreating) onDismiss() },
        title = {
            Text(
                text = "Neues Projekt erstellen",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Vorlage",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                templates.forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedTemplate = template },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTemplate == template)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = template.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selectedTemplate == template) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Projektname") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ExposedDropdownMenuBox(
                    expanded = minSdkExpanded,
                    onExpandedChange = { minSdkExpanded = !minSdkExpanded }
                ) {
                    OutlinedTextField(
                        value = "API $minSdk",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Minimum SDK") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minSdkExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = minSdkExpanded,
                        onDismissRequest = { minSdkExpanded = false }
                    ) {
                        minSdkOptions.forEach { sdk ->
                            DropdownMenuItem(
                                text = { Text("API $sdk") },
                                onClick = {
                                    minSdk = sdk
                                    minSdkExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Programmiersprache",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProjectLanguage.entries.forEach { language ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = selectedLanguage == language,
                                    onClick = { selectedLanguage = language },
                                    role = Role.RadioButton
                                )
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == language,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Gradle Sprache",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GradleLanguage.entries.forEach { language ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = selectedGradleLanguage == language,
                                    onClick = { selectedGradleLanguage = language },
                                    role = Role.RadioButton
                                )
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedGradleLanguage == language,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTemplate == null) {
                        errorMessage = "Bitte wähle eine Vorlage aus"
                        return@Button
                    }
                    if (projectName.isBlank()) {
                        errorMessage = "Bitte gib einen Projektnamen ein"
                        return@Button
                    }
                    if (packageName.isBlank()) {
                        errorMessage = "Bitte gib einen Package-Namen ein"
                        return@Button
                    }
                    
                    isCreating = true
                    errorMessage = null
                    
                    scope.launch {
                        val config = ProjectConfig(
                            projectName = projectName,
                            packageName = packageName,
                            minSdk = minSdk,
                            language = selectedLanguage,
                            gradleLanguage = selectedGradleLanguage
                        )
                        
                        val outputPath = context.filesDir.absolutePath + "/projects"
                        
                        val result = projectManager.createProject(
                            template = selectedTemplate!!,
                            config = config,
                            outputPath = outputPath
                        )
                        
                        result.fold(
                            onSuccess = { project ->
                                onProjectCreated(project)
                            },
                            onFailure = { error ->
                                errorMessage = "Fehler: ${error.message}"
                                isCreating = false
                            }
                        )
                    }
                },
                enabled = !isCreating
            ) {
                Text(if (isCreating) "Erstelle..." else "Erstellen")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCreating
            ) {
                Text("Abbrechen")
            }
        }
    )
}
