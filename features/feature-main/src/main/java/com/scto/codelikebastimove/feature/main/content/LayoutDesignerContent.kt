package com.scto.codelikebastimove.feature.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Splitscreen
import androidx.compose.material.icons.outlined.Tablet
import androidx.compose.material.icons.outlined.ViewQuilt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class DesignerViewMode(val label: String, val icon: ImageVector) {
    DESIGN("Design", Icons.Outlined.DesignServices),
    CODE("Code", Icons.Outlined.Code),
    SPLIT("Split", Icons.Outlined.Splitscreen)
}

enum class DevicePreview(val label: String, val icon: ImageVector) {
    PHONE("Phone", Icons.Outlined.PhoneAndroid),
    TABLET("Tablet", Icons.Outlined.Tablet)
}

@Composable
fun LayoutDesignerContent(
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(DesignerViewMode.DESIGN) }
    var devicePreview by remember { mutableStateOf(DevicePreview.PHONE) }
    
    Column(modifier = modifier.fillMaxSize()) {
        LayoutDesignerHeader()
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        DesignerToolbar(
            viewMode = viewMode,
            onViewModeChanged = { viewMode = it },
            devicePreview = devicePreview,
            onDevicePreviewChanged = { devicePreview = it }
        )
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        Row(modifier = Modifier.fillMaxSize()) {
            ComponentPalette(
                modifier = Modifier
                    .width(160.dp)
                    .fillMaxHeight()
            )
            
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            DesignCanvas(
                viewMode = viewMode,
                devicePreview = devicePreview,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            PropertiesPanel(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun LayoutDesignerHeader(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ViewQuilt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Layout Designer",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Row {
                IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DesignerToolbar(
    viewMode: DesignerViewMode,
    onViewModeChanged: (DesignerViewMode) -> Unit,
    devicePreview: DevicePreview,
    onDevicePreviewChanged: (DevicePreview) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DesignerViewMode.entries.forEach { mode ->
                    FilterChip(
                        selected = viewMode == mode,
                        onClick = { onViewModeChanged(mode) },
                        label = { Text(mode.label, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DevicePreview.entries.forEach { device ->
                    FilterChip(
                        selected = devicePreview == device,
                        onClick = { onDevicePreviewChanged(device) },
                        label = { Text(device.label, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = device.icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComponentPalette(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Components",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(components) { component ->
                    DraggableComponent(component = component)
                }
            }
        }
    }
}

@Composable
private fun DraggableComponent(
    component: LayoutComponent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = component.icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = component.name,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DesignCanvas(
    viewMode: DesignerViewMode,
    devicePreview: DevicePreview,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (viewMode) {
            DesignerViewMode.DESIGN -> DeviceFrame(devicePreview = devicePreview)
            DesignerViewMode.CODE -> CodeView()
            DesignerViewMode.SPLIT -> SplitView(devicePreview = devicePreview)
        }
    }
}

@Composable
private fun DeviceFrame(
    devicePreview: DevicePreview,
    modifier: Modifier = Modifier
) {
    val aspectRatio = when (devicePreview) {
        DevicePreview.PHONE -> 9f / 16f
        DevicePreview.TABLET -> 3f / 4f
    }
    
    Box(
        modifier = modifier
            .fillMaxHeight(0.9f)
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Drag components here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CodeView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(8.dp)
    ) {
        Text(
            text = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    
    <!-- Add your views here -->
    
</LinearLayout>
            """.trimIndent(),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SplitView(
    devicePreview: DevicePreview,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            DeviceFrame(devicePreview = devicePreview)
        }
        VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Box(modifier = Modifier.weight(1f)) {
            CodeView()
        }
    }
}

@Composable
private fun PropertiesPanel(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Properties",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Select a component to view its properties",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class LayoutComponent(
    val name: String,
    val icon: ImageVector,
    val category: String
)

private val components = listOf(
    LayoutComponent("TextView", Icons.Default.TextFields, "Text"),
    LayoutComponent("Button", Icons.Default.SmartButton, "Buttons"),
    LayoutComponent("ImageView", Icons.Default.Image, "Images"),
    LayoutComponent("CheckBox", Icons.Default.CheckBox, "Selection"),
    LayoutComponent("LinearLayout", Icons.Default.ViewColumn, "Layouts"),
    LayoutComponent("ConstraintLayout", Icons.Outlined.ViewQuilt, "Layouts")
)
