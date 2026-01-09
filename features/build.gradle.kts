plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.features"
}

dependencies {
    api(project(":core:ui"))
    api(project(":core:resources"))
    
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
