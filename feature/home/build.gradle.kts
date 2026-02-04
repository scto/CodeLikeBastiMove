plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.home"
}

dependencies {
    implementation(project(":core:logger"))
    api(project(":core:datastore:datastore"))
    api(project(":core:templates:templates-api"))
    implementation(project(":core:templates:templates-impl"))
    
    implementation(project(":feature:submodulemaker"))
}
