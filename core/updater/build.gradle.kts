plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.updater"
}

dependencies {
    implementation(project(":core:datastore:datastore"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    
    implementation(libs.okhttp.core)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
}
