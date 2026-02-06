package com.scto.codelikebastimove.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.datastore.EditorSettings
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorSettingsScreen(
  viewModel: SettingsViewModel = viewModel(),
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userPreferences by viewModel.userPreferences.collectAsState()
  val editorSettings = userPreferences.editorSettings

  var showFontSizeDialog by remember { mutableStateOf(false) }
  var showFontFamilyDialog by remember { mutableStateOf(false) }
  var showTabSizeDialog by remember { mutableStateOf(false) }
  var showEditorThemeDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      AdaptiveTopAppBar(
        title = "Editor-Einstellungen",
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.statusBarsPadding(),
      )
    },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    modifier = modifier,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
    ) {
      SettingsCategoryHeader(title = "Schrift")

      SliderSettingRow(
        title = "Schriftgröße",
        description = "${editorSettings.fontSize.toInt()} sp",
        icon = Icons.Default.FormatSize,
        value = editorSettings.fontSize,
        valueRange = 8f..32f,
        onValueChange = { viewModel.setEditorFontSize(it) },
      )

      ClickableSettingRow(
        title = "Schriftart",
        description = editorSettings.fontFamily,
        icon = Icons.Default.TextFields,
        onClick = { showFontFamilyDialog = true },
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = "Einrückung")

      SliderSettingRow(
        title = "Tab-Größe",
        description = "${editorSettings.tabSize} Leerzeichen",
        icon = Icons.Default.FormatIndentIncrease,
        value = editorSettings.tabSize.toFloat(),
        valueRange = 2f..8f,
        steps = 5,
        onValueChange = { viewModel.setEditorTabSize(it.toInt()) },
      )

      ToggleSettingRow(
        title = "Soft Tabs",
        description = "Leerzeichen statt Tabs verwenden",
        icon = Icons.Default.SpaceBar,
        isEnabled = editorSettings.useSoftTabs,
        onToggle = { viewModel.setEditorUseSoftTabs(!editorSettings.useSoftTabs) },
      )

      ToggleSettingRow(
        title = "Auto-Einrückung",
        description = "Automatische Einrückung beim Zeilenumbruch",
        icon = Icons.Default.FormatIndentIncrease,
        isEnabled = editorSettings.autoIndent,
        onToggle = { viewModel.setEditorAutoIndent(!editorSettings.autoIndent) },
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = "Anzeige")

      ToggleSettingRow(
        title = "Zeilennummern",
        description = "Zeilennummern am linken Rand anzeigen",
        icon = Icons.Default.FormatLineSpacing,
        isEnabled = editorSettings.showLineNumbers,
        onToggle = { viewModel.setEditorShowLineNumbers(!editorSettings.showLineNumbers) },
      )

      ToggleSettingRow(
        title = "Zeilenumbruch",
        description = "Lange Zeilen automatisch umbrechen",
        icon = Icons.Default.WrapText,
        isEnabled = editorSettings.wordWrap,
        onToggle = { viewModel.setEditorWordWrap(!editorSettings.wordWrap) },
      )

      ToggleSettingRow(
        title = "Aktuelle Zeile hervorheben",
        description = "Die aktuelle Cursorzeile hervorheben",
        icon = Icons.Default.FormatLineSpacing,
        isEnabled = editorSettings.highlightCurrentLine,
        onToggle = { viewModel.setEditorHighlightCurrentLine(!editorSettings.highlightCurrentLine) },
      )

      ToggleSettingRow(
        title = "Leerzeichen anzeigen",
        description = "Unsichtbare Zeichen sichtbar machen",
        icon = Icons.Default.SpaceBar,
        isEnabled = editorSettings.showWhitespace,
        onToggle = { viewModel.setEditorShowWhitespace(!editorSettings.showWhitespace) },
      )

      ToggleSettingRow(
        title = "Minimap",
        description = "Code-Übersicht am rechten Rand anzeigen",
        icon = Icons.Default.Map,
        isEnabled = editorSettings.minimapEnabled,
        onToggle = { viewModel.setEditorMinimapEnabled(!editorSettings.minimapEnabled) },
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = "Code-Bearbeitung")

      ToggleSettingRow(
        title = "Klammer-Matching",
        description = "Passende Klammern hervorheben",
        icon = Icons.Default.Code,
        isEnabled = editorSettings.bracketMatching,
        onToggle = { viewModel.setEditorBracketMatching(!editorSettings.bracketMatching) },
      )

      ToggleSettingRow(
        title = "Auto-Klammern",
        description = "Klammern automatisch schließen",
        icon = Icons.Default.Code,
        isEnabled = editorSettings.autoCloseBrackets,
        onToggle = { viewModel.setEditorAutoCloseBrackets(!editorSettings.autoCloseBrackets) },
      )

      ToggleSettingRow(
        title = "Auto-Anführungszeichen",
        description = "Anführungszeichen automatisch schließen",
        icon = Icons.Default.Code,
        isEnabled = editorSettings.autoCloseQuotes,
        onToggle = { viewModel.setEditorAutoCloseQuotes(!editorSettings.autoCloseQuotes) },
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()

      SettingsCategoryHeader(title = "Erscheinungsbild")

      ClickableSettingRow(
        title = "Editor-Theme",
        description = editorSettings.editorTheme,
        icon = Icons.Default.Palette,
        onClick = { showEditorThemeDialog = true },
      )

      ToggleSettingRow(
        title = "Smooth Scrolling",
        description = "Flüssiges Scrollen aktivieren",
        icon = Icons.Default.FormatLineSpacing,
        isEnabled = editorSettings.smoothScrolling,
        onToggle = { viewModel.setEditorSmoothScrolling(!editorSettings.smoothScrolling) },
      )

      ToggleSettingRow(
        title = "Sticky Scroll",
        description = "Funktionskopf beim Scrollen fixieren",
        icon = Icons.Default.FormatLineSpacing,
        isEnabled = editorSettings.stickyScroll,
        onToggle = { viewModel.setEditorStickyScroll(!editorSettings.stickyScroll) },
      )

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  if (showFontFamilyDialog) {
    FontFamilySelectionDialog(
      currentFontFamily = editorSettings.fontFamily,
      onFontFamilySelected = { fontFamily ->
        viewModel.setEditorFontFamily(fontFamily)
        showFontFamilyDialog = false
      },
      onDismiss = { showFontFamilyDialog = false },
    )
  }

  if (showEditorThemeDialog) {
    EditorThemeSelectionDialog(
      currentTheme = editorSettings.editorTheme,
      onThemeSelected = { theme ->
        viewModel.setEditorTheme(theme)
        showEditorThemeDialog = false
      },
      onDismiss = { showEditorThemeDialog = false },
    )
  }
}

@Composable
private fun SettingsCategoryHeader(title: String, modifier: Modifier = Modifier) {
  Text(
    text = title,
    style = MaterialTheme.typography.labelLarge,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Medium,
    modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
  )
}

@Composable
private fun ToggleSettingRow(
  title: String,
  description: String,
  icon: ImageVector,
  isEnabled: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onToggle)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(24.dp),
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      )
    }

    Switch(checked = isEnabled, onCheckedChange = { onToggle() })
  }
}

@Composable
private fun ClickableSettingRow(
  title: String,
  description: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(24.dp),
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      )
    }
  }
}

@Composable
private fun SliderSettingRow(
  title: String,
  description: String,
  icon: ImageVector,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  steps: Int = 0,
) {
  var sliderValue by remember(value) { mutableFloatStateOf(value) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp),
      )

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Slider(
      value = sliderValue,
      onValueChange = { sliderValue = it },
      onValueChangeFinished = { onValueChange(sliderValue) },
      valueRange = valueRange,
      steps = steps,
      modifier = Modifier.padding(start = 40.dp),
    )
  }
}

@Composable
private fun FontFamilySelectionDialog(
  currentFontFamily: String,
  onFontFamilySelected: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val fontFamilies = listOf(
    "JetBrains Mono",
    "Fira Code",
    "Source Code Pro",
    "Roboto Mono",
    "Cascadia Code",
    "Consolas",
    "Monaco",
    "Menlo",
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Schriftart auswählen") },
    text = {
      Column {
        fontFamilies.forEach { fontFamily ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onFontFamilySelected(fontFamily) }
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            RadioButton(
              selected = fontFamily == currentFontFamily,
              onClick = { onFontFamilySelected(fontFamily) },
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
              text = fontFamily,
              style = MaterialTheme.typography.bodyLarge,
              color = if (fontFamily == currentFontFamily) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.onSurface
              },
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("Schließen") }
    },
  )
}

@Composable
private fun EditorThemeSelectionDialog(
  currentTheme: String,
  onThemeSelected: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val themes = listOf(
    "Darcula",
    "QuietLight",
    "Monokai",
    "GitHub",
    "GitHub Dark",
    "Solarized Dark",
    "Solarized Light",
    "One Dark",
    "Dracula",
    "Nord",
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Editor-Theme auswählen") },
    text = {
      Column {
        themes.forEach { theme ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onThemeSelected(theme) }
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            RadioButton(
              selected = theme == currentTheme,
              onClick = { onThemeSelected(theme) },
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
              text = theme,
              style = MaterialTheme.typography.bodyLarge,
              color = if (theme == currentTheme) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.onSurface
              },
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("Schließen") }
    },
  )
}
