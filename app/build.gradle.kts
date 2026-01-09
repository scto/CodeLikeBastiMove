import com.android.build.gradle.AppExtension
import java.util.Properties
import java.io.FileInputStream

plugins {
    id("clbm.android.application")
    id("clbm.android.compose")
}

val keystorePropsFile = rootProject.file("release.properties")
val keystoreProps = Properties()

if (keystorePropsFile.exists()) {
    keystoreProps.load(FileInputStream(keystorePropsFile))
}

val hasValidSigningProps = keystorePropsFile.exists().also { exists ->
    if (exists) {
        FileInputStream(keystorePropsFile).use { keystoreProps.load(it) }
    }
}.let {
    listOf("storeFile", "storePassword", 
            "keyAlias", "keyPassword").all { key ->
        keystoreProps[key] != null
    }
}

android {
    namespace = "com.scto.codelikebastimove"
    
    lint {
        // --- Output Dateien (Wo die Warnungen geschrieben werden) ---
        
        // Schreibt einen XML-Bericht (gut für CI/CD Tools oder Parser im IDE)
        xmlReport = true
        xmlOutput = file("build/reports/lint/lint-report.xml")

        // Schreibt einen HTML-Bericht (gut für Menschen lesbar)
        htmlReport = true
        htmlOutput = file("build/reports/lint/lint-report.html")
        
        // Schreibt einen einfachen Text-Bericht
        textReport = true
        // textOutput = file("stdout") // Ausgabe in Konsole
        textOutput = file("build/reports/lint/lint-results.txt") // Ausgabe in Datei

        // --- Verhaltensregeln ---

        // Wenn true, bricht der Build bei Fehlern ab
        abortOnError = false

        // Wenn true, werden Warnungen als Fehler behandelt
        warningsAsErrors = false

        // Prüft auch alle Abhängigkeiten (kann den Build verlangsamen)
        checkDependencies = true
        
        // Führt Lint checks auch bei Release Builds aus
        checkReleaseBuilds = true
        
        // --- Konfigurationsdatei einbinden ---
        // Hier verweisen wir auf die lint.xml, die wir oben erstellt haben
        lintConfig = file("${project.rootDir}/lint.xml")

        // --- Baseline (Snapshot) ---
        // Wenn eine Datei hier angegeben ist, werden alle existierenden 
        // Warnungen darin gespeichert und ignoriert. Nur NEUE Warnungen werden gemeldet.
        // Nützlich für Legacy-Code.
        // baseline = file("lint-baseline.xml")
    }
        
    signingConfigs {
        if (hasValidSigningProps) {
            create("release") {
                storeFile = rootProject.file(keystoreProps["storeFile"] as String)
                storePassword = keystoreProps["storePassword"] as String
                keyAlias = keystoreProps["keyAlias"] as String
                keyPassword = keystoreProps["keyPassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "com.scto.codelikebastimove"
        targetSdk = libs.versions.sdk.target.get().toInt()
        versionCode = libs.versions.app.version.code.get().toInt()
        versionName = libs.versions.app.version.name.get()
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    buildTypes {
        release {
            if (hasValidSigningProps) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    api(project(":features"))
    api(project(":core:ui"))
    api(project(":core:resources"))
    api(project(":core:logger"))
    implementation(project(":core:datastore:datastore"))
    implementation(project(":core:templates:templates-api"))
    
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.lifecycle)
    
    implementation(libs.activity.compose)
    implementation(libs.navigation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.android)

    coreLibraryDesugaring(libs.desugaring)
    
    debugImplementation(libs.bundles.compose.debug)
}
