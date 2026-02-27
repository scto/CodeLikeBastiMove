package com.scto.codelikebastimove.feature.settings.navigation

sealed class SettingsDestination {
    data object Main : SettingsDestination()
    data object General : SettingsDestination()
    data object Editor : SettingsDestination()
    data object BuildAndRun : SettingsDestination()
    data object Documentation : SettingsDestination()
    data object Help : SettingsDestination()
    data object About : SettingsDestination()
    data object Debug : SettingsDestination()
}
