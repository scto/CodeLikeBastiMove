package com.scto.codelikebastimove.feature.designer.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

import com.scto.codelikebastimove.feature.designer.data.model.Block
import com.scto.codelikebastimove.feature.designer.data.model.BlockType

import kotlin.math.roundToInt

@Composable
fun BlockCanvas(
    blocks: List<Block>,
    selectedBlockId: String?,
    onBlockSelected: (String?) -> Unit,
    onBlockMoved: (String, Offset) -> Unit,
    onBlockDeleted: (String) -> Unit,
    onBlockDropped: (BlockType, Offset) -> Unit,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val gridSize = 16f
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val tappedBlock = blocks.find { block ->
                            val blockLeft = block.position.x * scale
                            val blockTop = block.position.y * scale
                            val blockRight = blockLeft + block.width * scale
                            val blockBottom = blockTop + block.height * scale
                            offset.x in blockLeft..blockRight && offset.y in blockTop..blockBottom
                        }
                        onBlockSelected(tappedBlock?.id)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color.Gray.copy(alpha = 0.1f)
            val gridSpacing = gridSize * scale * density.density
            
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }
        }
        
        blocks.forEach { block ->
            BlockItem(
                block = block,
                isSelected = block.id == selectedBlockId,
                scale = scale,
                onSelect = { onBlockSelected(block.id) },
                onMove = { offset -> onBlockMoved(block.id, offset) },
                onDelete = { onBlockDeleted(block.id) }
            )
        }
        
        if (blocks.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Drag blocks here",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Select components from the palette on the left",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun BlockItem(
    block: Block,
    isSelected: Boolean,
    scale: Float,
    onSelect: () -> Unit,
    onMove: (Offset) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(block.position.x) }
    var offsetY by remember { mutableStateOf(block.position.y) }
    
    val backgroundColor = when (block.type.category) {
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.LAYOUT -> 
            MaterialTheme.colorScheme.primaryContainer
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.CONTAINER -> 
            MaterialTheme.colorScheme.secondaryContainer
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.TEXT -> 
            MaterialTheme.colorScheme.tertiaryContainer
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.INPUT -> 
            MaterialTheme.colorScheme.surfaceContainerHigh
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.BUTTON -> 
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.IMAGE -> 
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.NAVIGATION -> 
            MaterialTheme.colorScheme.inversePrimary
        com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.CUSTOM -> 
            MaterialTheme.colorScheme.errorContainer
    }
    
    Box(
        modifier = modifier
            .offset { IntOffset((offsetX * scale).roundToInt(), (offsetY * scale).roundToInt()) }
            .size(
                width = (block.width * scale).dp,
                height = (block.height * scale).dp
            )
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onSelect() })
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x / scale
                    offsetY += dragAmount.y / scale
                    onMove(Offset(offsetX, offsetY))
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = "Drag",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = block.type.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isSelected) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            if (block.type == BlockType.TEXT) {
                val textValue = block.properties["text"]?.value as? String ?: "Text"
                Text(
                    text = textValue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            if (block.type == BlockType.BUTTON || block.type == BlockType.OUTLINED_BUTTON || block.type == BlockType.TEXT_BUTTON) {
                val buttonText = block.properties["text"]?.value as? String ?: "Button"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        if (block.type.category == com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.LAYOUT ||
            block.type.category == com.scto.codelikebastimove.feature.designer.data.model.BlockCategory.CONTAINER) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.3f),
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }
    }
}
