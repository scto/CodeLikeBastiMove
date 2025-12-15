package com.scto.codelikebastimove.feature.main.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.templates.api.ProjectManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubModuleMakerScreen(
    onBackClick: () -> Unit,
    projectManager: ProjectManager = koinInject()
) {
    // Beispiel: :features:login oder features:login
    var modulePathInput by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.example.mymodule") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentProject by projectManager.currentProject.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Neues Submodul erstellen") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (currentProject == null) {
                Text(
                    text = "Kein Projekt geöffnet. Bitte öffne zuerst ein Projekt.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "Aktuelles Projekt: ${currentProject?.name}",
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedTextField(
                    value = modulePathInput,
                    onValueChange = { modulePathInput = it },
                    label = { Text("Gradle Pfad (z.B. :features:login)") },
                    placeholder = { Text(":features:feature-name") },
                    supportingText = { Text("Nutze Doppelpunkte für Unterordner") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (modulePathInput.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Bitte Modulpfad eingeben") }
                            return@Button
                        }

                        val projectRoot = currentProject!!.path
                        val success = createSubModule(projectRoot, modulePathInput, packageName)

                        scope.launch {
                            if (success) {
                                snackbarHostState.showSnackbar("Modul '$modulePathInput' erfolgreich erstellt!")
                                modulePathInput = ""
                            } else {
                                snackbarHostState.showSnackbar("Fehler: Modul existiert bereits oder Pfad ungültig.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modulePathInput.isNotBlank()
                ) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Modul erstellen")
                }
            }
        }
    }
}

private fun createSubModule(rootPath: String, moduleInputName: String, packageName: String): Boolean {
    return try {
        val rootDir = File(rootPath)

        // 1. Pfad normalisieren
        // Stellt sicher, dass der Gradle-Pfad mit einem Doppelpunkt beginnt (z.B. ":features:login")
        val gradlePath = if (moduleInputName.startsWith(":")) moduleInputName else ":$moduleInputName"

        // Entfernt den führenden Doppelpunkt und ersetzt den Rest durch Dateiseparatoren
        // Beispiel: ":features:login" -> "features/login" (auf Linux/Mac) oder "features\login" (auf Windows)
        val relativeFilePath = gradlePath.trimStart(':').replace(':', File.separatorChar)

        val moduleDir = File(rootDir, relativeFilePath)

        if (moduleDir.exists()) return false

        // mkdirs() erstellt auch alle notwendigen Elternordner (z.B. den Ordner 'features')
        moduleDir.mkdirs()

        // 2. build.gradle.kts erstellen
        val buildFile = File(moduleDir, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }

            android {
                namespace = "$packageName"
                compileSdk = 34

                defaultConfig {
                    minSdk = 24
                }
                
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }

            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.appcompat:appcompat:1.6.1")
                implementation("com.google.android.material:material:1.11.0")
            }
        """.trimIndent())

        // 3. .gitignore erstellen
        val gitignore = File(moduleDir, ".gitignore")
        gitignore.writeText("/build\n")

        // 4. src Ordner Struktur erstellen
        val srcMain = File(moduleDir, "src/main")
        srcMain.mkdirs()

        // AndroidManifest.xml
        val manifest = File(srcMain, "AndroidManifest.xml")
        manifest.writeText("""
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
            </manifest>
        """.trimIndent())

        // Java/Kotlin Ordner
        val packagePath = packageName.replace('.', '/')
        val codeDir = File(srcMain, "kotlin/$packagePath")
        codeDir.mkdirs()

        // 5. settings.gradle.kts aktualisieren
        val settingsFile = File(rootDir, "settings.gradle.kts")
        if (settingsFile.exists()) {
            // Fügt include(":features:login") hinzu
            settingsFile.appendText("\ninclude(\"$gradlePath\")")
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}