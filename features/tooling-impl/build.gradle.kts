plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.features.tooling.impl"
}

dependencies {
    api(project(":features:tooling-api"))
    api(project(":features:termux:termux-app"))
    
    implementation(project(":core:core-logger"))
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
