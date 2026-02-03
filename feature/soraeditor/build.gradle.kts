plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.soraeditor"
}

dependencies {
    /* Project Modules */
    implementation(project(":core:actions:actions-api"))
    implementation(project(":core:logger"))
    implementation(project(":core:plugin:plugin-api"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))

    /* Rosemoe Sora Editor - Common */
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.5"))
    implementation(libs.common.editor)
    implementation(libs.common.editor.textmate)
    implementation(libs.common.editor.treesitter)

    /* Android Tree-Sitter (AndroidIDE) */
    implementation(libs.androidide.ts)
    runtimeOnly("com.itsaky.androidide.treesitter:annotations:4.3.2") // Oft nicht im Version Catalog, daher sicherheitshalber explizit oder via libs prüfen

    // Languages
    implementation(libs.androidide.ts.java)
    implementation(libs.androidide.ts.kotlin)
    implementation(libs.androidide.ts.json)
    implementation(libs.androidide.ts.xml)
    implementation(libs.androidide.ts.cpp)
    implementation(libs.androidide.ts.log)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}
