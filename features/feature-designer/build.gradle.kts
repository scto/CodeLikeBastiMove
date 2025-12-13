plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.designer"
}

dependencies {
    api(project(":core:core-datastore"))
    api(project(":features:feature-editor"))
    
    implementation("androidx.documentfile:documentfile:1.1.0")
}
