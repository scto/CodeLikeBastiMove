package com.scto.codelikebastimove.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.feature.editor.EditorScreen
import com.scto.codelikebastimove.feature.gallery.GalleryScreen
import com.scto.codelikebastimove.feature.home.HomeScreen
import com.scto.codelikebastimove.feature.settings.SettingsScreen
import com.scto.codelikebastimove.feature.slideshow.SlideshowScreen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Gallery : Screen("gallery", "Gallery", Icons.Default.Image)
    object Slideshow : Screen("slideshow", "Slideshow", Icons.Default.Slideshow)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Editor : Screen("editor", "Editor", Icons.Default.Code)
}

val screens = listOf(Screen.Home, Screen.Gallery, Screen.Slideshow, Screen.Settings)

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var currentProject by remember { mutableStateOf<Project?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToEditor = { project ->
                    currentProject = project
                    navController.navigate(Screen.Editor.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Gallery.route) {
            GalleryScreen()
        }
        composable(Screen.Slideshow.route) {
            SlideshowScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Editor.route) {
            currentProject?.let { project ->
                EditorScreen(project = project)
            }
        }
    }
}

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    navController: NavHostController,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = content
    )
}
