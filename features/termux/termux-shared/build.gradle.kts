plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.termux.shared"
}

dependencies {
    implementation(project(":core:core-logger"))
    
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation(libs.androidx.annotation)
}
