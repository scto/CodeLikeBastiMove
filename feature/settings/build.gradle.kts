plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.settings"
}

dependencies {
    api(project(":core:datastore:datastore"))
    implementation(project(":core:auth"))
    implementation(project(":core:ui"))
}
