plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.editor"
}

dependencies {
    api(project(":features:feature-treeview"))
    api(project(":features:feature-git"))
    api(project(":core:templates-api"))
}
