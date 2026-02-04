package com.scto.codelikebastimove.core.updater.di

import com.scto.codelikebastimove.core.updater.UpdateRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val updaterModule = module {
    single { UpdateRepository(androidContext()) }
}
