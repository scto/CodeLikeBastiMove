plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.templates.api"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
}
