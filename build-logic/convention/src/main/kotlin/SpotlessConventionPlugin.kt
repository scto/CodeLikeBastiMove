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

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SpotlessConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.diffplug.spotless")

      extensions.configure<SpotlessExtension> {
        kotlin {
          target("src/**/*.kt")
          targetExclude("**/build/**/*.kt")
          ktlint()
          // Fix: Added delimiter argument and switched to copyright.kt
          licenseHeaderFile(
            rootProject.file("config/spotless/copyright.kt"),
            "^(package|import|@file|class|fun|object|interface)",
          )
        }
        groovy {
          target("**/*.gradle")
          targetExclude("**/build/**/*.gradle")
          // Look for the first line that doesn't have a block comment (assumed to be the license)
          licenseHeaderFile(
            rootProject.file("config/spotless/copyright.gradle"),
            "(^(?![\\/ ]\\*).*$)",
          )
        }
        kotlinGradle {
          target("*.gradle.kts")
          ktlint()
          // Fix: Added delimiter argument
          licenseHeaderFile(
            rootProject.file("config/spotless/copyright.kt"),
            "^(package|import|@file|plugins|dependencyResolutionManagement)",
          )
        }
        format("xml") {
          target("**/*.xml")
          targetExclude("**/build/**/*.xml")
          // Keep using copyright.xml for XML files as it has <!-- --> comments
          licenseHeaderFile(rootProject.file("config/spotless/copyright.xml"), "(<[^!?])")
        }
      }
    }
  }
}
