plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.treeview"
}

dependencies {
    api(project(":core:templates:templates-api"))
    implementation(project(":core:resources"))

    implementation(project(":feature:buildvariants"))
    implementation(project(":feature:submodulemaker"))

}
