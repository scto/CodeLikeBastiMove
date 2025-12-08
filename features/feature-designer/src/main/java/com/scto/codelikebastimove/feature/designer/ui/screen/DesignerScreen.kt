package com.scto.codelikebastimove.feature.designer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.feature.designer.data.model.BlockType
import com.scto.codelikebastimove.feature.designer.data.model.ExportConfig
import com.scto.codelikebastimove.feature.designer.ui.canvas.BlockCanvas
import com.scto.codelikebastimove.feature.designer.ui.inspector.PropertyInspector
import com.scto.codelikebastimove.feature.designer.ui.palette.PalettePanel
import com.scto.codelikebastimove.feature.designer.ui.preview.PreviewPane

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignerScreen(
    projectName: String = "New Layout",
    onBackClick: () -> Unit,
    onFilePicker: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: DesignerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        if (uiState.currentProject == null) {
            viewModel.createNewProject(projectName)
        }
    }
    
    LaunchedEffect(uiState.exportSuccess) {
        uiState.exportSuccess?.let { success ->
            snackbarHostState.showSnackbar(
                if (success) "Export successful!" else "Export failed"
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.currentProject?.name ?: "Layout Designer",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${uiState.blocks.size} blocks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showThemeSelector() }) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Theme"
                        )
                    }
                    IconButton(onClick = { viewModel.togglePreview() }) {
                        Icon(
                            imageVector = Icons.Default.Preview,
                            contentDescription = "Preview"
                        )
                    }
                    IconButton(onClick = { viewModel.showCodeDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "View Code"
                        )
                    }
                    IconButton(onClick = { viewModel.showExportDialog() }) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PalettePanel(
                onBlockSelected = { blockType ->
                    viewModel.addBlock(blockType, Offset(100f, 100f))
                },
                customComponents = uiState.customComponents,
                onCustomComponentSelected = { component ->
                    viewModel.addCustomComponentBlock(component, Offset(100f, 100f))
                },
                onAddCustomComponent = { viewModel.showComponentCreator() }
            )
            
            if (uiState.showPreview) {
                PreviewPane(
                    blocks = uiState.blocks,
                    onRefresh = { },
                    onShowCode = { viewModel.showCodeDialog() },
                    modifier = Modifier.weight(1f)
                )
            } else {
                BlockCanvas(
                    blocks = uiState.blocks,
                    selectedBlockId = uiState.selectedBlockId,
                    onBlockSelected = { viewModel.selectBlock(it) },
                    onBlockMoved = { id, offset -> viewModel.moveBlock(id, offset) },
                    onBlockDeleted = { viewModel.deleteBlock(it) },
                    onBlockDropped = { type, offset -> viewModel.addBlock(type, offset) },
                    scale = uiState.scale,
                    modifier = Modifier.weight(1f)
                )
            }
            
            PropertyInspector(
                selectedBlock = uiState.selectedBlock,
                onPropertyChanged = { key, value -> 
                    viewModel.updateBlockProperty(key, value) 
                },
                onBlockDeleted = {
                    uiState.selectedBlockId?.let { viewModel.deleteBlock(it) }
                }
            )
        }
    }
    
    if (uiState.showCodeDialog) {
        CodePreviewDialog(
            code = uiState.generatedCode,
            validation = uiState.validation,
            onDismiss = { viewModel.hideCodeDialog() },
            onExport = { 
                viewModel.hideCodeDialog()
                viewModel.showExportDialog()
            }
        )
    }
    
    if (uiState.showExportDialog) {
        ExportDialog(
            exportPreview = uiState.exportPreview,
            exportConfig = uiState.exportConfig,
            isExporting = uiState.isExporting,
            exportSuccess = uiState.exportSuccess,
            exportMessage = uiState.exportMessage,
            selectedTheme = uiState.selectedTheme,
            onConfigChanged = { viewModel.updateExportConfig(it) },
            onExport = { viewModel.performExport() },
            onDismiss = { viewModel.hideExportDialog() },
            onPickPath = onFilePicker
        )
    }
    
    if (uiState.showThemeSelector) {
        ThemeSelectorDialog(
            themes = uiState.savedThemes,
            selectedTheme = uiState.selectedTheme,
            onThemeSelected = { viewModel.selectTheme(it) },
            onDismiss = { viewModel.hideThemeSelector() }
        )
    }
}

@Composable
private fun CodePreviewDialog(
    code: String,
    validation: com.scto.codelikebastimove.feature.designer.data.model.ValidationResult?,
    onDismiss: () -> Unit,
    onExport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Generated Code")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (validation != null) {
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (validation.isValid) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Syntax valid",
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${validation.errors.size} error(s)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        if (validation.warnings.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "${validation.warnings.size} warning(s)",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onExport) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportDialog(
    exportPreview: com.scto.codelikebastimove.feature.designer.domain.usecase.ExportPreview?,
    exportConfig: ExportConfig,
    isExporting: Boolean,
    exportSuccess: Boolean?,
    exportMessage: String,
    selectedTheme: com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor?,
    onConfigChanged: (ExportConfig) -> Unit,
    onExport: () -> Unit,
    onDismiss: () -> Unit,
    onPickPath: ((String) -> Unit)?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Export Layout",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Export Path",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = exportConfig.exportPath,
                            onValueChange = { 
                                onConfigChanged(exportConfig.copy(exportPath = it))
                            },
                            label = { Text("Path") },
                            placeholder = { Text("e.g., app/src/main/java/com/example/ui") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(
                            onClick = { onPickPath?.invoke("export") }
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = "Browse")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Options",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = exportConfig.includeImports,
                            onCheckedChange = { 
                                onConfigChanged(exportConfig.copy(includeImports = it))
                            }
                        )
                        Text("Include imports")
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = exportConfig.formatCode,
                            onCheckedChange = { 
                                onConfigChanged(exportConfig.copy(formatCode = it))
                            }
                        )
                        Text("Format code")
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = exportConfig.validateSyntax,
                            onCheckedChange = { 
                                onConfigChanged(exportConfig.copy(validateSyntax = it))
                            }
                        )
                        Text("Validate syntax before export")
                    }
                }
            }
            
            if (selectedTheme != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Theme Export",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = exportConfig.themeName,
                            onValueChange = { 
                                onConfigChanged(exportConfig.copy(themeName = it))
                            },
                            label = { Text("Theme Name") },
                            placeholder = { Text("e.g., MyAppTheme") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = exportConfig.exportThemeToRepo,
                                onCheckedChange = { 
                                    onConfigChanged(exportConfig.copy(exportThemeToRepo = it))
                                }
                            )
                            Text("Export to extension repository")
                        }
                        
                        if (exportConfig.exportThemeToRepo) {
                            OutlinedTextField(
                                value = exportConfig.themeRepoName,
                                onValueChange = { 
                                    onConfigChanged(exportConfig.copy(themeRepoName = it))
                                },
                                label = { Text("Repository Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = exportConfig.themeRepoDescription,
                                onValueChange = { 
                                    onConfigChanged(exportConfig.copy(themeRepoDescription = it))
                                },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Component Prefix",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportConfig.componentPrefix,
                        onValueChange = { 
                            onConfigChanged(exportConfig.copy(componentPrefix = it))
                        },
                        label = { Text("Prefix") },
                        placeholder = { Text("e.g., MyApp") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = {
                            Text("Custom components will be prefixed (e.g., MyAppButton)")
                        }
                    )
                }
            }
            
            if (exportPreview?.validation != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (exportPreview.validation.isValid) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (exportPreview.validation.isValid) {
                                Icons.Default.Check
                            } else {
                                Icons.Default.Error
                            },
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = if (exportPreview.validation.isValid) {
                                    "Validation passed"
                                } else {
                                    "Validation failed"
                                },
                                fontWeight = FontWeight.Medium
                            )
                            if (!exportPreview.validation.isValid) {
                                exportPreview.validation.errors.forEach { error ->
                                    Text(
                                        text = error.message,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            if (exportSuccess != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (exportSuccess) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (exportSuccess) Icons.Default.Check else Icons.Default.Error,
                            contentDescription = null
                        )
                        Text(exportMessage)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onExport,
                    modifier = Modifier.weight(1f),
                    enabled = !isExporting && (exportPreview?.validation?.isValid ?: true)
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text("Export")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThemeSelectorDialog(
    themes: List<com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor>,
    selectedTheme: com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor?,
    onThemeSelected: (com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Theme")
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedTheme == null) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    onClick = { onThemeSelected(null) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Default Theme",
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Use Material 3 defaults",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                themes.forEach { theme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTheme?.name == theme.name) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        onClick = { onThemeSelected(theme) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = theme.name,
                                    fontWeight = FontWeight.Medium
                                )
                                if (theme.description.isNotEmpty()) {
                                    Text(
                                        text = theme.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                if (themes.isEmpty()) {
                    Text(
                        text = "No saved themes. Create themes in ThemeBuilder to use here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
