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

package con.scto.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import con.scto.convention.libs
import con.scto.convention.javaVersion
import con.scto.convention.version
import con.scto.convention.versionInt

/**
 * Zentrale Android Konfiguration für Apps und Libraries.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = versionInt("compileSdk")

        defaultConfig {
            minSdk = versionInt("minSdk")
        }

        val javaVersion = javaVersion("java")

        compileOptions {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
            isCoreLibraryDesugaringEnabled = true
        }

        configureKotlin(javaVersion)
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}

private fun Project.configureKotlin(javaVersion: org.gradle.api.JavaVersion) {
    tasks.withType<KotlinCompile>().configureEach {
        // MIGRATION: 'kotlinOptions' -> 'compilerOptions'
        compilerOptions {
            // Konvertiert die Gradle JavaVersion (z.B. "17") in das Kotlin JvmTarget Enum
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
            
            allWarningsAsErrors.set(false)
            
            // In compilerOptions ist freeCompilerArgs eine ListProperty, daher .addAll()
            freeCompilerArgs.addAll(
                  "-opt-in=kotlin.RequiresOptIn",
                  "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                  "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                  "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
                  "-Xcontext-parameters",
                  // https://youtrack.jetbrains.com/issue/KT-73255
                  "-Xannotation-default-target=param-property",
            )
        }
    }
}