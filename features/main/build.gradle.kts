plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.main"
}

dependencies {
    implementation(project(":core:core-logger"))
    api(project(":core:core-datastore"))
    api(project(":core:templates:templates-api"))
    implementation(project(":core:templates:templates-impl"))
    
    api(project(":features:soraeditor"))
    api(project(":features:git"))
    api(project(":features:treeview"))
    api(project(":features:designer"))
    api(project(":features:explorer"))
    api(project(":features:assetstudio"))
    api(project(":features:themebuilder"))
    api(project(":features:submodulemaker"))
}
