package com.scto.codelikebastimove.feature.submodulemaker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleType
import com.scto.codelikebastimove.feature.submodulemaker.model.ProgrammingLanguage

private val SubModuleBackground = Color(0xFF121212)
private val SubModuleCardBackground = Color(0xFF1E1E1E)
private val SubModuleAccentColor = Color(0xFF8B7355)
private val SubModuleTextColor = Color(0xFFE0D4C8)
private val SubModuleSecondaryText = Color(0xFF888888)
private val SuccessColor = Color(0xFF4CAF50)
private val ErrorColor = Color(0xFFE57373)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubModuleMakerScreen(
    onBackClick: () -> Unit,
    viewModel: SubModuleMakerViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.createResult) {
        uiState.createResult?.let { result ->
            when (result) {
                is CreateModuleResult.Success -> {
                    snackbarHostState.showSnackbar("Module created: ${result.gradlePath}")
                }
                is CreateModuleResult.Error -> {
                    snackbarHostState.showSnackbar("Error: ${result.message}")
                }
            }
            viewModel.clearResult()
        }
    }

    Scaffold(
        topBar = {
            AdaptiveTopAppBar(
                title = stringResource(R.string.sub_module_maker),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.close),
                            tint = SubModuleTextColor,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SubModuleBackground,
                    titleContentColor = SubModuleTextColor,
                ),
                modifier = Modifier.statusBarsPadding(),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SubModuleBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SubModuleBackground)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GradleNotationCard(
                notation = uiState.gradleNotation,
                parsedNotation = uiState.parsedNotation,
                error = uiState.notationError,
                suggestions = uiState.suggestedNotations,
                onNotationChange = viewModel::setGradleNotation,
                onSuggestionClick = viewModel::selectSuggestion,
            )

            ModuleOptionsCard(
                selectedLanguage = uiState.selectedLanguage,
                selectedModuleType = uiState.selectedModuleType,
                useCompose = uiState.useCompose,
                onLanguageSelected = viewModel::setLanguage,
                onModuleTypeSelected = viewModel::setModuleType,
                onUseComposeChange = viewModel::setUseCompose,
            )

            AdvancedOptionsCard(
                basePackageName = uiState.basePackageName,
                customPackageName = uiState.customPackageName,
                generatedPackage = viewModel.getGeneratedPackageName(),
                minSdk = uiState.minSdk,
                targetSdk = uiState.targetSdk,
                onBasePackageChange = viewModel::setBasePackageName,
                onCustomPackageChange = viewModel::setCustomPackageName,
                onMinSdkChange = viewModel::setMinSdk,
                onTargetSdkChange = viewModel::setTargetSdk,
            )

            PreviewCard(
                config = viewModel.getPreviewConfig(),
                basePackage = uiState.basePackageName,
            )

            CreateButton(
                enabled = uiState.parsedNotation != null && !uiState.isCreating,
                isLoading = uiState.isCreating,
                onClick = { viewModel.createModule(context) },
            )

            AboutCard()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GradleNotationCard(
    notation: String,
    parsedNotation: com.scto.codelikebastimove.feature.submodulemaker.model.GradleNotation?,
    error: String?,
    suggestions: List<String>,
    onNotationChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = SubModuleAccentColor,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Gradle Notation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SubModuleTextColor,
                    )
                    Text(
                        text = "Enter module path like :core:common or :feature:auth",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubModuleSecondaryText,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notation,
                onValueChange = onNotationChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = ":feature:my-module",
                        color = SubModuleSecondaryText,
                        fontFamily = FontFamily.Monospace,
                    )
                },
                leadingIcon = {
                    Text(
                        text = ":",
                        color = SubModuleAccentColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                trailingIcon = {
                    if (parsedNotation != null) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Valid",
                            tint = SuccessColor,
                        )
                    } else if (error != null) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = ErrorColor,
                        )
                    }
                },
                isError = error != null,
                supportingText = {
                    when {
                        error != null -> Text(error, color = ErrorColor)
                        parsedNotation != null -> Text(
                            "Directory: ${parsedNotation.directoryPath}",
                            color = SuccessColor,
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SubModuleTextColor,
                    unfocusedTextColor = SubModuleTextColor,
                    focusedBorderColor = SubModuleAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    errorBorderColor = ErrorColor,
                    cursorColor = SubModuleAccentColor,
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done,
                ),
            )

            AnimatedVisibility(visible = suggestions.isNotEmpty()) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Suggestions",
                        style = MaterialTheme.typography.labelMedium,
                        color = SubModuleSecondaryText,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        suggestions.take(6).forEach { suggestion ->
                            AssistChip(
                                onClick = { onSuggestionClick(suggestion) },
                                label = {
                                    Text(
                                        text = suggestion,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFF2A2A2A),
                                    labelColor = SubModuleTextColor,
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = Color(0xFF444444),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleOptionsCard(
    selectedLanguage: ProgrammingLanguage,
    selectedModuleType: ModuleType,
    useCompose: Boolean,
    onLanguageSelected: (ProgrammingLanguage) -> Unit,
    onModuleTypeSelected: (ModuleType) -> Unit,
    onUseComposeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = SubModuleAccentColor,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Module Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SubModuleTextColor,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Programming Language",
                style = MaterialTheme.typography.labelLarge,
                color = SubModuleTextColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProgrammingLanguage.entries.forEach { language ->
                    FilterChip(
                        selected = selectedLanguage == language,
                        onClick = { onLanguageSelected(language) },
                        label = {
                            Text(
                                text = language.displayName,
                                color = if (selectedLanguage == language) Color.White else SubModuleTextColor,
                            )
                        },
                        leadingIcon = if (selectedLanguage == language) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White,
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SubModuleAccentColor,
                            containerColor = Color(0xFF2A2A2A),
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedLanguage == language,
                            borderColor = Color(0xFF444444),
                            selectedBorderColor = SubModuleAccentColor,
                        ),
                        shape = RoundedCornerShape(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Module Type",
                style = MaterialTheme.typography.labelLarge,
                color = SubModuleTextColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedModuleType == type,
                        onClick = { onModuleTypeSelected(type) },
                        label = {
                            Text(
                                text = type.displayName,
                                color = if (selectedModuleType == type) Color.White else SubModuleTextColor,
                            )
                        },
                        leadingIcon = if (selectedModuleType == type) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White,
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SubModuleAccentColor,
                            containerColor = Color(0xFF2A2A2A),
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedModuleType == type,
                            borderColor = Color(0xFF444444),
                            selectedBorderColor = SubModuleAccentColor,
                        ),
                        shape = RoundedCornerShape(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Jetpack Compose",
                        style = MaterialTheme.typography.labelLarge,
                        color = SubModuleTextColor,
                    )
                    Text(
                        text = "Add Compose support and sample screen",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubModuleSecondaryText,
                    )
                }
                Switch(
                    checked = useCompose,
                    onCheckedChange = onUseComposeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SubModuleAccentColor,
                        uncheckedThumbColor = SubModuleSecondaryText,
                        uncheckedTrackColor = Color(0xFF2A2A2A),
                    ),
                )
            }
        }
    }
}

@Composable
private fun AdvancedOptionsCard(
    basePackageName: String,
    customPackageName: String,
    generatedPackage: String,
    minSdk: Int,
    targetSdk: Int,
    onBasePackageChange: (String) -> Unit,
    onCustomPackageChange: (String) -> Unit,
    onMinSdkChange: (Int) -> Unit,
    onTargetSdkChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = null,
                    tint = SubModuleAccentColor,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Advanced Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SubModuleTextColor,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = basePackageName,
                onValueChange = onBasePackageChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Base Package Name", color = SubModuleSecondaryText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SubModuleTextColor,
                    unfocusedTextColor = SubModuleTextColor,
                    focusedBorderColor = SubModuleAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = SubModuleAccentColor,
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customPackageName,
                onValueChange = onCustomPackageChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Custom Package (optional)", color = SubModuleSecondaryText) },
                placeholder = { Text(generatedPackage, color = SubModuleSecondaryText.copy(alpha = 0.5f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SubModuleTextColor,
                    unfocusedTextColor = SubModuleTextColor,
                    focusedBorderColor = SubModuleAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = SubModuleAccentColor,
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
            )

            if (generatedPackage.isNotBlank() && customPackageName.isBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Generated: $generatedPackage",
                    style = MaterialTheme.typography.bodySmall,
                    color = SuccessColor,
                    fontFamily = FontFamily.Monospace,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = minSdk.toString(),
                    onValueChange = { it.toIntOrNull()?.let(onMinSdkChange) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Min SDK", color = SubModuleSecondaryText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SubModuleTextColor,
                        unfocusedTextColor = SubModuleTextColor,
                        focusedBorderColor = SubModuleAccentColor,
                        unfocusedBorderColor = Color(0xFF444444),
                        cursorColor = SubModuleAccentColor,
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = targetSdk.toString(),
                    onValueChange = { it.toIntOrNull()?.let(onTargetSdkChange) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Target SDK", color = SubModuleSecondaryText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SubModuleTextColor,
                        unfocusedTextColor = SubModuleTextColor,
                        focusedBorderColor = SubModuleAccentColor,
                        unfocusedBorderColor = Color(0xFF444444),
                        cursorColor = SubModuleAccentColor,
                        focusedContainerColor = Color(0xFF2A2A2A),
                        unfocusedContainerColor = Color(0xFF2A2A2A),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }
    }
}

@Composable
private fun PreviewCard(
    config: com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig?,
    basePackage: String,
    modifier: Modifier = Modifier,
) {
    if (config == null) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = SubModuleAccentColor,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SubModuleTextColor,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PreviewRow("Gradle Path", config.gradlePath)
            PreviewRow("Directory", config.directoryPath)
            PreviewRow("Module Name", config.moduleName)
            PreviewRow("Package", config.generatePackageName(basePackage))
            PreviewRow("Language", config.language.displayName)
            PreviewRow("Type", config.moduleType.displayName)
            PreviewRow("Compose", if (config.useCompose) "Yes" else "No")
        }
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SubModuleSecondaryText,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = SubModuleTextColor,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun CreateButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SubModuleAccentColor,
            contentColor = Color.White,
            disabledContainerColor = SubModuleAccentColor.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f),
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (isLoading) "Creating..." else "Create Module")
    }
}

@Composable
private fun AboutCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "About Gradle Notation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SubModuleTextColor,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gradle notation is the standard way to reference modules in a multi-module Android project. It uses colons (:) to separate path segments.",
                style = MaterialTheme.typography.bodyMedium,
                color = SubModuleSecondaryText,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NotationExample(":app", "Root-level app module")
                NotationExample(":core:common", "common module inside core folder")
                NotationExample(":feature:auth", "auth module inside feature folder")
                NotationExample(":data:repository", "repository module inside data folder")
            }
        }
    }
}

@Composable
private fun NotationExample(notation: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2A2A2A))
                .border(1.dp, Color(0xFF444444), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = notation,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = SubModuleAccentColor,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = SubModuleSecondaryText,
        )
    }
}
