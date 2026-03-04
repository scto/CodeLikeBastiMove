plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.buildvariants"
}

dependencies {
    implementation(libs.androidx.documentfile)
}
