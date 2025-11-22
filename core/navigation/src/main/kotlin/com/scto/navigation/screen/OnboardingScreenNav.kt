/*
 * Copyright 2024 | Dmitri Chernysh | http://mobile-dev.pro
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.scto.navigation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scto.di.rememberNavViewModel
import com.scto.navigation.Screen
import com.scto.navigation.ext.navigateTo
import com.scto.navigation.graph.OnBoardingNavGraph
import com.scto.onboarding.di.featureOnBoardingModule
import com.scto.onboarding.view.OnBoardingFirstScreen
import com.scto.onboarding.view.OnBoardingPermissionsScreen
import com.scto.onboarding.view.OnBoardingScreen
import com.scto.onboarding.view.OnBoardingSecondScreen
import com.scto.onboarding.view.OnBoardingThirdScreen
import com.scto.onboarding.view.vm.OnBoardingViewModel
import com.scto.ui.compositionlocal.LocalAnalytics
import com.scto.ui.ext.findActivity

fun NavGraphBuilder.onBoardingScreen(onNavigateToRoot: (Screen) -> Unit) {
    composable(
        route = Screen.OnBoarding.route
    ) {
        val navController = rememberNavController()

        // ViewModel initialisieren, um den Onboarding-Status zu speichern
        val viewModel = rememberNavViewModel<OnBoardingViewModel>(
            modules = { listOf(featureOnBoardingModule) }
        )

        val nestedNavGraph: @Composable () -> Unit = {
            OnBoardingNavGraph(
                navController = navController
            )
        }
        
        OnBoardingScreen(
            nestedNavGraph,
            onNext = {
                when (navController.currentDestination?.route) {
                    // Geänderte Reihenfolge: First -> Second -> Third -> Permissions -> Home
                    Screen.OnBoardingFirst.route -> navController.navigateTo(Screen.OnBoardingSecond)
                    Screen.OnBoardingSecond.route -> navController.navigateTo(Screen.OnBoardingThird)
                    Screen.OnBoardingThird.route -> navController.navigateTo(Screen.OnBoardingPermissions)
                    
                    Screen.OnBoardingPermissions.route -> {
                        // Onboarding abschließen (firstRun = false setzen)
                        viewModel.completeOnboarding()
                        
                        Screen.Home.withClearBackStack()
                            .also(onNavigateToRoot)
                    }

                    else -> {}
                }
            }
        )
    }
}

fun NavGraphBuilder.onBoardingPermissionsScreen() {
    composable(
        route = Screen.OnBoardingPermissions.route
    ) {
        val context = LocalContext.current
        val analytics = LocalAnalytics.current

        LaunchedEffect(Unit) {
            analytics.trackScreen(
                "OnBoardingPermissionsScreen",
                context.findActivity()
            )
        }
        OnBoardingPermissionsScreen()
    }
}

fun NavGraphBuilder.onBoardingFirstScreen() {
    composable(
        route = Screen.OnBoardingFirst.route
    ) {
        val context = LocalContext.current
        val analytics = LocalAnalytics.current

        LaunchedEffect(Unit) {
            analytics.trackScreen(
                "OnBoardingFirstScreen",
                context.findActivity()
            )
        }
        OnBoardingFirstScreen()
    }
}

fun NavGraphBuilder.onBoardingSecondScreen() {
    composable(
        route = Screen.OnBoardingSecond.route
    ) {
        val context = LocalContext.current
        val analytics = LocalAnalytics.current

        LaunchedEffect(Unit) {
            analytics.trackScreen(
                "OnBoardingSecondScreen",
                context.findActivity()
            )
        }

        OnBoardingSecondScreen()
    }
}

fun NavGraphBuilder.onBoardingThirdScreen() {
    composable(
        route = Screen.OnBoardingThird.route
    ) {

        val context = LocalContext.current
        val analytics = LocalAnalytics.current

        LaunchedEffect(Unit) {
            analytics.trackScreen(
                "OnBoardingThirdScreen",
                context.findActivity()
            )
        }

        OnBoardingThirdScreen()
    }
}