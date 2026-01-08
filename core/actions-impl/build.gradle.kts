plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.actions.impl"
}

dependencies {
    implementation(project(":core:actions-api"))
    implementation(project(":core:core-logger"))
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
