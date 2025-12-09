plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.onboarding"
}

dependencies {
    implementation(project(":core:core-logger"))
    api(project(":core:core-datastore"))
    
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
}
