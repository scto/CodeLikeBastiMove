package com.scto.codelikebastimove.feature.treeview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.unit.dp

data class TreeNodeData(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<TreeNodeData> = emptyList()
)

@Composable
fun TreeView(
    nodes: List<TreeNodeData>,
    onNodeClick: (TreeNodeData) -> Unit,
    modifier: Modifier = Modifier,
    selectedPath: String? = null
) {
    LazyColumn(modifier = modifier) {
        items(nodes) { node ->
            TreeNodeItem(
                node = node,
                level = 0,
                onNodeClick = onNodeClick,
                selectedPath = selectedPath
            )
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: TreeNodeData,
    level: Int,
    onNodeClick: (TreeNodeData) -> Unit,
    selectedPath: String?
) {
    var isExpanded by remember { mutableStateOf(level == 0) }
    val isSelected = node.path == selectedPath
    
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (node.isDirectory) {
                        isExpanded = !isExpanded
                    }
                    onNodeClick(node)
                },
            color = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (level * 16 + 8).dp,
                        top = 6.dp,
                        bottom = 6.dp,
                        end = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (node.isDirectory) {
                    Icon(
                        imageVector = if (isExpanded) 
                            Icons.Default.KeyboardArrowDown 
                        else 
                            Icons.Default.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Spacer(modifier = Modifier.width(18.dp))
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Icon(
                    imageVector = getFileIcon(node),
                    contentDescription = if (node.isDirectory) "Folder" else "File",
                    modifier = Modifier.size(20.dp),
                    tint = getFileIconColor(node)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = node.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        if (node.isDirectory) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    node.children.forEach { child ->
                        TreeNodeItem(
                            node = child,
                            level = level + 1,
                            onNodeClick = onNodeClick,
                            selectedPath = selectedPath
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getFileIcon(node: TreeNodeData): ImageVector {
    return when {
        node.isDirectory -> Icons.Default.Folder
        else -> Icons.Default.Description
    }
}

@Composable
private fun getFileIconColor(node: TreeNodeData): Color {
    return when {
        node.isDirectory -> Color(0xFFFFCC00)
        node.name.endsWith(".kt") -> Color(0xFF7F52FF)
        node.name.endsWith(".java") -> Color(0xFFE76F00)
        node.name.endsWith(".xml") -> Color(0xFFE44D26)
        node.name.endsWith(".gradle") || node.name.endsWith(".gradle.kts") -> Color(0xFF02303A)
        node.name.endsWith(".json") -> Color(0xFF000000)
        node.name.endsWith(".md") -> Color(0xFF083FA1)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
