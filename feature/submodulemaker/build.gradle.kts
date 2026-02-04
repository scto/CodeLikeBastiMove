plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.submodulemaker"
}

dependencies {
    implementation(libs.androidx.documentfile)
}
