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

include(":core:actions:actions-api")
include(":core:actions:actions-impl")
include(":core:core-datastore")
include(":core:core-datastore-proto")
include(":core:core-logger")
include(":core:core-resources")
include(":core:core-ui")
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

include(":features")
include(":features:feature-designer")
include(":features:feature-explorer")
include(":features:feature-home")
include(":features:feature-main")
include(":features:feature-onboarding")
include(":features:feature-settings")
//include(":features:feature-slidingpanel")
include(":features:feature-assetstudio")
include(":features:feature-soraeditor")
include(":features:submodulemaker")
include(":features:feature-themebuilder")
include(":features:treeview")

include(":features:git")
