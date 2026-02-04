plugins {
    id("clbm.android.library")
}

android {
    namespace = "com.scto.codelikebastimove.feature"
}

dependencies {
    /* Core Modules */
    api(project(":core:resources"))
    api(project(":core:ui"))

    /* Feature Modules */
    api(project(":feature:assetstudio"))
    api(project(":feature:auth"))
    api(project(":feature:designer"))
    api(project(":feature:explorer"))
    api(project(":feature:git"))
    // feature:home removed - functionality moved to core:project-manager and feature:git
    api(project(":feature:main"))
    api(project(":feature:onboarding"))
    api(project(":feature:settings"))
    api(project(":feature:soraeditor"))
    api(project(":feature:submodulemaker"))
    api(project(":feature:themebuilder"))
    api(project(":feature:treeview"))
}
