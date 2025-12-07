package com.scto.codelikebastimove.feature.main.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FileTreeDrawerContent(
    projectName: String,
    onFileClick: (FileTreeItem) -> Unit,
    onNavigateToAssetStudio: () -> Unit,
    onNavigateToAIAgent: () -> Unit,
    onNavigateToBuildVariants: () -> Unit,
    onNavigateToSubModuleMaker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projectTree = remember { createDrawerFileTree(projectName) }
    
    Column(
        modifier = modifier.fillMaxHeight()
    ) {
        DrawerHeader(title = "Dateipfad")
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(projectTree) { item ->
                DrawerFileTreeItemRow(
                    item = item,
                    onItemClick = onFileClick
                )
            }
        }
        
        HorizontalDivider()
        
        DrawerBottomActions(
            onAssetStudioClick = onNavigateToAssetStudio,
            onAIAgentClick = onNavigateToAIAgent,
            onBuildVariantsClick = onNavigateToBuildVariants,
            onSubModuleMakerClick = onNavigateToSubModuleMaker
        )
    }
}

@Composable
private fun DrawerHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DrawerFileTreeItemRow(
    item: FileTreeItem,
    onItemClick: (FileTreeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(item.level < 1) }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (item.isDirectory) {
                        isExpanded = !isExpanded
                    } else {
                        onItemClick(item)
                    }
                }
                .padding(
                    start = (16 + item.level * 20).dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.isDirectory) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = getDrawerFileIcon(item),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = getDrawerFileIconColor(item)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = item.name,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        if (item.isDirectory) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    item.children.forEach { child ->
                        DrawerFileTreeItemRow(
                            item = child,
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerBottomActions(
    onAssetStudioClick: () -> Unit,
    onAIAgentClick: () -> Unit,
    onBuildVariantsClick: () -> Unit,
    onSubModuleMakerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Brush, contentDescription = null) },
            label = { Text("Asset Studio") },
            selected = false,
            onClick = onAssetStudioClick,
            shape = RoundedCornerShape(8.dp)
        )
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Memory, contentDescription = null) },
            label = { Text("AI Agent") },
            selected = false,
            onClick = onAIAgentClick,
            shape = RoundedCornerShape(8.dp)
        )
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Android, contentDescription = null) },
            label = { Text("Build Variants") },
            selected = false,
            onClick = onBuildVariantsClick,
            shape = RoundedCornerShape(8.dp)
        )
        
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Extension, contentDescription = null) },
            label = { Text("Sub-Module Maker") },
            selected = false,
            onClick = onSubModuleMakerClick,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

private fun getDrawerFileIcon(item: FileTreeItem): ImageVector {
    return when {
        item.isDirectory && item.name == "res" -> Icons.Default.FolderOpen
        item.isDirectory -> Icons.Default.Folder
        item.name.endsWith(".kt") || item.name.endsWith(".java") -> Icons.Outlined.Description
        item.name.endsWith(".xml") -> Icons.Outlined.Settings
        item.name.endsWith(".png") || item.name.endsWith(".jpg") -> Icons.Outlined.Image
        else -> Icons.Outlined.Description
    }
}

@Composable
private fun getDrawerFileIconColor(item: FileTreeItem): Color {
    return when {
        item.isDirectory -> MaterialTheme.colorScheme.primary
        item.name.endsWith(".kt") -> Color(0xFF7F52FF)
        item.name.endsWith(".java") -> Color(0xFFE76F00)
        item.name.endsWith(".xml") -> Color(0xFFE44D26)
        item.name.endsWith(".gradle.kts") -> Color(0xFF02303A)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun createDrawerFileTree(projectName: String): List<FileTreeItem> {
    return listOf(
        FileTreeItem(
            name = projectName,
            path = projectName,
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem(".acside", ".acside", true, level = 1),
                FileTreeItem(".git", ".git", true, level = 1),
                FileTreeItem(".github", ".github", true, level = 1),
                FileTreeItem(".gradle", ".gradle", true, level = 1),
                FileTreeItem(".kotlin", ".kotlin", true, level = 1),
                FileTreeItem("app", "app", true, level = 1),
                FileTreeItem("attached_assets", "attached_assets", true, level = 1),
                FileTreeItem("build", "build", true, level = 1),
                FileTreeItem("build-logic", "build-logic", true, level = 1),
                FileTreeItem("core", "core", true, level = 1),
                FileTreeItem("features", "features", true, level = 1),
                FileTreeItem("gradle", "gradle", true, level = 1),
                FileTreeItem(".gitignore", ".gitignore", false, level = 1)
            )
        )
    )
}
