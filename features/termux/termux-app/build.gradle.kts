plugins {
    id("codelikebastimove.android.library")
    id("codelikebastimove.android.library.compose")
}

android {
    namespace = "com.termux.app"
}

dependencies {
    api(project(":features:termux:termux-shared"))
    api(project(":features:termux:termux-view"))
    api(project(":features:termux:termux-emulator"))
    
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-resources"))
    implementation(project(":core:core-logger"))
    
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
