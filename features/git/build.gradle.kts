plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.features.git"
}

dependencies {
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-resources"))
}
