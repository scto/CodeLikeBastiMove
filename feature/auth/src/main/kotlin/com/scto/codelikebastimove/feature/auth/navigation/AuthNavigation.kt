package com.scto.codelikebastimove.feature.auth.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scto.codelikebastimove.feature.auth.screen.LoginScreen
import com.scto.codelikebastimove.feature.auth.screen.RegisterScreen
import com.scto.codelikebastimove.feature.auth.viewmodel.AuthViewModel

sealed class AuthRoute(val route: String) {
    data object Login : AuthRoute("login")
    data object Register : AuthRoute("register")
}

@Composable
fun AuthNavHost(
    navController: NavHostController = rememberNavController(),
    onAuthSuccess: () -> Unit,
    startDestination: String = AuthRoute.Login.route
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AuthRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = onAuthSuccess,
                onNavigateToRegister = {
                    navController.navigate(AuthRoute.Register.route)
                },
                viewModel = authViewModel
            )
        }
        
        composable(AuthRoute.Register.route) {
            RegisterScreen(
                onRegistrationSuccess = onAuthSuccess,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }
    }
}
