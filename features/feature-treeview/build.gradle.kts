plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.treeview"
}

dependencies {
    api(project(":core:templates-api"))
}
