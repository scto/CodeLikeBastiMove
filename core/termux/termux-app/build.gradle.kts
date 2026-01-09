plugins {
    id("codelikebastimove.android.library")
    id("codelikebastimove.android.library.compose")
}

android {
    namespace = "com.termux.app"
}

dependencies {
    api(project(":core:termux:termux-shared"))
    api(project(":core:termux:termux-view"))
    api(project(":core:termux:termux-emulator"))
    
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-resources"))
    implementation(project(":core:core-logger"))
    
    implementation("androidx.annotation:annotation:1.7.1")
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.7.0")
}
