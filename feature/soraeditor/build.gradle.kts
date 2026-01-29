plugins {
    id("clbm.android.feature")
}

android {
    namespace = "com.scto.codelikebastimove.feature.soraeditor"
}

dependencies {
    /* Project Modules */
    implementation(project(":core:actions:actions-api"))
    implementation(project(":core:common"))
    implementation(project(":core:logger"))
    implementation(project(":core:plugin:plugin-api"))
    implementation(project(":core:resources"))
    implementation(project(":core:ui"))
    
    /* Rosemore Sora Editor */
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.4"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")
    implementation("io.github.Rosemoe.sora-editor:language-treesitter")
    
    /* Android Tree-Sitter */
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/android-tree-sitter
    implementation("com.itsaky.androidide.treesitter:android-tree-sitter:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/annotations
    runtimeOnly("com.itsaky.androidide.treesitter:annotations:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-aidl
    implementation("com.itsaky.androidide.treesitter:tree-sitter-aidl:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-c
    implementation("com.itsaky.androidide.treesitter:tree-sitter-c:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-cpp
    implementation("com.itsaky.androidide.treesitter:tree-sitter-cpp:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-java
    implementation("com.itsaky.androidide.treesitter:tree-sitter-java:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-json
    implementation("com.itsaky.androidide.treesitter:tree-sitter-json:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-kotlin
    implementation("com.itsaky.androidide.treesitter:tree-sitter-kotlin:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-log
    implementation("com.itsaky.androidide.treesitter:tree-sitter-log:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-properties
    implementation("com.itsaky.androidide.treesitter:tree-sitter-properties:4.3.2")
    // Source: https://mvnrepository.com/artifact/com.itsaky.androidide.treesitter/tree-sitter-xml
    implementation("com.itsaky.androidide.treesitter:tree-sitter-xml:4.3.2")

    implementation(libs.kotlinx.coroutines.android)
}