package com.scto.codelikebastimove.feature.submodulemaker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildVariantsScreen(
  projectPath: String = "",
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: BuildVariantsViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(projectPath) { viewModel.loadBuildVariants(projectPath) }

  Scaffold(
    topBar = {
      AdaptiveTopAppBar(
        title = "Build Varianten",
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.loadBuildVariants(projectPath) }) {
            Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren")
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    modifier = modifier,
  ) { paddingValues ->
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(paddingValues)
    ) {
      when {
        uiState.isLoading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator()
              Spacer(modifier = Modifier.height(16.dp))
              Text("Scanne Projekt-Module...")
            }
          }
        }
        uiState.error != null -> {
          Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Icon(
              imageVector = Icons.Default.Error,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = uiState.error ?: "Ein unbekannter Fehler ist aufgetreten",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.error,
              textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.loadBuildVariants(projectPath) }) {
              Icon(Icons.Default.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Erneut versuchen")
            }
          }
        }
        uiState.variants.isEmpty() -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp),
              )
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "Keine Module gefunden",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
              )
              Text(
                text = "Projektpfad: ${if(projectPath.isBlank()) "Nicht gesetzt" else projectPath}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
              )
            }
          }
        }
        else -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            item {
              ProjectInfoHeader(
                projectName = uiState.projectName,
                totalModules = uiState.totalModules,
                androidModules = uiState.androidModules,
              )
              Spacer(modifier = Modifier.height(16.dp))
            }
            
            uiState.moduleGroups.forEach { group ->
              item(key = "group_${group.name}") {
                ModuleGroupHeader(
                  groupName = group.name,
                  moduleCount = group.modules.size,
                  isExpanded = group.isExpanded,
                  onToggle = { viewModel.toggleGroupExpanded(group.name) },
                )
              }
              
              if (group.isExpanded) {
                items(
                  items = group.modules,
                  key = { "module_${it.moduleName}" }
                ) { variant ->
                  AnimatedVisibility(
                    visible = true,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                  ) {
                    BuildVariantCard(
                      variant = variant,
                      onVariantSelected = { newVariant ->
                        viewModel.updateVariant(variant.moduleName, newVariant)
                      },
                    )
                  }
                }
              }
              
              item { Spacer(modifier = Modifier.height(8.dp)) }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ProjectInfoHeader(
  projectName: String,
  totalModules: Int,
  androidModules: Int,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    shape = RoundedCornerShape(12.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.Folder,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
          modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = projectName.ifBlank { "Projekt" },
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
      
      Spacer(modifier = Modifier.height(12.dp))
      
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        StatChip(
          label = "Gesamt",
          value = totalModules.toString(),
          icon = Icons.Default.ViewModule,
        )
        StatChip(
          label = "Android",
          value = androidModules.toString(),
          icon = Icons.Default.Android,
        )
        StatChip(
          label = "Andere",
          value = (totalModules - androidModules).toString(),
          icon = Icons.Default.Code,
        )
      }
    }
  }
}

@Composable
private fun StatChip(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = value,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
      }
    }
  }
}

@Composable
private fun ModuleGroupHeader(
  groupName: String,
  moduleCount: Int,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val rotationAngle by animateFloatAsState(
    targetValue = if (isExpanded) 0f else -90f,
    label = "rotation"
  )
  
  Surface(
    onClick = onToggle,
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
        modifier = Modifier.rotate(rotationAngle).size(24.dp),
        tint = MaterialTheme.colorScheme.primary,
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = groupName,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.weight(1f),
      )
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
      ) {
        Text(
          text = moduleCount.toString(),
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
      }
    }
  }
}

@Composable
private fun BuildVariantCard(
  variant: BuildVariant,
  onVariantSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  
  val moduleTypeIcon = when (variant.moduleType) {
    ModuleType.APPLICATION -> Icons.Default.Android
    ModuleType.LIBRARY -> Icons.Default.LibraryBooks
    ModuleType.JAVA_LIBRARY -> Icons.Default.Code
    ModuleType.KOTLIN_LIBRARY -> Icons.Default.Code
    ModuleType.UNKNOWN -> Icons.Default.ViewModule
  }
  
  val moduleTypeLabel = when (variant.moduleType) {
    ModuleType.APPLICATION -> "App"
    ModuleType.LIBRARY -> "Android Library"
    ModuleType.JAVA_LIBRARY -> "Java Library"
    ModuleType.KOTLIN_LIBRARY -> "Kotlin Library"
    ModuleType.UNKNOWN -> "Modul"
  }
  
  val moduleTypeColor = when (variant.moduleType) {
    ModuleType.APPLICATION -> MaterialTheme.colorScheme.primary
    ModuleType.LIBRARY -> MaterialTheme.colorScheme.secondary
    ModuleType.JAVA_LIBRARY -> MaterialTheme.colorScheme.tertiary
    ModuleType.KOTLIN_LIBRARY -> Color(0xFF7F52FF)
    ModuleType.UNKNOWN -> MaterialTheme.colorScheme.outline
  }
  
  val indentPadding = (variant.depth * 16).dp

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = indentPadding),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(10.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = moduleTypeColor.copy(alpha = 0.12f),
        modifier = Modifier.size(36.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = moduleTypeIcon,
            contentDescription = null,
            tint = moduleTypeColor,
            modifier = Modifier.size(20.dp),
          )
        }
      }
      
      Spacer(modifier = Modifier.width(12.dp))
      
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = getDisplayModuleName(variant.moduleName),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = moduleTypeLabel,
          style = MaterialTheme.typography.labelSmall,
          color = moduleTypeColor.copy(alpha = 0.8f),
        )
      }
      
      Spacer(modifier = Modifier.width(8.dp))
      
      Box {
        Surface(
          onClick = { expanded = true },
          shape = RoundedCornerShape(6.dp),
          color = getBuildTypeColor(variant.activeVariant).copy(alpha = 0.15f),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = variant.activeVariant,
              style = MaterialTheme.typography.labelMedium,
              color = getBuildTypeColor(variant.activeVariant),
              fontWeight = FontWeight.SemiBold,
            )
            if (variant.availableVariants.size > 1) {
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = getBuildTypeColor(variant.activeVariant),
                modifier = Modifier.size(18.dp),
              )
            }
          }
        }

        if (variant.availableVariants.size > 1) {
          DropdownMenu(
            expanded = expanded, 
            onDismissRequest = { expanded = false },
          ) {
            variant.availableVariants.forEach { variantName ->
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .background(
                          color = getBuildTypeColor(variantName),
                          shape = RoundedCornerShape(4.dp),
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                      text = variantName,
                      fontWeight = if (variantName == variant.activeVariant) 
                        FontWeight.Bold else FontWeight.Normal,
                    )
                    if (variantName == variant.activeVariant) {
                      Spacer(modifier = Modifier.width(8.dp))
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                      )
                    }
                  }
                },
                onClick = {
                  onVariantSelected(variantName)
                  expanded = false
                },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun getBuildTypeColor(buildType: String): Color {
  return when {
    buildType.contains("debug", ignoreCase = true) -> Color(0xFF4CAF50)
    buildType.contains("release", ignoreCase = true) -> Color(0xFFE91E63)
    buildType.contains("staging", ignoreCase = true) -> Color(0xFFFF9800)
    buildType.contains("qa", ignoreCase = true) -> Color(0xFF2196F3)
    buildType.contains("benchmark", ignoreCase = true) -> Color(0xFF9C27B0)
    buildType == "main" -> MaterialTheme.colorScheme.outline
    else -> MaterialTheme.colorScheme.tertiary
  }
}

private fun getDisplayModuleName(fullName: String): String {
  val parts = fullName.trimStart(':').split(':')
  return if (parts.size > 1) {
    parts.last()
  } else {
    parts.firstOrNull() ?: fullName
  }
}
