plugins {
    alias(libs.plugins.clbm.android.library)
}

android {
    namespace = "com.scto.codelikebastimove.core.utils"
}

dependencies {
    implementation(project(":core:logger"))
    implementation(project(":core:datastore:datastore"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.google.accompanist.permissions)
}
