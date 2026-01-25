plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.datastore"
}

dependencies {
    implementation(project(":core:logger"))
    api(project(":core:datastore:datastore-proto"))
    api(project(":core:resources"))
    
    api(libs.androidx.dataStore.core)
    api(libs.androidx.dataStore.preferences)
    api(libs.androidx.security.crypto)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
