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

import com.scto.convention.javaVersion
import com.scto.convention.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("io.gitlab.arturbosch.detekt")

      val javaVersion = javaVersion("java")

      extensions.configure<DetektExtension> {
        toolVersion = libs.findVersion("detekt").get().toString()
        source.setFrom(files("src/main/kotlin", "src/main/java"))
        config.setFrom(files("${project.rootDir}/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        autoCorrect = true
      }

      dependencies {
        // Fügt das Detekt Formatting Plugin (Wrapper um ktlint) hinzu
        add("detektPlugins", libs.findLibrary("detektFormatting").get())
      }

      tasks.withType<Detekt>().configureEach {
        jvmTarget = javaVersion.toString()
        reports {
          html.required.set(true)
          xml.required.set(true)
          txt.required.set(false)
          sarif.required.set(true)
        }
      }
    }
  }
}
