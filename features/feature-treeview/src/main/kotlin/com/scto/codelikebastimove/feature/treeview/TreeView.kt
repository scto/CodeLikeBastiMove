package com.scto.codelikebastimove.feature.treeview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val TreeViewBackground = Color(0xFF121212)
private val TreeViewFolderColor = Color(0xFFD4A574)
private val TreeViewFileColor = Color(0xFFB8A99A)
private val TreeViewTextColor = Color(0xFFE0E0E0)
private val TreeViewChevronColor = Color(0xFF888888)
private val TreeViewSelectedBackground = Color(0xFF2A2A2A)

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
    selectedPath: String? = null,
    projectName: String = "Project",
    onSettingsClick: (() -> Unit)? = null,
    onNewFolderClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TreeViewBackground)
    ) {
        TreeViewHeader(
            projectName = projectName,
            onSettingsClick = onSettingsClick,
            onNewFolderClick = onNewFolderClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
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
}

@Composable
private fun TreeViewHeader(
    projectName: String,
    onSettingsClick: (() -> Unit)?,
    onNewFolderClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TreeViewBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = projectName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TreeViewTextColor
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (onSettingsClick != null) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TreeViewFileColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (onNewFolderClick != null) {
                IconButton(
                    onClick = onNewFolderClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = "New Folder",
                        tint = TreeViewFolderColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
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

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "chevron_rotation"
    )

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
            color = if (isSelected) TreeViewSelectedBackground else Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (level * 20 + 12).dp,
                        top = 10.dp,
                        bottom = 10.dp,
                        end = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (node.isDirectory) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(rotationAngle),
                        tint = TreeViewChevronColor
                    )
                } else {
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = getFileIcon(node),
                    contentDescription = if (node.isDirectory) "Folder" else "File",
                    modifier = Modifier.size(20.dp),
                    tint = getFileIconColor(node)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = node.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TreeViewTextColor
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
        node.isDirectory -> TreeViewFolderColor
        node.name.endsWith(".kt") -> Color(0xFF7F52FF)
        node.name.endsWith(".java") -> Color(0xFFE76F00)
        node.name.endsWith(".xml") -> Color(0xFFE44D26)
        node.name.endsWith(".gradle") || node.name.endsWith(".gradle.kts") -> Color(0xFF02303A)
        node.name.endsWith(".json") -> Color(0xFFCFB94D)
        node.name.endsWith(".md") -> Color(0xFF519ABA)
        node.name.endsWith(".properties") -> Color(0xFF8B8B8B)
        node.name.startsWith(".") -> Color(0xFF888888)
        else -> TreeViewFileColor
    }
}
