plugins {
    id("codelikebastimove.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.settings"
}

dependencies {
    api(project(":core:core-datastore"))
}
