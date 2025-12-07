package com.scto.codelikebastimove.feature.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.feature.main.components.BottomSheetBar
import com.scto.codelikebastimove.feature.main.components.ContentNavigationRail
import com.scto.codelikebastimove.feature.main.components.MainTopAppBar
import com.scto.codelikebastimove.feature.main.content.AssetsStudioContent
import com.scto.codelikebastimove.feature.main.content.EditorContent
import com.scto.codelikebastimove.feature.main.content.GitContent
import com.scto.codelikebastimove.feature.main.content.LayoutDesignerContent
import com.scto.codelikebastimove.feature.main.content.ProjectContent
import com.scto.codelikebastimove.feature.main.content.ThemeBuilderContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationSheetContent(
                    onDismiss = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    projectName = uiState.projectName,
                    hasUnsavedChanges = uiState.hasUnsavedChanges,
                    isLoading = uiState.isLoading,
                    onNavigationClick = {
                        scope.launch { drawerState.open() }
                    },
                    onRunClick = { },
                    onDebugClick = { },
                    onSaveClick = { },
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    ContentNavigationRail(
                        selectedContent = uiState.currentContent,
                        onContentSelected = viewModel::onContentTypeChanged
                    )
                    
                    VerticalDivider()
                    
                    AnimatedContent(
                        targetState = uiState.currentContent,
                        transitionSpec = {
                            (fadeIn() + slideInHorizontally { it / 4 })
                                .togetherWith(fadeOut() + slideOutHorizontally { -it / 4 })
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        label = "content_transition"
                    ) { contentType ->
                        when (contentType) {
                            MainContentType.EDITOR -> EditorContent()
                            MainContentType.PROJECT -> ProjectContent()
                            MainContentType.GIT -> GitContent()
                            MainContentType.ASSETS_STUDIO -> AssetsStudioContent()
                            MainContentType.THEME_BUILDER -> ThemeBuilderContent()
                            MainContentType.LAYOUT_DESIGNER -> LayoutDesignerContent()
                        }
                    }
                }
                
                BottomSheetBar(
                    isExpanded = uiState.isBottomSheetExpanded,
                    selectedContent = uiState.bottomSheetContent,
                    onToggleExpand = viewModel::onBottomSheetToggle,
                    onContentSelected = viewModel::onBottomSheetContentChanged
                )
            }
        }
    }
}

@Composable
private fun NavigationSheetContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight()
    ) {
        NavigationDrawerHeader()
        NavigationDrawerContent(onDismiss = onDismiss)
    }
}

@Composable
private fun NavigationDrawerHeader(
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "CodeLikeBastiMove",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Android IDE",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        NavigationDrawerItem(
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.FolderOpen,
                    contentDescription = null
                )
            },
            label = { Text("Open Project") },
            selected = false,
            onClick = { onDismiss() }
        )
        
        NavigationDrawerItem(
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.CreateNewFolder,
                    contentDescription = null
                )
            },
            label = { Text("New Project") },
            selected = false,
            onClick = { onDismiss() }
        )
        
        NavigationDrawerItem(
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = null
                )
            },
            label = { Text("Clone Repository") },
            selected = false,
            onClick = { onDismiss() }
        )
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        NavigationDrawerItem(
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null
                )
            },
            label = { Text("Settings") },
            selected = false,
            onClick = { onDismiss() }
        )
        
        NavigationDrawerItem(
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            },
            label = { Text("About") },
            selected = false,
            onClick = { onDismiss() }
        )
    }
}
