/*
package com.scto.codelikebastimove.feature.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import com.scto.codelikebastimove.feature.main.navigation.MainDestination
import com.scto.codelikebastimove.feature.main.screens.AIAgentScreen
import com.scto.codelikebastimove.feature.main.screens.AssetStudioScreen
import com.scto.codelikebastimove.feature.main.screens.BuildVariantsScreen
import com.scto.codelikebastimove.feature.main.screens.CloneRepositoryScreen
import com.scto.codelikebastimove.feature.main.screens.ConsoleScreen
import com.scto.codelikebastimove.feature.main.screens.CreateProjectScreen
import com.scto.codelikebastimove.feature.main.screens.HomeScreen
import com.scto.codelikebastimove.feature.main.screens.IDESettingsScreen
import com.scto.codelikebastimove.feature.main.screens.IDEWorkspaceScreen
import com.scto.codelikebastimove.feature.main.screens.OpenProjectScreen
import com.scto.codelikebastimove.feature.main.screens.SubModuleMakerScreen
import com.scto.codelikebastimove.feature.designer.ui.screen.DesignerScreen
import com.scto.codelikebastimove.feature.main.assetstudio.VectorAssetStudioScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    onExitApp: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val projectRootPath by viewModel.currentProjectPath.collectAsState()
    
    BackHandler(enabled = uiState.currentDestination != MainDestination.Home) {
        if (!viewModel.onBackPressed()) {
            onExitApp()
        }
    }
    
    AnimatedContent(
        targetState = uiState.currentDestination,
        transitionSpec = {
            if (targetState == MainDestination.Home) {
                (fadeIn() + slideInHorizontally { -it / 4 })
                    .togetherWith(fadeOut() + slideOutHorizontally { it / 4 })
            } else {
                (fadeIn() + slideInHorizontally { it / 4 })
                    .togetherWith(fadeOut() + slideOutHorizontally { -it / 4 })
            }
        },
        modifier = modifier,
        label = "main_navigation"
    ) { destination ->
        when (destination) {
            MainDestination.Home -> {
                HomeScreen(
                    onNavigate = { viewModel.onNavigate(it) },
                    onCreateProject = { 
                        viewModel.onNavigate(MainDestination.CreateProject)
                    },
                    onOpenProject = { 
                        viewModel.onNavigate(MainDestination.OpenProject)
                    },
                    onCloneRepository = { 
                        viewModel.onNavigate(MainDestination.CloneRepository)
                    }
                )
            }
            
            MainDestination.CreateProject -> {
                CreateProjectScreen(
                    onBackClick = { viewModel.onBackPressed() },
                    onCreateProject = { name, packageName, templateType, minSdk, useKotlin, useKotlinDsl ->
                        viewModel.createProject(name, packageName, templateType, minSdk, useKotlin, useKotlinDsl)
                    }
                )
            }
            
            MainDestination.OpenProject -> {
                OpenProjectScreen(
                    projects = uiState.projects,
                    onBackClick = { viewModel.onBackPressed() },
                    onProjectSelected = { project ->
                        viewModel.onOpenProject(project.path, project.name)
                    },
                    onProjectDelete = { project ->
                        viewModel.deleteProject(project.path)
                    },
                    onBrowseFolder = { }
                )
            }
            
            MainDestination.CloneRepository -> {
                CloneRepositoryScreen(
                    onBackClick = { viewModel.onBackPressed() },
                    onClone = { url, branch, shallowClone, singleBranch ->
                        viewModel.cloneRepository(url, branch, shallowClone, singleBranch)
                    },
                    isLoading = uiState.isLoading,
                    cloneProgress = uiState.cloneProgress
                )
            }
            
            MainDestination.IDE -> {
                IDEWorkspaceScreen(
                    projectName = uiState.projectName,
                    projectPath = uiState.projectPath,
                    currentContent = uiState.currentContent,
                    isBottomSheetExpanded = uiState.isBottomSheetExpanded,
                    bottomSheetContent = uiState.bottomSheetContent,
                    fileSystemVersion = uiState.lastFileSystemUpdate,
                    onNavigate = { viewModel.onNavigate(it) },
                    onBackToHome = { viewModel.onCloseProject() },
                    onContentTypeChanged = { viewModel.onContentTypeChanged(it) },
                    onBottomSheetToggle = { viewModel.onBottomSheetToggle() },
                    onBottomSheetContentChanged = { viewModel.onBottomSheetContentChanged(it) },
                    viewModel = viewModel
                )
            }
            
            MainDestination.Settings -> {
                IDESettingsScreen(
                    onBackClick = { viewModel.onBackPressed() },
                    onNavigateToAIAgent = { viewModel.onNavigate(MainDestination.AIAgent) }
                )
            }
            
            MainDestination.AssetStudio -> {
                AssetStudioScreen(
                    onBackClick = { viewModel.onBackPressed() },
                    onLaunchStudio = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
                    onCreateDrawable = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
                    onCreateIcon = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
                    onImportImage = { viewModel.onNavigate(MainDestination.VectorAssetStudio) }
                )
            }
            
            MainDestination.AIAgent -> {
                AIAgentScreen(
                    onBackClick = { viewModel.onBackPressed() }
                )
            }
            
            MainDestination.BuildVariants -> {
                // Hier wurde der projectPath hinzugefügt
                BuildVariantsScreen(
                    projectPath = uiState.projectPath,
                    onBackClick = { viewModel.onBackPressed() }
                )
            }
            
            MainDestination.SubModuleMaker -> {
                SubModuleMakerScreen(
                    onBackClick = { viewModel.onBackPressed() },
                    onCreateModule = { modulePath, packageName, language, type ->
                        viewModel.createSubModule(modulePath, packageName, language, type)
                        viewModel.onNavigate(MainDestination.IDE)
                    }
                )
            }
            
            MainDestination.Console -> {
                ConsoleScreen(
                    onBackClick = { viewModel.onBackPressed() }
                )
            }
            
            MainDestination.Documentation -> {
                HomeScreen(
                    onNavigate = { viewModel.onNavigate(it) },
                    onCreateProject = { viewModel.onNavigate(MainDestination.CreateProject) },
                    onOpenProject = { viewModel.onNavigate(MainDestination.OpenProject) },
                    onCloneRepository = { viewModel.onNavigate(MainDestination.CloneRepository) }
                )
            }
            
            MainDestination.LayoutDesigner -> {
                DesignerScreen(
                    projectName = uiState.projectName,
                    onBackClick = { viewModel.onBackPressed() }
                )
            }
            
            MainDestination.VectorAssetStudio -> {
                VectorAssetStudioScreen(
                    onBackClick = { viewModel.onBackPressed() }
                )
            }
        }
    }
}
*/
package com.scto.codelikebastimove.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import com.scto.codelikebastimove.feature.main.components.ContentNavigationRail
import com.scto.codelikebastimove.feature.main.content.*
import com.scto.codelikebastimove.feature.main.screens.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            ContentNavigationRail(
                selectedContent = uiState.selectedContent,
                onContentSelected = viewModel::onContentSelected,
                onOpenSettings = { viewModel.onContentSelected(MainContentType.Settings) }
            )

            Column(modifier = Modifier.fillMaxSize()) {
                when (uiState.selectedContent) {
                    MainContentType.Project -> ProjectContent(
                        onOpenProject = { viewModel.onContentSelected(MainContentType.OpenProject) },
                        onCreateProject = { viewModel.onContentSelected(MainContentType.CreateProject) }
                    )
                    MainContentType.Editor -> EditorContent()
                    MainContentType.ThemeBuilder -> ThemeBuilderContent()
                    MainContentType.LayoutDesigner -> LayoutDesignerContent()
                    MainContentType.Git -> GitContent(
                        onCloneRepo = { viewModel.onContentSelected(MainContentType.CloneRepository) }
                    )
                    MainContentType.Assets -> AssetsStudioContent(
                        onOpenAssetStudio = { viewModel.onContentSelected(MainContentType.AssetStudio) }
                    )
                    MainContentType.OpenProject -> OpenProjectScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Project) },
                        onProjectOpened = { viewModel.onContentSelected(MainContentType.Project) }
                    )
                    MainContentType.CreateProject -> CreateProjectScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Project) },
                        onProjectCreated = { viewModel.onContentSelected(MainContentType.Project) }
                    )
                    MainContentType.CloneRepository -> CloneRepositoryScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Git) },
                        onRepositoryCloned = { viewModel.onContentSelected(MainContentType.Project) }
                    )
                    MainContentType.Settings -> IDESettingsScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Project) }
                    )
                    MainContentType.AssetStudio -> AssetStudioScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Assets) }
                    )
                    MainContentType.BuildVariants -> BuildVariantsScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Project) }
                    )
                    MainContentType.SubModuleMaker -> SubModuleMakerScreen(
                        onBackClick = { viewModel.onContentSelected(MainContentType.Project) },
                        // Wir übergeben leere Defaults, da der Screen sich selbst via ProjectManager versorgt.
                        // Die Parameter sind nur da, um die Signatur kompatibel zu halten, falls wir sie nicht entfernen wollen.
                        projectPath = "",
                        onCreateModule = { _, _, _, _ ->
                            // Optional: Navigiere zurück nach Erstellung
                            // viewModel.onContentSelected(MainContentType.Project)
                        }
                    )
                    MainContentType.AIAgent -> AIAgentScreen()
                    MainContentType.Console -> ConsoleScreen()
                    MainContentType.IDEWorkspace -> IDEWorkspaceScreen()
                }
            }
        }
    }
}