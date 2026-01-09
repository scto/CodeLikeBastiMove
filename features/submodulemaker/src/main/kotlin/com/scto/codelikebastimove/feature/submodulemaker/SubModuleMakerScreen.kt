package com.scto.codelikebastimove.feature.submodulemaker

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import com.scto.codelikebastimove.feature.submodulemaker.model.ProgrammingLanguage

private val SubModuleBackground = Color(0xFF121212)
private val SubModuleCardBackground = Color(0xFF1E1E1E)
private val SubModuleAccentColor = Color(0xFF8B7355)
private val SubModuleTextColor = Color(0xFFE0D4C8)
private val SubModuleSecondaryText = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubModuleMakerScreen(
    onBackClick: () -> Unit,
    onCreateModule: (ModuleConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var moduleName by remember { mutableStateOf("") }
    var folderPath by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(ProgrammingLanguage.KOTLIN) }

    Scaffold(
        topBar = {
            AdaptiveTopAppBar(
                title = "Sub-Module Maker",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SubModuleTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SubModuleBackground,
                    titleContentColor = SubModuleTextColor
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = SubModuleBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SubModuleBackground)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderCard()

            ModuleConfigurationCard(
                moduleName = moduleName,
                folderPath = folderPath,
                selectedLanguage = selectedLanguage,
                onModuleNameChange = { moduleName = it },
                onFolderPathChange = { folderPath = it },
                onLanguageSelected = { selectedLanguage = it },
                onCreateClick = {
                    val gradlePath = buildGradlePath(folderPath, moduleName)
                    val config = ModuleConfig(
                        gradlePath = gradlePath,
                        language = selectedLanguage,
                        packageName = ""
                    )
                    onCreateModule(config)
                }
            )

            AboutCard()
        }
    }
}

private fun buildGradlePath(folderPath: String, moduleName: String): String {
    val cleanFolder = folderPath.trim().replace("/", ":").trimStart(':').trimEnd(':')
    val cleanModule = moduleName.trim().replace(" ", "-").lowercase()
    
    return if (cleanFolder.isNotEmpty()) {
        ":$cleanFolder:$cleanModule"
    } else {
        ":$cleanModule"
    }
}

@Composable
private fun HeaderCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Sub-Module Maker",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SubModuleTextColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create new sub-modules for your project",
                style = MaterialTheme.typography.bodyMedium,
                color = SubModuleSecondaryText
            )
        }
    }
}

@Composable
private fun ModuleConfigurationCard(
    moduleName: String,
    folderPath: String,
    selectedLanguage: ProgrammingLanguage,
    onModuleNameChange: (String) -> Unit,
    onFolderPathChange: (String) -> Unit,
    onLanguageSelected: (ProgrammingLanguage) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Module Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SubModuleTextColor
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Programming Language",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = SubModuleTextColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProgrammingLanguage.entries.forEach { language ->
                        FilterChip(
                            selected = selectedLanguage == language,
                            onClick = { onLanguageSelected(language) },
                            label = { 
                                Text(
                                    text = language.displayName,
                                    color = if (selectedLanguage == language) 
                                        Color.White else SubModuleTextColor
                                ) 
                            },
                            leadingIcon = if (selectedLanguage == language) {
                                { 
                                    Icon(
                                        Icons.Default.Check, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    ) 
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SubModuleAccentColor,
                                containerColor = Color(0xFF2A2A2A),
                                selectedLabelColor = Color.White,
                                labelColor = SubModuleTextColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedLanguage == language,
                                borderColor = Color(0xFF444444),
                                selectedBorderColor = SubModuleAccentColor
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = folderPath,
                onValueChange = onFolderPathChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        "Enter folder path (e.g., features, core/ui)", 
                        color = SubModuleSecondaryText
                    ) 
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Extension,
                        contentDescription = null,
                        tint = SubModuleSecondaryText
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SubModuleTextColor,
                    unfocusedTextColor = SubModuleTextColor,
                    focusedBorderColor = SubModuleAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = SubModuleAccentColor,
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = moduleName,
                onValueChange = onModuleNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        "Enter module name (e.g., login, network)", 
                        color = SubModuleSecondaryText
                    ) 
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Extension,
                        contentDescription = null,
                        tint = SubModuleSecondaryText
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SubModuleTextColor,
                    unfocusedTextColor = SubModuleTextColor,
                    focusedBorderColor = SubModuleAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = SubModuleAccentColor,
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (moduleName.isNotBlank()) {
                val gradlePath = buildGradlePath(folderPath, moduleName)
                Text(
                    text = "Gradle path: $gradlePath",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubModuleAccentColor
                )
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = moduleName.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SubModuleAccentColor,
                    contentColor = Color.White,
                    disabledContainerColor = SubModuleAccentColor.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Module")
            }
        }
    }
}

@Composable
private fun AboutCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SubModuleCardBackground
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "About Sub-Modules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SubModuleTextColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sub-modules help organize your project into smaller, manageable components. Each module can have its own dependencies and build configuration.",
                style = MaterialTheme.typography.bodyMedium,
                color = SubModuleSecondaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gradle notation: :folderName:moduleName",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = SubModuleAccentColor
            )
        }
    }
}
