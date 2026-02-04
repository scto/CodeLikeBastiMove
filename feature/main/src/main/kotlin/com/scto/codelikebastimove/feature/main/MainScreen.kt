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
import com.scto.codelikebastimove.feature.assetstudio.screen.AssetStudioScreen
import com.scto.codelikebastimove.feature.assetstudio.screen.VectorAssetStudioScreen
import com.scto.codelikebastimove.feature.designer.ui.screen.DesignerScreen
import com.scto.codelikebastimove.feature.git.ui.screens.GitCloneScreen
import com.scto.codelikebastimove.feature.git.ui.screens.GitScreen
import com.scto.codelikebastimove.feature.main.navigation.MainDestination
import com.scto.codelikebastimove.feature.main.screens.AIAgentScreen
import com.scto.codelikebastimove.feature.main.screens.BuildVariantsScreen
import com.scto.codelikebastimove.feature.main.screens.ConsoleScreen
import com.scto.codelikebastimove.feature.home.navigation.HomeDestination
import com.scto.codelikebastimove.feature.home.screens.CreateProjectScreen
import com.scto.codelikebastimove.feature.home.screens.HomeScreen
import com.scto.codelikebastimove.feature.home.screens.ImportProjectScreen
import com.scto.codelikebastimove.feature.settings.EditorSettingsScreen
import com.scto.codelikebastimove.feature.settings.IDESettingsScreen
import com.scto.codelikebastimove.feature.main.screens.IDEWorkspaceScreen
import com.scto.codelikebastimove.feature.home.screens.OpenProjectScreen
import com.scto.codelikebastimove.feature.submodulemaker.SubModuleMakerScreen

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainViewModel = viewModel(),
  onExitApp: () -> Unit = {},
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
        (fadeIn() + slideInHorizontally { -it / 4 }).togetherWith(
          fadeOut() + slideOutHorizontally { it / 4 }
        )
      } else {
        (fadeIn() + slideInHorizontally { it / 4 }).togetherWith(
          fadeOut() + slideOutHorizontally { -it / 4 }
        )
      }
    },
    modifier = modifier,
    label = "main_navigation",
  ) { destination ->
    when (destination) {
      MainDestination.Home -> {
        HomeScreen(
          onNavigate = { homeDestination ->
            val mainDestination = when (homeDestination) {
              HomeDestination.Console -> MainDestination.Console
              HomeDestination.Settings -> MainDestination.Settings
              HomeDestination.Documentation -> MainDestination.Documentation
              else -> MainDestination.Home
            }
            viewModel.onNavigate(mainDestination)
          },
          onCreateProject = { viewModel.onNavigate(MainDestination.CreateProject) },
          onImportProject = { viewModel.onNavigate(MainDestination.ImportProject) },
          onOpenProject = { viewModel.onNavigate(MainDestination.OpenProject) },
          onCloneRepository = { viewModel.onNavigate(MainDestination.GitClone) },
        )
      }

      MainDestination.CreateProject -> {
        CreateProjectScreen(
          onBackClick = { viewModel.onBackPressed() },
          onCreateProject = { name, packageName, templateType, minSdk, useKotlin, useKotlinDsl ->
            viewModel.createProject(
              name,
              packageName,
              templateType,
              minSdk,
              useKotlin,
              useKotlinDsl,
            )
          },
        )
      }

      MainDestination.OpenProject -> {
        OpenProjectScreen(
          projects = uiState.projects,
          onBackClick = { viewModel.onBackPressed() },
          onProjectSelected = { project -> viewModel.onOpenProject(project.path, project.name) },
          onProjectDelete = { project -> viewModel.deleteProject(project.path) },
          onBrowseFolder = { viewModel.onNavigate(MainDestination.ImportProject) },
        )
      }

      MainDestination.ImportProject -> {
        ImportProjectScreen(
          onBackClick = {
            viewModel.clearImportState()
            viewModel.onBackPressed()
          },
          onBrowseClick = {
            // Note: File picker integration requires Activity result handling
            // Users can manually enter/paste the project path in the text field
          },
          onImportProject = { path, copyToWorkspace ->
            viewModel.importProject(path, copyToWorkspace)
          },
          onPathChanged = { path ->
            viewModel.updateSelectedImportPath(path)
          },
          selectedPath = uiState.selectedImportPath,
          isLoading = uiState.isLoading,
          importProgress = uiState.importProgress,
        )
      }

      MainDestination.GitClone -> {
        GitCloneScreen(
          rootDirectory = uiState.rootDirectory,
          onBackClick = { viewModel.onBackPressed() },
          onCloneSuccess = { projectPath ->
            viewModel.onOpenProject(projectPath, projectPath.substringAfterLast("/"))
          },
        )
      }

      MainDestination.GitPanel -> {
        GitScreen(
          projectPath = uiState.projectPath,
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
          viewModel = viewModel,
        )
      }

      MainDestination.Settings -> {
        IDESettingsScreen(
          onBackClick = { viewModel.onBackPressed() },
          onNavigateToAIAgent = { viewModel.onNavigate(MainDestination.AIAgent) },
          onNavigateToEditorSettings = { viewModel.onNavigate(MainDestination.EditorSettings) },
        )
      }

      MainDestination.EditorSettings -> {
        EditorSettingsScreen(
          onBackClick = { viewModel.onBackPressed() },
        )
      }

      MainDestination.AssetStudio -> {
        AssetStudioScreen(
          onBackClick = { viewModel.onBackPressed() },
          onLaunchStudio = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
          onCreateDrawable = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
          onCreateIcon = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
          onImportImage = { viewModel.onNavigate(MainDestination.VectorAssetStudio) },
        )
      }

      MainDestination.AIAgent -> {
        AIAgentScreen(onBackClick = { viewModel.onBackPressed() })
      }

      MainDestination.BuildVariants -> {
        // Hier wurde der projectPath hinzugefügt
        BuildVariantsScreen(
          projectPath = uiState.projectPath,
          onBackClick = { viewModel.onBackPressed() },
        )
      }

      MainDestination.SubModuleMaker -> {
        SubModuleMakerScreen(
          onBackClick = { viewModel.onBackPressed() },
        )
      }

      MainDestination.Console -> {
        ConsoleScreen(onBackClick = { viewModel.onBackPressed() })
      }

      MainDestination.Documentation -> {
        HomeScreen(
          onNavigate = { homeDestination ->
            val mainDestination = when (homeDestination) {
              HomeDestination.Console -> MainDestination.Console
              HomeDestination.Settings -> MainDestination.Settings
              HomeDestination.Documentation -> MainDestination.Documentation
              else -> MainDestination.Home
            }
            viewModel.onNavigate(mainDestination)
          },
          onCreateProject = { viewModel.onNavigate(MainDestination.CreateProject) },
          onImportProject = { viewModel.onNavigate(MainDestination.ImportProject) },
          onOpenProject = { viewModel.onNavigate(MainDestination.OpenProject) },
          onCloneRepository = { viewModel.onNavigate(MainDestination.GitClone) },
        )
      }

      MainDestination.LayoutDesigner -> {
        DesignerScreen(
          projectName = uiState.projectName,
          onBackClick = { viewModel.onBackPressed() },
        )
      }

      MainDestination.VectorAssetStudio -> {
        VectorAssetStudioScreen(onBackClick = { viewModel.onBackPressed() })
      }
    }
  }
}
