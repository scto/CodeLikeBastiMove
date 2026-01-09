plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.onboarding"
}

dependencies {
    implementation(project(":core:logger"))
    api(project(":core:datastore:datastore"))
    
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
}
