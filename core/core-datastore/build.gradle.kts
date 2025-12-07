plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.datastore"
}

dependencies {
    api(project(":core:core-datastore-proto"))
    
    api(libs.data.store.core)
    api(libs.data.store.preferences)
    api(libs.security.crypto)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
