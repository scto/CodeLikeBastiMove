/*
 * Copyright 2024 Thomas Schmid
 */

import com.android.build.api.dsl.ApplicationExtension
import com.scto.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
        // Optional: Falls du KSP nutzt
        // apply("com.google.comtools.ksp")
      }

      extensions.configure<ApplicationExtension> { configureKotlinAndroid(this) }
    }
  }
}
