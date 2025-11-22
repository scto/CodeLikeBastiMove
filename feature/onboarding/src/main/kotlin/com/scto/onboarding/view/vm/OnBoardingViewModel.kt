package com.scto.onboarding.view.vm

import androidx.lifecycle.viewModelScope

import com.scto.coroutines.BaseCoroutinesUseCase
import com.scto.settings.core.usecase.GetAppSettingsUseCase
import com.scto.settings.core.usecase.UpdateAppSettingsUseCase
import com.scto.ui.state.UIState
import com.scto.ui.vm.BaseViewModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateAppSettingsUseCase: UpdateAppSettingsUseCase
) : BaseViewModel<UIState>() {

    override val initialState: UIState
        get() = object : UIState {}

    override fun observeState(): Flow<UIState> = flowOf()

    fun completeOnboarding() {
        viewModelScope.launch {
            // Holen der aktuellen Einstellungen, um andere Werte (wie DarkMode) nicht zu überschreiben
            getAppSettingsUseCase.execute().getOrNull()?.let { currentSettings ->
                val newSettings = currentSettings.copy(isFirstRun = false)
                updateAppSettingsUseCase.execute(newSettings)
            }
        }
    }
}