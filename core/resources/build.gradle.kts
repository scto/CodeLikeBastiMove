plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.resources"
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.nav.fragment)
    implementation(libs.androidx.nav.ui)
    implementation(libs.androidx.preference)
    implementation(libs.google.material)
}