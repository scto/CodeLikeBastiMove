/*
 * Copyright 2025 Thomas Schmid
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import org.gradle.kotlin.dsl.compileOnly
import org.gradle.kotlin.dsl.gradlePlugin
import org.gradle.kotlin.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.scto.build.logic"

val javaVersion = libs.versions.java.get().toInt()
//val javaVersion = javaVersion("java").toInt()

java {
    sourceCompatibility = JavaVersion.values()[javaVersion - 1]
    targetCompatibility = JavaVersion.values()[javaVersion - 1]
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf("JVM_$javaVersion"))
    }
}

// Abhängigkeiten für die Plugins selbst (Kompilierzeit)
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.firebase.perf.gradlePlugin) // Optional, falls genutzt
    compileOnly(libs.dokka.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    // Detekt Plugin (for DetektConventionPlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("application") {
            id = "com.scto.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("library") {
            id = "com.scto.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidKoin") {
            id = "com.scto.android.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("firebase") {
            id = "com.scto.firebase"
            implementationClass = "FirebaseConventionPlugin"
        }
        register("dokka") {
            id = "com.scto.dokka"
            implementationClass = "DokkaConventionPlugin"
        }
        // --- Feature Plugin (Das "Arbeitspferd") ---
        register("feature") {
            id = "com.scto.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        // --- Fähigkeits-Plugins (Capabilities) ---
        register("compose") {
            id = "com.scto.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("lint") {
            id = "com.scto.lint"
            implementationClass = "LintConventionPlugin"
        }
        register("room") {
            id = "com.scto.room"
            implementationClass = "RoomConventionPlugin"
        }
        // --- Tooling ---
        register("codeQuality") {
            id = "com.scto.codequality"
            implementationClass = "CodeQualityConventionPlugin"
        }
        register("spotless") {
            id = "com.scto.spotless"
            implementationClass = "SpotlessConventionPlugin"
        }
        register("detekt") {
            id = "com.scto.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}