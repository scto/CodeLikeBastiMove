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
    }
}

rootProject.name = "CodeLikeBastiMove"

include(":app")

include(":core:core-datastore")
include(":core:core-datastore-proto")
include(":core:core-resources")
include(":core:templates-api")
include(":core:templates-impl")
include(":core:core-ui")

include(":features")
include(":features:feature-editor")
//include(":features:feature-gallery")
include(":features:feature-git")
include(":features:feature-home")
include(":features:feature-onboarding")
include(":features:feature-settings")
//include(":features:feature-slideshow")
include(":features:feature-treeview")

