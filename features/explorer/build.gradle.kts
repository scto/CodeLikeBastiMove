plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.explorer"
}

dependencies {
    api(project(":core:datastore:datastore"))
}
