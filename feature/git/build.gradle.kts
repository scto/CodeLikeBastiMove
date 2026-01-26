plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.git"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:resources"))
}
