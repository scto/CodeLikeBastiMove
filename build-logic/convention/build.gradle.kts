import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.scto.codelikebastimove.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.gradle)
    compileOnly(libs.gradle.api)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kotlin.stdlib)
    
    implementation(libs.detekt.plugin)
    implementation(libs.spotless.plugin)
    implementation(libs.ktlint.jlleitschuh.plugin)
    
    // Workaround to make version catalog type-safe accessors available in convention plugins
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "codelikebastimove.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "codelikebastimove.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "codelikebastimove.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "codelikebastimove.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "codelikebastimove.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidKoin") {
            id = "codelikebastimove.android.koin"
            implementationClass = "AndroidKoinConventionPlugin"
        }
        register("candroidCodeQuality") {
            id = "codelikebastimove.android.code.quality"
            implementationClass = "AndroidCodeQualiyConventionPlugin"
        }
    }
}
