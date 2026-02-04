package com.scto.codelikebastimove.feature.treeview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import java.io.File

private val TreeViewBackground = Color(0xFF1E1E1E)
private val TreeViewHeaderBackground = Color(0xFF252526)
private val TreeViewFolderColor = Color(0xFFD4A574)
private val TreeViewFileColor = Color(0xFFB8A99A)
private val TreeViewTextColor = Color(0xFFE0E0E0)
private val TreeViewChevronColor = Color(0xFF888888)
private val TreeViewSelectedBackground = Color(0xFF094771)
private val TreeViewHoverBackground = Color(0xFF2A2D2E)
private val AccentColor = Color(0xFF0D6EFD)

data class FileTreeNode(
  val file: File?,
  val name: String,
  val path: String,
  val isDirectory: Boolean,
  val level: Int = 0,
  val children: List<FileTreeNode> = emptyList(),
)

@Composable
fun EnhancedTreeView(
  rootPath: String,
  projectName: String,
  onFileClick: (FileTreeNode) -> Unit,
  onFileOperationComplete: () -> Unit,
  modifier: Modifier = Modifier,
  fileSystemVersion: Long = 0L,
) {
  var viewState by remember { mutableStateOf(TreeViewState()) }
  var showCreateFileDialog by remember { mutableStateOf(false) }
  var showCreateFolderDialog by remember { mutableStateOf(false) }
  var showRenameDialog by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }
  var contextMenuNode by remember { mutableStateOf<FileTreeNode?>(null) }
  var showContextMenu by remember { mutableStateOf(false) }
  var clipboardPath by remember { mutableStateOf<String?>(null) }
  var clipboardIsCut by remember { mutableStateOf(false) }

  val fileOperations = remember { DefaultFileOperations() }

  val nodes = remember(rootPath, fileSystemVersion, viewState.showHiddenFiles, viewState.currentMode) {
    if (rootPath.isNotBlank()) {
      buildFileTree(File(rootPath), viewState.showHiddenFiles, viewState.currentMode)
    } else {
      emptyList()
    }
  }

  val filteredNodes = remember(nodes, viewState.searchQuery) {
    if (viewState.searchQuery.isBlank()) nodes
    else filterNodes(nodes, viewState.searchQuery)
  }

  Column(modifier = modifier.fillMaxSize().background(TreeViewBackground)) {
    EnhancedTreeViewHeader(
      projectName = projectName,
      viewState = viewState,
      onViewModeChange = { viewState = viewState.copy(currentMode = it) },
      onToggleHiddenFiles = { viewState = viewState.copy(showHiddenFiles = !viewState.showHiddenFiles) },
      onToggleSearch = { viewState = viewState.copy(isSearchActive = !viewState.isSearchActive) },
      onNewFile = {
        contextMenuNode = FileTreeNode(File(rootPath), projectName, rootPath, true)
        showCreateFileDialog = true
      },
      onNewFolder = {
        contextMenuNode = FileTreeNode(File(rootPath), projectName, rootPath, true)
        showCreateFolderDialog = true
      },
    )

    if (viewState.isSearchActive) {
      SearchBar(
        query = viewState.searchQuery,
        onQueryChange = { viewState = viewState.copy(searchQuery = it) },
        onClose = { viewState = viewState.copy(isSearchActive = false, searchQuery = "") },
      )
    }

    if (filteredNodes.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = if (rootPath.isBlank()) "No project opened" else "No files found",
          color = TreeViewTextColor.copy(alpha = 0.6f),
        )
      }
    } else {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(filteredNodes) { node ->
          EnhancedTreeNodeItem(
            node = node,
            level = 0,
            selectedPath = viewState.selectedPath,
            expandedPaths = viewState.expandedPaths,
            onNodeClick = { clickedNode ->
              viewState = viewState.copy(selectedPath = clickedNode.path)
              if (!clickedNode.isDirectory) {
                onFileClick(clickedNode)
              }
            },
            onNodeExpand = { expandedNode ->
              val newExpanded = if (viewState.expandedPaths.contains(expandedNode.path)) {
                viewState.expandedPaths - expandedNode.path
              } else {
                viewState.expandedPaths + expandedNode.path
              }
              viewState = viewState.copy(expandedPaths = newExpanded)
            },
            onNodeLongClick = { longClickedNode ->
              contextMenuNode = longClickedNode
              showContextMenu = true
            },
          )
        }
      }
    }
  }

  contextMenuNode?.let { node ->
    FileContextMenu(
      isVisible = showContextMenu,
      isDirectory = node.isDirectory,
      hasClipboard = clipboardPath != null,
      onAction = { action ->
        when (action) {
          FileContextAction.NewFile -> showCreateFileDialog = true
          FileContextAction.NewFolder -> showCreateFolderDialog = true
          FileContextAction.Rename -> showRenameDialog = true
          FileContextAction.Delete -> showDeleteDialog = true
          FileContextAction.Copy -> {
            clipboardPath = node.path
            clipboardIsCut = false
          }
          FileContextAction.Cut -> {
            clipboardPath = node.path
            clipboardIsCut = true
          }
          FileContextAction.Paste -> {
            clipboardPath?.let { sourcePath ->
              val destPath = "${node.path}/${File(sourcePath).name}"
              if (clipboardIsCut) {
                fileOperations.move(sourcePath, destPath)
                clipboardPath = null
              } else {
                fileOperations.copy(sourcePath, destPath)
              }
              onFileOperationComplete()
            }
          }
          FileContextAction.Refresh -> onFileOperationComplete()
        }
      },
      onDismiss = { showContextMenu = false },
    )

    CreateFileDialog(
      isVisible = showCreateFileDialog,
      isFolder = false,
      parentPath = if (node.isDirectory) node.path else File(node.path).parent ?: rootPath,
      onConfirm = { fileName ->
        val parentPath = if (node.isDirectory) node.path else File(node.path).parent ?: rootPath
        fileOperations.createFile(parentPath, fileName)
        showCreateFileDialog = false
        onFileOperationComplete()
      },
      onDismiss = { showCreateFileDialog = false },
    )

    CreateFileDialog(
      isVisible = showCreateFolderDialog,
      isFolder = true,
      parentPath = if (node.isDirectory) node.path else File(node.path).parent ?: rootPath,
      onConfirm = { folderName ->
        val parentPath = if (node.isDirectory) node.path else File(node.path).parent ?: rootPath
        fileOperations.createFolder(parentPath, folderName)
        showCreateFolderDialog = false
        onFileOperationComplete()
      },
      onDismiss = { showCreateFolderDialog = false },
    )

    RenameDialog(
      isVisible = showRenameDialog,
      currentName = node.name,
      onConfirm = { newName ->
        fileOperations.rename(node.path, newName)
        showRenameDialog = false
        onFileOperationComplete()
      },
      onDismiss = { showRenameDialog = false },
    )

    DeleteConfirmDialog(
      isVisible = showDeleteDialog,
      fileName = node.name,
      isDirectory = node.isDirectory,
      onConfirm = {
        fileOperations.delete(node.path)
        showDeleteDialog = false
        onFileOperationComplete()
      },
      onDismiss = { showDeleteDialog = false },
    )
  }
}

@Composable
private fun EnhancedTreeViewHeader(
  projectName: String,
  viewState: TreeViewState,
  onViewModeChange: (TreeViewMode) -> Unit,
  onToggleHiddenFiles: () -> Unit,
  onToggleSearch: () -> Unit,
  onNewFile: () -> Unit,
  onNewFolder: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showViewModeMenu by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth().background(TreeViewHeaderBackground)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = viewState.currentMode.icon,
          contentDescription = null,
          tint = AccentColor,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = projectName,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          color = TreeViewTextColor,
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        IconButton(onClick = onNewFile, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.NoteAdd,
            contentDescription = "New File",
            tint = TreeViewFileColor,
            modifier = Modifier.size(18.dp),
          )
        }

        IconButton(onClick = onNewFolder, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.CreateNewFolder,
            contentDescription = "New Folder",
            tint = TreeViewFolderColor,
            modifier = Modifier.size(18.dp),
          )
        }

        IconButton(onClick = onToggleSearch, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = if (viewState.isSearchActive) AccentColor else TreeViewFileColor,
            modifier = Modifier.size(18.dp),
          )
        }

        IconButton(onClick = onToggleHiddenFiles, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = if (viewState.showHiddenFiles) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = "Toggle Hidden Files",
            tint = if (viewState.showHiddenFiles) AccentColor else TreeViewFileColor,
            modifier = Modifier.size(18.dp),
          )
        }

        Box {
          IconButton(onClick = { showViewModeMenu = true }, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "View Mode",
              tint = TreeViewFileColor,
              modifier = Modifier.size(18.dp),
            )
          }

          DropdownMenu(
            expanded = showViewModeMenu,
            onDismissRequest = { showViewModeMenu = false },
          ) {
            TreeViewMode.entries.forEach { mode ->
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = mode.icon,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp),
                      tint = if (viewState.currentMode == mode) AccentColor else TreeViewTextColor,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = mode.displayName,
                      color = if (viewState.currentMode == mode) AccentColor else TreeViewTextColor,
                    )
                  }
                },
                onClick = {
                  onViewModeChange(mode)
                  showViewModeMenu = false
                },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(TreeViewHeaderBackground)
      .padding(horizontal = 12.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    OutlinedTextField(
      value = query,
      onValueChange = onQueryChange,
      placeholder = { Text("Search files...", color = TreeViewTextColor.copy(alpha = 0.5f)) },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AccentColor,
        unfocusedBorderColor = TreeViewChevronColor,
        focusedTextColor = TreeViewTextColor,
        unfocusedTextColor = TreeViewTextColor,
        cursorColor = AccentColor,
      ),
      modifier = Modifier.weight(1f),
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = null,
          tint = TreeViewChevronColor,
          modifier = Modifier.size(18.dp),
        )
      },
      trailingIcon = {
        IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = TreeViewChevronColor,
            modifier = Modifier.size(18.dp),
          )
        }
      },
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EnhancedTreeNodeItem(
  node: FileTreeNode,
  level: Int,
  selectedPath: String?,
  expandedPaths: Set<String>,
  onNodeClick: (FileTreeNode) -> Unit,
  onNodeExpand: (FileTreeNode) -> Unit,
  onNodeLongClick: (FileTreeNode) -> Unit,
) {
  val isExpanded = expandedPaths.contains(node.path)
  val isSelected = node.path == selectedPath

  val rotationAngle by animateFloatAsState(
    targetValue = if (isExpanded) 90f else 0f,
    label = "chevron_rotation",
  )

  Column {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .combinedClickable(
          onClick = {
            if (node.isDirectory) {
              onNodeExpand(node)
            }
            onNodeClick(node)
          },
          onLongClick = { onNodeLongClick(node) },
        ),
      color = when {
        isSelected -> TreeViewSelectedBackground
        else -> Color.Transparent
      },
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = (level * 16 + 8).dp, top = 6.dp, bottom = 6.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (node.isDirectory) {
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            modifier = Modifier.size(16.dp).rotate(rotationAngle),
            tint = TreeViewChevronColor,
          )
        } else {
          Spacer(modifier = Modifier.width(16.dp))
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
          imageVector = getNodeIcon(node),
          contentDescription = if (node.isDirectory) "Folder" else "File",
          modifier = Modifier.size(18.dp),
          tint = getNodeIconColor(node),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
          text = node.name,
          style = MaterialTheme.typography.bodySmall,
          color = TreeViewTextColor,
        )
      }
    }

    if (node.isDirectory) {
      AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically(),
      ) {
        Column {
          node.children.forEach { child ->
            EnhancedTreeNodeItem(
              node = child,
              level = level + 1,
              selectedPath = selectedPath,
              expandedPaths = expandedPaths,
              onNodeClick = onNodeClick,
              onNodeExpand = onNodeExpand,
              onNodeLongClick = onNodeLongClick,
            )
          }
        }
      }
    }
  }
}

private fun getNodeIcon(node: FileTreeNode): ImageVector {
  return when {
    node.isDirectory -> Icons.Default.Folder
    else -> Icons.Default.Description
  }
}

private fun getNodeIconColor(node: FileTreeNode): Color {
  return when {
    node.isDirectory -> TreeViewFolderColor
    node.name.endsWith(".kt") -> Color(0xFF7F52FF)
    node.name.endsWith(".java") -> Color(0xFFE76F00)
    node.name.endsWith(".xml") -> Color(0xFFE44D26)
    node.name.endsWith(".gradle") || node.name.endsWith(".gradle.kts") -> Color(0xFF02303A)
    node.name.endsWith(".json") -> Color(0xFFCFB94D)
    node.name.endsWith(".md") -> Color(0xFF519ABA)
    node.name.endsWith(".properties") -> Color(0xFF8B8B8B)
    node.name.endsWith(".yaml") || node.name.endsWith(".yml") -> Color(0xFFCB171E)
    node.name.endsWith(".toml") -> Color(0xFF9C4121)
    node.name.startsWith(".") -> Color(0xFF888888)
    else -> TreeViewFileColor
  }
}

private fun buildFileTree(
  root: File,
  includeHidden: Boolean,
  viewMode: TreeViewMode,
): List<FileTreeNode> {
  if (!root.exists() || !root.isDirectory) return emptyList()

  return when (viewMode) {
    TreeViewMode.FILE_VIEW -> buildFileViewTree(root, includeHidden, 0)
    TreeViewMode.PACKAGE_VIEW -> buildPackageViewTree(root, includeHidden)
    TreeViewMode.MODULE_VIEW -> buildModuleViewTree(root, includeHidden)
    TreeViewMode.PROJECT_VIEW -> buildProjectViewTree(root, includeHidden)
  }
}

private fun buildFileViewTree(dir: File, includeHidden: Boolean, level: Int): List<FileTreeNode> {
  val files = dir.listFiles() ?: return emptyList()

  return files
    .filter { includeHidden || !it.name.startsWith(".") }
    .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    .map { file ->
      FileTreeNode(
        file = file,
        name = file.name,
        path = file.absolutePath,
        isDirectory = file.isDirectory,
        level = level,
        children = if (file.isDirectory) buildFileViewTree(file, includeHidden, level + 1) else emptyList(),
      )
    }
}

private fun buildPackageViewTree(root: File, includeHidden: Boolean): List<FileTreeNode> {
  val srcDirs = listOf("src/main/kotlin", "src/main/java", "src/test/kotlin", "src/test/java")
  val packageNodes = mutableListOf<FileTreeNode>()

  srcDirs.forEach { srcDir ->
    val srcPath = File(root, srcDir)
    if (srcPath.exists()) {
      collectPackages(srcPath, srcPath, includeHidden)?.let { packageNodes.add(it) }
    }
  }

  if (packageNodes.isEmpty()) {
    return buildFileViewTree(root, includeHidden, 0)
  }

  return packageNodes
}

private fun collectPackages(root: File, currentDir: File, includeHidden: Boolean): FileTreeNode? {
  if (!currentDir.exists() || !currentDir.isDirectory) return null

  val files = currentDir.listFiles() ?: return null
  val children = mutableListOf<FileTreeNode>()

  files
    .filter { includeHidden || !it.name.startsWith(".") }
    .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    .forEach { file ->
      if (file.isDirectory) {
        collectPackages(root, file, includeHidden)?.let { children.add(it) }
      } else {
        children.add(
          FileTreeNode(
            file = file,
            name = file.name,
            path = file.absolutePath,
            isDirectory = false,
          )
        )
      }
    }

  val packageName = currentDir.relativeTo(root).path.replace(File.separator, ".")

  return FileTreeNode(
    file = currentDir,
    name = if (packageName.isBlank()) root.name else packageName,
    path = currentDir.absolutePath,
    isDirectory = true,
    children = children,
  )
}

private fun buildModuleViewTree(root: File, includeHidden: Boolean): List<FileTreeNode> {
  val modules = mutableListOf<FileTreeNode>()

  fun findModules(dir: File) {
    val buildGradle = File(dir, "build.gradle.kts")
    val buildGradleGroovy = File(dir, "build.gradle")

    if (buildGradle.exists() || buildGradleGroovy.exists()) {
      modules.add(
        FileTreeNode(
          file = dir,
          name = dir.name,
          path = dir.absolutePath,
          isDirectory = true,
          children = buildFileViewTree(dir, includeHidden, 0),
        )
      )
    } else {
      dir.listFiles()
        ?.filter { it.isDirectory && (includeHidden || !it.name.startsWith(".")) }
        ?.forEach { findModules(it) }
    }
  }

  findModules(root)

  if (modules.isEmpty()) {
    return buildFileViewTree(root, includeHidden, 0)
  }

  return modules.sortedBy { it.name.lowercase() }
}

private fun buildProjectViewTree(root: File, includeHidden: Boolean): List<FileTreeNode> {
  val projectStructure = mutableListOf<FileTreeNode>()
  val appDir = File(root, "app")
  val coreDir = File(root, "core")
  val featureDir = File(root, "feature")

  if (appDir.exists()) {
    projectStructure.add(
      FileTreeNode(
        file = appDir,
        name = "app",
        path = appDir.absolutePath,
        isDirectory = true,
        children = buildFileViewTree(appDir, includeHidden, 0),
      )
    )
  }

  if (coreDir.exists()) {
    projectStructure.add(
      FileTreeNode(
        file = coreDir,
        name = "core",
        path = coreDir.absolutePath,
        isDirectory = true,
        children = buildFileViewTree(coreDir, includeHidden, 0),
      )
    )
  }

  if (featureDir.exists()) {
    projectStructure.add(
      FileTreeNode(
        file = featureDir,
        name = "feature",
        path = featureDir.absolutePath,
        isDirectory = true,
        children = buildFileViewTree(featureDir, includeHidden, 0),
      )
    )
  }

  val otherFiles = root.listFiles()
    ?.filter { file ->
      (includeHidden || !file.name.startsWith(".")) &&
        file.name != "app" && file.name != "core" && file.name != "feature" &&
        file.name != "build" && file.name != ".gradle" && file.name != ".idea"
    }
    ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    ?.map { file ->
      FileTreeNode(
        file = file,
        name = file.name,
        path = file.absolutePath,
        isDirectory = file.isDirectory,
        children = if (file.isDirectory) buildFileViewTree(file, includeHidden, 0) else emptyList(),
      )
    } ?: emptyList()

  projectStructure.addAll(otherFiles)

  if (projectStructure.isEmpty()) {
    return buildFileViewTree(root, includeHidden, 0)
  }

  return projectStructure
}

private fun filterNodes(nodes: List<FileTreeNode>, query: String): List<FileTreeNode> {
  val lowerQuery = query.lowercase()
  return nodes.mapNotNull { node ->
    val matchesQuery = node.name.lowercase().contains(lowerQuery)
    val filteredChildren = filterNodes(node.children, query)

    when {
      matchesQuery -> node
      filteredChildren.isNotEmpty() -> node.copy(children = filteredChildren)
      else -> null
    }
  }
}
