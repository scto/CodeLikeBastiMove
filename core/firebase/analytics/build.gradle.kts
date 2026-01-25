plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.firebase.analytics"
}

dependencies {
    implementation(project(":core:logger"))
    
    implementation(platform(libs.firebase.bom))
    api(libs.firebase.analytics)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
