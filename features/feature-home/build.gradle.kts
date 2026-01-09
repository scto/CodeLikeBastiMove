plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.home"
}

dependencies {
    api(project(":core:templates:templates-api"))
    api(project(":core:templates:templates-impl"))
    api(project(":core:core-datastore"))
}
