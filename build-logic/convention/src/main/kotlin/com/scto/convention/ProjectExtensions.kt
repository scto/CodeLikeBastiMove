package com.scto.convention

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.version(key: String): String =
  libs
    .findVersion(key)
    .orElseThrow {
      IllegalStateException(
        "Version '$key' was not found in gradle/libs.versions.toml. Please add '$key = \"...\"' under [versions]."
      )
    }
    .toString()

fun Project.versionInt(key: String): Int = version(key).toInt()

fun Project.javaVersion(key: String): JavaVersion = JavaVersion.toVersion(version(key))
