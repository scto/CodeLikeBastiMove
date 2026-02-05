package com.scto.codelikebastimove.feature.treeview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class DrawerTab(val title: String, val icon: ImageVector) {
  FILES("Files", Icons.Default.Folder),
  BUILD("Build", Icons.Default.Android),
  MODULE("Module", Icons.Default.Extension),
  ASSETS("Assets", Icons.Default.Brush),
  GIT("Git", Icons.Default.MergeType),
  THEME("Theme", Icons.Default.Palette),
  LAYOUT("Layout", Icons.Default.ViewQuilt),
  SETTINGS("Settings", Icons.Default.Settings),
  TERMINAL("Terminal", Icons.Default.Terminal),
}

@Composable
fun FileTreeDrawer(
  projectName: String,
  projectPath: String,
  onFileClick: (FileTreeNode) -> Unit,
  onFileOperationComplete: () -> Unit,
  onOpenTerminalSheet: () -> Unit = {},
  onNavigateToSettings: () -> Unit = {},
  fileSystemVersion: Long = 0L,
  modifier: Modifier = Modifier,
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = DrawerTab.entries

  Column(modifier = modifier.fillMaxHeight()) {
    DrawerHeader(title = projectName)

    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      edgePadding = 8.dp,
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
      contentColor = MaterialTheme.colorScheme.onSurface,
      divider = {},
    ) {
      tabs.forEachIndexed { index, tab ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = tab.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
              Text(text = tab.title, fontSize = 12.sp)
            }
          },
        )
      }
    }

    HorizontalDivider()

    AnimatedContent(
      targetState = selectedTab,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      modifier = Modifier.weight(1f),
      label = "drawer_tab_content",
    ) { tabIndex ->
      when (tabs[tabIndex]) {
        DrawerTab.FILES -> {
          EnhancedTreeView(
            rootPath = projectPath,
            projectName = projectName,
            onFileClick = onFileClick,
            onFileOperationComplete = onFileOperationComplete,
            fileSystemVersion = fileSystemVersion,
          )
        }
        DrawerTab.BUILD -> {
          BuildVariantsTabContent()
        }
        DrawerTab.MODULE -> {
          SubModuleMakerTabContent()
        }
        DrawerTab.ASSETS -> {
          AssetStudioTabContent()
        }
        DrawerTab.GIT -> {
          GitTabContent()
        }
        DrawerTab.THEME -> {
          ThemeTabContent()
        }
        DrawerTab.LAYOUT -> {
          LayoutTabContent()
        }
        DrawerTab.SETTINGS -> {
          SettingsTabContent(onNavigateToSettings = onNavigateToSettings)
        }
        DrawerTab.TERMINAL -> {
          TerminalTabContent(onOpenTerminalSheet = onOpenTerminalSheet)
        }
      }
    }
  }
}

@Composable
private fun DrawerHeader(title: String, modifier: Modifier = Modifier) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    modifier = modifier.fillMaxWidth(),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(16.dp),
    )
  }
}

@Composable
private fun BuildVariantsTabContent(modifier: Modifier = Modifier) {
  var selectedModule by remember { mutableStateOf("app") }
  val modules = listOf("app", "core:core-ui", "core:core-datastore", "features:feature-main")
  val variants = listOf("debug", "release")
  var selectedVariant by remember { mutableStateOf("debug") }

  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Build Varianten",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Text(
      text = "Modul auswählen",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      modules.forEach { module ->
        FilterChip(
          selected = selectedModule == module,
          onClick = { selectedModule = module },
          label = { Text(module, fontSize = 11.sp) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }

    Text(
      text = "Variante",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      variants.forEach { variant ->
        FilterChip(
          selected = selectedVariant == variant,
          onClick = { selectedVariant = variant },
          label = { Text(variant) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
      Icon(Icons.Default.PlayArrow, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Build starten")
    }
  }
}

@Composable
private fun SubModuleMakerTabContent(modifier: Modifier = Modifier) {
  var moduleName by remember { mutableStateOf("") }
  var selectedLanguage by remember { mutableStateOf("Kotlin") }
  var selectedType by remember { mutableStateOf("Library") }

  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Sub-Module erstellen",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    OutlinedTextField(
      value = moduleName,
      onValueChange = { moduleName = it },
      label = { Text("Modulname") },
      placeholder = { Text("z.B. feature-auth") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )

    Text(
      text = "Sprache",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf("Kotlin", "Java").forEach { lang ->
        FilterChip(
          selected = selectedLanguage == lang,
          onClick = { selectedLanguage = lang },
          label = { Text(lang) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }

    Text(
      text = "Modultyp",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf("Application", "Library").forEach { type ->
        FilterChip(
          selected = selectedType == type,
          onClick = { selectedType = type },
          label = { Text(type) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Button(onClick = {}, enabled = moduleName.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
      Icon(Icons.Default.Extension, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Modul erstellen")
    }
  }
}

@Composable
private fun AssetStudioTabContent(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Asset Studio",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = Icons.Default.Brush,
          contentDescription = null,
          modifier = Modifier.size(48.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Launch Asset Studio",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    }

    Text(
      text = "Quick Actions",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AssetQuickAction(
      title = "Create Drawable",
      description = "Vector or shape drawable",
      onClick = {},
    )

    AssetQuickAction(title = "Create Icon", description = "Launcher or action icon", onClick = {})

    AssetQuickAction(title = "Import Image", description = "From gallery or file", onClick = {})
  }
}

@Composable
private fun AssetQuickAction(
  title: String,
  description: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun TerminalTabContent(onOpenTerminalSheet: () -> Unit, modifier: Modifier = Modifier) {
  var terminalInput by remember { mutableStateOf("") }
  val terminalOutput = remember {
    mutableStateOf(listOf("$ gradle --version", "Gradle 8.14.3", "Kotlin: 2.2.20", "$ "))
  }

  Column(modifier = modifier.fillMaxSize().background(Color(0xFF1E1E1E))) {
    Row(
      modifier = Modifier.fillMaxWidth().background(Color(0xFF2D2D2D)).padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = "Terminal", color = Color.White, fontWeight = FontWeight.Medium)

      OutlinedButton(onClick = onOpenTerminalSheet) {
        Icon(
          imageVector = Icons.Default.Terminal,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Full Terminal", fontSize = 12.sp)
      }
    }

    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp)) {
      items(terminalOutput.value) { line ->
        Text(
          text = line,
          color = if (line.startsWith("$")) Color(0xFF4EC9B0) else Color.White,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth().background(Color(0xFF2D2D2D)).padding(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "$ ",
        color = Color(0xFF4EC9B0),
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
      )

      BasicTextField(
        value = terminalInput,
        onValueChange = { terminalInput = it },
        textStyle =
          TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
        cursorBrush = SolidColor(Color.White),
        modifier = Modifier.weight(1f),
      )

      IconButton(
        onClick = {
          if (terminalInput.isNotBlank()) {
            val newOutput = terminalOutput.value.toMutableList()
            newOutput.add("$ $terminalInput")
            newOutput.add("Command executed: $terminalInput")
            newOutput.add("$ ")
            terminalOutput.value = newOutput
            terminalInput = ""
          }
        }
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Run",
          tint = Color(0xFF4EC9B0),
        )
      }
    }
  }
}

@Composable
private fun GitTabContent(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Git Version Control",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = Icons.Default.MergeType,
          contentDescription = null,
          modifier = Modifier.size(48.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Open Git Panel",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    }

    Text(
      text = "Quick Actions",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AssetQuickAction(title = "Commit Changes", description = "Stage and commit files", onClick = {})
    AssetQuickAction(title = "Push", description = "Push commits to remote", onClick = {})
    AssetQuickAction(title = "Pull", description = "Pull changes from remote", onClick = {})
  }
}

@Composable
private fun ThemeTabContent(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Theme Builder",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = null,
          modifier = Modifier.size(48.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Open Theme Builder",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    }

    Text(
      text = "Quick Themes",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AssetQuickAction(title = "Material Default", description = "Standard Material 3 colors", onClick = {})
    AssetQuickAction(title = "Dynamic Colors", description = "System wallpaper-based theme", onClick = {})
  }
}

@Composable
private fun LayoutTabContent(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Layout Designer",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = Icons.Default.ViewQuilt,
          contentDescription = null,
          modifier = Modifier.size(48.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Open Layout Designer",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    }

    Text(
      text = "Templates",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AssetQuickAction(title = "Empty Activity", description = "Blank Compose layout", onClick = {})
    AssetQuickAction(title = "List Layout", description = "LazyColumn with items", onClick = {})
  }
}

@Composable
private fun SettingsTabContent(
  onNavigateToSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Settings",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )

    Card(
      onClick = onNavigateToSettings,
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          modifier = Modifier.size(48.dp),
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Open Settings",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    }

    Text(
      text = "Quick Settings",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AssetQuickAction(title = "General", description = "Theme and appearance", onClick = {})
    AssetQuickAction(title = "Editor", description = "Font, tabs, and formatting", onClick = {})
    AssetQuickAction(title = "Build & Run", description = "Build configuration", onClick = {})
  }
}
