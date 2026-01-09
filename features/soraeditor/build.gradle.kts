plugins {
    id("codelikebastimove.android.library")
    id("codelikebastimove.android.library.compose")
}

android {
    namespace = "com.scto.codelikebastimove.features.soraeditor"
}

dependencies {
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-resources"))
    implementation(project(":core:core-logger"))
    implementation(project(":core:plugin:plugin-api"))
    implementation(project(":core:actions:actions-api"))
    
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.23.4"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")
    implementation("io.github.Rosemoe.sora-editor:language-treesitter")
    
    implementation("io.github.AaronNakanwormo.android-tree-sitter:android-tree-sitter:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-java:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-kotlin:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-xml:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-c:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-cpp:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-make:4.1.0")
    implementation("io.github.AaronNakanwormo.android-tree-sitter:tree-sitter-json:4.1.0")
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
