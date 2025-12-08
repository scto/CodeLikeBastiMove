plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.slidingpanel"
}

dependencies {
    api(project(":core:core-datastore"))
    api(project(":features:feature-treeview"))
    api(project(":features:feature-designer"))
}
