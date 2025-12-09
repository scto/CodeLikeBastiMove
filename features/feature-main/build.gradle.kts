plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.main"
}

dependencies {
    implementation(project(":core:core-logger"))
    api(project(":core:core-datastore"))
    api(project(":core:templates-api"))
    implementation(project(":core:templates-impl"))
    
    api(project(":features:feature-editor"))
    api(project(":features:feature-git"))
    api(project(":features:feature-treeview"))
    api(project(":features:feature-designer"))
    api(project(":features:feature-explorer"))
    api(project(":features:feature-slidingpanel"))
}
