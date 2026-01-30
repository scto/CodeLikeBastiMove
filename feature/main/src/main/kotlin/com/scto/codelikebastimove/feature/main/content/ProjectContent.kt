package com.scto.codelikebastimove.feature.main.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.main.MainViewModel
import java.io.File

data class ProjectFileItem(
  val file: File?,
  val name: String,
  val isDirectory: Boolean,
  val path: String,
  val children: List<ProjectFileItem> = emptyList(),
  val level: Int = 0,
)

@Composable
fun ProjectContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {
  val projectRootPath by viewModel.currentProjectPath.collectAsState()
  val projectRoot =
    remember(projectRootPath) { if (projectRootPath != null) File(projectRootPath!!) else null }

  var selectedViewType by remember { mutableStateOf(ProjectViewType.ANDROID) }
  var isDropdownExpanded by remember { mutableStateOf(false) }

  val treeItems =
    remember(projectRoot, selectedViewType) {
      if (projectRoot == null || !projectRoot.exists()) {
        emptyList()
      } else {
        when (selectedViewType) {
          ProjectViewType.ANDROID -> createAndroidViewTree(projectRoot)
          ProjectViewType.PROJECT -> createProjectViewTree(projectRoot)
          ProjectViewType.PACKAGES -> createPackagesViewTree(projectRoot)
        }
      }
    }

  Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { isDropdownExpanded = true },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = selectedViewType.displayName,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.weight(1f),
      )
      Icon(Icons.Default.ArrowDropDown, contentDescription = "Ansicht ändern")

      DropdownMenu(
        expanded = isDropdownExpanded,
        onDismissRequest = { isDropdownExpanded = false },
      ) {
        ProjectViewType.entries.forEach { type ->
          DropdownMenuItem(
            text = { Text(type.displayName) },
            onClick = {
              selectedViewType = type
              isDropdownExpanded = false
            },
          )
        }
      }
    }

    HorizontalDivider()

    if (treeItems.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
          "Kein Projekt geöffnet",
          style = MaterialTheme.typography.bodyMedium,
          color = Color.Gray,
        )
      }
    } else {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(treeItems) { item ->
          ProjectTreeItemRow(
            item = item,
            onItemClick = { clickedItem ->
              if (!clickedItem.isDirectory) {
                viewModel.openFile(clickedItem.path)
              }
            },
          )
        }
      }
    }
  }
}

enum class ProjectViewType(val displayName: String) {
  ANDROID("Android"),
  PROJECT("Projekt"),
  PACKAGES("Pakete"),
}

@Composable
fun ProjectTreeItemRow(item: ProjectFileItem, onItemClick: (ProjectFileItem) -> Unit) {
  var isExpanded by remember { mutableStateOf(false) }

  Column {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .clickable {
            if (item.isDirectory) {
              isExpanded = !isExpanded
            } else {
              onItemClick(item)
            }
          }
          .padding(start = (8 + item.level * 16).dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (item.isDirectory) {
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      } else {
        Spacer(modifier = Modifier.width(16.dp))
      }

      Spacer(modifier = Modifier.width(4.dp))

      Icon(
        imageVector = getFileIcon(item),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = getFileIconColor(item),
      )

      Spacer(modifier = Modifier.width(8.dp))

      Text(text = item.name, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }

    AnimatedVisibility(
      visible = isExpanded,
      enter = expandVertically(),
      exit = shrinkVertically(),
    ) {
      Column {
        item.children.forEach { child ->
          ProjectTreeItemRow(item = child, onItemClick = onItemClick)
        }
      }
    }
  }
}

private fun getFileIcon(item: ProjectFileItem): ImageVector {
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
private fun getFileIconColor(item: ProjectFileItem): Color {
  return when {
    item.isDirectory -> MaterialTheme.colorScheme.primary
    item.name.endsWith(".kt") -> Color(0xFF7F52FF)
    item.name.endsWith(".java") -> Color(0xFFE76F00)
    item.name.endsWith(".xml") -> Color(0xFFE44D26)
    item.name.endsWith(".gradle.kts") -> Color(0xFF02303A)
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }
}

private fun createAndroidViewTree(root: File): List<ProjectFileItem> {
  val nodes = mutableListOf<ProjectFileItem>()
  val appDir = File(root, "app")

  val appChildren = mutableListOf<ProjectFileItem>()

  val manifestFile = File(appDir, "src/main/AndroidManifest.xml")
  if (manifestFile.exists()) {
    val manifestItem =
      ProjectFileItem(
        file = manifestFile.parentFile,
        name = "manifests",
        isDirectory = true,
        path = manifestFile.parent!!,
        level = 1,
        children =
          listOf(
            ProjectFileItem(
              file = manifestFile,
              name = "AndroidManifest.xml",
              isDirectory = false,
              path = manifestFile.path,
              level = 2,
            )
          ),
      )
    appChildren.add(manifestItem)
  }

  val javaSrc = File(appDir, "src/main/java")
  val kotlinSrc = File(appDir, "src/main/kotlin")
  val codeChildren = mutableListOf<ProjectFileItem>()

  if (javaSrc.exists()) {
    codeChildren.addAll(createPackagesViewTree(javaSrc, 2))
  }
  if (kotlinSrc.exists()) {
    codeChildren.addAll(createPackagesViewTree(kotlinSrc, 2))
  }

  if (codeChildren.isNotEmpty()) {
    appChildren.add(
      ProjectFileItem(
        file = null,
        name = "java",
        isDirectory = true,
        path = "",
        level = 1,
        children = codeChildren,
      )
    )
  }

  val resDir = File(appDir, "src/main/res")
  if (resDir.exists()) {
    val children = createProjectViewTree(resDir, 2)

    if (children.isNotEmpty()) {
      appChildren.add(
        ProjectFileItem(
          file = resDir,
          name = "res",
          isDirectory = true,
          path = resDir.path,
          level = 1,
          children = children,
        )
      )
    }
  }

  if (appChildren.isNotEmpty()) {
    nodes.add(
      ProjectFileItem(
        file = appDir,
        name = "app",
        isDirectory = true,
        path = appDir.path,
        level = 0,
        children = appChildren,
      )
    )
  }

  val gradleFiles =
    root
      .listFiles { file ->
        file.name.endsWith(".gradle") ||
          file.name.endsWith(".gradle.kts") ||
          file.name == "gradle.properties" ||
          file.name == "libs.versions.toml"
      }
      ?.sortedBy { it.name }

  if (gradleFiles != null && gradleFiles.isNotEmpty()) {
    val scriptNodes =
      gradleFiles
        .map { file ->
          ProjectFileItem(
            file = file,
            name = file.name,
            isDirectory = false,
            path = file.path,
            level = 1,
          )
        }
        .toMutableList()

    val appBuildGradle = File(appDir, "build.gradle.kts")
    if (appBuildGradle.exists()) {
      scriptNodes.add(
        0,
        ProjectFileItem(
          file = appBuildGradle,
          name = "build.gradle.kts (Module: :app)",
          isDirectory = false,
          path = appBuildGradle.path,
          level = 1,
        ),
      )
    }

    nodes.add(
      ProjectFileItem(
        file = null,
        name = "Gradle Scripts",
        isDirectory = true,
        path = "",
        level = 0,
        children = scriptNodes,
      )
    )
  }

  return nodes
}

private fun createProjectViewTree(root: File, startLevel: Int = 0): List<ProjectFileItem> {
  if (!root.exists() || !root.isDirectory) return emptyList()

  val files = root.listFiles() ?: return emptyList()
  val sortedFiles = files.sortedWith(compareBy({ !it.isDirectory }, { it.name }))

  return sortedFiles.map { file ->
    val children =
      if (file.isDirectory) {
        createProjectViewTree(file, startLevel + 1)
      } else {
        emptyList()
      }

    ProjectFileItem(
      file = file,
      name = file.name,
      isDirectory = file.isDirectory,
      path = file.path,
      level = startLevel,
      children = children,
    )
  }
}

private fun createPackagesViewTree(root: File, startLevel: Int = 0): List<ProjectFileItem> {
  if (!root.exists() || !root.isDirectory) return emptyList()

  val files = root.listFiles() ?: return emptyList()
  val sortedFiles = files.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
  val nodes = mutableListOf<ProjectFileItem>()

  for (file in sortedFiles) {
    if (file.isDirectory) {
      val flattenedNode = tryFlattenPackage(file, startLevel)
      nodes.add(flattenedNode)
    } else {
      nodes.add(
        ProjectFileItem(
          file = file,
          name = file.name,
          isDirectory = false,
          path = file.path,
          level = startLevel,
        )
      )
    }
  }
  return nodes
}

private fun tryFlattenPackage(dir: File, level: Int): ProjectFileItem {
  val children = dir.listFiles()

  if (children != null && children.size == 1 && children[0].isDirectory) {
    val childDir = children[0]
    val childNode = tryFlattenPackage(childDir, level)

    return childNode.copy(
      name = "${dir.name}.${childNode.name}",
      file = childNode.file,
      level = level,
    )
  } else {
    val normalChildren = createPackagesViewTree(dir, level + 1)
    return ProjectFileItem(
      file = dir,
      name = dir.name,
      isDirectory = true,
      path = dir.path,
      level = level,
      children = normalChildren,
    )
  }
}
