plugins {
    id("clbm.android.library")
    id("clbm.android.compose")
}

android {
    namespace = "com.scto.codelikebastimove.core.projectmanager"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:resources"))
    implementation(project(":core:templates:templates-api"))
    implementation(project(":core:datastore:datastore"))
}
