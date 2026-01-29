plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.common"
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:resources"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.google.material)
    
    
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.security.crypto)


    implementation(libs.google.gson)

    implementation(libs.common.utilcode)
    implementation(libs.termux.app.termux.shared)
    
}
