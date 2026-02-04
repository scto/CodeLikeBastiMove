package com.scto.codelikebastimove.feature.home.navigation

sealed class HomeDestination {
    data object Home : HomeDestination()
    data object CreateProject : HomeDestination()
    data object ImportProject : HomeDestination()
    data object OpenProject : HomeDestination()
    data object CloneRepository : HomeDestination()
    data object Console : HomeDestination()
    data object Settings : HomeDestination()
    data object Documentation : HomeDestination()
    data object IDE : HomeDestination()
}
