package com.scto.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
  commonExtension.apply {
    compileSdk = versionInt("compileSdk")

    defaultConfig { minSdk = versionInt("minSdk") }

    val javaVersion = javaVersion("java")

    compileOptions {
      sourceCompatibility = javaVersion
      targetCompatibility = javaVersion
      isCoreLibraryDesugaringEnabled = true
    }

    configureKotlin(javaVersion)
  }

  dependencies { add("coreLibraryDesugaring", libs.findLibrary("desugaring").get()) }
}

private fun Project.configureKotlin(javaVersion: org.gradle.api.JavaVersion) {
  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))

      allWarningsAsErrors.set(false)

      freeCompilerArgs.addAll(
        "-opt-in=kotlin.RequiresOptIn",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      )
    }
  }
}
