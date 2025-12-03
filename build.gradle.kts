// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false version "8.11.1"
    id("com.android.library") apply false version "8.11.1"
    id("org.jetbrains.kotlin.android") apply false version "2.0.0"
    id("org.jetbrains.kotlin.plugin.compose") apply false version "2.0.0"
    id("com.google.protobuf") apply false version "0.9.5"
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
