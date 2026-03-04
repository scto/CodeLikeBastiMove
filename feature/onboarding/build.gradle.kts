plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.onboarding"
}

dependencies {
    implementation(project(":core:logger"))
    api(project(":core:datastore:datastore"))
    api(project(":core:utils"))

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
}
