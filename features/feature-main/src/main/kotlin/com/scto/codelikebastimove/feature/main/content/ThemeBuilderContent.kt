package com.scto.codelikebastimove.feature.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Data class representing the editable configuration of a Theme.
 */
data class ThemeConfig(
    val primary: Color = Color(0xFF6750A4),
    val secondary: Color = Color(0xFF625B71),
    val tertiary: Color = Color(0xFF7D5260),
    val background: Color = Color(0xFFFFFBFE),
    val surface: Color = Color(0xFFFFFBFE),
    val error: Color = Color(0xFFB3261E),
    val onPrimary: Color = Color.White,
    val onSecondary: Color = Color.White,
    val onBackground: Color = Color(0xFF1C1B1F),
    val onSurface: Color = Color(0xFF1C1B1F)
)

/**
 * Main Content for the Theme Builder Feature.
 * Allows users to edit theme colors and preview changes live.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBuilderContent() {
    var themeConfig by remember { mutableStateOf(ThemeConfig()) }
    var showCodeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Theme Builder",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = { themeConfig = ThemeConfig() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Theme")
                }
                Button(onClick = { showCodeDialog = true }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Export Code")
                }
            }
        }

        Divider()

        Row(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            // Left Panel: Property Editors
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Color Scheme",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ColorPickerItem("Primary", themeConfig.primary) { themeConfig = themeConfig.copy(primary = it) }
                ColorPickerItem("On Primary", themeConfig.onPrimary) { themeConfig = themeConfig.copy(onPrimary = it) }
                Spacer(Modifier.height(8.dp))
                
                ColorPickerItem("Secondary", themeConfig.secondary) { themeConfig = themeConfig.copy(secondary = it) }
                ColorPickerItem("On Secondary", themeConfig.onSecondary) { themeConfig = themeConfig.copy(onSecondary = it) }
                Spacer(Modifier.height(8.dp))

                ColorPickerItem("Tertiary", themeConfig.tertiary) { themeConfig = themeConfig.copy(tertiary = it) }
                Spacer(Modifier.height(8.dp))

                ColorPickerItem("Background", themeConfig.background) { themeConfig = themeConfig.copy(background = it) }
                ColorPickerItem("On Background", themeConfig.onBackground) { themeConfig = themeConfig.copy(onBackground = it) }
                Spacer(Modifier.height(8.dp))

                ColorPickerItem("Surface", themeConfig.surface) { themeConfig = themeConfig.copy(surface = it) }
                ColorPickerItem("On Surface", themeConfig.onSurface) { themeConfig = themeConfig.copy(onSurface = it) }
                Spacer(Modifier.height(8.dp))

                ColorPickerItem("Error", themeConfig.error) { themeConfig = themeConfig.copy(error = it) }
            }

            // Right Panel: Live Preview
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Wrap preview in a Box with border to distinct it from the editor tool
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    ThemePreview(themeConfig)
                }
            }
        }
    }

    if (showCodeDialog) {
        CodeExportDialog(themeConfig) { showCodeDialog = false }
    }
}

@Composable
fun ColorPickerItem(label: String, currentColor: Color, onColorChange: (Color) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { showDialog = true }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(currentColor)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = currentColor.toHexString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showDialog) {
        SimpleColorSelectionDialog(
            initialColor = currentColor,
            onDismiss = { showDialog = false },
            onColorSelected = {
                onColorChange(it)
                showDialog = false
            }
        )
    }
}

/**
 * A dialog to select a color from a predefined palette or presets.
 * (In a full production app, this would be a full HSV/RGB slider picker)
 */
@Composable
fun SimpleColorSelectionDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val basicColors = listOf(
        Color(0xFF6750A4), Color(0xFF625B71), Color(0xFF7D5260),
        Color(0xFFB3261E), Color(0xFF21005D), Color(0xFFFFFFFF),
        Color(0xFF000000), Color(0xFF1C1B1F), Color(0xFF31111D),
        Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFF018786),
        Color(0xFF4CAF50), Color(0xFFFB8C00), Color(0xFFE91E63)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Color", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(24.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(basicColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onColorSelected(color) }
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == initialColor) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (color.luminance() > 0.5f) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePreview(config: ThemeConfig) {
    val previewColorScheme = lightColorScheme(
        primary = config.primary,
        onPrimary = config.onPrimary,
        secondary = config.secondary,
        onSecondary = config.onSecondary,
        tertiary = config.tertiary,
        background = config.background,
        onBackground = config.onBackground,
        surface = config.surface,
        onSurface = config.onSurface,
        error = config.error
    )

    MaterialTheme(colorScheme = previewColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App Preview") },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Typography & Components", style = MaterialTheme.typography.titleLarge)
                
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Card Title", style = MaterialTheme.typography.titleMedium)
                        Text("This is a card displaying the surface color and onSurface typography.", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}) { Text("Filled") }
                    FilledTonalButton(onClick = {}) { Text("Tonal") }
                    OutlinedButton(onClick = {}) { Text("Outlined") }
                }

                TextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Text Field") },
                    placeholder = { Text("Type something...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Switch item")
                    Spacer(Modifier.weight(1f))
                    androidx.compose.material3.Switch(checked = true, onCheckedChange = {})
                }
                
                Slider(value = 0.5f, onValueChange = {})
            }
        }
    }
}

@Composable
fun CodeExportDialog(config: ThemeConfig, onDismiss: () -> Unit) {
    val code = """
        package com.scto.codelikebastimove.core.ui.theme

        import androidx.compose.ui.graphics.Color
        import androidx.compose.material3.lightColorScheme

        val LightColorScheme = lightColorScheme(
            primary = Color(${config.primary.toHexString().replace("#", "0xFF")}),
            onPrimary = Color(${config.onPrimary.toHexString().replace("#", "0xFF")}),
            secondary = Color(${config.secondary.toHexString().replace("#", "0xFF")}),
            onSecondary = Color(${config.onSecondary.toHexString().replace("#", "0xFF")}),
            tertiary = Color(${config.tertiary.toHexString().replace("#", "0xFF")}),
            background = Color(${config.background.toHexString().replace("#", "0xFF")}),
            onBackground = Color(${config.onBackground.toHexString().replace("#", "0xFF")}),
            surface = Color(${config.surface.toHexString().replace("#", "0xFF")}),
            onSurface = Color(${config.onSurface.toHexString().replace("#", "0xFF")}),
            error = Color(${config.error.toHexString().replace("#", "0xFF")})
        )
    """.trimIndent()

    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Generated Kotlin Code", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = code,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Copy to Clipboard")
                    }
                }
            }
        }
    }
}

// Extension to get Hex String
fun Color.toHexString(): String {
    val argb = this.toArgb()
    return String.format("#%08X", argb)
}

// Extension to calc luminance for contrast text
fun Color.luminance(): Float {
    val red = this.red
    val green = this.green
    val blue = this.blue
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}

/*
package com.scto.codelikebastimove.feature.main.content

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class ThemeColors(
    val primary: Color = Color(0xFFD4C4A8),
    val secondary: Color = Color(0xFFB8977E),
    val tertiary: Color = Color(0xFF8B7355),
    val error: Color = Color(0xFFB3261E),
    val primaryContainer: Color = Color(0xFFF5E6D3),
    val secondaryContainer: Color = Color(0xFFE8D5C4),
    val tertiaryContainer: Color = Color(0xFFD4C4B4),
    val errorContainer: Color = Color(0xFFF9DEDC),
    val surface: Color = Color(0xFFFFFBFE),
    val surfaceVariant: Color = Color(0xFFE7E0EC),
    val background: Color = Color(0xFFFFFBFE),
    val outline: Color = Color(0xFF79747E)
)

@Composable
fun ThemeBuilderContent(
    modifier: Modifier = Modifier
) {
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
    val schemeOptions = listOf("Tonal Spot", "Neutral", "Vibrant", "Expressive", "Fidelity", "Content", "Monochromatic", "Rainbow", "Fruit Salad")
    
    var showExportMenu by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxSize()) {
        ThemeHeader(
            themeName = themeName,
            onThemeNameChange = { themeName = it }
        )
        
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Preview") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { 
                    Text(
                        "Edit", 
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                }
            )
        }
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                if (currentPage == 1) {
                    item {
                        ComponentPreviewSection(themeColors, selectedPlatform)
                    }
                    
                    item {
                        PlatformSelector(
                            selectedPlatform = selectedPlatform,
                            platforms = platforms,
                            onPlatformSelected = { selectedPlatform = it }
                        )
                    }
                    
                    item {
                        DynamicColorSection(
                            dynamicColorEnabled = dynamicColorEnabled,
                            onDynamicColorChange = { dynamicColorEnabled = it }
                        )
                    }
                    
                    item {
                        SchemeSelector(
                            selectedScheme = selectedScheme,
                            schemeOptions = schemeOptions,
                            onSchemeSelected = { selectedScheme = it },
                            enabled = !dynamicColorEnabled
                        )
                    }
                    
                    item {
                        ColorSchemePreviewSection(
                            title = "Light Scheme", 
                            isDark = false, 
                            themeColors = themeColors,
                            onColorClick = { name, color ->
                                editingColorName = name
                                editingColor = color
                                showColorPicker = true
                            }
                        )
                    }
                    
                    item {
                        ColorSchemePreviewSection(
                            title = "Dark Scheme", 
                            isDark = true, 
                            themeColors = themeColors,
                            onColorClick = { name, color ->
                                editingColorName = name
                                editingColor = color
                                showColorPicker = true
                            }
                        )
                    }
                    
                    item {
                        TonalPaletteSection(themeColors)
                    }
                } else {
                    item {
                        ThemeNameCard(
                            themeName = themeName,
                            displayFont = displayFont,
                            bodyFont = bodyFont
                        )
                    }
                    
                    item {
                        SeedColorRow(
                            themeColors = themeColors,
                            onColorSelected = { color ->
                                themeColors = themeColors.copy(primary = color)
                            },
                            onPickColor = {
                                editingColorName = "Primary"
                                editingColor = themeColors.primary
                                showColorPicker = true
                            }
                        )
                    }
                }
            } else {
                item {
                    FontSelectionSection(
                        displayFont = displayFont,
                        bodyFont = bodyFont,
                        onDisplayFontChange = { displayFont = it },
                        onBodyFontChange = { bodyFont = it }
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
            }
        )
    }
    
    if (showColorPicker) {
        ColorPickerDialog(
            colorName = editingColorName,
            initialColor = editingColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { newColor ->
                themeColors = when (editingColorName) {
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
                showColorPicker = false
            }
        )
    }
}

@Composable
private fun ColorPickerDialog(
    colorName: String,
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(0.5f) }
    var lightness by remember { mutableFloatStateOf(0.5f) }
    var hexInput by remember { mutableStateOf(colorToHex(initialColor)) }
    
    val currentColor = Color.hsl(hue, saturation, lightness)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Edit $colorName",
                style = MaterialTheme.typography.titleMedium
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                )
                
                Column {
                    Text("Hue: ${hue.toInt()}°", fontSize = 12.sp)
                    Slider(
                        value = hue,
                        onValueChange = { 
                            hue = it
                            hexInput = colorToHex(Color.hsl(hue, saturation, lightness))
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentColor,
                            activeTrackColor = currentColor
                        )
                    )
                }
                
                Column {
                    Text("Saturation: ${(saturation * 100).toInt()}%", fontSize = 12.sp)
                    Slider(
                        value = saturation,
                        onValueChange = { 
                            saturation = it
                            hexInput = colorToHex(Color.hsl(hue, saturation, lightness))
                        },
                        valueRange = 0f..1f
                    )
                }
                
                Column {
                    Text("Lightness: ${(lightness * 100).toInt()}%", fontSize = 12.sp)
                    Slider(
                        value = lightness,
                        onValueChange = { 
                            lightness = it
                            hexInput = colorToHex(Color.hsl(hue, saturation, lightness))
                        },
                        valueRange = 0f..1f
                    )
                }
                
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { input ->
                        hexInput = input
                        if (input.length == 7 && input.startsWith("#")) {
                            try {
                                val parsed = android.graphics.Color.parseColor(input)
                                val r = android.graphics.Color.red(parsed) / 255f
                                val g = android.graphics.Color.green(parsed) / 255f
                                val b = android.graphics.Color.blue(parsed) / 255f
                                val max = maxOf(r, g, b)
                                val min = minOf(r, g, b)
                                lightness = (max + min) / 2f
                                saturation = if (max == min) 0f else {
                                    if (lightness > 0.5f) (max - min) / (2f - max - min)
                                    else (max - min) / (max + min)
                                }
                                hue = when {
                                    max == min -> 0f
                                    max == r -> (60f * ((g - b) / (max - min)) + 360f) % 360f
                                    max == g -> 60f * ((b - r) / (max - min)) + 120f
                                    else -> 60f * ((r - g) / (max - min)) + 240f
                                }
                            } catch (_: Exception) { }
                        }
                    },
                    label = { Text("Hex Color") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .clickable {
                                    hexInput = colorToHex(color)
                                    val argb = color.toArgb()
                                    val r = android.graphics.Color.red(argb) / 255f
                                    val g = android.graphics.Color.green(argb) / 255f
                                    val b = android.graphics.Color.blue(argb) / 255f
                                    val max = maxOf(r, g, b)
                                    val min = minOf(r, g, b)
                                    lightness = (max + min) / 2f
                                    saturation = if (max == min) 0f else {
                                        if (lightness > 0.5f) (max - min) / (2f - max - min)
                                        else (max - min) / (max + min)
                                    }
                                    hue = when {
                                        max == min -> 0f
                                        max == r -> (60f * ((g - b) / (max - min)) + 360f) % 360f
                                        max == g -> 60f * ((b - r) / (max - min)) + 120f
                                        else -> 60f * ((r - g) / (max - min)) + 240f
                                    }
                                }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onColorSelected(currentColor) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun colorToHex(color: Color): String {
    val argb = color.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}

private val presetColors = listOf(
    Color(0xFFD4C4A8), Color(0xFFB8977E), Color(0xFF8B7355),
    Color(0xFF6750A4), Color(0xFF2196F3), Color(0xFF4CAF50),
    Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFF9C27B0),
    Color(0xFF00BCD4), Color(0xFFFF5722), Color(0xFF795548)
)

@Composable
private fun ThemeHeader(
    themeName: String,
    onThemeNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                if (isEditing) {
                    OutlinedTextField(
                        value = themeName,
                        onValueChange = onThemeNameChange,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { isEditing = false }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(18.dp))
                    }
                } else {
                    Text(
                        text = themeName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { isEditing = true }
                    )
                    IconButton(onClick = { isEditing = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit name", modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(20.dp))
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Switch to light mode") },
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Show info dialog") },
                        onClick = { showMenu = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicColorSection(
    dynamicColorEnabled: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dynamic Color",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Use colors from your wallpaper (Android 12+)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = dynamicColorEnabled,
                onCheckedChange = onDynamicColorChange
            )
        }
    }
}

@Composable
private fun SchemeSelector(
    selectedScheme: String,
    schemeOptions: List<String>,
    onSchemeSelected: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Color Scheme Style",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface 
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                if (!enabled) {
                    Text(
                        text = "(Disabled with Dynamic Color)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                schemeOptions.forEach { scheme ->
                    val isSelected = selectedScheme == scheme
                    FilterChip(
                        selected = isSelected,
                        onClick = { if (enabled) onSchemeSelected(scheme) },
                        enabled = enabled,
                        label = { 
                            Text(
                                text = scheme,
                                fontSize = 11.sp,
                                maxLines = 1
                            ) 
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = getSchemeColor(scheme),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = getSchemeDescription(selectedScheme),
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant 
                       else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

private fun getSchemeColor(scheme: String): Color {
    return when (scheme) {
        "Tonal Spot" -> Color(0xFF6750A4)
        "Neutral" -> Color(0xFF79747E)
        "Vibrant" -> Color(0xFFE91E63)
        "Expressive" -> Color(0xFFFF5722)
        "Fidelity" -> Color(0xFF4CAF50)
        "Content" -> Color(0xFF2196F3)
        "Monochromatic" -> Color(0xFF424242)
        "Rainbow" -> Color(0xFF9C27B0)
        "Fruit Salad" -> Color(0xFFFF9800)
        else -> Color(0xFF6750A4)
    }
}

private fun getSchemeDescription(scheme: String): String {
    return when (scheme) {
        "Tonal Spot" -> "Default Material You scheme with balanced tonal colors"
        "Neutral" -> "Muted colors with minimal saturation for subtle themes"
        "Vibrant" -> "High saturation colors for bold, energetic themes"
        "Expressive" -> "Playful colors with varied hues for creative themes"
        "Fidelity" -> "Colors stay close to the source for brand accuracy"
        "Content" -> "Optimized for content-heavy apps with readable contrast"
        "Monochromatic" -> "Single hue with varying lightness for minimal themes"
        "Rainbow" -> "Full spectrum of colors for colorful, diverse themes"
        "Fruit Salad" -> "Warm, fruity colors for playful, organic themes"
        else -> "Select a color scheme style"
    }
}

@Composable
private fun ComponentPreviewSection(
    themeColors: ThemeColors,
    selectedPlatform: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(themeColors.primary, themeColors.secondary, themeColors.tertiary).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedCard(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Textfield",
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
            OutlinedCard(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Textfield",
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(selected = false, onClick = { }, label = { Text("Assist", fontSize = 11.sp) })
            FilterChip(selected = false, onClick = { }, label = { Text("Filter", fontSize = 11.sp) })
            FilterChip(selected = false, onClick = { }, label = { Text("Suggestion", fontSize = 11.sp) })
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Button", fontSize = 12.sp)
            }
            OutlinedButton(onClick = { }, modifier = Modifier.height(36.dp)) {
                Text("+ Button", fontSize = 12.sp)
            }
            OutlinedButton(onClick = { }, modifier = Modifier.height(36.dp)) {
                Text("Button", fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(themeColors.primary, themeColors.secondary, themeColors.tertiary).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color.Black.copy(alpha = 0.6f))
                }
            }
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(themeColors.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text("+ Button", fontSize = 11.sp, color = Color.Black.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun PlatformSelector(
    selectedPlatform: String,
    platforms: List<String>,
    onPlatformSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        platforms.forEach { platform ->
            FilterChip(
                selected = selectedPlatform == platform,
                onClick = { onPlatformSelected(platform) },
                label = { 
                    Text(
                        text = if (platform == "Android") "And\nroid" else platform,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        lineHeight = 13.sp
                    )
                },
                leadingIcon = if (selectedPlatform == platform) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.height(40.dp)
            )
        }
    }
}

@Composable
private fun ColorSchemePreviewSection(
    title: String,
    isDark: Boolean,
    themeColors: ThemeColors,
    onColorClick: (String, Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDark) Color(0xFF1C1B1F) else Color(0xFFFFFBFE)
    val textColor = if (isDark) Color.White else Color.Black
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    ColorCell("Primary", themeColors.primary, Color.Black, Modifier.weight(1f)) { 
                        onColorClick("Primary", themeColors.primary) 
                    }
                    ColorCell("Secondary", themeColors.secondary, Color.Black, Modifier.weight(1f)) {
                        onColorClick("Secondary", themeColors.secondary)
                    }
                    ColorCell("Tertiary", themeColors.tertiary, Color.Black, Modifier.weight(1f)) {
                        onColorClick("Tertiary", themeColors.tertiary)
                    }
                    ColorCell("Error", themeColors.error, Color.White, Modifier.weight(1f)) {
                        onColorClick("Error", themeColors.error)
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    ColorCell("On Primary", Color(0xFF4A3C2A), Color.White, Modifier.weight(1f)) {}
                    ColorCell("On Secondary", Color(0xFF3D2E1E), Color.White, Modifier.weight(1f)) {}
                    ColorCell("On Tertiary", Color(0xFF2D1F0F), Color.White, Modifier.weight(1f)) {}
                    ColorCell("On Error", Color(0xFF601410), Color.White, Modifier.weight(1f)) {}
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    ColorCell("Primary Cont.", themeColors.primaryContainer, Color.Black, Modifier.weight(1f)) {
                        onColorClick("Primary Container", themeColors.primaryContainer)
                    }
                    ColorCell("Secondary Cont.", themeColors.secondaryContainer, Color.Black, Modifier.weight(1f)) {
                        onColorClick("Secondary Container", themeColors.secondaryContainer)
                    }
                    ColorCell("Tertiary Cont.", themeColors.tertiaryContainer, Color.Black, Modifier.weight(1f)) {
                        onColorClick("Tertiary Container", themeColors.tertiaryContainer)
                    }
                    ColorCell("Error Cont.", themeColors.errorContainer, Color.Black, Modifier.weight(1f)) {}
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    ColorCell("Surface Dim", if (isDark) Color(0xFF141218) else Color(0xFFDED8E1), textColor.copy(alpha = 0.7f), Modifier.weight(1f)) {}
                    ColorCell("Surface", themeColors.surface, textColor.copy(alpha = 0.7f), Modifier.weight(1f)) {
                        onColorClick("Surface", themeColors.surface)
                    }
                    ColorCell("Surface Bright", if (isDark) Color(0xFF3B383E) else Color(0xFFFFFBFE), textColor.copy(alpha = 0.7f), Modifier.weight(1f)) {}
                    ColorCell("Inverse Surf.", if (isDark) Color(0xFFE6E1E5) else Color(0xFF313033), if (isDark) Color.Black else Color.White, Modifier.weight(1f)) {}
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    ColorCell("On Surface", textColor, bgColor, Modifier.weight(1f)) {}
                    ColorCell("On Surf. Var.", textColor.copy(alpha = 0.7f), bgColor, Modifier.weight(1f)) {}
                    ColorCell("Outline", themeColors.outline, bgColor, Modifier.weight(1f)) {}
                    ColorCell("Scrim", Color.Black, Color.White, Modifier.weight(1f)) {}
                }
            }
        }
    }
}

@Composable
private fun ColorCell(
    label: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .background(color)
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 5.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 6.sp
        )
    }
}

@Composable
private fun TonalPaletteSection(
    themeColors: ThemeColors,
    modifier: Modifier = Modifier
) {
    val tones = listOf(100, 99, 95, 90, 80, 70, 60, 50, 40, 35, 30, 25, 20, 15, 10, 5, 0)
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TonalPaletteRow("Primary", themeColors.primary, tones)
        TonalPaletteRow("Secondary", themeColors.secondary, tones)
        TonalPaletteRow("Tertiary", themeColors.tertiary, tones)
        TonalPaletteRow("Neutral", Color(0xFF9E9E9E), tones)
        TonalPaletteRow("Neutral Variant", Color(0xFF8D8D8D), tones)
    }
}

@Composable
private fun TonalPaletteRow(
    name: String,
    baseColor: Color,
    tones: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            tones.forEach { tone ->
                val toneColor = baseColor.copy(
                    red = (baseColor.red * tone / 100f).coerceIn(0f, 1f),
                    green = (baseColor.green * tone / 100f).coerceIn(0f, 1f),
                    blue = (baseColor.blue * tone / 100f).coerceIn(0f, 1f)
                )
                
                Box(
                    modifier = Modifier
                        .size(20.dp, 28.dp)
                        .background(toneColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tone.toString(),
                        fontSize = 5.sp,
                        color = if (tone > 50) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FontSelectionSection(
    displayFont: String,
    bodyFont: String,
    onDisplayFontChange: (String) -> Unit,
    onBodyFontChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Choose fonts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Display, headlines, & titles",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "As the largest text on the screen, these styles are reserved for short, important text, and high-emphasis text.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            FontDropdown(
                selectedFont = displayFont,
                onFontSelected = onDisplayFontChange
            )
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Body & Labels",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Typefaces intended for body and label which are readable at smaller sizes and comfortably read in longer passages.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            FontDropdown(
                selectedFont = bodyFont,
                onFontSelected = onBodyFontChange
            )
        }
    }
}

@Composable
private fun FontDropdown(
    selectedFont: String,
    onFontSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val fonts = listOf("-- System Default --", "Roboto", "Inter", "Open Sans", "Montserrat", "Poppins", "Lato", "Noto Sans", "Source Sans Pro")
    
    Box(modifier = modifier) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedFont, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fonts.forEach { font ->
                DropdownMenuItem(
                    text = { Text(font) },
                    onClick = {
                        onFontSelected(font)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    currentPage: Int,
    totalPages: Int,
    showExportMenu: Boolean,
    onExportMenuToggle: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onExport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentPage of $totalPages",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    enabled = currentPage > 1,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Back", fontSize = 13.sp)
                }
                
                Box {
                    Button(
                        onClick = { 
                            if (currentPage < totalPages) onNext()
                            else onExportMenuToggle(true)
                        },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            if (currentPage < totalPages) "Pick your fonts" else "Export theme",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (currentPage < totalPages) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { onExportMenuToggle(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export as ZIP (Jetpack Compose)") },
                            onClick = { onExport("compose") }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as ZIP (Android XML)") },
                            onClick = { onExport("android") }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as ZIP (Web/CSS)") },
                            onClick = { onExport("web") }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as JSON") },
                            onClick = { onExport("json") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeNameCard(
    themeName: String,
    displayFont: String,
    bodyFont: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = displayFont.replace("-- ", "").replace(" --", ""),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = bodyFont.replace("-- ", "").replace(" --", ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeedColorRow(
    themeColors: ThemeColors,
    onColorSelected: (Color) -> Unit,
    onPickColor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seedColors = listOf(
        Color(0xFFE8B896), Color(0xFFD4A8A8), Color(0xFFC4C4A8),
        Color(0xFFE8A8A8), Color(0xFFA8C4D4)
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            seedColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (color == themeColors.primary) 3.dp else 1.dp,
                            color = if (color == themeColors.primary) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable(onClick = onPickColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Colorize,
                    contentDescription = "Pick color",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun exportTheme(
    context: Context,
    themeName: String,
    colors: ThemeColors,
    displayFont: String,
    bodyFont: String,
    format: String
) {
    try {
        val cacheDir = context.cacheDir
        val themeDir = File(cacheDir, "theme_export")
        themeDir.mkdirs()
        
        when (format) {
            "compose" -> {
                val colorKt = generateComposeColorKt(themeName, colors)
                val themeKt = generateComposeThemeKt(themeName)
                val typeKt = generateComposeTypeKt(displayFont, bodyFont)
                
                File(themeDir, "Color.kt").writeText(colorKt)
                File(themeDir, "Theme.kt").writeText(themeKt)
                File(themeDir, "Type.kt").writeText(typeKt)
            }
            "android" -> {
                val colorsXml = generateAndroidColorsXml(colors)
                val themeXml = generateAndroidThemeXml(themeName)
                val typeXml = generateAndroidTypeXml(displayFont, bodyFont)
                
                File(themeDir, "colors.xml").writeText(colorsXml)
                File(themeDir, "themes.xml").writeText(themeXml)
                File(themeDir, "type.xml").writeText(typeXml)
            }
            "web" -> {
                val cssContent = generateWebCss(themeName, colors, displayFont, bodyFont)
                File(themeDir, "theme.css").writeText(cssContent)
            }
            "json" -> {
                val jsonContent = generateThemeJson(themeName, colors, displayFont, bodyFont)
                File(themeDir, "theme.json").writeText(jsonContent)
            }
        }
        
        val zipFile = File(cacheDir, "${themeName.replace(" ", "_")}_${format}.zip")
        createZip(themeDir, zipFile)
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            zipFile
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Export Theme"))
        
        Toast.makeText(context, "Theme exported successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun createZip(sourceDir: File, zipFile: File) {
    ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
        sourceDir.listFiles()?.forEach { file ->
            zos.putNextEntry(ZipEntry(file.name))
            file.inputStream().use { it.copyTo(zos) }
            zos.closeEntry()
        }
    }
}

private fun generateAndroidColorsXml(colors: ThemeColors): String {
    return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="md_theme_primary">${colorToHex(colors.primary)}</color>
    <color name="md_theme_secondary">${colorToHex(colors.secondary)}</color>
    <color name="md_theme_tertiary">${colorToHex(colors.tertiary)}</color>
    <color name="md_theme_error">${colorToHex(colors.error)}</color>
    <color name="md_theme_primaryContainer">${colorToHex(colors.primaryContainer)}</color>
    <color name="md_theme_secondaryContainer">${colorToHex(colors.secondaryContainer)}</color>
    <color name="md_theme_tertiaryContainer">${colorToHex(colors.tertiaryContainer)}</color>
    <color name="md_theme_errorContainer">${colorToHex(colors.errorContainer)}</color>
    <color name="md_theme_surface">${colorToHex(colors.surface)}</color>
    <color name="md_theme_surfaceVariant">${colorToHex(colors.surfaceVariant)}</color>
    <color name="md_theme_background">${colorToHex(colors.background)}</color>
    <color name="md_theme_outline">${colorToHex(colors.outline)}</color>
</resources>"""
}

private fun generateAndroidThemeXml(themeName: String): String {
    val safeName = themeName.replace(" ", "").replace("-", "")
    return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.$safeName" parent="Theme.Material3.DayNight">
        <item name="colorPrimary">@color/md_theme_primary</item>
        <item name="colorSecondary">@color/md_theme_secondary</item>
        <item name="colorTertiary">@color/md_theme_tertiary</item>
        <item name="colorError">@color/md_theme_error</item>
        <item name="colorPrimaryContainer">@color/md_theme_primaryContainer</item>
        <item name="colorSecondaryContainer">@color/md_theme_secondaryContainer</item>
        <item name="colorTertiaryContainer">@color/md_theme_tertiaryContainer</item>
        <item name="colorErrorContainer">@color/md_theme_errorContainer</item>
        <item name="colorSurface">@color/md_theme_surface</item>
        <item name="colorSurfaceVariant">@color/md_theme_surfaceVariant</item>
        <item name="android:colorBackground">@color/md_theme_background</item>
        <item name="colorOutline">@color/md_theme_outline</item>
    </style>
</resources>"""
}

private fun generateAndroidTypeXml(displayFont: String, bodyFont: String): String {
    val displayFontFamily = if (displayFont == "-- System Default --") "sans-serif" else displayFont.lowercase().replace(" ", "_")
    val bodyFontFamily = if (bodyFont == "-- System Default --") "sans-serif" else bodyFont.lowercase().replace(" ", "_")
    
    return """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="TextAppearance.Display" parent="TextAppearance.Material3.DisplayLarge">
        <item name="fontFamily">$displayFontFamily</item>
    </style>
    <style name="TextAppearance.Headline" parent="TextAppearance.Material3.HeadlineLarge">
        <item name="fontFamily">$displayFontFamily</item>
    </style>
    <style name="TextAppearance.Title" parent="TextAppearance.Material3.TitleLarge">
        <item name="fontFamily">$displayFontFamily</item>
    </style>
    <style name="TextAppearance.Body" parent="TextAppearance.Material3.BodyLarge">
        <item name="fontFamily">$bodyFontFamily</item>
    </style>
    <style name="TextAppearance.Label" parent="TextAppearance.Material3.LabelLarge">
        <item name="fontFamily">$bodyFontFamily</item>
    </style>
</resources>"""
}

private fun generateWebCss(themeName: String, colors: ThemeColors, displayFont: String, bodyFont: String): String {
    val displayFontStack = if (displayFont == "-- System Default --") "system-ui, sans-serif" else "'$displayFont', sans-serif"
    val bodyFontStack = if (bodyFont == "-- System Default --") "system-ui, sans-serif" else "'$bodyFont', sans-serif"
    
    return """:root {
  /* ${themeName} - Material Theme */
  
  /* Colors */
  --md-sys-color-primary: ${colorToHex(colors.primary)};
  --md-sys-color-secondary: ${colorToHex(colors.secondary)};
  --md-sys-color-tertiary: ${colorToHex(colors.tertiary)};
  --md-sys-color-error: ${colorToHex(colors.error)};
  --md-sys-color-primary-container: ${colorToHex(colors.primaryContainer)};
  --md-sys-color-secondary-container: ${colorToHex(colors.secondaryContainer)};
  --md-sys-color-tertiary-container: ${colorToHex(colors.tertiaryContainer)};
  --md-sys-color-error-container: ${colorToHex(colors.errorContainer)};
  --md-sys-color-surface: ${colorToHex(colors.surface)};
  --md-sys-color-surface-variant: ${colorToHex(colors.surfaceVariant)};
  --md-sys-color-background: ${colorToHex(colors.background)};
  --md-sys-color-outline: ${colorToHex(colors.outline)};
  
  /* Typography */
  --md-sys-typescale-display-font: $displayFontStack;
  --md-sys-typescale-headline-font: $displayFontStack;
  --md-sys-typescale-title-font: $displayFontStack;
  --md-sys-typescale-body-font: $bodyFontStack;
  --md-sys-typescale-label-font: $bodyFontStack;
}

body {
  font-family: var(--md-sys-typescale-body-font);
  background-color: var(--md-sys-color-background);
  color: var(--md-sys-color-on-surface, #1C1B1F);
}

h1, h2, h3 {
  font-family: var(--md-sys-typescale-display-font);
}

.primary {
  background-color: var(--md-sys-color-primary);
}

.secondary {
  background-color: var(--md-sys-color-secondary);
}

.tertiary {
  background-color: var(--md-sys-color-tertiary);
}
"""
}

private fun generateThemeJson(themeName: String, colors: ThemeColors, displayFont: String, bodyFont: String): String {
    return """{
  "name": "$themeName",
  "colors": {
    "primary": "${colorToHex(colors.primary)}",
    "secondary": "${colorToHex(colors.secondary)}",
    "tertiary": "${colorToHex(colors.tertiary)}",
    "error": "${colorToHex(colors.error)}",
    "primaryContainer": "${colorToHex(colors.primaryContainer)}",
    "secondaryContainer": "${colorToHex(colors.secondaryContainer)}",
    "tertiaryContainer": "${colorToHex(colors.tertiaryContainer)}",
    "errorContainer": "${colorToHex(colors.errorContainer)}",
    "surface": "${colorToHex(colors.surface)}",
    "surfaceVariant": "${colorToHex(colors.surfaceVariant)}",
    "background": "${colorToHex(colors.background)}",
    "outline": "${colorToHex(colors.outline)}"
  },
  "typography": {
    "displayFont": "$displayFont",
    "bodyFont": "$bodyFont"
  }
}"""
}

private fun generateComposeColorKt(themeName: String, colors: ThemeColors): String {
    val packageName = themeName.lowercase().replace(" ", "").replace("-", "")
    return """package com.example.$packageName.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val md_theme_light_primary = Color(${colorToArgbHex(colors.primary)})
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(${colorToArgbHex(colors.primaryContainer)})
val md_theme_light_onPrimaryContainer = Color(0xFF1D1B20)
val md_theme_light_secondary = Color(${colorToArgbHex(colors.secondary)})
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(${colorToArgbHex(colors.secondaryContainer)})
val md_theme_light_onSecondaryContainer = Color(0xFF1D1B20)
val md_theme_light_tertiary = Color(${colorToArgbHex(colors.tertiary)})
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(${colorToArgbHex(colors.tertiaryContainer)})
val md_theme_light_onTertiaryContainer = Color(0xFF1D1B20)
val md_theme_light_error = Color(${colorToArgbHex(colors.error)})
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(${colorToArgbHex(colors.errorContainer)})
val md_theme_light_onErrorContainer = Color(0xFF410E0B)
val md_theme_light_background = Color(${colorToArgbHex(colors.background)})
val md_theme_light_onBackground = Color(0xFF1C1B1F)
val md_theme_light_surface = Color(${colorToArgbHex(colors.surface)})
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(${colorToArgbHex(colors.surfaceVariant)})
val md_theme_light_onSurfaceVariant = Color(0xFF49454F)
val md_theme_light_outline = Color(${colorToArgbHex(colors.outline)})
val md_theme_light_outlineVariant = Color(0xFFCAC4D0)
val md_theme_light_scrim = Color(0xFF000000)
val md_theme_light_inverseSurface = Color(0xFF313033)
val md_theme_light_inverseOnSurface = Color(0xFFF4EFF4)
val md_theme_light_inversePrimary = Color(${colorToArgbHex(colors.primary.copy(alpha = 0.8f))})
val md_theme_light_surfaceTint = Color(${colorToArgbHex(colors.primary)})

// Dark Theme Colors
val md_theme_dark_primary = Color(${colorToArgbHex(colors.primary.copy(red = minOf(colors.primary.red * 1.2f, 1f), green = minOf(colors.primary.green * 1.2f, 1f), blue = minOf(colors.primary.blue * 1.2f, 1f)))})
val md_theme_dark_onPrimary = Color(0xFF1D1B20)
val md_theme_dark_primaryContainer = Color(${colorToArgbHex(colors.primary.copy(alpha = 0.7f))})
val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)
val md_theme_dark_secondary = Color(${colorToArgbHex(colors.secondary.copy(red = minOf(colors.secondary.red * 1.2f, 1f), green = minOf(colors.secondary.green * 1.2f, 1f), blue = minOf(colors.secondary.blue * 1.2f, 1f)))})
val md_theme_dark_onSecondary = Color(0xFF1D1B20)
val md_theme_dark_secondaryContainer = Color(${colorToArgbHex(colors.secondary.copy(alpha = 0.7f))})
val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)
val md_theme_dark_tertiary = Color(${colorToArgbHex(colors.tertiary.copy(red = minOf(colors.tertiary.red * 1.2f, 1f), green = minOf(colors.tertiary.green * 1.2f, 1f), blue = minOf(colors.tertiary.blue * 1.2f, 1f)))})
val md_theme_dark_onTertiary = Color(0xFF1D1B20)
val md_theme_dark_tertiaryContainer = Color(${colorToArgbHex(colors.tertiary.copy(alpha = 0.7f))})
val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)
val md_theme_dark_error = Color(0xFFF2B8B5)
val md_theme_dark_onError = Color(0xFF601410)
val md_theme_dark_errorContainer = Color(0xFF8C1D18)
val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)
val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_onBackground = Color(0xFFE6E1E5)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE6E1E5)
val md_theme_dark_surfaceVariant = Color(0xFF49454F)
val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
val md_theme_dark_outline = Color(0xFF938F99)
val md_theme_dark_outlineVariant = Color(0xFF49454F)
val md_theme_dark_scrim = Color(0xFF000000)
val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
val md_theme_dark_inverseOnSurface = Color(0xFF313033)
val md_theme_dark_inversePrimary = Color(${colorToArgbHex(colors.primary)})
val md_theme_dark_surfaceTint = Color(${colorToArgbHex(colors.primary.copy(red = minOf(colors.primary.red * 1.2f, 1f), green = minOf(colors.primary.green * 1.2f, 1f), blue = minOf(colors.primary.blue * 1.2f, 1f)))})
"""
}

private fun generateComposeThemeKt(themeName: String): String {
    val packageName = themeName.lowercase().replace(" ", "").replace("-", "")
    val safeName = themeName.replace(" ", "").replace("-", "")
    
    return """package com.example.$packageName.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint
)

@Composable
fun ${safeName}Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
"""
}

private fun generateComposeTypeKt(displayFont: String, bodyFont: String): String {
    val displayFontFamily = if (displayFont == "-- System Default --") "FontFamily.Default" else "FontFamily.Default /* Replace with $displayFont */"
    val bodyFontFamily = if (bodyFont == "-- System Default --") "FontFamily.Default" else "FontFamily.Default /* Replace with $bodyFont */"
    
    return """package com.example.theme.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = $displayFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = $bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = $bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = $bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = $bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = $bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = $bodyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
"""
}

private fun colorToArgbHex(color: Color): String {
    val argb = color.toArgb()
    return String.format("0x%08X", argb)
}
*/