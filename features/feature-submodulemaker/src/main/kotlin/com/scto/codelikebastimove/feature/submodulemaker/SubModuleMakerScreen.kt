package com.scto.codelikebastimove.feature.submodulemaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.scto.codelikebastimove.feature.submodulemaker.components.ComposeToggle
import com.scto.codelikebastimove.feature.submodulemaker.components.LanguageSelector
import com.scto.codelikebastimove.feature.submodulemaker.components.ModulePathInput
import com.scto.codelikebastimove.feature.submodulemaker.components.ModulePreviewCard
import com.scto.codelikebastimove.feature.submodulemaker.components.ModuleTypeSelector
import com.scto.codelikebastimove.feature.submodulemaker.components.PackageNameInput
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleConfig
import com.scto.codelikebastimove.feature.submodulemaker.model.ModuleType
import com.scto.codelikebastimove.feature.submodulemaker.model.ProgrammingLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubModuleMakerScreen(
    onBackClick: () -> Unit,
    onCreateModule: (ModuleConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var config by remember { mutableStateOf(ModuleConfig()) }

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
            HeaderCard()

            ConfigurationCard(
                config = config,
                onConfigChange = { config = it }
            )

            ModulePreviewCard(config = config)

            CreateButton(
                enabled = config.isValid,
                onClick = { onCreateModule(config) }
            )
        }
    }
}

@Composable
private fun HeaderCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = "Erstelle neue Sub-Module mit Gradle Notation. Die Module werden automatisch in settings.gradle.kts eingetragen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ConfigurationCard(
    config: ModuleConfig,
    onConfigChange: (ModuleConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Konfiguration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LanguageSelector(
                selectedLanguage = config.language,
                onLanguageSelected = { onConfigChange(config.copy(language = it)) }
            )

            ModuleTypeSelector(
                selectedType = config.moduleType,
                onTypeSelected = { onConfigChange(config.copy(moduleType = it)) }
            )

            ComposeToggle(
                enabled = config.useCompose,
                onToggle = { onConfigChange(config.copy(useCompose = it)) }
            )

            ModulePathInput(
                value = config.gradlePath,
                onValueChange = { newPath ->
                    val newPackage = if (config.packageName.isEmpty() && newPath.isNotEmpty()) {
                        val cleanPath = newPath.replace(":", ".").trim('.')
                        "com.example.app.$cleanPath"
                    } else {
                        config.packageName
                    }
                    onConfigChange(config.copy(gradlePath = newPath, packageName = newPackage))
                }
            )

            PackageNameInput(
                value = config.packageName,
                onValueChange = { onConfigChange(config.copy(packageName = it)) }
            )
        }
    }
}

@Composable
private fun CreateButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
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
