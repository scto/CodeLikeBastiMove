plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.plugin.impl"
}

dependencies {
    api(project(":core:plugin:plugin-api"))
    implementation(project(":core:logger"))
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
}
