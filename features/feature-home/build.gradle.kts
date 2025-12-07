plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.home"
}

dependencies {
    api(project(":core:templates-api"))
    api(project(":core:templates-impl"))
    api(project(":core:core-datastore"))
}
