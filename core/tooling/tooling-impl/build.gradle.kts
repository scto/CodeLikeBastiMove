plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.features.tooling.impl"
}

dependencies {
    api(project(":core:tooling:tooling-api"))
    api(project(":core:termux:termux-app"))
    
    implementation(project(":core:core-logger"))
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
