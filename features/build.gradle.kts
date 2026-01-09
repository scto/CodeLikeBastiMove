plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.features"
}

dependencies {
    api(project(":core:core-ui"))
    api(project(":core:core-resources"))
    
    api(project(":features:home"))
    api(project(":features:settings"))
    api(project(":features:treeview"))
    api(project(":features:soraeditor"))
    api(project(":features:git"))
    api(project(":features:onboarding"))
    api(project(":features:main"))
    api(project(":features:designer"))
    api(project(":features:explorer"))
}
