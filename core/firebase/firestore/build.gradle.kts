plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.firebase.firestore"
}

dependencies {
    implementation(project(":core:logger"))
    
    implementation(platform(libs.firebase.bom))
    api(libs.firebase.firestore)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
