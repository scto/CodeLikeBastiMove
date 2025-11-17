/*
 * Copyright 2025 | Dmitri Chernysh | https://github.com/dmitriy-chernysh
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.mobiledevpro.settings.core.usecase

import com.mobiledevpro.coroutines.BaseCoroutinesFLowUseCase
import com.mobiledevpro.coroutines.None
import com.mobiledevpro.settings.core.datastore.AppSettingsManager
import com.mobiledevpro.settings.core.model.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case to get the general settings of the app
 *
 * Created on Jan 03, 2025.
 *
 */
class GetAppSettingsUseCase(
    private val settingsManager: AppSettingsManager
) : BaseCoroutinesFLowUseCase<None, Settings>(Dispatchers.IO) {

    override fun buildUseCaseFlow(params: None?): Flow<Settings> =
        settingsManager.get()
            .map { appSettings ->
                Settings(
                    darkMode = appSettings.darkMode
                )
            }
}