package com.scto.codelikebastimove.feature.main

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
import com.scto.codelikebastimove.feature.main.screens.ConsoleScreen
import com.scto.codelikebastimove.feature.main.screens.HomeScreen
import com.scto.codelikebastimove.feature.main.screens.IDESettingsScreen
import com.scto.codelikebastimove.feature.main.screens.IDEWorkspaceScreen
import com.scto.codelikebastimove.feature.main.screens.OpenProjectScreen
import com.scto.codelikebastimove.feature.main.screens.SubModuleMakerScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
                    onCreateProject = { },
                    onOpenProject = { 
                        viewModel.onNavigate(MainDestination.OpenProject)
                    },
                    onCloneRepository = { }
                )
            }
            
            MainDestination.OpenProject -> {
                OpenProjectScreen(
                    onBackClick = { viewModel.onNavigate(MainDestination.Home) },
                    onProjectSelected = { project ->
                        viewModel.onOpenProject(project.name)
                    },
                    onBrowseFolder = { }
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
                    onBackClick = { 
                        if (uiState.isProjectOpen) {
                            viewModel.onNavigate(MainDestination.IDE)
                        } else {
                            viewModel.onNavigate(MainDestination.Home)
                        }
                    },
                    onNavigateToAIAgent = { viewModel.onNavigate(MainDestination.AIAgent) }
                )
            }
            
            MainDestination.AssetStudio -> {
                AssetStudioScreen(
                    onBackClick = { 
                        if (uiState.isProjectOpen) {
                            viewModel.onNavigate(MainDestination.IDE)
                        } else {
                            viewModel.onNavigate(MainDestination.Home)
                        }
                    },
                    onLaunchStudio = { },
                    onCreateDrawable = { },
                    onCreateIcon = { },
                    onImportImage = { }
                )
            }
            
            MainDestination.AIAgent -> {
                AIAgentScreen(
                    onBackClick = { 
                        viewModel.onNavigate(MainDestination.Settings)
                    }
                )
            }
            
            MainDestination.BuildVariants -> {
                BuildVariantsScreen(
                    onBackClick = { 
                        viewModel.onNavigate(MainDestination.IDE)
                    }
                )
            }
            
            MainDestination.SubModuleMaker -> {
                SubModuleMakerScreen(
                    onBackClick = { 
                        viewModel.onNavigate(MainDestination.IDE)
                    },
                    onCreateModule = { name, language ->
                        viewModel.onNavigate(MainDestination.IDE)
                    }
                )
            }
            
            MainDestination.Console -> {
                ConsoleScreen(
                    onBackClick = { viewModel.onNavigate(MainDestination.Home) }
                )
            }
            
            MainDestination.Documentation -> {
                HomeScreen(
                    onNavigate = { viewModel.onNavigate(it) },
                    onCreateProject = { },
                    onOpenProject = { viewModel.onOpenProject("New Project") },
                    onCloneRepository = { }
                )
            }
        }
    }
}
