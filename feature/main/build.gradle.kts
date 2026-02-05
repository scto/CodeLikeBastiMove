plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.main"
}

dependencies {
    implementation(project(":core:logger"))
    implementation(project(":core:utils"))
    api(project(":core:datastore:datastore"))
    api(project(":core:templates:templates-api"))
    implementation(project(":core:templates:templates-impl"))
    api(project(":core:project-manager"))
    
    api(project(":feature:soraeditor"))
    api(project(":feature:git"))
    api(project(":feature:treeview"))
    api(project(":feature:designer"))
    api(project(":feature:explorer"))
    api(project(":feature:assetstudio"))
    api(project(":feature:themebuilder"))
    api(project(":feature:submodulemaker"))
    api(project(":feature:settings"))
}
