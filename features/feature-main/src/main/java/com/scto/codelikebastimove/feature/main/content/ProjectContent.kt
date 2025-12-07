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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

data class FileTreeItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: List<FileTreeItem> = emptyList(),
    val level: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectContent(
    modifier: Modifier = Modifier,
    onFileClick: (FileTreeItem) -> Unit = {}
) {
    var currentViewMode by remember { mutableStateOf(ProjectViewMode.ANDROID) }
    var showViewModeMenu by remember { mutableStateOf(false) }
    
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
                    modifier = Modifier.clickable { showViewModeMenu = true }
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
                    onItemClick = { onFileClick(it) }
                )
            }
        }
    }
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
