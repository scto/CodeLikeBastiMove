plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.designer"
}

dependencies {
    api(project(":core:datastore:datastore"))
    api(project(":features:soraeditor"))
    
    implementation(libs.androidx.documentfile)
}
