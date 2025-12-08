package com.scto.codelikebastimove.feature.slidingpanel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.scto.codelikebastimove.feature.slidingpanel.components.SlidingPanelNavigationRail
import com.scto.codelikebastimove.feature.slidingpanel.containers.AssetStudioContainer
import com.scto.codelikebastimove.feature.slidingpanel.containers.BuildVariant
import com.scto.codelikebastimove.feature.slidingpanel.containers.BuildVariantsContainer
import com.scto.codelikebastimove.feature.slidingpanel.containers.FileTreeContainer
import com.scto.codelikebastimove.feature.slidingpanel.containers.FileViewMode
import com.scto.codelikebastimove.feature.slidingpanel.containers.GitChangedFile
import com.scto.codelikebastimove.feature.slidingpanel.containers.ModuleBuildVariant
import com.scto.codelikebastimove.feature.slidingpanel.containers.OpenedFile
import com.scto.codelikebastimove.feature.slidingpanel.containers.ThemeBuilderContainer
import com.scto.codelikebastimove.feature.slidingpanel.containers.ThemeColors

@Composable
fun SlidingPanelScreen(
    openedFiles: List<OpenedFile>,
    gitChangedFiles: List<GitChangedFile>,
    modules: List<ModuleBuildVariant>,
    activeVariant: BuildVariant,
    hasVariantChanges: Boolean,
    seedColor: Color,
    useDynamicColors: Boolean,
    themeColors: ThemeColors,
    onFileClicked: (String) -> Unit,
    onFileCloseClicked: (String) -> Unit,
    onGitFileClicked: (String) -> Unit,
    onModuleVariantChanged: (String, BuildVariant) -> Unit,
    onApplyVariantChanges: () -> Unit,
    onSeedColorChanged: (Color) -> Unit,
    onDynamicColorsToggled: (Boolean) -> Unit,
    onExportTheme: () -> Unit,
    onOpenFullThemeEditor: () -> Unit,
    onCreateDrawable: () -> Unit,
    onCreateIcon: () -> Unit,
    onImportImage: () -> Unit,
    onOpenVectorAssetStudio: () -> Unit,
    onOpenLayoutDesigner: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenSettings: () -> Unit,
    fileTreeContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedNavItem by remember { mutableStateOf(SlidingPanelNavigationType.FILE_TREE) }
    var currentViewMode by remember { mutableStateOf(FileViewMode.PROJECT) }
    
    Row(modifier = modifier.fillMaxSize()) {
        SlidingPanelNavigationRail(
            selectedItem = selectedNavItem,
            onItemSelected = { item ->
                when (item) {
                    SlidingPanelNavigationType.LAYOUT_DESIGNER -> onOpenLayoutDesigner()
                    SlidingPanelNavigationType.TERMINAL -> onOpenTerminal()
                    SlidingPanelNavigationType.SETTINGS -> onOpenSettings()
                    else -> selectedNavItem = item
                }
            }
        )
        
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        
        AnimatedContent(
            targetState = selectedNavItem,
            transitionSpec = {
                (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it / 4 } + fadeOut())
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            label = "panel_content"
        ) { navItem ->
            when (navItem) {
                SlidingPanelNavigationType.FILE_TREE -> {
                    FileTreeContainer(
                        openedFiles = openedFiles,
                        gitChangedFiles = gitChangedFiles,
                        currentViewMode = currentViewMode,
                        onViewModeChanged = { currentViewMode = it },
                        onFileClicked = onFileClicked,
                        onFileCloseClicked = onFileCloseClicked,
                        onGitFileClicked = onGitFileClicked,
                        fileTreeContent = fileTreeContent
                    )
                }
                
                SlidingPanelNavigationType.BUILD_VARIANTS -> {
                    BuildVariantsContainer(
                        modules = modules,
                        activeVariant = activeVariant,
                        onModuleVariantChanged = onModuleVariantChanged,
                        onApplyChanges = onApplyVariantChanges,
                        hasChanges = hasVariantChanges
                    )
                }
                
                SlidingPanelNavigationType.ASSET_STUDIO -> {
                    AssetStudioContainer(
                        onCreateDrawable = onCreateDrawable,
                        onCreateIcon = onCreateIcon,
                        onImportImage = onImportImage,
                        onOpenVectorAssetStudio = onOpenVectorAssetStudio
                    )
                }
                
                SlidingPanelNavigationType.THEME_BUILDER -> {
                    ThemeBuilderContainer(
                        seedColor = seedColor,
                        useDynamicColors = useDynamicColors,
                        themeColors = themeColors,
                        onSeedColorChanged = onSeedColorChanged,
                        onDynamicColorsToggled = onDynamicColorsToggled,
                        onExportTheme = onExportTheme,
                        onOpenFullEditor = onOpenFullThemeEditor
                    )
                }
                
                else -> {}
            }
        }
    }
}
