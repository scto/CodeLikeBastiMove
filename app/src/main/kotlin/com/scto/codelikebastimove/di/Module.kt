package com.scto.codelikebastimove.di

import com.scto.codelikebastimove.core.datastore.di.coreDataStoreModule
import com.scto.codelikebastimove.feature.settings.di.featureSettingsModule
// Importiere hier weitere Feature Module, z.B. OnboardingModule

import org.koin.dsl.module

val appModule = module {
    includes(
        coreDataStoreModule,
        featureSettingsModule
        // Füge hier weitere Module hinzu
    )
}
