plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.feature"
}

dependencies {
    api(project(":core:ui"))
    api(project(":core:resources"))
    
    api(project(":feature:home"))
    api(project(":feature:settings"))
    api(project(":feature:treeview"))
    api(project(":feature:soraeditor"))
    api(project(":feature:git"))
    api(project(":feature:onboarding"))
    api(project(":feature:main"))
    api(project(":feature:designer"))
    api(project(":feature:explorer"))
    api(project(":feature:auth"))
}
