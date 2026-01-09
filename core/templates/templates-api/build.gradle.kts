plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.templates.api"
}

dependencies {
    api(project(":core:resources"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
}
