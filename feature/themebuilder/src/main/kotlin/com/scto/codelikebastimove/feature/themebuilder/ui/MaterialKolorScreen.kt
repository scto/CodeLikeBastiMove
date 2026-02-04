package com.scto.codelikebastimove.feature.themebuilder.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.feature.themebuilder.CodePreviewType
import com.scto.codelikebastimove.feature.themebuilder.ExportFormat
import com.scto.codelikebastimove.feature.themebuilder.ThemeBuilderTab
import com.scto.codelikebastimove.feature.themebuilder.ThemeBuilderViewModel
import com.scto.codelikebastimove.feature.themebuilder.generator.GeneratedTheme
import com.scto.codelikebastimove.feature.themebuilder.generator.Material3ColorScheme
import com.scto.codelikebastimove.feature.themebuilder.generator.SchemeStyle
import com.scto.codelikebastimove.feature.themebuilder.generator.TonalPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialKolorScreen(
    viewModel: ThemeBuilderViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val theme = uiState.generatedTheme

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Theme Builder",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "MaterialKolor Style",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkPreview() }) {
                        Icon(
                            imageVector = if (uiState.isDarkPreview) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Toggle dark mode",
                        )
                    }
                    IconButton(onClick = { viewModel.showExportDialog() }) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                SeedColorSection(
                    seedColor = uiState.seedColor,
                    onColorSelected = viewModel::setSeedColor,
                    presetColors = ThemeBuilderViewModel.presetColors,
                )

                Spacer(modifier = Modifier.height(24.dp))

                SchemeStyleSection(
                    selectedStyle = uiState.schemeStyle,
                    onStyleSelected = viewModel::setSchemeStyle,
                )

                Spacer(modifier = Modifier.height(24.dp))

                ThemeSettingsSection(
                    themeName = uiState.themeName,
                    packageName = uiState.packageName,
                    onThemeNameChange = viewModel::setThemeName,
                    onPackageNameChange = viewModel::setPackageName,
                )
            }

            VerticalDivider()

            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
            ) {
                TabRow(
                    selectedTabIndex = ThemeBuilderTab.entries.indexOf(uiState.selectedTab),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    ThemeBuilderTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.setSelectedTab(tab) },
                            text = { Text(tab.title) },
                        )
                    }
                }

                if (theme != null) {
                    AnimatedContent(
                        targetState = uiState.selectedTab,
                        label = "TabContent",
                    ) { tab ->
                        when (tab) {
                            ThemeBuilderTab.COLORS -> ColorsTabContent(
                                theme = theme,
                                isDark = uiState.isDarkPreview,
                                modifier = Modifier.fillMaxSize(),
                            )
                            ThemeBuilderTab.PALETTES -> PalettesTabContent(
                                theme = theme,
                                modifier = Modifier.fillMaxSize(),
                            )
                            ThemeBuilderTab.PREVIEW -> PreviewTabContent(
                                theme = theme,
                                isDark = uiState.isDarkPreview,
                                modifier = Modifier.fillMaxSize(),
                            )
                            ThemeBuilderTab.EXPORT -> ExportTabContent(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showCodePreview) {
        CodePreviewSheet(
            code = uiState.generatedCode,
            codeType = uiState.codePreviewType,
            onDismiss = { viewModel.hideCodePreview() },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeedColorSection(
    seedColor: Color,
    onColorSelected: (Color) -> Unit,
    presetColors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Seed Color",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(seedColor)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = String.format("#%06X", seedColor.toArgb() and 0xFFFFFF),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                )
                Text(
                    text = "Primary seed",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Presets",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            presetColors.forEach { color ->
                val isSelected = color == seedColor
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (isSelected) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            }
                        )
                        .clickable { onColorSelected(color) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SchemeStyleSection(
    selectedStyle: SchemeStyle,
    onStyleSelected: (SchemeStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Scheme Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        SchemeStyle.entries.forEach { style ->
            val isSelected = style == selectedStyle

            Card(
                onClick = { onStyleSelected(style) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = style.displayName,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSettingsSection(
    themeName: String,
    packageName: String,
    onThemeNameChange: (String) -> Unit,
    onPackageNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Export Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = themeName,
            onValueChange = onThemeNameChange,
            label = { Text("Theme Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = packageName,
            onValueChange = onPackageNameChange,
            label = { Text("Package Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ColorsTabContent(
    theme: GeneratedTheme,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheme = if (isDark) theme.darkScheme else theme.lightScheme

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = if (isDark) "Dark Scheme" else "Light Scheme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item { ColorRoleSection("Primary", scheme.primary, scheme.onPrimary, scheme.primaryContainer, scheme.onPrimaryContainer) }
        item { ColorRoleSection("Secondary", scheme.secondary, scheme.onSecondary, scheme.secondaryContainer, scheme.onSecondaryContainer) }
        item { ColorRoleSection("Tertiary", scheme.tertiary, scheme.onTertiary, scheme.tertiaryContainer, scheme.onTertiaryContainer) }
        item { ColorRoleSection("Error", scheme.error, scheme.onError, scheme.errorContainer, scheme.onErrorContainer) }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Surface Colors",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                ColorChip("Background", scheme.background, Modifier.weight(1f))
                ColorChip("Surface", scheme.surface, Modifier.weight(1f))
                ColorChip("Surface Variant", scheme.surfaceVariant, Modifier.weight(1f))
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                ColorChip("Outline", scheme.outline, Modifier.weight(1f))
                ColorChip("Outline Variant", scheme.outlineVariant, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ColorRoleSection(
    name: String,
    main: Color,
    onMain: Color,
    container: Color,
    onContainer: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            ColorChip(name, main, Modifier.weight(1f))
            ColorChip("On $name", onMain, Modifier.weight(1f))
            ColorChip("$name Container", container, Modifier.weight(1f))
            ColorChip("On $name Container", onContainer, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ColorChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (color.luminance() > 0.5f) Color.Black else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = String.format("#%06X", color.toArgb() and 0xFFFFFF),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun PalettesTabContent(
    theme: GeneratedTheme,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { TonalPaletteRow("Primary", theme.primaryPalette) }
        item { TonalPaletteRow("Secondary", theme.secondaryPalette) }
        item { TonalPaletteRow("Tertiary", theme.tertiaryPalette) }
        item { TonalPaletteRow("Neutral", theme.neutralPalette) }
        item { TonalPaletteRow("Neutral Variant", theme.neutralVariantPalette) }
        item { TonalPaletteRow("Error", theme.errorPalette) }
    }
}

@Composable
private fun TonalPaletteRow(
    name: String,
    palette: TonalPalette,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            listOf(
                0 to palette.tone0,
                10 to palette.tone10,
                20 to palette.tone20,
                30 to palette.tone30,
                40 to palette.tone40,
                50 to palette.tone50,
                60 to palette.tone60,
                70 to palette.tone70,
                80 to palette.tone80,
                90 to palette.tone90,
                95 to palette.tone95,
                99 to palette.tone99,
                100 to palette.tone100,
            ).forEach { (tone, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(color),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$tone",
                        fontSize = 8.sp,
                        color = if (color.luminance() > 0.5f) Color.Black else Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewTabContent(
    theme: GeneratedTheme,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheme = if (isDark) theme.darkScheme else theme.lightScheme

    Surface(
        color = scheme.background,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Surface(
                color = scheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = scheme.primary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Theme Preview",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurface,
                        )
                        Text(
                            text = "This is how your theme looks",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.primary,
                        contentColor = scheme.onPrimary,
                    ),
                ) {
                    Text("Primary")
                }
                OutlinedButton(onClick = {}) {
                    Text("Outlined", color = scheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = scheme.primaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Primary Container",
                        color = scheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Content on primary container",
                        color = scheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = scheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Secondary Container",
                        color = scheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Content on secondary container",
                        color = scheme.onSecondaryContainer.copy(alpha = 0.8f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = scheme.tertiaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tertiary Container",
                        color = scheme.onTertiaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Content on tertiary container",
                        color = scheme.onTertiaryContainer.copy(alpha = 0.8f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = scheme.errorContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Error Container",
                        color = scheme.onErrorContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Error message content",
                        color = scheme.onErrorContainer.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportTabContent(
    viewModel: ThemeBuilderViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Export Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExportFormat.entries.forEach { format ->
            Card(
                onClick = { viewModel.setExportFormat(format) },
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.exportFormat == format) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (uiState.exportFormat == format) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(text = format.displayName)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Preview Code",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        CodePreviewType.entries.forEach { type ->
            OutlinedCard(
                onClick = { viewModel.showCodePreview(type) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = type.displayName)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CodePreviewSheet(
    code: String,
    codeType: CodePreviewType,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = codeType.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                ) {
                    item {
                        Text(
                            text = code,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun Color.luminance(): Float {
    val r = if (red <= 0.03928f) red / 12.92f else ((red + 0.055f) / 1.055f).pow(2.4f)
    val g = if (green <= 0.03928f) green / 12.92f else ((green + 0.055f) / 1.055f).pow(2.4f)
    val b = if (blue <= 0.03928f) blue / 12.92f else ((blue + 0.055f) / 1.055f).pow(2.4f)
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

private fun Float.pow(exponent: Float): Float = java.lang.Math.pow(this.toDouble(), exponent.toDouble()).toFloat()
