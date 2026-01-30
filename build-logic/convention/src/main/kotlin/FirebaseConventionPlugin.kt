/*
 * Copyright 2024 Thomas Schmid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.scto.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FirebaseConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      dependencies {
        // BOM sorgt dafür, dass alle Firebase Versionen kompatibel sind
        val bom = libs.findLibrary("firebase-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("firebase-analytics").get())
        add("implementation", libs.findLibrary("firebase-crashlytics").get())
        add("implementation", libs.findLibrary("firebase-perf").get())
      }

      // Wende Plugins nur an, wenn es sich um eine App handelt
      pluginManager.withPlugin("com.android.application") {
        // Crashlytics Gradle Plugin
        pluginManager.apply(libs.findPlugin("firebase-crashlytics").get().get().pluginId)
        // Performance Monitoring Gradle Plugin
        pluginManager.apply(libs.findPlugin("firebase-perf").get().get().pluginId)
      }
    }
  }
}
