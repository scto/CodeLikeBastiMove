plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.explorer"
}

dependencies {
    api(project(":core:core-datastore"))
}
