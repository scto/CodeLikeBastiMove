package com.scto.codelikebastimove.feature.main.content

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThemeBuilderContent(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedPlatform by remember { mutableStateOf("Android") }
    val platforms = listOf("Android", "Windows", "Web", "Linux")
    
    Column(modifier = modifier.fillMaxSize()) {
        ThemeHeader()
        
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
                text = { Text("Edit", color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) }
            )
        }
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                item {
                    ComponentPreviewSection(selectedPlatform)
                }
                
                item {
                    PlatformSelector(
                        selectedPlatform = selectedPlatform,
                        platforms = platforms,
                        onPlatformSelected = { selectedPlatform = it }
                    )
                }
                
                item {
                    ColorSchemePreviewSection(title = "Light Scheme", isDark = false)
                }
                
                item {
                    ColorSchemePreviewSection(title = "Dark Scheme", isDark = true)
                }
                
                item {
                    TonalPaletteSection()
                }
            } else {
                item {
                    FontSelectionSection()
                }
            }
        }
        
        BottomActionBar()
    }
}

@Composable
private fun ThemeHeader(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "material-theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            var showMenu by remember { mutableStateOf(false) }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
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
private fun ComponentPreviewSection(
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
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (index) {
                                0 -> Color(0xFFD4C4A8)
                                1 -> Color(0xFFB8977E)
                                else -> Color(0xFF8B7355)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedCard(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Textfield",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedCard(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Textfield",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text("Assist") },
                leadingIcon = {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text("Filter") }
            )
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text("Suggestion") }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Button")
            }
            OutlinedButton(onClick = { }) {
                Text("+ Button")
            }
            OutlinedButton(onClick = { }) {
                Text("Button")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Color(0xFFD4C4A8) to "+",
                Color(0xFFB8977E) to "+",
                Color(0xFF8B7355) to "+",
                Color(0xFFD4C4A8) to "+ Button"
            ).forEach { (color, text) ->
                Box(
                    modifier = Modifier
                        .size(if (text == "+ Button") 80.dp else 48.dp, 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = text, color = Color.Black.copy(alpha = 0.7f))
                }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        platforms.forEach { platform ->
            FilterChip(
                selected = selectedPlatform == platform,
                onClick = { onPlatformSelected(platform) },
                label = { 
                    Text(
                        text = if (platform == "Android") "And\nroid" else platform,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = if (selectedPlatform == platform) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}

@Composable
private fun ColorSchemePreviewSection(
    title: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDark) Color(0xFF1C1B1F) else Color(0xFFFFFBFE)
    val textColor = if (isDark) Color.White else Color.Black
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("Primary", Color(0xFFD4C4A8), Color.Black, Modifier.weight(1f))
                    ColorCell("Secondary", Color(0xFFB8977E), Color.Black, Modifier.weight(1f))
                    ColorCell("Tertiary", Color(0xFF8B7355), Color.Black, Modifier.weight(1f))
                    ColorCell("Error", Color(0xFFB3261E), Color.White, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("On Primary", Color(0xFF4A3C2A), Color.White, Modifier.weight(1f))
                    ColorCell("On Secondary", Color(0xFF3D2E1E), Color.White, Modifier.weight(1f))
                    ColorCell("On Tertiary", Color(0xFF2D1F0F), Color.White, Modifier.weight(1f))
                    ColorCell("On Error", Color(0xFF601410), Color.White, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("Primary Container", Color(0xFFF5E6D3), Color.Black, Modifier.weight(1f))
                    ColorCell("Secondary Container", Color(0xFFE8D5C4), Color.Black, Modifier.weight(1f))
                    ColorCell("Tertiary Container", Color(0xFFD4C4B4), Color.Black, Modifier.weight(1f))
                    ColorCell("Error Container", Color(0xFFF9DEDC), Color.Black, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("On Primary Cont.", Color(0xFF2D1F0F), Color.White, Modifier.weight(1f))
                    ColorCell("On Secondary Cont.", Color(0xFF2D1F0F), Color.White, Modifier.weight(1f))
                    ColorCell("On Tertiary Cont.", Color(0xFF2D1F0F), Color.White, Modifier.weight(1f))
                    ColorCell("On Error Cont.", Color(0xFF410E0B), Color.White, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("Surface Dim", if (isDark) Color(0xFF141218) else Color(0xFFDED8E1), textColor.copy(alpha = 0.7f), Modifier.weight(1f))
                    ColorCell("Surface", if (isDark) Color(0xFF1C1B1F) else Color(0xFFFFFBFE), textColor.copy(alpha = 0.7f), Modifier.weight(1f))
                    ColorCell("Surface Bright", if (isDark) Color(0xFF3B383E) else Color(0xFFFFFBFE), textColor.copy(alpha = 0.7f), Modifier.weight(1f))
                    ColorCell("Inverse Surface", if (isDark) Color(0xFFE6E1E5) else Color(0xFF313033), if (isDark) Color.Black else Color.White, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("Surf. Container\nLowest", if (isDark) Color(0xFF0F0D13) else Color(0xFFFFFFFF), textColor.copy(alpha = 0.5f), Modifier.weight(1f))
                    ColorCell("Surf. Container\nLow", if (isDark) Color(0xFF1D1B20) else Color(0xFFF7F2FA), textColor.copy(alpha = 0.5f), Modifier.weight(1f))
                    ColorCell("Surf. Container", if (isDark) Color(0xFF211F26) else Color(0xFFF3EDF7), textColor.copy(alpha = 0.5f), Modifier.weight(1f))
                    ColorCell("Surf. Container\nHigh", if (isDark) Color(0xFF2B2930) else Color(0xFFECE6F0), textColor.copy(alpha = 0.5f), Modifier.weight(1f))
                    ColorCell("Surf. Container\nHighest", if (isDark) Color(0xFF36343B) else Color(0xFFE6E0E9), textColor.copy(alpha = 0.5f), Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ColorCell("On Surface", textColor, bgColor, Modifier.weight(1f))
                    ColorCell("On Surface Var.", textColor.copy(alpha = 0.7f), bgColor, Modifier.weight(1f))
                    ColorCell("Outline", Color(0xFF79747E), bgColor, Modifier.weight(1f))
                    ColorCell("Outline Variant", Color(0xFFCAC4D0), bgColor, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Spacer(modifier = Modifier.weight(2f))
                    ColorCell("Inverse On Surface", if (isDark) Color(0xFF313033) else Color(0xFFF4EFF4), if (isDark) Color.White else Color.Black, Modifier.weight(1f))
                    ColorCell("Inverse Primary", if (isDark) Color(0xFFD4C4A8) else Color(0xFF6B5D4A), if (isDark) Color.Black else Color.White, Modifier.weight(1f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Spacer(modifier = Modifier.weight(2f))
                    ColorCell("Scrim", Color(0xFF000000), Color.White, Modifier.weight(1f))
                    ColorCell("Shadow", Color(0xFF000000), Color.White, Modifier.weight(1f))
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(color)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 6.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 7.sp
        )
    }
}

@Composable
private fun TonalPaletteSection(modifier: Modifier = Modifier) {
    val tones = listOf(100, 99, 98, 95, 90, 80, 70, 60, 50, 40, 35, 30, 25, 20, 15, 10, 5, 0)
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TonalPaletteRow(
            name = "Primary",
            baseColor = Color(0xFFD4C4A8),
            tones = tones
        )
        TonalPaletteRow(
            name = "Secondary",
            baseColor = Color(0xFFB8977E),
            tones = tones
        )
        TonalPaletteRow(
            name = "Tertiary",
            baseColor = Color(0xFF8B7355),
            tones = tones
        )
        TonalPaletteRow(
            name = "Neutral",
            baseColor = Color(0xFF9E9E9E),
            tones = tones
        )
        TonalPaletteRow(
            name = "Neutral Variant",
            baseColor = Color(0xFF8D8D8D),
            tones = tones
        )
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
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
                        .size(24.dp, 32.dp)
                        .background(toneColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tone.toString(),
                        fontSize = 6.sp,
                        color = if (tone > 50) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FontSelectionSection(modifier: Modifier = Modifier) {
    var displayFont by remember { mutableStateOf("-- System Default --") }
    var bodyFont by remember { mutableStateOf("-- System Default --") }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Choose fonts",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Display, headlines, & titles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "As the largest text on the screen, these styles are reserved for short, important text, and high-emphasis text.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            FontDropdown(
                selectedFont = displayFont,
                onFontSelected = { displayFont = it }
            )
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Body & Labels",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Typefaces intended for body and label which are readable at smaller sizes and comfortably read in longer passages.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            FontDropdown(
                selectedFont = bodyFont,
                onFontSelected = { bodyFont = it }
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
    val fonts = listOf("-- System Default --", "Roboto", "Inter", "Open Sans", "Montserrat", "Poppins", "Lato")
    
    Box(modifier = modifier) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedFont)
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
private fun BottomActionBar(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1 of 2",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { }) {
                    Text("Back")
                }
                
                Button(onClick = { }) {
                    Text("Export theme")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeNameCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "System Default",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "System Default",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeedColorRow(modifier: Modifier = Modifier) {
    val seedColors = listOf(
        Color(0xFFE8B896),
        Color(0xFFD4A8A8),
        Color(0xFFC4C4A8),
        Color(0xFFE8A8A8),
        Color(0xFFFFFFFF)
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            seedColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Colorize,
                    contentDescription = "Pick color",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
