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
                    currentContent = uiState.currentContent,
                    isBottomSheetExpanded = uiState.isBottomSheetExpanded,
                    bottomSheetContent = uiState.bottomSheetContent,
                    onNavigate = { viewModel.onNavigate(it) },
                    onBackToHome = { viewModel.onCloseProject() },
                    onContentTypeChanged = { viewModel.onContentTypeChanged(it) },
                    onBottomSheetToggle = { viewModel.onBottomSheetToggle() },
                    onBottomSheetContentChanged = { viewModel.onBottomSheetContentChanged(it) }
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
                BuildVariantsScreen(
                    onBackClick = { viewModel.onBackPressed() }
                )
            }
            
            MainDestination.SubModuleMaker -> {
                SubModuleMakerScreen(
                    onBackClick = { viewModel.onBackPressed() },
                    onCreateModule = { name, language ->
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
