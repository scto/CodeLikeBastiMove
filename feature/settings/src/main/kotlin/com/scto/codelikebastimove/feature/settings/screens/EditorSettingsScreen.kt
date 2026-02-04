package com.scto.codelikebastimove.feature.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.WrapText
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.EditorSettings
import com.scto.codelikebastimove.core.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorSettingsScreen(
    editorSettings: EditorSettings,
    onFontSizeChanged: (Float) -> Unit,
    onTabSizeChanged: (Int) -> Unit,
    onUseSoftTabsChanged: (Boolean) -> Unit,
    onShowLineNumbersChanged: (Boolean) -> Unit,
    onWordWrapChanged: (Boolean) -> Unit,
    onHighlightCurrentLineChanged: (Boolean) -> Unit,
    onAutoIndentChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings_editor)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_editor_font),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditorSliderSetting(
                        icon = Icons.Default.FormatSize,
                        title = stringResource(R.string.settings_font_size),
                        value = editorSettings.fontSize,
                        valueRange = 10f..32f,
                        valueLabel = "${editorSettings.fontSize.toInt()} sp",
                        onValueChange = onFontSizeChanged
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    EditorSliderSetting(
                        icon = Icons.Default.FormatIndentIncrease,
                        title = stringResource(R.string.settings_tab_size),
                        value = editorSettings.tabSize.toFloat(),
                        valueRange = 2f..8f,
                        steps = 5,
                        valueLabel = "${editorSettings.tabSize} ${stringResource(R.string.settings_spaces)}",
                        onValueChange = { onTabSizeChanged(it.toInt()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_editor_display),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    EditorSwitchSetting(
                        icon = Icons.Default.FormatLineSpacing,
                        title = stringResource(R.string.settings_show_line_numbers),
                        subtitle = stringResource(R.string.settings_show_line_numbers_subtitle),
                        checked = editorSettings.showLineNumbers,
                        onCheckedChange = onShowLineNumbersChanged
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    EditorSwitchSetting(
                        icon = Icons.AutoMirrored.Filled.WrapText,
                        title = stringResource(R.string.settings_word_wrap),
                        subtitle = stringResource(R.string.settings_word_wrap_subtitle),
                        checked = editorSettings.wordWrap,
                        onCheckedChange = onWordWrapChanged
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    EditorSwitchSetting(
                        icon = Icons.Default.Highlight,
                        title = stringResource(R.string.settings_highlight_current_line),
                        subtitle = stringResource(R.string.settings_highlight_current_line_subtitle),
                        checked = editorSettings.highlightCurrentLine,
                        onCheckedChange = onHighlightCurrentLineChanged
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_editor_behavior),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    EditorSwitchSetting(
                        icon = Icons.Default.TextFields,
                        title = stringResource(R.string.settings_use_soft_tabs),
                        subtitle = stringResource(R.string.settings_use_soft_tabs_subtitle),
                        checked = editorSettings.useSoftTabs,
                        onCheckedChange = onUseSoftTabsChanged
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    EditorSwitchSetting(
                        icon = Icons.Default.FormatIndentIncrease,
                        title = stringResource(R.string.settings_auto_indent),
                        subtitle = stringResource(R.string.settings_auto_indent_subtitle),
                        checked = editorSettings.autoIndent,
                        onCheckedChange = onAutoIndentChanged
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EditorSwitchSetting(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun EditorSliderSetting(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: String,
    onValueChange: (Float) -> Unit,
    steps: Int = 0
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = valueLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(start = 40.dp)
        )
    }
}
