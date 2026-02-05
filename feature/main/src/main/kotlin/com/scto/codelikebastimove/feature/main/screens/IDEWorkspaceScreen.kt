package com.scto.codelikebastimove.feature.main.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar
import com.scto.codelikebastimove.feature.git.ui.screens.GitScreen
import com.scto.codelikebastimove.feature.main.BottomSheetContentType
import com.scto.codelikebastimove.feature.main.MainContentType
import com.scto.codelikebastimove.feature.main.MainViewModel
import com.scto.codelikebastimove.feature.assetstudio.ui.AssetsStudioContent
import com.scto.codelikebastimove.feature.designer.ui.LayoutDesignerContent
import com.scto.codelikebastimove.feature.main.components.BottomSheetBar
import com.scto.codelikebastimove.feature.main.components.ContentNavigationRail
import com.scto.codelikebastimove.feature.main.content.ProjectContent
import com.scto.codelikebastimove.feature.soraeditor.screen.SoraEditorScreen
import com.scto.codelikebastimove.feature.main.navigation.MainDestination
import com.scto.codelikebastimove.feature.themebuilder.ThemeBuilderContent
import com.scto.codelikebastimove.feature.soraeditor.viewmodel.SoraEditorViewModel
import com.scto.codelikebastimove.feature.treeview.FileTreeDrawer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IDEWorkspaceScreen(
  projectName: String,
  projectPath: String,
  currentContent: MainContentType,
  isBottomSheetExpanded: Boolean,
  bottomSheetContent: BottomSheetContentType,
  fileSystemVersion: Long = 0L,
  onNavigate: (MainDestination) -> Unit,
  onBackToHome: () -> Unit,
  onContentTypeChanged: (MainContentType) -> Unit,
  onBottomSheetToggle: () -> Unit,
  onBottomSheetContentChanged: (BottomSheetContentType) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainViewModel = viewModel(),
  editorViewModel: SoraEditorViewModel = viewModel(),
) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val editorUiState by editorViewModel.uiState.collectAsState()

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet {
        FileTreeDrawer(
          projectName = projectName,
          projectPath = projectPath,
          fileSystemVersion = fileSystemVersion,
          onFileClick = { fileNode ->
            if (!fileNode.isDirectory) {
              editorViewModel.openFile(fileNode.path)
              scope.launch { drawerState.close() }
            }
          },
          onFileOperationComplete = {
            viewModel.refreshFileSystem()
          },
          onOpenTerminalSheet = {
            onBottomSheetContentChanged(BottomSheetContentType.TERMINAL)
            scope.launch { drawerState.close() }
          },
        )
      }
    },
    modifier = modifier,
  ) {
    Scaffold(
      topBar = {
        IDETopAppBar(
          projectName = projectName,
          onMenuClick = { scope.launch { drawerState.open() } },
          onStopClick = {},
          onUndoClick = {},
          onMoreClick = {},
        )
      },
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
      Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
          ContentNavigationRail(
            selectedContent = currentContent,
            onContentSelected = onContentTypeChanged,
          )

          VerticalDivider()

          AnimatedContent(
            targetState = currentContent,
            transitionSpec = {
              (fadeIn() + slideInHorizontally { it / 4 }).togetherWith(
                fadeOut() + slideOutHorizontally { -it / 4 }
              )
            },
            modifier = Modifier.weight(1f).fillMaxHeight(),
            label = "content_transition",
          ) { contentType ->
            when (contentType) {
              MainContentType.EDITOR -> SoraEditorScreen(
                tabs = editorUiState.tabs,
                activeTabId = editorUiState.activeTabId,
                onTabSelect = { tabId -> editorViewModel.selectTab(tabId) },
                onTabClose = { tabId -> editorViewModel.closeTab(tabId) },
                onTextChange = { tabId, text -> editorViewModel.updateContent(tabId, text) },
                onSave = { tabId -> editorViewModel.saveFile(tabId) },
              )
              MainContentType.PROJECT -> ProjectContent(
                viewModel = viewModel,
                editorViewModel = editorViewModel,
              )
              MainContentType.GIT -> GitScreen(projectPath = projectPath)
              MainContentType.ASSETS_STUDIO -> AssetsStudioContent()
              MainContentType.THEME_BUILDER -> ThemeBuilderContent()
              MainContentType.LAYOUT_DESIGNER -> LayoutDesignerContent()
            }
          }
        }

        BottomSheetBar(
          isExpanded = isBottomSheetExpanded,
          selectedContent = bottomSheetContent,
          onToggleExpand = onBottomSheetToggle,
          onContentSelected = onBottomSheetContentChanged,
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IDETopAppBar(
  projectName: String,
  onMenuClick: () -> Unit,
  onStopClick: () -> Unit,
  onUndoClick: () -> Unit,
  onMoreClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  AdaptiveTopAppBar(
    title = projectName,
    navigationIcon = {
      IconButton(onClick = onMenuClick) {
        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
      }
    },
    actions = {
      IconButton(onClick = onStopClick) {
        Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
      }
      IconButton(onClick = onUndoClick) {
        Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
      }
      IconButton(onClick = onMoreClick) {
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
      ),
    modifier = modifier.statusBarsPadding(),
  )
}
