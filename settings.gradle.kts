pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
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
include(":features")
include(":features:feature-home")
include(":features:feature-gallery")
include(":features:feature-slideshow")
include(":features:feature-settings")
include(":features:feature-treeview")
include(":features:feature-editor")
include(":core:core-datastore-proto")
include(":core:core-datastore")
include(":core:templates-api")
include(":core:templates-impl")
