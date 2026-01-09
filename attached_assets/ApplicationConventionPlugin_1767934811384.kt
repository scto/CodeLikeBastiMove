/*
 * Copyright 2024 Thomas Schmid
 */

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

import dev.scto.convention.configureKotlinAndroid
import dev.scto.convention.versionInt

class ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                // Optional: Falls du KSP nutzt
                // apply("com.google.devtools.ksp") 
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = versionInt("targetSdk")
            }
        }
    }
}