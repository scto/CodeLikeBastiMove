package com.scto.codelikebastimove.feature.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import com.scto.codelikebastimove.core.templates.api.ProjectTemplate
import com.scto.codelikebastimove.feature.main.MainViewModel

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubModuleMakerScreen(
    onBack: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Data from ViewModel
    val moduleNameInput by viewModel.moduleNameInput.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedTemplate by viewModel.selectedTemplate.collectAsState()
    val availableTemplates by viewModel.availableTemplates.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Module") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Project Context Info
            if (currentProject != null) {
                Text(
                    text = "In Project: ${currentProject?.name}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "No Project Open!",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Module Name / Gradle Path Input
            OutlinedTextField(
                value = moduleNameInput,
                onValueChange = { viewModel.setModuleNameInput(it) },
                label = { Text("Module Path (Gradle Notation)") },
                placeholder = { Text(":features:my-feature") },
                supportingText = { Text("Use ':' to create nested modules (e.g. :core:ui)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Language Selection
            Text("Language", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProjectLanguage.entries.forEach { language ->
                    LanguageChip(
                        language = language,
                        selected = language == selectedLanguage,
                        onClick = { viewModel.setSelectedLanguage(language) }
                    )
                }
            }

            // Template Selection
            Text("Template", style = MaterialTheme.typography.titleMedium)
            TemplateSelector(
                templates = availableTemplates,
                selectedTemplate = selectedTemplate,
                onTemplateSelected = { viewModel.setSelectedTemplate(it) }
            )

            // Template Description
            selectedTemplate?.let { template ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button
            Button(
                onClick = {
                    viewModel.createModuleWithGradleNotation(
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Module created successfully!")
                                onBack()
                            }
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: $error")
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = moduleNameInput.isNotBlank() && selectedTemplate != null && currentProject != null
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Module")
            }
        }
    }
}

@Composable
private fun LanguageChip(
    language: ProjectLanguage,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .background(containerColor, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = language.name,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun TemplateSelector(
    templates: List<ProjectTemplate>,
    selectedTemplate: ProjectTemplate?,
    onTemplateSelected: (ProjectTemplate) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedTemplate?.name ?: "Select Template")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            templates.forEach { template ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(template.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    },
                    onClick = {
                        onTemplateSelected(template)
                        expanded = false
                    }
                )
            }
        }
    }
}