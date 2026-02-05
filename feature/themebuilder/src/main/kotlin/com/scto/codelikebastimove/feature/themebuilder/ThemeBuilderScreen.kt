package com.scto.codelikebastimove.feature.themebuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.themebuilder.components.BottomActionBar
import com.scto.codelikebastimove.feature.themebuilder.components.ColorPickerDialog
import com.scto.codelikebastimove.feature.themebuilder.components.ColorSchemePreviewSection
import com.scto.codelikebastimove.feature.themebuilder.components.ComponentPreviewSection
import com.scto.codelikebastimove.feature.themebuilder.components.DynamicColorSection
import com.scto.codelikebastimove.feature.themebuilder.components.FontSelectionSection
import com.scto.codelikebastimove.feature.themebuilder.components.PlatformSelector
import com.scto.codelikebastimove.feature.themebuilder.components.SchemeSelector
import com.scto.codelikebastimove.feature.themebuilder.components.SeedColorRow
import com.scto.codelikebastimove.feature.themebuilder.components.ThemeHeader
import com.scto.codelikebastimove.feature.themebuilder.components.ThemeNameCard
import com.scto.codelikebastimove.feature.themebuilder.components.TonalPaletteSection
import com.scto.codelikebastimove.feature.themebuilder.export.exportTheme
import com.scto.codelikebastimove.feature.themebuilder.generator.MaterialColorGenerator
import com.scto.codelikebastimove.feature.themebuilder.generator.SchemeStyle
import com.scto.codelikebastimove.feature.themebuilder.model.ThemeColors
import com.scto.codelikebastimove.feature.themebuilder.util.schemeOptions

@Composable
fun ThemeBuilderContent(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) }
  var currentPage by remember { mutableIntStateOf(1) }
  var selectedPlatform by remember { mutableStateOf("Android") }
  val platforms = listOf("Android", "Windows", "Web", "Linux")

  var themeColors by remember { mutableStateOf(ThemeColors()) }
  var showColorPicker by remember { mutableStateOf(false) }
  var editingColorName by remember { mutableStateOf("") }
  var editingColor by remember { mutableStateOf(Color.White) }

  var displayFont by remember { mutableStateOf("-- System Default --") }
  var bodyFont by remember { mutableStateOf("-- System Default --") }
  var themeName by remember { mutableStateOf("material-theme") }

  var dynamicColorEnabled by remember { mutableStateOf(false) }
  var selectedScheme by remember { mutableStateOf("Tonal Spot") }
  val colorGenerator = remember { MaterialColorGenerator() }
  var seedColor by remember { mutableStateOf(themeColors.primary) }

  var showExportMenu by remember { mutableStateOf(false) }

  fun regenerateColors(seed: Color, scheme: String) {
    val schemeStyle = when (scheme) {
      "Tonal Spot" -> SchemeStyle.TONAL_SPOT
      "Vibrant" -> SchemeStyle.VIBRANT
      "Expressive" -> SchemeStyle.EXPRESSIVE
      "Fidelity" -> SchemeStyle.FIDELITY
      "Monochromatic", "Monochrome" -> SchemeStyle.MONOCHROME
      "Neutral" -> SchemeStyle.NEUTRAL
      "Content" -> SchemeStyle.CONTENT
      "Rainbow" -> SchemeStyle.EXPRESSIVE
      "Fruit Salad" -> SchemeStyle.VIBRANT
      else -> SchemeStyle.TONAL_SPOT
    }
    val generated = colorGenerator.generateTheme(seed, schemeStyle)
    val lightScheme = generated.lightScheme
    themeColors = ThemeColors(
      primary = lightScheme.primary,
      secondary = lightScheme.secondary,
      tertiary = lightScheme.tertiary,
      error = lightScheme.error,
      primaryContainer = lightScheme.primaryContainer,
      secondaryContainer = lightScheme.secondaryContainer,
      tertiaryContainer = lightScheme.tertiaryContainer,
      errorContainer = lightScheme.errorContainer,
      surface = lightScheme.surface,
      surfaceVariant = lightScheme.surfaceVariant,
      background = lightScheme.background,
      outline = lightScheme.outline,
    )
  }

  LaunchedEffect(selectedScheme) {
    if (!dynamicColorEnabled) {
      regenerateColors(seedColor, selectedScheme)
    }
  }

  LaunchedEffect(dynamicColorEnabled) {
    if (!dynamicColorEnabled) {
      regenerateColors(seedColor, selectedScheme)
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    ThemeHeader(themeName = themeName, onThemeNameChange = { themeName = it })

    TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
      Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Preview") })
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            "Edit",
            color =
              if (selectedTab == 1) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        },
      )
    }

    LazyColumn(
      modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      if (selectedTab == 0) {
        if (currentPage == 1) {
          item { ComponentPreviewSection(themeColors, selectedPlatform) }

          item {
            PlatformSelector(
              selectedPlatform = selectedPlatform,
              platforms = platforms,
              onPlatformSelected = { selectedPlatform = it },
            )
          }

          item {
            DynamicColorSection(
              dynamicColorEnabled = dynamicColorEnabled,
              onDynamicColorChange = { dynamicColorEnabled = it },
            )
          }

          item {
            SchemeSelector(
              selectedScheme = selectedScheme,
              schemeOptions = schemeOptions,
              onSchemeSelected = { selectedScheme = it },
              enabled = !dynamicColorEnabled,
            )
          }

          item {
            ColorSchemePreviewSection(
              title = stringResource(R.string.light_scheme),
              isDark = false,
              themeColors = themeColors,
              onColorClick = { name, color ->
                editingColorName = name
                editingColor = color
                showColorPicker = true
              },
            )
          }

          item {
            ColorSchemePreviewSection(
              title = stringResource(R.string.dark_scheme),
              isDark = true,
              themeColors = themeColors,
              onColorClick = { name, color ->
                editingColorName = name
                editingColor = color
                showColorPicker = true
              },
            )
          }

          item { TonalPaletteSection(themeColors) }
        } else {
          item {
            ThemeNameCard(themeName = themeName, displayFont = displayFont, bodyFont = bodyFont)
          }

          item {
            SeedColorRow(
              themeColors = themeColors,
              onColorSelected = { color -> 
                seedColor = color
                regenerateColors(color, selectedScheme)
              },
              onPickColor = {
                editingColorName = "Primary"
                editingColor = seedColor
                showColorPicker = true
              },
            )
          }
        }
      } else {
        item {
          FontSelectionSection(
            displayFont = displayFont,
            bodyFont = bodyFont,
            onDisplayFontChange = { displayFont = it },
            onBodyFontChange = { bodyFont = it },
          )
        }
      }
    }

    BottomActionBar(
      currentPage = currentPage,
      totalPages = 2,
      showExportMenu = showExportMenu,
      onExportMenuToggle = { showExportMenu = it },
      onBack = { if (currentPage > 1) currentPage-- },
      onNext = { if (currentPage < 2) currentPage++ },
      onExport = { format ->
        showExportMenu = false
        exportTheme(context, themeName, themeColors, displayFont, bodyFont, format)
      },
    )
  }

  if (showColorPicker) {
    ColorPickerDialog(
      colorName = editingColorName,
      initialColor = editingColor,
      onDismiss = { showColorPicker = false },
      onColorSelected = { newColor ->
        if (editingColorName == "Primary" && !dynamicColorEnabled) {
          seedColor = newColor
          regenerateColors(newColor, selectedScheme)
        } else {
          themeColors =
            when (editingColorName) {
              "Primary" -> themeColors.copy(primary = newColor)
              "Secondary" -> themeColors.copy(secondary = newColor)
              "Tertiary" -> themeColors.copy(tertiary = newColor)
              "Error" -> themeColors.copy(error = newColor)
              "Primary Container" -> themeColors.copy(primaryContainer = newColor)
              "Secondary Container" -> themeColors.copy(secondaryContainer = newColor)
              "Tertiary Container" -> themeColors.copy(tertiaryContainer = newColor)
              "Surface" -> themeColors.copy(surface = newColor)
              else -> themeColors
            }
        }
        showColorPicker = false
      },
    )
  }
}
