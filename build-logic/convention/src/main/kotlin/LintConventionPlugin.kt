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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    extensions.configure<ApplicationExtension> {
                        lint.configureLint(target)
                    }
                    /*
                    extensions.configure<ApplicationExtension> { lint(Lint::configure) }
                    */
                pluginManager.hasPlugin("com.android.library") ->
                    extensions.configure<LibraryExtension> {
                        lint.configureLint(target)
                    }
                    /*
                    extensions.configure<LibraryExtension> { lint(Lint::configure) }
                    */
                else -> {
                    // Falls es ein reines Kotlin-Modul ohne Android-Plugin ist
                    pluginManager.apply("com.android.lint")
                    extensions.configure<Lint> {
                        configureLint(target)
                    }
                }
            }
        }
    }
}

/**
 * Extension Function, die das Project-Objekt mitnimmt, 
 * um auf Pfade und Utilities zuzugreifen.
 */
private fun Lint.configureLint(project: Project) {
    // Berichte in das zentrale Build-Verzeichnis schreiben
    val reportDir = project.rootProject.layout.buildDirectory.dir("reports/lint")

    xmlReport = true
    xmlOutput = project.file("${reportDir.get()}/lint-report.xml")

    htmlReport = true
    htmlOutput = project.file("${reportDir.get()}/lint-report.html")
        
    textReport = true
    textOutput = project.file("${reportDir.get()}/lint-report.txt")

    sarifReport = true
    sarifOutput = project.file("${reportDir.get()}/lint-report.sarif")
    
    abortOnError = false
    warningsAsErrors = false

    // PERFORMANCE: Wir nutzen dein isRunningOnAndroidDevice aus Utilities.kt
    // Auf dem Handy schalten wir die Tiefenprüfung der Abhängigkeiten aus.
    //checkDependencies = !project.isRunningOnAndroidDevice
    checkDependencies = true
        
    checkReleaseBuilds = false
        
    // Pfad zur lint.xml im Root-Verzeichnis
    lintConfig = project.file("${project.rootDir}/lint.xml")
        
    disable += "GradleDependency"

    enable += "Deprecated"
    // Erlaubt das automatische Fixen via ./gradlew lintFix
    /*
    @Suppress("UnstableApiUsage")
    enable {
        add("Deprecated")
    }
    */
}

/*
private fun Lint.configure() {
    xmlReport = true
    checkDependencies = true
}
*/