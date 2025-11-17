plugins {
    id("core-module")
    id("com.google.devtools.ksp")
}

android.defaultConfig {
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

android.buildFeatures.buildConfig = true

dependencies {
    api(libs.room)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}
