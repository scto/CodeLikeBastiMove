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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.core.datastore.ProjectTemplateType
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar
import com.scto.codelikebastimove.core.ui.util.isLandscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onBackClick: () -> Unit,
    onCreateProject: (name: String, packageName: String, templateType: ProjectTemplateType, minSdk: Int, useKotlin: Boolean, useKotlinDsl: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var projectName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.example.myapp") }
    var selectedTemplate by remember { mutableStateOf(ProjectTemplateType.EMPTY_COMPOSE) }
    var minSdk by remember { mutableIntStateOf(24) }
    var useKotlin by remember { mutableStateOf(true) }
    var useKotlinDsl by remember { mutableStateOf(true) }
    var showMinSdkDropdown by remember { mutableStateOf(false) }
    
    val minSdkOptions = listOf(21, 23, 24, 26, 28, 29, 30, 31, 33, 34)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AdaptiveTopAppBar(
            title = "Neues Projekt erstellen",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.statusBarsPadding()
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00D9FF),
                                    Color(0xFF00B4D8),
                                    Color(0xFF7C3AED),
                                    Color(0xFFA855F7)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CLBM",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Text(
                text = "Projektdetails",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            OutlinedTextField(
                value = projectName,
                onValueChange = { 
                    projectName = it
                    if (packageName == "com.example.myapp" || packageName.endsWith(".myapp")) {
                        val safeName = it.lowercase().replace(" ", "").replace("-", "")
                        packageName = "com.example.$safeName"
                    }
                },
                label = { Text("Projektname") },
                placeholder = { Text("MyApplication") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                label = { Text("Package Name") },
                placeholder = { Text("com.example.myapp") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Box {
                OutlinedTextField(
                    value = "API $minSdk",
                    onValueChange = {},
                    label = { Text("Minimum SDK") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showMinSdkDropdown = true }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Select")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                DropdownMenu(
                    expanded = showMinSdkDropdown,
                    onDismissRequest = { showMinSdkDropdown = false }
                ) {
                    minSdkOptions.forEach { sdk ->
                        DropdownMenuItem(
                            text = { Text("API $sdk") },
                            onClick = {
                                minSdk = sdk
                                showMinSdkDropdown = false
                            }
                        )
                    }
                }
            }
            
            Text(
                text = "Vorlage auswählen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            ProjectTemplateType.entries.forEach { template ->
                TemplateCard(
                    template = template,
                    isSelected = selectedTemplate == template,
                    onClick = { selectedTemplate = template }
                )
            }
            
            Text(
                text = "Sprache & Build",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kotlin verwenden")
                Switch(
                    checked = useKotlin,
                    onCheckedChange = { useKotlin = it }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kotlin DSL (build.gradle.kts)")
                Switch(
                    checked = useKotlinDsl,
                    onCheckedChange = { useKotlinDsl = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        onCreateProject(
                            projectName,
                            packageName,
                            selectedTemplate,
                            minSdk,
                            useKotlin,
                            useKotlinDsl
                        )
                    }
                },
                enabled = projectName.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Projekt erstellen", fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TemplateCard(
    template: ProjectTemplateType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = getTemplateDescription(template),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun getTemplateDescription(template: ProjectTemplateType): String {
    return when (template) {
        ProjectTemplateType.EMPTY_ACTIVITY -> "Eine leere Activity mit XML-Layouts"
        ProjectTemplateType.EMPTY_COMPOSE -> "Eine leere Activity mit Jetpack Compose UI"
        ProjectTemplateType.BOTTOM_NAVIGATION -> "Activity mit Bottom Navigation Bar"
        ProjectTemplateType.NAVIGATION_DRAWER -> "Activity mit Navigation Drawer"
        ProjectTemplateType.TABBED -> "Activity mit Tabs und ViewPager"
    }
}
