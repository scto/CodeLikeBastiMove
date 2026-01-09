/*
 * Copyright 2024 Thomas Schmid
 */

package con.scto.convention

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Zugriff auf den "libs" Version Catalog.
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Liest eine Version als String aus dem Catalog mit klarer Fehlermeldung.
 */
fun Project.version(key: String): String =
    libs.findVersion(key).orElseThrow {
        IllegalStateException("Version '$key' wurde nicht in gradle/libs.versions.toml gefunden. Bitte füge '$key = \"...\"' unter [versions] hinzu.")
    }.toString()

/**
 * Liest eine Version als Int aus dem Catalog (für SDK Versionen).
 */
fun Project.versionInt(key: String): Int =
    version(key).toInt()

/**
 * Liest eine Java-Version aus dem Catalog und konvertiert sie.
 */
fun Project.javaVersion(key: String): JavaVersion =
    JavaVersion.toVersion(version(key))