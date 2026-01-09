import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.scto.codelikebastimove.buildlogic"

val javaVersion = libs.versions.java.get().toInt()

java {
    sourceCompatibility = JavaVersion.values()[javaVersion - 1]
    targetCompatibility = JavaVersion.values()[javaVersion - 1]
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf("JVM_$javaVersion"))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "codelikebastimove.android.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "codelikebastimove.android.application.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "codelikebastimove.android.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "codelikebastimove.android.library.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "codelikebastimove.android.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("androidKoin") {
            id = "codelikebastimove.android.koin"
            implementationClass = "KoinConventionPlugin"
        }
    }
}
