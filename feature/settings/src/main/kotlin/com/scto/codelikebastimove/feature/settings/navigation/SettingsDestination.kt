package com.scto.codelikebastimove.feature.settings.navigation

sealed class SettingsDestination {
    data object About : SettingsDestination()

    data object AiAgent : SettingsDestination()

    data object App : SettingsDestination()

    data object Appearance : SettingsDestination()

    data object BuildAndRun : SettingsDestination()

    data object Debug : SettingsDestination()

    data object Developer : SettingsDestination()

    data object Editor : SettingsDestination()

    data object General : SettingsDestination()

    //data object Git : SettingsDestination()

    //data object Main : SettingsDestination()

    data object Privacy : SettingsDestination()

    data object Statistics : SettingsDestination()

    data object Termux : SettingsDestination()

    data object Documentation : SettingsDestination()
    
    data object Help : SettingsDestination()
}
