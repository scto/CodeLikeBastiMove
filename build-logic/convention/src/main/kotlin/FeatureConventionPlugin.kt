/*
 * Copyright 2024 Thomas Schmid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.build.gradle.LibraryExtension

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

import com.scto.convention.libs

/**
 * Standardisiertes Plugin für Feature-Module.
 * Kombiniert Library, Koin und Compose.
 */
class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("clbm.android.library")
                apply("clbm.android.compose")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            dependencies {
                add("api", project(":core:ui"))
                add("api", project(":core:resources"))

                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))

                add("api", libs.findLibrary("androidx-core-ktx").get())
                add("api", libs.findLibrary("coroutines-android").get())
                
                add("api", platform(libs.findLibrary("compose-bom").get()))
                add("api", libs.findBundle("compose").get())
                add("api", libs.findBundle("lifecycle").get())
                
                add("api", libs.findLibrary("activity-compose").get())
                
                add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
            }
        }
    }
}
