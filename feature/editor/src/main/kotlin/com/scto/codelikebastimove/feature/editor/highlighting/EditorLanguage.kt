package com.scto.codelikebastimove.feature.editor.highlighting

import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.LanguageAnalyzer
import io.github.rosemoe.sora.lang.empty.EmptyLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorAutoCompleteWindow
import io.github.rosemoe.sora.widget.analyze.CodeAnalyzer
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import io.github.rosemoe.sora.widget.TextEditor

/**
 * Enum representing supported editor languages.
 * Each language defines how it sets up itself for the [CodeEditor].
 */
enum class EditorLanguage(
    val languageId: String,
    val displayName: String,
    val fileExtensions: List<String>,
) {
    PlainText("plaintext", "Plain Text", listOf("txt")),
    Kotlin("kotlin", "Kotlin", listOf("kt", "kts")),
    Java("java", "Java", listOf("java")),
    Xml("xml", "XML", listOf("xml", "html", "htm", "xhtml")),
    JavaScript("javascript", "JavaScript", listOf("js")),
    Json("json", "JSON", listOf("json")),
    Markdown("markdown", "Markdown", listOf("md", "markdown")),
    Python("python", "Python", listOf("py")),
    Shell("shell", "Shell", listOf("sh", "bash")),
    Gradle("gradle", "Gradle", listOf("gradle")),
    Cpp("cpp", "C++", listOf("cpp", "cxx", "c", "h", "hpp")),
    Rust("rust", "Rust", listOf("rs")),
    Dart("dart", "Dart", listOf("dart")),
    Yaml("yaml", "YAML", listOf("yaml", "yml")),
    Properties("properties", "Properties", listOf("properties")),
    Groovy("groovy", "Groovy", listOf("groovy")),
    Php("php", "PHP", listOf("php")),
    Go("go", "Go", listOf("go")),
    Swift("swift", "Swift", listOf("swift")),
    TypeScript("typescript", "TypeScript", listOf("ts")),
    Css("css", "CSS", listOf("css")),
    Html("html", "HTML", listOf("html", "htm")),
    Sql("sql", "SQL", listOf("sql"));

    /**
     * Sets up the language for the given [TextEditor].
     * This typically involves setting the [TextEditor.getEditorLanguage] and
     * potentially registering analyzers.
     */
    fun setup(editor: CodeEditor?) {
        editor ?: return
        when (this) {
            // Register TextMate or TreeSitter languages here.
            // For now, we use a simple EmptyLanguage as a placeholder or specific simple ones.
            PlainText -> editor.setEditorLanguage(EmptyLanguage())
            // Example for Kotlin (assuming TextMate grammar is registered via EditorUtils)
            Kotlin -> EditorUtils.GrammarRegistry.getLanguage("source.kotlin")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Java -> EditorUtils.GrammarRegistry.getLanguage("source.java")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Xml -> EditorUtils.GrammarRegistry.getLanguage("text.xml")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            JavaScript -> EditorUtils.GrammarRegistry.getLanguage("source.js")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Json -> EditorUtils.GrammarRegistry.getLanguage("source.json")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Markdown -> EditorUtils.GrammarRegistry.getLanguage("source.gfm")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Python -> EditorUtils.GrammarRegistry.getLanguage("source.python")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Shell -> EditorUtils.GrammarRegistry.getLanguage("source.shell")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Gradle -> EditorUtils.GrammarRegistry.getLanguage("source.gradle")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Cpp -> EditorUtils.GrammarRegistry.getLanguage("source.cpp")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Rust -> EditorUtils.GrammarRegistry.getLanguage("source.rust")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Dart -> EditorUtils.GrammarRegistry.getLanguage("source.dart")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Yaml -> EditorUtils.GrammarRegistry.getLanguage("source.yaml")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Properties -> EditorUtils.GrammarRegistry.getLanguage("source.properties")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Groovy -> EditorUtils.GrammarRegistry.getLanguage("source.groovy")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Php -> EditorUtils.GrammarRegistry.getLanguage("source.php")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Go -> EditorUtils.GrammarRegistry.getLanguage("source.go")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Swift -> EditorUtils.GrammarRegistry.getLanguage("source.swift")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            TypeScript -> EditorUtils.GrammarRegistry.getLanguage("source.ts")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Css -> EditorUtils.GrammarRegistry.getLanguage("source.css")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Html -> EditorUtils.GrammarRegistry.getLanguage("text.html.basic")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            Sql -> EditorUtils.GrammarRegistry.getLanguage("source.sql")?.let { editor.setEditorLanguage(it) } ?: editor.setEditorLanguage(EmptyLanguage())
            else -> editor.setEditorLanguage(EmptyLanguage())
        }
    }

    companion object {
        fun fromFileName(fileName: String): EditorLanguage {
            val extension = fileName.substringAfterLast('.', "").lowercase()
            return entries.find { lang -> lang.fileExtensions.contains(extension) } ?: PlainText
        }
    }
}
