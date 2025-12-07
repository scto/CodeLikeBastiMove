plugins {
    id("codelikebastimove.android.library")
    id("codelikebastimove.android.library.compose")
}

android {
    namespace = "com.scto.codelikebastimove.core.ui"
}

dependencies {
    api(project(":core:core-resources"))
    
    api(platform(libs.compose.bom))
    api(libs.bundles.compose)
    api(libs.bundles.lifecycle)
    api(libs.activity.compose)
    
    implementation(libs.androidx.core.ktx)
    
    debugImplementation(libs.compose.ui.tooling)
}
