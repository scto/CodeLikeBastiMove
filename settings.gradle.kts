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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "CodeLikeBastiMove"

include(":app")

include(":core:actions:actions-api")
include(":core:actions:actions-impl")
include(":core:datastore:datastore")
include(":core:datastore:datastore-proto")
include(":core:logger")
include(":core:resources")
include(":core:ui")
include(":core:plugin:plugin-api")
include(":core:plugin:plugin-impl")
include(":core:templates:templates-api")
include(":core:templates:templates-impl")
include(":core:tooling:tooling-api")
include(":core:tooling:tooling-impl")
include(":core:termux:termux-shared")
include(":core:termux:termux-emulator")
include(":core:termux:termux-view")
include(":core:termux:termux-app")
include(":core:auth")

include(":feature")
include(":feature:assetstudio")
include(":feature:designer")
include(":feature:explorer")
include(":feature:home")
include(":feature:main")
include(":feature:onboarding")
include(":feature:settings")
include(":feature:soraeditor")
include(":feature:submodulemaker")
include(":feature:themebuilder")
include(":feature:treeview")
include(":feature:git")
include(":feature:auth")
