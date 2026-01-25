plugins {
    id("clbm.android.library")
    id("clbm.android.compose")
}

android {
    namespace = "com.scto.codelikebastimove.core.ui"
}

dependencies {
    api(project(":core:resources"))
    
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewModelCompose)
    api(libs.androidx.activity.compose)
    
    implementation(libs.androidx.core.ktx)
    
    debugImplementation(libs.androidx.compose.ui.tooling)
}
