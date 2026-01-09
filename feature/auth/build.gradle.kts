plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.auth"
}

dependencies {
    implementation(project(":core:logger"))
    implementation(project(":core:auth"))
    implementation(project(":core:ui"))
    
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
}
