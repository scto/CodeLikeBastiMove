plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.onboarding"
}

dependencies {
    api(project(":core:core-datastore"))
    
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
}
