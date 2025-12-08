package com.scto.codelikebastimove.feature.main.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

enum class ProjectViewMode(
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    ANDROID(
        title = "Android",
        icon = Icons.Default.Android,
        description = "Files grouped by type"
    ),
    PROJECT(
        title = "Project",
        icon = Icons.Outlined.FolderOpen,
        description = "Directory structure"
    ),
    PACKAGES(
        title = "Packages",
        icon = Icons.Outlined.Inventory2,
        description = "Files by package"
    )
}

enum class FileOperation {
    NONE,
    COPY,
    CUT
}

enum class NewFileTemplate(
    val title: String,
    val icon: ImageVector,
    val extension: String,
    val templateContent: String
) {
    KOTLIN_CLASS(
        title = "Kotlin Class",
        icon = Icons.Outlined.Class,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

class {{NAME}} {
    
}
""".trimIndent()
    ),
    KOTLIN_INTERFACE(
        title = "Kotlin Interface",
        icon = Icons.Outlined.Extension,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

interface {{NAME}} {
    
}
""".trimIndent()
    ),
    KOTLIN_OBJECT(
        title = "Kotlin Object",
        icon = Icons.Outlined.DataObject,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

object {{NAME}} {
    
}
""".trimIndent()
    ),
    KOTLIN_DATA_CLASS(
        title = "Kotlin Data Class",
        icon = Icons.Outlined.Class,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

data class {{NAME}}(
    val id: String
)
""".trimIndent()
    ),
    KOTLIN_SEALED_CLASS(
        title = "Kotlin Sealed Class",
        icon = Icons.Outlined.Class,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

sealed class {{NAME}} {
    
}
""".trimIndent()
    ),
    KOTLIN_ENUM(
        title = "Kotlin Enum",
        icon = Icons.Outlined.Class,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

enum class {{NAME}} {
    
}
""".trimIndent()
    ),
    KOTLIN_ANNOTATION(
        title = "Kotlin Annotation",
        icon = Icons.Outlined.Code,
        extension = ".kt",
        templateContent = """
package {{PACKAGE}}

annotation class {{NAME}}
""".trimIndent()
    ),
    JAVA_CLASS(
        title = "Java Class",
        icon = Icons.Outlined.Class,
        extension = ".java",
        templateContent = """
package {{PACKAGE}};

public class {{NAME}} {
    
}
""".trimIndent()
    ),
    JAVA_INTERFACE(
        title = "Java Interface",
        icon = Icons.Outlined.Extension,
        extension = ".java",
        templateContent = """
package {{PACKAGE}};

public interface {{NAME}} {
    
}
""".trimIndent()
    ),
    JAVA_ENUM(
        title = "Java Enum",
        icon = Icons.Outlined.Class,
        extension = ".java",
        templateContent = """
package {{PACKAGE}};

public enum {{NAME}} {
    
}
""".trimIndent()
    ),
    JAVA_ANNOTATION(
        title = "Java Annotation",
        icon = Icons.Outlined.Code,
        extension = ".java",
        templateContent = """
package {{PACKAGE}};

public @interface {{NAME}} {
    
}
""".trimIndent()
    ),
    XML_LAYOUT(
        title = "XML Layout",
        icon = Icons.Outlined.Description,
        extension = ".xml",
        templateContent = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
    ),
    XML_VALUES(
        title = "XML Values",
        icon = Icons.Outlined.Description,
        extension = ".xml",
        templateContent = """
<?xml version="1.0" encoding="utf-8"?>
<resources>

</resources>
""".trimIndent()
    ),
    EMPTY_FILE(
        title = "Empty File",
        icon = Icons.Outlined.Description,
        extension = "",
        templateContent = ""
    )
}

data class FileTreeItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<FileTreeItem> = emptyList(),
    val level: Int = 0
)

data class ClipboardItem(
    val item: FileTreeItem,
    val operation: FileOperation
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectContent(
    modifier: Modifier = Modifier,
    onFileClick: (FileTreeItem) -> Unit = {}
) {
    var currentViewMode by remember { mutableStateOf(ProjectViewMode.ANDROID) }
    var showViewModeMenu by remember { mutableStateOf(false) }
    var clipboard by remember { mutableStateOf<ClipboardItem?>(null) }
    
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showNewFolderDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<FileTreeItem?>(null) }
    var parentForNewItem by remember { mutableStateOf<FileTreeItem?>(null) }
    
    val projectTree = remember(currentViewMode) {
        when (currentViewMode) {
            ProjectViewMode.ANDROID -> createAndroidViewTree()
            ProjectViewMode.PROJECT -> createProjectViewTree()
            ProjectViewMode.PACKAGES -> createPackagesViewTree()
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .combinedClickable(onClick = { showViewModeMenu = true })
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = currentViewMode.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentViewMode.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Change view",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    DropdownMenu(
                        expanded = showViewModeMenu,
                        onDismissRequest = { showViewModeMenu = false }
                    ) {
                        ProjectViewMode.entries.forEach { viewMode ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = viewMode.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (viewMode == currentViewMode) 
                                                MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        Column {
                                            Text(
                                                text = viewMode.title,
                                                fontWeight = if (viewMode == currentViewMode) 
                                                    FontWeight.SemiBold 
                                                else FontWeight.Normal,
                                                color = if (viewMode == currentViewMode) 
                                                    MaterialTheme.colorScheme.primary 
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = viewMode.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    currentViewMode = viewMode
                                    showViewModeMenu = false
                                }
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        ProjectNameHeader(projectName = "CodeLikeBastiMove")
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(projectTree) { item ->
                FileTreeItemRow(
                    item = item,
                    clipboard = clipboard,
                    onItemClick = { onFileClick(it) },
                    onCopy = { clipboard = ClipboardItem(it, FileOperation.COPY) },
                    onCut = { clipboard = ClipboardItem(it, FileOperation.CUT) },
                    onPaste = { targetItem ->
                        clipboard?.let { clip ->
                            // Handle paste logic
                            clipboard = null
                        }
                    },
                    onRename = {
                        selectedItem = it
                        showRenameDialog = true
                    },
                    onDelete = {
                        selectedItem = it
                        showDeleteDialog = true
                    },
                    onNewFile = {
                        parentForNewItem = it
                        showNewFileDialog = true
                    },
                    onNewFolder = {
                        parentForNewItem = it
                        showNewFolderDialog = true
                    }
                )
            }
        }
    }
    
    if (showNewFileDialog) {
        NewFileDialog(
            parentPath = parentForNewItem?.path ?: "",
            onDismiss = { showNewFileDialog = false },
            onCreate = { template, name ->
                // Handle file creation
                showNewFileDialog = false
            }
        )
    }
    
    if (showNewFolderDialog) {
        NewFolderDialog(
            onDismiss = { showNewFolderDialog = false },
            onCreate = { folderName ->
                // Handle folder creation
                showNewFolderDialog = false
            }
        )
    }
    
    if (showRenameDialog && selectedItem != null) {
        RenameDialog(
            currentName = selectedItem!!.name,
            onDismiss = { 
                showRenameDialog = false
                selectedItem = null
            },
            onRename = { newName ->
                // Handle rename
                showRenameDialog = false
                selectedItem = null
            }
        )
    }
    
    if (showDeleteDialog && selectedItem != null) {
        DeleteConfirmDialog(
            itemName = selectedItem!!.name,
            isDirectory = selectedItem!!.isDirectory,
            onDismiss = { 
                showDeleteDialog = false
                selectedItem = null
            },
            onConfirm = {
                // Handle delete
                showDeleteDialog = false
                selectedItem = null
            }
        )
    }
}

@Composable
private fun NewFileDialog(
    parentPath: String,
    onDismiss: () -> Unit,
    onCreate: (NewFileTemplate, String) -> Unit
) {
    var selectedTemplate by remember { mutableStateOf<NewFileTemplate?>(null) }
    var fileName by remember { mutableStateOf("") }
    var showTemplateList by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (showTemplateList) "New File" else "Enter Name")
        },
        text = {
            if (showTemplateList) {
                LazyColumn(
                    modifier = Modifier.height(400.dp)
                ) {
                    item {
                        Text(
                            text = "Kotlin",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(listOf(
                        NewFileTemplate.KOTLIN_CLASS,
                        NewFileTemplate.KOTLIN_INTERFACE,
                        NewFileTemplate.KOTLIN_OBJECT,
                        NewFileTemplate.KOTLIN_DATA_CLASS,
                        NewFileTemplate.KOTLIN_SEALED_CLASS,
                        NewFileTemplate.KOTLIN_ENUM,
                        NewFileTemplate.KOTLIN_ANNOTATION
                    )) { template ->
                        TemplateItem(
                            template = template,
                            onClick = {
                                selectedTemplate = template
                                showTemplateList = false
                            }
                        )
                    }
                    
                    item {
                        Text(
                            text = "Java",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(listOf(
                        NewFileTemplate.JAVA_CLASS,
                        NewFileTemplate.JAVA_INTERFACE,
                        NewFileTemplate.JAVA_ENUM,
                        NewFileTemplate.JAVA_ANNOTATION
                    )) { template ->
                        TemplateItem(
                            template = template,
                            onClick = {
                                selectedTemplate = template
                                showTemplateList = false
                            }
                        )
                    }
                    
                    item {
                        Text(
                            text = "XML",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(listOf(
                        NewFileTemplate.XML_LAYOUT,
                        NewFileTemplate.XML_VALUES
                    )) { template ->
                        TemplateItem(
                            template = template,
                            onClick = {
                                selectedTemplate = template
                                showTemplateList = false
                            }
                        )
                    }
                    
                    item {
                        Text(
                            text = "Other",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    item {
                        TemplateItem(
                            template = NewFileTemplate.EMPTY_FILE,
                            onClick = {
                                selectedTemplate = NewFileTemplate.EMPTY_FILE
                                showTemplateList = false
                            }
                        )
                    }
                }
            } else {
                Column {
                    Text(
                        text = selectedTemplate?.title ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (!showTemplateList) {
                TextButton(
                    onClick = {
                        selectedTemplate?.let { onCreate(it, fileName) }
                    },
                    enabled = fileName.isNotBlank()
                ) {
                    Text("Create")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (showTemplateList) {
                    onDismiss()
                } else {
                    showTemplateList = true
                    fileName = ""
                }
            }) {
                Text(if (showTemplateList) "Cancel" else "Back")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TemplateItem(
    template: NewFileTemplate,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = template.icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = when {
                template.extension == ".kt" -> Color(0xFF7F52FF)
                template.extension == ".java" -> Color(0xFFE76F00)
                template.extension == ".xml" -> Color(0xFFE44D26)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        Text(
            text = template.title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun NewFolderDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Folder") },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text("Folder name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(folderName) },
                enabled = folderName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RenameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("New name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onRename(newName) },
                enabled = newName.isNotBlank() && newName != currentName
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteConfirmDialog(
    itemName: String,
    isDirectory: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete ${if (isDirectory) "Folder" else "File"}") },
        text = {
            Text(
                "Are you sure you want to delete \"$itemName\"?" +
                if (isDirectory) "\n\nThis will delete all contents inside the folder." else ""
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProjectNameHeader(
    projectName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountTree,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = projectName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileTreeItemRow(
    item: FileTreeItem,
    clipboard: ClipboardItem?,
    onItemClick: (FileTreeItem) -> Unit,
    onCopy: (FileTreeItem) -> Unit,
    onCut: (FileTreeItem) -> Unit,
    onPaste: (FileTreeItem) -> Unit,
    onRename: (FileTreeItem) -> Unit,
    onDelete: (FileTreeItem) -> Unit,
    onNewFile: (FileTreeItem) -> Unit,
    onNewFolder: (FileTreeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(item.level < 2) }
    var showContextMenu by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            if (item.isDirectory) {
                                isExpanded = !isExpanded
                            } else {
                                onItemClick(item)
                            }
                        },
                        onLongClick = { showContextMenu = true }
                    )
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
            
            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                offset = DpOffset((12 + item.level * 16).dp, 0.dp)
            ) {
                if (item.isDirectory) {
                    DropdownMenuItem(
                        text = { Text("New File") },
                        onClick = {
                            showContextMenu = false
                            onNewFile(item)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.NoteAdd, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("New Folder") },
                        onClick = {
                            showContextMenu = false
                            onNewFolder(item)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.CreateNewFolder, contentDescription = null)
                        }
                    )
                    HorizontalDivider()
                }
                
                DropdownMenuItem(
                    text = { Text("Copy") },
                    onClick = {
                        showContextMenu = false
                        onCopy(item)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                    }
                )
                
                DropdownMenuItem(
                    text = { Text("Cut") },
                    onClick = {
                        showContextMenu = false
                        onCut(item)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                    }
                )
                
                if (item.isDirectory && clipboard != null) {
                    DropdownMenuItem(
                        text = { Text("Paste") },
                        onClick = {
                            showContextMenu = false
                            onPaste(item)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ContentPaste, contentDescription = null)
                        }
                    )
                }
                
                HorizontalDivider()
                
                DropdownMenuItem(
                    text = { Text("Rename") },
                    onClick = {
                        showContextMenu = false
                        onRename(item)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null)
                    }
                )
                
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showContextMenu = false
                        onDelete(item)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
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
                            clipboard = clipboard,
                            onItemClick = onItemClick,
                            onCopy = onCopy,
                            onCut = onCut,
                            onPaste = onPaste,
                            onRename = onRename,
                            onDelete = onDelete,
                            onNewFile = onNewFile,
                            onNewFolder = onNewFolder
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

private fun createAndroidViewTree(): List<FileTreeItem> {
    return listOf(
        FileTreeItem(
            name = "app",
            path = "app",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem(
                    name = "manifests",
                    path = "app/manifests",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem("AndroidManifest.xml", "app/src/main/AndroidManifest.xml", false, level = 2)
                    )
                ),
                FileTreeItem(
                    name = "kotlin+java",
                    path = "app/kotlin+java",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem(
                            name = "com.scto.codelikebastimove",
                            path = "app/src/main/kotlin/com/scto/codelikebastimove",
                            isDirectory = true,
                            level = 2,
                            children = listOf(
                                FileTreeItem("MainActivity.kt", "MainActivity.kt", false, level = 3)
                            )
                        ),
                        FileTreeItem(
                            name = "com.scto.codelikebastimove (androidTest)",
                            path = "app/src/androidTest",
                            isDirectory = true,
                            level = 2
                        ),
                        FileTreeItem(
                            name = "com.scto.codelikebastimove (test)",
                            path = "app/src/test",
                            isDirectory = true,
                            level = 2
                        )
                    )
                ),
                FileTreeItem(
                    name = "res",
                    path = "app/res",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem("drawable", "app/src/main/res/drawable", true, level = 2),
                        FileTreeItem("mipmap", "app/src/main/res/mipmap", true, level = 2),
                        FileTreeItem("values", "app/src/main/res/values", true, level = 2)
                    )
                )
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
        FileTreeItem(
            name = "Gradle Scripts",
            path = "gradle",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("build.gradle.kts (Project)", "build.gradle.kts", false, level = 1),
                FileTreeItem("build.gradle.kts (Module: app)", "app/build.gradle.kts", false, level = 1),
                FileTreeItem("settings.gradle.kts", "settings.gradle.kts", false, level = 1),
                FileTreeItem("gradle.properties", "gradle.properties", false, level = 1),
                FileTreeItem("libs.versions.toml", "gradle/libs.versions.toml", false, level = 1)
            )
        )
    )
}

private fun createProjectViewTree(): List<FileTreeItem> {
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
                                FileTreeItem("kotlin", "app/src/main/kotlin", true, level = 3),
                                FileTreeItem("res", "app/src/main/res", true, level = 3),
                                FileTreeItem("AndroidManifest.xml", "app/src/main/AndroidManifest.xml", false, level = 3)
                            )
                        ),
                        FileTreeItem("androidTest", "app/src/androidTest", true, level = 2),
                        FileTreeItem("test", "app/src/test", true, level = 2)
                    )
                ),
                FileTreeItem("build.gradle.kts", "app/build.gradle.kts", false, level = 1),
                FileTreeItem("proguard-rules.pro", "app/proguard-rules.pro", false, level = 1)
            )
        ),
        FileTreeItem(
            name = "build-logic",
            path = "build-logic",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("convention", "build-logic/convention", true, level = 1),
                FileTreeItem("settings.gradle.kts", "build-logic/settings.gradle.kts", false, level = 1)
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
                FileTreeItem("core-datastore", "core/core-datastore", true, level = 1),
                FileTreeItem("core-datastore-proto", "core/core-datastore-proto", true, level = 1),
                FileTreeItem("templates-api", "core/templates-api", true, level = 1),
                FileTreeItem("templates-impl", "core/templates-impl", true, level = 1)
            )
        ),
        FileTreeItem(
            name = "features",
            path = "features",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("feature-main", "features/feature-main", true, level = 1),
                FileTreeItem("feature-home", "features/feature-home", true, level = 1),
                FileTreeItem("feature-editor", "features/feature-editor", true, level = 1),
                FileTreeItem("feature-git", "features/feature-git", true, level = 1),
                FileTreeItem("feature-settings", "features/feature-settings", true, level = 1),
                FileTreeItem("feature-onboarding", "features/feature-onboarding", true, level = 1)
            )
        ),
        FileTreeItem(
            name = "gradle",
            path = "gradle",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("wrapper", "gradle/wrapper", true, level = 1),
                FileTreeItem("libs.versions.toml", "gradle/libs.versions.toml", false, level = 1)
            )
        ),
        FileTreeItem("build.gradle.kts", "build.gradle.kts", false, level = 0),
        FileTreeItem("settings.gradle.kts", "settings.gradle.kts", false, level = 0),
        FileTreeItem("gradle.properties", "gradle.properties", false, level = 0)
    )
}

private fun createPackagesViewTree(): List<FileTreeItem> {
    return listOf(
        FileTreeItem(
            name = "com.scto.codelikebastimove",
            path = "com/scto/codelikebastimove",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("MainActivity.kt", "MainActivity.kt", false, level = 1)
            )
        ),
        FileTreeItem(
            name = "com.scto.codelikebastimove.core.ui",
            path = "com/scto/codelikebastimove/core/ui",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem(
                    name = "theme",
                    path = "theme",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem("Theme.kt", "Theme.kt", false, level = 2),
                        FileTreeItem("Colors.kt", "Colors.kt", false, level = 2),
                        FileTreeItem("Typography.kt", "Typography.kt", false, level = 2)
                    )
                )
            )
        ),
        FileTreeItem(
            name = "com.scto.codelikebastimove.core.datastore",
            path = "com/scto/codelikebastimove/core/datastore",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("UserPreferences.kt", "UserPreferences.kt", false, level = 1),
                FileTreeItem("UserPreferencesRepository.kt", "UserPreferencesRepository.kt", false, level = 1)
            )
        ),
        FileTreeItem(
            name = "com.scto.codelikebastimove.feature.main",
            path = "com/scto/codelikebastimove/feature/main",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("MainScreen.kt", "MainScreen.kt", false, level = 1),
                FileTreeItem("MainViewModel.kt", "MainViewModel.kt", false, level = 1),
                FileTreeItem(
                    name = "components",
                    path = "components",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem("MainTopAppBar.kt", "MainTopAppBar.kt", false, level = 2),
                        FileTreeItem("ContentNavigationRail.kt", "ContentNavigationRail.kt", false, level = 2),
                        FileTreeItem("BottomSheetBar.kt", "BottomSheetBar.kt", false, level = 2)
                    )
                ),
                FileTreeItem(
                    name = "content",
                    path = "content",
                    isDirectory = true,
                    level = 1,
                    children = listOf(
                        FileTreeItem("EditorContent.kt", "EditorContent.kt", false, level = 2),
                        FileTreeItem("ProjectContent.kt", "ProjectContent.kt", false, level = 2),
                        FileTreeItem("GitContent.kt", "GitContent.kt", false, level = 2)
                    )
                )
            )
        ),
        FileTreeItem(
            name = "com.scto.codelikebastimove.feature.git",
            path = "com/scto/codelikebastimove/feature/git",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("GitScreen.kt", "GitScreen.kt", false, level = 1),
                FileTreeItem("GitCommand.kt", "GitCommand.kt", false, level = 1)
            )
        ),
        FileTreeItem(
            name = "com.scto.codelikebastimove.feature.onboarding",
            path = "com/scto/codelikebastimove/feature/onboarding",
            isDirectory = true,
            level = 0,
            children = listOf(
                FileTreeItem("OnboardingScreen.kt", "OnboardingScreen.kt", false, level = 1),
                FileTreeItem("OnboardingViewModel.kt", "OnboardingViewModel.kt", false, level = 1)
            )
        )
    )
}
