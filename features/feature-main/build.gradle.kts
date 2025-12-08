plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.main"
}

dependencies {
    api(project(":core:core-datastore"))
    api(project(":core:templates-api"))
    
    api(project(":features:feature-editor"))
    api(project(":features:feature-git"))
    api(project(":features:feature-treeview"))
    api(project(":features:feature-designer"))
}
