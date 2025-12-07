package com.scto.codelikebastimove.feature.main.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

data class FileTreeItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<FileTreeItem> = emptyList(),
    val level: Int = 0
)

@Composable
fun ProjectContent(
    modifier: Modifier = Modifier,
    onFileClick: (FileTreeItem) -> Unit = {}
) {
    val projectTree = remember { createSampleProjectTree() }
    
    Column(modifier = modifier.fillMaxSize()) {
        ProjectHeader()
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(projectTree) { item ->
                FileTreeItemRow(
                    item = item,
                    onItemClick = { onFileClick(it) }
                )
            }
        }
    }
}

@Composable
private fun ProjectHeader(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CodeLikeBastiMove",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FileTreeItemRow(
    item: FileTreeItem,
    onItemClick: (FileTreeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(item.level < 2) }
    
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
                    start = (12 + item.level * 16).dp,
                    end = 12.dp,
                    top = 6.dp,
                    bottom = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.isDirectory) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Icon(
                imageVector = getFileIcon(item),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = getFileIconColor(item)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = item.name,
                fontSize = 13.sp,
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
                        FileTreeItemRow(
                            item = child,
                            onItemClick = onItemClick
                        )
                    }
                }
            }
        }
    }
}

private fun getFileIcon(item: FileTreeItem): ImageVector {
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
private fun getFileIconColor(item: FileTreeItem): Color {
    return when {
        item.isDirectory -> MaterialTheme.colorScheme.primary
        item.name.endsWith(".kt") -> Color(0xFF7F52FF)
        item.name.endsWith(".java") -> Color(0xFFE76F00)
        item.name.endsWith(".xml") -> Color(0xFFE44D26)
        item.name.endsWith(".gradle.kts") -> Color(0xFF02303A)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun createSampleProjectTree(): List<FileTreeItem> {
    return listOf(
        FileTreeItem(
            name = "app",
            path = "app",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem(
                    name = "src",
                    path = "app/src",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem(
                            name = "main",
                            path = "app/src/main",
                            isDirectory = true,
                            level = 2,
                            children = listOf(
                                FileTreeItem("java", "app/src/main/java", true, level = 3),
                                FileTreeItem("res", "app/src/main/res", true, level = 3),
                                FileTreeItem("AndroidManifest.xml", "app/src/main/AndroidManifest.xml", false, level = 3)
                            )
                        )
                    )
                ),
                FileTreeItem("build.gradle.kts", "app/build.gradle.kts", false, level = 1)
            )
        ),
        FileTreeItem(
            name = "core",
            path = "core",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("core-ui", "core/core-ui", true, level = 1),
                FileTreeItem("core-resources", "core/core-resources", true, level = 1),
                FileTreeItem("core-datastore", "core/core-datastore", true, level = 1)
            )
        ),
        FileTreeItem(
            name = "features",
            path = "features",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("feature-main", "features/feature-main", true, level = 1),
                FileTreeItem("feature-editor", "features/feature-editor", true, level = 1),
                FileTreeItem("feature-git", "features/feature-git", true, level = 1)
            )
        ),
        FileTreeItem("build.gradle.kts", "build.gradle.kts", false, level = 0),
        FileTreeItem("settings.gradle.kts", "settings.gradle.kts", false, level = 0)
    )
}
