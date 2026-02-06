package com.scto.codelikebastimove.feature.settings.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.datastore.CursorAnimationType
import com.scto.codelikebastimove.core.datastore.LineEndingType
import com.scto.codelikebastimove.core.datastore.RenderWhitespaceMode
import com.scto.codelikebastimove.feature.settings.SettingsViewModel
import com.scto.codelikebastimove.feature.settings.components.PreferenceGroup
import com.scto.codelikebastimove.feature.settings.components.PreferenceLayout
import com.scto.codelikebastimove.feature.settings.components.PreferenceTemplate
import com.scto.codelikebastimove.feature.settings.components.SettingsSlider
import com.scto.codelikebastimove.feature.settings.components.SettingsToggle

@Composable
fun EditorSettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val userPreferences by viewModel.userPreferences.collectAsState()
    val editorSettings = userPreferences.editorSettings

    var showFontFamilyDialog by remember { mutableStateOf(false) }
    var showEditorThemeDialog by remember { mutableStateOf(false) }

    PreferenceLayout(
        label = "Editor-Einstellungen",
        onBack = onBackClick,
        modifier = modifier,
    ) {
        PreferenceGroup(heading = "Schrift") {
            SettingsSlider(
                label = "Schriftgröße",
                description = "${editorSettings.fontSize.toInt()} sp",
                value = editorSettings.fontSize,
                valueRange = 8f..32f,
                onValueChange = { viewModel.setEditorFontSize(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Schriftart",
                description = editorSettings.fontFamily,
                showSwitch = false,
                onClick = { showFontFamilyDialog = true },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )
        }

        PreferenceGroup(heading = "Einrückung") {
            SettingsSlider(
                label = "Tab-Größe",
                description = "${editorSettings.tabSize} Leerzeichen",
                value = editorSettings.tabSize.toFloat(),
                valueRange = 2f..8f,
                steps = 5,
                onValueChange = { viewModel.setEditorTabSize(it.toInt()) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.FormatIndentIncrease,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Soft Tabs",
                description = "Leerzeichen statt Tabs verwenden",
                checked = editorSettings.useSoftTabs,
                onCheckedChange = { viewModel.setEditorUseSoftTabs(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.SpaceBar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Auto-Einrückung",
                description = "Automatische Einrückung beim Zeilenumbruch",
                checked = editorSettings.autoIndent,
                onCheckedChange = { viewModel.setEditorAutoIndent(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.FormatIndentIncrease,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )
        }

        PreferenceGroup(heading = "Anzeige") {
            SettingsToggle(
                label = "Zeilennummern",
                description = "Zeilennummern am linken Rand anzeigen",
                checked = editorSettings.showLineNumbers,
                onCheckedChange = { viewModel.setEditorShowLineNumbers(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.FormatLineSpacing,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Zeilenumbruch",
                description = "Lange Zeilen automatisch umbrechen",
                checked = editorSettings.wordWrap,
                onCheckedChange = { viewModel.setEditorWordWrap(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.WrapText,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Aktuelle Zeile hervorheben",
                description = "Die aktuelle Cursorzeile hervorheben",
                checked = editorSettings.highlightCurrentLine,
                onCheckedChange = { viewModel.setEditorHighlightCurrentLine(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Highlight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Leerzeichen anzeigen",
                description = "Unsichtbare Zeichen sichtbar machen",
                checked = editorSettings.showWhitespace,
                onCheckedChange = { viewModel.setEditorShowWhitespace(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.SpaceBar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Minimap",
                description = "Code-Übersicht am rechten Rand anzeigen",
                checked = editorSettings.minimapEnabled,
                onCheckedChange = { viewModel.setEditorMinimapEnabled(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Sticky Scroll",
                description = "Funktionskopf beim Scrollen fixieren",
                checked = editorSettings.stickyScroll,
                onCheckedChange = { viewModel.setEditorStickyScroll(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.ViewHeadline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Pin Line Numbers",
                description = "Keep line numbers visible while scrolling horizontally",
                checked = editorSettings.pinLineNumber,
                onCheckedChange = { viewModel.setEditorPinLineNumber(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.PinDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            DropdownSettingItem(
                label = "Render Whitespace",
                description = "Show whitespace characters",
                selectedValue = editorSettings.renderWhitespace.name,
                options = RenderWhitespaceMode.entries.map { it.name },
                onOptionSelected = { viewModel.setEditorRenderWhitespace(RenderWhitespaceMode.valueOf(it)) },
                startIcon = Icons.Default.SpaceBar,
            )
        }

        PreferenceGroup(heading = "Verhalten") {
            SettingsToggle(
                label = "Fast Delete",
                description = "Delete empty lines and multiple spaces quickly",
                checked = editorSettings.fastDelete,
                onCheckedChange = { viewModel.setEditorFastDelete(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            DropdownSettingItem(
                label = "Cursor Animation",
                description = "Animation style for cursor",
                selectedValue = editorSettings.cursorAnimation.name,
                options = CursorAnimationType.entries.map { it.name },
                onOptionSelected = { viewModel.setEditorCursorAnimation(CursorAnimationType.valueOf(it)) },
                startIcon = Icons.Default.Animation,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Keyboard Suggestions",
                description = "Show keyboard autocomplete suggestions",
                checked = editorSettings.keyboardSuggestion,
                onCheckedChange = { viewModel.setEditorKeyboardSuggestion(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Hide Soft Keyboard",
                description = "Hide on-screen keyboard (use external keyboard)",
                checked = editorSettings.hideSoftKbd,
                onCheckedChange = { viewModel.setEditorHideSoftKbd(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.KeyboardHide,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )
        }

        PreferenceGroup(heading = "File Settings") {
            DropdownSettingItem(
                label = "Line Ending",
                description = "Line ending style for saved files",
                selectedValue = editorSettings.lineEndingSetting.name,
                options = LineEndingType.entries.map { it.name },
                onOptionSelected = { viewModel.setEditorLineEnding(LineEndingType.valueOf(it)) },
                startIcon = Icons.Default.SwapVert,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Final Newline",
                description = "Ensure files end with a newline",
                checked = editorSettings.finalNewline,
                onCheckedChange = { viewModel.setEditorFinalNewline(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )
        }

        PreferenceGroup(heading = "Erscheinungsbild") {
            SettingsToggle(
                label = "Editor-Theme",
                description = editorSettings.editorTheme,
                showSwitch = false,
                onClick = { showEditorThemeDialog = true },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggle(
                label = "Smooth Scrolling",
                description = "Flüssiges Scrollen aktivieren",
                checked = editorSettings.smoothScrolling,
                onCheckedChange = { viewModel.setEditorSmoothScrolling(it) },
                startWidget = {
                    Icon(
                        imageVector = Icons.Default.FormatLineSpacing,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )
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
private fun DropdownSettingItem(
    label: String,
    description: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    startIcon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    var expanded by remember { mutableStateOf(false) }

    PreferenceTemplate(
        title = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        modifier = Modifier.clickable { expanded = true },
        description = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        startWidget = {
            Icon(
                imageVector = startIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        },
        endWidget = {
            Text(
                text = selectedValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        },
    )
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
