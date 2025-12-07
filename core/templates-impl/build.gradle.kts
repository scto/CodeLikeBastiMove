plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.templates.impl"
}

dependencies {
    api(project(":core:templates-api"))
    api(project(":core:core-resources"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
