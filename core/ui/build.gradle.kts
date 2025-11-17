plugins {
    id("core-compose-module")
}
dependencies {
    api(libs.bundles.lifecycle)
    api(libs.coil)
    api(libs.lottie)
    api(libs.splash.screen)
    api(projects.core.analytics)
}
