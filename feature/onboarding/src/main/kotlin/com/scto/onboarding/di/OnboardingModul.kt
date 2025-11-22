package com.scto.onboarding.di

import com.scto.onboarding.view.vm.OnBoardingViewModel
import com.scto.settings.core.usecase.GetAppSettingsUseCase
import com.scto.settings.core.usecase.UpdateAppSettingsUseCase

import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureOnBoardingModule = module {
    scope<OnBoardingViewModel> {
        viewModelOf(::OnBoardingViewModel)
        scopedOf(::GetAppSettingsUseCase)
        scopedOf(::UpdateAppSettingsUseCase)
    }
}