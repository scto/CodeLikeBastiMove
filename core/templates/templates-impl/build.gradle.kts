plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.core.templates.impl"
}

dependencies {
    implementation(project(":core:logger"))
    implementation(project(":core:datastore:datastore"))
    api(project(":core:templates:templates-api"))
    api(project(":core:resources"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
