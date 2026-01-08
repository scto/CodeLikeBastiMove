plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.termux.view"
}

dependencies {
    api(project(":features:termux:termux-emulator"))
    
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)
}
