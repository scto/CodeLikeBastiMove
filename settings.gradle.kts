pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // NEU: JitPack Repository hinzufügen, um die TreeView-Bibliothek zu laden
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "CodeLikeBastiMove"

include(":app")

include(":core:core-datastore")
include(":core:core-datastore-proto")
include(":core:core-logger")
include(":core:core-resources")
include(":core:core-ui")
include(":core:templates-api")
include(":core:templates-impl")


include(":features")
include(":features:feature-designer")
include(":features:feature-editor")
include(":features:feature-explorer")
include(":features:feature-git")
include(":features:feature-home")
include(":features:feature-main")
include(":features:feature-onboarding")
include(":features:feature-settings")
//include(":features:feature-slidingpanel")
include(":features:feature-submodulemaker")
include(":features:feature-themebuilder")
include(":features:feature-treeview")
