plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.datastore"
}

dependencies {
    implementation(project(":core:core-logger"))
    api(project(":core:core-datastore-proto"))
    api(project(":core:core-resources"))
    
    api(libs.data.store.core)
    api(libs.data.store.preferences)
    api(libs.security.crypto)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
