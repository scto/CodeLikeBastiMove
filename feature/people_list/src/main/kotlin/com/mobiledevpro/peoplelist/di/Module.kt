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
package com.mobiledevpro.peoplelist.di

import com.mobiledevpro.peoplelist.domain.usecase.GetPeopleListUseCase
import com.mobiledevpro.peoplelist.view.vm.PeopleListViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * User Profile screen module
 *
 * Created on Jul 22, 2023.
 *
 */

val featurePeopleListModule = module {

    scope<PeopleListViewModel> {
        viewModelOf(::PeopleListViewModel)
        scopedOf(::GetPeopleListUseCase)
    }
}