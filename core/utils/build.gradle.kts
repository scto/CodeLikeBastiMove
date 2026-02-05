plugins {
    alias(libs.plugins.clbm.android.library)
}

android {
    namespace = "com.scto.codelikebastimove.core.utils"
}

dependencies {
    implementation(project(":core:logger"))
    implementation(libs.kotlinx.coroutines.core)
}
