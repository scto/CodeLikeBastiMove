import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.scto.build.logic"

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
            id = "scto.scto.android.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "scto.scto.android.application.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "scto.scto.android.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "scto.scto.android.library.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "scto.scto.android.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("androidKoin") {
            id = "scto.scto.android.koin"
            implementationClass = "KoinConventionPlugin"
        }
    }
}
