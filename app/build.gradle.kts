import com.android.build.gradle.AppExtension
import java.util.Properties
import java.io.FileInputStream

plugins {
    id("codelikebastimove.android.application")
    id("codelikebastimove.android.application.compose")
    id("codelikebastimove.android.koin")
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
        checkReleaseBuilds = false
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
    api(project(":core:core-ui"))
    api(project(":core:core-resources"))
    api(project(":core:core-logger"))
    implementation(project(":core:core-datastore"))
    implementation(project(":core:templates-api"))
    
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
