package com.scto.codelikebastimove.feature.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.assetstudio.screen.AssetStudioScreen
import com.scto.codelikebastimove.feature.assetstudio.screen.VectorAssetStudioScreen
import com.scto.codelikebastimove.feature.designer.ui.screen.DesignerScreen
import com.scto.codelikebastimove.feature.git.ui.screens.GitCloneScreen
import com.scto.codelikebastimove.feature.git.ui.screens.GitScreen
import com.scto.codelikebastimove.feature.main.navigation.MainDestination
import com.scto.codelikebastimove.feature.main.screens.CreateProjectScreen
import com.scto.codelikebastimove.feature.main.screens.ImportProjectScreen
import com.scto.codelikebastimove.feature.main.screens.OpenProjectScreen
import com.scto.codelikebastimove.feature.submodulemaker.BuildVariantsScreen
import com.scto.codelikebastimove.feature.main.screens.IDEWorkspaceScreen
import com.scto.codelikebastimove.feature.settings.app.SettingsAppScreen
import com.scto.codelikebastimove.feature.settings.editor.EditorSettingsScreen
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
        HomeContent(
          onNavigate = { viewModel.onNavigate(it) },
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
          directoryContents = uiState.directoryContents,
          onBackClick = { viewModel.onBackPressed() },
          onProjectSelected = { project -> viewModel.onOpenProject(project.path, project.name) },
          onDirectorySelected = { dirItem -> viewModel.onOpenProject(dirItem.path, dirItem.name) },
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
          isBottomSheetExpanded = uiState.isBottomSheetExpanded,
          bottomSheetContent = uiState.bottomSheetContent,
          fileSystemVersion = uiState.lastFileSystemUpdate,
          onNavigate = { viewModel.onNavigate(it) },
          onBottomSheetToggle = { viewModel.onBottomSheetToggle() },
          onBottomSheetContentChanged = { viewModel.onBottomSheetContentChanged(it) },
          viewModel = viewModel,
        )
      }

      MainDestination.Settings -> {
        SettingsAppScreen(
          onBackClick = { viewModel.onBackPressed() },
          onNavigateToGeneral = { viewModel.onNavigate(MainDestination.GeneralSettings) },
          onNavigateToEditorSettings = { viewModel.onNavigate(MainDestination.EditorSettings) },
          onNavigateToAIAgent = { viewModel.onNavigate(MainDestination.AIAgent) },
          onNavigateToBuildAndRun = { viewModel.onNavigate(MainDestination.BuildAndRunSettings) },
          onNavigateToTermux = { viewModel.onNavigate(MainDestination.TermuxSettings) },
          onNavigateToStatistics = { viewModel.onNavigate(MainDestination.StatisticsSettings) },
          onNavigateToDeveloperOptions = { viewModel.onNavigate(MainDestination.DeveloperOptions) },
          onNavigateToAbout = { viewModel.onNavigate(MainDestination.About) },
        )
      }

      MainDestination.GeneralSettings -> {
        com.scto.codelikebastimove.feature.settings.general.GeneralSettingsScreen(
          onBack = { viewModel.onBackPressed() },
        )
      }

      MainDestination.BuildAndRunSettings -> {
        com.scto.codelikebastimove.feature.settings.general.GeneralSettingsScreen(
          onBack = { viewModel.onBackPressed() },
        )
      }

      MainDestination.TermuxSettings -> {
        com.scto.codelikebastimove.feature.settings.general.GeneralSettingsScreen(
          onBack = { viewModel.onBackPressed() },
        )
      }

      MainDestination.StatisticsSettings -> {
        com.scto.codelikebastimove.feature.settings.general.GeneralSettingsScreen(
          onBack = { viewModel.onBackPressed() },
        )
      }

      MainDestination.DeveloperOptions -> {
        com.scto.codelikebastimove.feature.settings.debug.DebugSettingsScreen(
          onBack = { viewModel.onBackPressed() },
        )
      }

      MainDestination.About -> {
        com.scto.codelikebastimove.feature.settings.about.AboutScreen(
          onBack = { viewModel.onBackPressed() },
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
        HomeContent(
          onNavigate = { viewModel.onNavigate(it) },
          onCreateProject = { viewModel.onNavigate(MainDestination.CreateProject) },
          onImportProject = { viewModel.onNavigate(MainDestination.ImportProject) },
          onOpenProject = { viewModel.onNavigate(MainDestination.OpenProject) },
          onCloneRepository = { viewModel.onNavigate(MainDestination.GitClone) },
        )
      }

      MainDestination.BuildVariants -> {
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
        HomeContent(
          onNavigate = { viewModel.onNavigate(it) },
          onCreateProject = { viewModel.onNavigate(MainDestination.CreateProject) },
          onImportProject = { viewModel.onNavigate(MainDestination.ImportProject) },
          onOpenProject = { viewModel.onNavigate(MainDestination.OpenProject) },
          onCloneRepository = { viewModel.onNavigate(MainDestination.GitClone) },
        )
      }

      MainDestination.Documentation -> {
        HomeContent(
          onNavigate = { viewModel.onNavigate(it) },
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
        VectorAssetStudioScreen(onBack = { viewModel.onBackPressed() })
      }
    }
  }
}

@Composable
private fun HomeContent(
    onNavigate: (MainDestination) -> Unit,
    onCreateProject: () -> Unit,
    onImportProject: () -> Unit,
    onOpenProject: () -> Unit,
    onCloneRepository: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.your_ideas_anywhere),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppLogo()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.get_started),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.start_your_project),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        HomeActionButton(
            icon = Icons.Default.Add,
            title = stringResource(R.string.create_project),
            onClick = onCreateProject,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Outlined.FileDownload,
            title = stringResource(R.string.import_project),
            onClick = onImportProject,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Folder,
            title = stringResource(R.string.open_existing_project),
            onClick = onOpenProject,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Outlined.CloudDownload,
            title = stringResource(R.string.clone_repository),
            onClick = onCloneRepository,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Terminal,
            title = stringResource(R.string.console),
            onClick = { onNavigate(MainDestination.Console) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Settings,
            title = stringResource(R.string.settings),
            onClick = { onNavigate(MainDestination.Settings) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Tune,
            title = stringResource(R.string.ide_configurations),
            onClick = { onNavigate(MainDestination.Settings) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Book,
            title = stringResource(R.string.documentation),
            onClick = { onNavigate(MainDestination.Documentation) },
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00D9FF),
                        Color(0xFF00B4D8),
                        Color(0xFF7C3AED),
                        Color(0xFFA855F7)
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CLBM",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
            )
            Text(
                text = "</>",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
