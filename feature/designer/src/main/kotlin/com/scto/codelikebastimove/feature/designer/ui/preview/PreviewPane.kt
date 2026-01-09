package com.scto.codelikebastimove.feature.designer.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.scto.codelikebastimove.feature.designer.data.model.Block
import com.scto.codelikebastimove.feature.designer.data.model.BlockType

data class DevicePreset(
    val name: String,
    val width: Float,
    val height: Float,
    val density: Float = 2f
)

val devicePresets = listOf(
    DevicePreset("Phone", 360f, 640f),
    DevicePreset("Phone Large", 412f, 732f),
    DevicePreset("Tablet", 800f, 1280f, 1.5f),
    DevicePreset("Foldable", 673f, 841f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewPane(
    blocks: List<Block>,
    onRefresh: () -> Unit,
    onShowCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(0.5f) }
    var selectedDeviceIndex by remember { mutableIntStateOf(0) }
    var isLandscape by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    
    val selectedDevice = devicePresets[selectedDeviceIndex]
    val deviceWidth = if (isLandscape) selectedDevice.height else selectedDevice.width
    val deviceHeight = if (isLandscape) selectedDevice.width else selectedDevice.height
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Row {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Preview"
                    )
                }
                IconButton(onClick = onShowCode) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Show Code"
                    )
                }
            }
        }
        
        HorizontalDivider()
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleChoiceSegmentedButtonRow {
                devicePresets.forEachIndexed { index, device ->
                    SegmentedButton(
                        selected = selectedDeviceIndex == index,
                        onClick = { selectedDeviceIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = devicePresets.size
                        ),
                        icon = {
                            val icon = when (index) {
                                0 -> Icons.Default.Smartphone
                                1 -> Icons.Default.PhoneAndroid
                                2 -> Icons.Default.Tablet
                                else -> Icons.Default.Fullscreen
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    ) {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            
            FilterChip(
                selected = isLandscape,
                onClick = { isLandscape = !isLandscape },
                label = { Text(if (isLandscape) "Landscape" else "Portrait") }
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { scale = (scale - 0.1f).coerceAtLeast(0.2f) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out"
                )
            }
            
            Slider(
                value = scale,
                onValueChange = { scale = it },
                valueRange = 0.2f..2f,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = { scale = (scale + 0.1f).coerceAtMost(2f) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom In"
                )
            }
            
            Text(
                text = "${(scale * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(48.dp)
            )
            
            IconButton(
                onClick = { 
                    scale = 0.5f
                    offsetX = 0f
                    offsetY = 0f
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitScreen,
                    contentDescription = "Fit to Screen"
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.2f, 2f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }
                    .size(
                        width = deviceWidth.dp,
                        height = deviceHeight.dp
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (blocks.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Preview",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add blocks to see preview",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        PreviewContent(blocks = blocks)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewContent(
    blocks: List<Block>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        blocks.forEach { block ->
            PreviewBlock(
                block = block,
                modifier = Modifier.padding(
                    start = (block.position.x / 4).dp,
                    top = (block.position.y / 4).dp
                )
            )
        }
    }
}

@Composable
private fun PreviewBlock(
    block: Block,
    modifier: Modifier = Modifier
) {
    val previewModifier = modifier
        .size(
            width = (block.width / 2).dp,
            height = (block.height / 2).dp
        )
    
    when (block.type) {
        BlockType.TEXT -> {
            val text = block.properties["text"]?.value as? String ?: "Text"
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = previewModifier
            )
        }
        BlockType.BUTTON -> {
            val text = block.properties["text"]?.value as? String ?: "Button"
            androidx.compose.material3.Button(
                onClick = {},
                modifier = previewModifier,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text, style = MaterialTheme.typography.labelSmall)
            }
        }
        BlockType.OUTLINED_BUTTON -> {
            val text = block.properties["text"]?.value as? String ?: "Button"
            androidx.compose.material3.OutlinedButton(
                onClick = {},
                modifier = previewModifier,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text, style = MaterialTheme.typography.labelSmall)
            }
        }
        BlockType.TEXT_FIELD, BlockType.OUTLINED_TEXT_FIELD -> {
            val label = block.properties["label"]?.value as? String ?: "Label"
            Box(
                modifier = previewModifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        BlockType.COLUMN -> {
            Column(
                modifier = previewModifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                block.children.forEach { child ->
                    PreviewBlock(block = child)
                }
            }
        }
        BlockType.ROW -> {
            Row(
                modifier = previewModifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                block.children.forEach { child ->
                    PreviewBlock(block = child)
                }
            }
        }
        BlockType.BOX, BlockType.CARD, BlockType.SURFACE -> {
            Box(
                modifier = previewModifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                block.children.forEach { child ->
                    PreviewBlock(block = child)
                }
            }
        }
        BlockType.TOP_APP_BAR -> {
            val title = block.properties["title"]?.value as? String ?: "Title"
            Box(
                modifier = previewModifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        BlockType.SWITCH -> {
            val checked = block.properties["checked"]?.value as? Boolean ?: false
            androidx.compose.material3.Switch(
                checked = checked,
                onCheckedChange = {},
                modifier = previewModifier
            )
        }
        BlockType.CHECKBOX -> {
            val checked = block.properties["checked"]?.value as? Boolean ?: false
            androidx.compose.material3.Checkbox(
                checked = checked,
                onCheckedChange = {},
                modifier = previewModifier
            )
        }
        BlockType.SPACER -> {
            Spacer(modifier = previewModifier)
        }
        BlockType.DIVIDER -> {
            HorizontalDivider(modifier = previewModifier)
        }
        else -> {
            Box(
                modifier = previewModifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = block.type.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
