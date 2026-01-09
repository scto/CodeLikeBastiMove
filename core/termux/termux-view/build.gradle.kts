plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.termux.view"
}

dependencies {
    api(project(":core:termux:termux-emulator"))
    
    implementation("androidx.annotation:annotation:1.7.1")
    implementation(libs.androidx.core.ktx)
}
