plugins {
    id("codelikebastimove.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.features"
}

dependencies {
    api(project(":core:core-ui"))
    api(project(":core:core-resources"))
    
    api(project(":features:feature-home"))
    api(project(":features:feature-settings"))
    api(project(":features:treeview"))
    api(project(":features:feature-soraeditor"))
    api(project(":features:git"))
    api(project(":features:feature-onboarding"))
    api(project(":features:feature-main"))
    api(project(":features:feature-designer"))
    api(project(":features:feature-explorer"))
    //api(project(":features:feature-slidingpanel"))
}
