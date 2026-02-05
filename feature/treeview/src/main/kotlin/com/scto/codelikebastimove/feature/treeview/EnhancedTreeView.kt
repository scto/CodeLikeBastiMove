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
  onEditorSettings: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  var showViewModeMenu by remember { mutableStateOf(false) }
  var showOptionsMenu by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth().background(TreeViewHeaderBackground)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Folder,
          contentDescription = null,
          tint = TreeViewFolderColor,
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
          IconButton(onClick = { showOptionsMenu = true }, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = TreeViewFileColor,
              modifier = Modifier.size(18.dp),
            )
          }

          DropdownMenu(
            expanded = showOptionsMenu,
            onDismissRequest = { showOptionsMenu = false },
          ) {
            DropdownMenuItem(
              text = { Text("Editor Settings", color = TreeViewTextColor) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  tint = TreeViewTextColor,
                  modifier = Modifier.size(18.dp),
                )
              },
              onClick = {
                onEditorSettings()
                showOptionsMenu = false
              },
            )
          }
        }
      }
    }

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
      Surface(
        onClick = { showViewModeMenu = true },
        color = Color(0xFF333333),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
      ) {
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
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = viewState.currentMode.displayName,
              style = MaterialTheme.typography.bodyMedium,
              color = TreeViewTextColor,
            )
          }
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TreeViewChevronColor,
            modifier = Modifier.size(16.dp).rotate(90f),
          )
        }
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
    TreeViewMode.ANDROID_VIEW -> buildAndroidViewTree(root, includeHidden)
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

private fun buildAndroidViewTree(root: File, includeHidden: Boolean): List<FileTreeNode> {
  val androidStructure = mutableListOf<FileTreeNode>()
  val discoveredModules = mutableListOf<Pair<String, File>>()
  
  val settingsKts = File(root, "settings.gradle.kts")
  val settingsGroovy = File(root, "settings.gradle")
  val settingsFile = when {
    settingsKts.exists() -> settingsKts
    settingsGroovy.exists() -> settingsGroovy
    else -> null
  }
  
  if (settingsFile != null) {
    val content = settingsFile.readText()
    val modulePaths = parseIncludeStatements(content)
    
    modulePaths.forEach { gradlePath ->
      val relativePath = gradlePath.trimStart(':').replace(':', File.separatorChar)
      val moduleDir = File(root, relativePath)
      if (moduleDir.exists() && hasGradleBuildFile(moduleDir)) {
        discoveredModules.add(gradlePath to moduleDir)
      }
    }
  }
  
  if (discoveredModules.isEmpty()) {
    val appDir = File(root, "app")
    if (appDir.exists() && hasGradleBuildFile(appDir)) {
      discoveredModules.add(":app" to appDir)
    }
  }
  
  discoveredModules
    .sortedWith(compareBy({ !it.first.equals(":app", ignoreCase = true) }, { it.first }))
    .forEach { (gradlePath, moduleDir) ->
      val displayName = gradlePath.trimStart(':')
      androidStructure.add(buildAndroidModuleNode(moduleDir, displayName, includeHidden))
    }
  
  val gradleScriptsNode = buildGradleScriptsNode(root, discoveredModules)
  if (gradleScriptsNode.children.isNotEmpty()) {
    androidStructure.add(gradleScriptsNode)
  }
  
  if (androidStructure.isEmpty()) {
    return buildFileViewTree(root, includeHidden, 0)
  }
  
  return androidStructure
}

private fun parseIncludeStatements(content: String): List<String> {
  val modules = mutableListOf<String>()
  val lines = content.lines()
  var i = 0
  
  while (i < lines.size) {
    var line = lines[i].trim()
    
    if (line.startsWith("//") || line.startsWith("/*")) {
      i++
      continue
    }
    
    if (line.contains("includeBuild")) {
      i++
      continue
    }
    
    if (line.contains("include")) {
      val fullStatement = StringBuilder(line)
      while (!fullStatement.contains(")") && !fullStatement.endsWith("\"") && 
             !fullStatement.endsWith("'") && i + 1 < lines.size) {
        i++
        fullStatement.append(" ").append(lines[i].trim())
      }
      
      val statement = fullStatement.toString()
      val stringPattern = Regex("""["']([^"']+)["']""")
      stringPattern.findAll(statement).forEach { match ->
        val value = match.groupValues[1]
        if (value.startsWith(":") || value.contains(":")) {
          val modulePath = if (value.startsWith(":")) value else ":$value"
          if (!modules.contains(modulePath)) {
            modules.add(modulePath)
          }
        }
      }
    }
    i++
  }
  
  return modules.distinct()
}

private fun hasGradleBuildFile(dir: File): Boolean {
  return File(dir, "build.gradle.kts").exists() || File(dir, "build.gradle").exists()
}

private fun buildAndroidModuleNode(moduleDir: File, moduleName: String, includeHidden: Boolean): FileTreeNode {
  val children = mutableListOf<FileTreeNode>()
  
  val manifestNodes = mutableListOf<FileTreeNode>()
  val mainManifest = File(moduleDir, "src/main/AndroidManifest.xml")
  val debugManifest = File(moduleDir, "src/debug/AndroidManifest.xml")
  val releaseManifest = File(moduleDir, "src/release/AndroidManifest.xml")
  
  listOf(mainManifest to "AndroidManifest.xml", debugManifest to "AndroidManifest.xml (debug)", 
         releaseManifest to "AndroidManifest.xml (release)").forEach { (file, name) ->
    if (file.exists()) {
      manifestNodes.add(FileTreeNode(file = file, name = name, path = file.absolutePath, isDirectory = false))
    }
  }
  
  if (manifestNodes.isNotEmpty()) {
    children.add(FileTreeNode(
      file = null,
      name = "manifests",
      path = "${moduleDir.absolutePath}/__manifests__",
      isDirectory = true,
      children = manifestNodes,
    ))
  }
  
  val sourceNodes = mutableListOf<FileTreeNode>()
  
  val mainKotlin = File(moduleDir, "src/main/kotlin")
  val mainJava = File(moduleDir, "src/main/java")
  val mainSrc = when {
    mainKotlin.exists() -> mainKotlin
    mainJava.exists() -> mainJava
    else -> null
  }
  if (mainSrc != null) {
    sourceNodes.addAll(buildPackageNodes(mainSrc, includeHidden))
  }
  
  if (sourceNodes.isNotEmpty()) {
    children.add(FileTreeNode(
      file = null,
      name = if (mainKotlin.exists()) "kotlin" else "java",
      path = "${moduleDir.absolutePath}/__source__",
      isDirectory = true,
      children = sourceNodes,
    ))
  }
  
  val testNodes = mutableListOf<FileTreeNode>()
  val testKotlin = File(moduleDir, "src/test/kotlin")
  val testJava = File(moduleDir, "src/test/java")
  val testSrc = when {
    testKotlin.exists() -> testKotlin
    testJava.exists() -> testJava
    else -> null
  }
  if (testSrc != null) {
    testNodes.addAll(buildPackageNodes(testSrc, includeHidden))
  }
  if (testNodes.isNotEmpty()) {
    children.add(FileTreeNode(
      file = null,
      name = if (testKotlin.exists()) "kotlin (test)" else "java (test)",
      path = "${moduleDir.absolutePath}/__test_source__",
      isDirectory = true,
      children = testNodes,
    ))
  }
  
  val androidTestNodes = mutableListOf<FileTreeNode>()
  val androidTestKotlin = File(moduleDir, "src/androidTest/kotlin")
  val androidTestJava = File(moduleDir, "src/androidTest/java")
  val androidTestSrc = when {
    androidTestKotlin.exists() -> androidTestKotlin
    androidTestJava.exists() -> androidTestJava
    else -> null
  }
  if (androidTestSrc != null) {
    androidTestNodes.addAll(buildPackageNodes(androidTestSrc, includeHidden))
  }
  if (androidTestNodes.isNotEmpty()) {
    children.add(FileTreeNode(
      file = null,
      name = if (androidTestKotlin.exists()) "kotlin (androidTest)" else "java (androidTest)",
      path = "${moduleDir.absolutePath}/__androidtest_source__",
      isDirectory = true,
      children = androidTestNodes,
    ))
  }
  
  val resDir = File(moduleDir, "src/main/res")
  if (resDir.exists()) {
    children.add(FileTreeNode(
      file = resDir,
      name = "res",
      path = resDir.absolutePath,
      isDirectory = true,
      children = buildResViewTree(resDir, includeHidden),
    ))
  }
  
  val assetsDir = File(moduleDir, "src/main/assets")
  if (assetsDir.exists()) {
    children.add(FileTreeNode(
      file = assetsDir,
      name = "assets",
      path = assetsDir.absolutePath,
      isDirectory = true,
      children = buildFileViewTree(assetsDir, includeHidden, 0),
    ))
  }
  
  return FileTreeNode(
    file = moduleDir,
    name = moduleName,
    path = moduleDir.absolutePath,
    isDirectory = true,
    children = children,
  )
}

private fun buildPackageNodes(srcDir: File, includeHidden: Boolean): List<FileTreeNode> {
  val packages = mutableListOf<FileTreeNode>()
  
  fun collectPackageFiles(dir: File, packagePath: String) {
    val files = dir.listFiles() ?: return
    val sourceFiles = files.filter { !it.isDirectory && (includeHidden || !it.name.startsWith(".")) }
    val subdirs = files.filter { it.isDirectory && (includeHidden || !it.name.startsWith(".")) }
    
    if (sourceFiles.isNotEmpty()) {
      val displayName = if (packagePath.isBlank()) "(default package)" else packagePath
      val fileNodes = sourceFiles.sortedBy { it.name.lowercase() }.map { file ->
        FileTreeNode(
          file = file,
          name = file.name,
          path = file.absolutePath,
          isDirectory = false,
        )
      }
      packages.add(
        FileTreeNode(
          file = dir,
          name = displayName,
          path = dir.absolutePath,
          isDirectory = true,
          children = fileNodes,
        )
      )
    }
    
    subdirs.sortedBy { it.name.lowercase() }.forEach { subdir ->
      val newPackagePath = if (packagePath.isBlank()) subdir.name else "$packagePath.${subdir.name}"
      collectPackageFiles(subdir, newPackagePath)
    }
  }
  
  collectPackageFiles(srcDir, "")
  return packages
}

private fun buildResViewTree(resDir: File, includeHidden: Boolean): List<FileTreeNode> {
  val files = resDir.listFiles() ?: return emptyList()
  
  return files
    .filter { (includeHidden || !it.name.startsWith(".")) }
    .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    .map { file ->
      FileTreeNode(
        file = file,
        name = file.name,
        path = file.absolutePath,
        isDirectory = file.isDirectory,
        children = if (file.isDirectory) {
          file.listFiles()
            ?.filter { includeHidden || !it.name.startsWith(".") }
            ?.sortedBy { it.name.lowercase() }
            ?.map { child ->
              FileTreeNode(
                file = child,
                name = child.name,
                path = child.absolutePath,
                isDirectory = child.isDirectory,
              )
            } ?: emptyList()
        } else emptyList(),
      )
    }
}

private fun buildGradleScriptsNode(root: File, discoveredModules: List<Pair<String, File>>): FileTreeNode {
  val gradleFiles = mutableListOf<FileTreeNode>()
  
  val buildGradle = File(root, "build.gradle.kts")
  val buildGradleGroovy = File(root, "build.gradle")
  val settingsGradle = File(root, "settings.gradle.kts")
  val settingsGradleGroovy = File(root, "settings.gradle")
  val gradleProperties = File(root, "gradle.properties")
  val localProperties = File(root, "local.properties")
  val gradleWrapper = File(root, "gradle/wrapper/gradle-wrapper.properties")
  val versionsCatalog = File(root, "gradle/libs.versions.toml")
  
  listOf(
    buildGradle to "build.gradle.kts (Project)",
    buildGradleGroovy to "build.gradle (Project)",
    settingsGradle to "settings.gradle.kts",
    settingsGradleGroovy to "settings.gradle",
    gradleProperties to "gradle.properties (Project)",
    localProperties to "local.properties",
    gradleWrapper to "gradle-wrapper.properties",
    versionsCatalog to "libs.versions.toml",
  ).forEach { (file, displayName) ->
    if (file.exists()) {
      gradleFiles.add(
        FileTreeNode(
          file = file,
          name = displayName,
          path = file.absolutePath,
          isDirectory = false,
        )
      )
    }
  }
  
  discoveredModules
    .sortedBy { it.first }
    .forEach { (gradlePath, moduleDir) ->
      val moduleBuildGradleKts = File(moduleDir, "build.gradle.kts")
      val moduleBuildGradle = File(moduleDir, "build.gradle")
      val moduleProguard = File(moduleDir, "proguard-rules.pro")
      
      if (moduleBuildGradleKts.exists()) {
        gradleFiles.add(FileTreeNode(
          file = moduleBuildGradleKts,
          name = "build.gradle.kts ($gradlePath)",
          path = moduleBuildGradleKts.absolutePath,
          isDirectory = false,
        ))
      } else if (moduleBuildGradle.exists()) {
        gradleFiles.add(FileTreeNode(
          file = moduleBuildGradle,
          name = "build.gradle ($gradlePath)",
          path = moduleBuildGradle.absolutePath,
          isDirectory = false,
        ))
      }
      
      if (moduleProguard.exists()) {
        gradleFiles.add(FileTreeNode(
          file = moduleProguard,
          name = "proguard-rules.pro ($gradlePath)",
          path = moduleProguard.absolutePath,
          isDirectory = false,
        ))
      }
    }
  
  return FileTreeNode(
    file = null,
    name = "Gradle Scripts",
    path = "${root.absolutePath}/__gradle_scripts__",
    isDirectory = true,
    children = gradleFiles,
  )
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
