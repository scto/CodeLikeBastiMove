plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.firebase.auth"
}

dependencies {
    implementation(project(":core:logger"))
    
    implementation(platform(libs.firebase.bom))
    api(libs.firebase.auth)
    
    api(libs.play.services.auth)
    api(libs.androidx.credentials)
    api(libs.credentials.play.services.auth)
    api(libs.identity.google.id)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
