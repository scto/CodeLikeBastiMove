package com.scto.codelikebastimove.feature.soraeditor.model

enum class EditorLanguageType(
    val displayName: String,
    val fileExtensions: List<String>,
    val textMateScopeName: String,
    val mimeType: String
) {
    JAVA(
        displayName = "Java",
        fileExtensions = listOf("java"),
        textMateScopeName = "source.java",
        mimeType = "text/x-java-source"
    ),
    KOTLIN(
        displayName = "Kotlin",
        fileExtensions = listOf("kt", "kts"),
        textMateScopeName = "source.kotlin",
        mimeType = "text/x-kotlin"
    ),
    XML(
        displayName = "XML",
        fileExtensions = listOf("xml"),
        textMateScopeName = "text.xml",
        mimeType = "application/xml"
    ),
    GRADLE_GROOVY(
        displayName = "Gradle (Groovy)",
        fileExtensions = listOf("gradle"),
        textMateScopeName = "source.groovy.gradle",
        mimeType = "text/x-groovy"
    ),
    GRADLE_KOTLIN(
        displayName = "Gradle (Kotlin DSL)",
        fileExtensions = listOf("gradle.kts"),
        textMateScopeName = "source.kotlin",
        mimeType = "text/x-kotlin"
    ),
    AIDL(
        displayName = "AIDL",
        fileExtensions = listOf("aidl"),
        textMateScopeName = "source.aidl",
        mimeType = "text/x-aidl"
    ),
    CPP(
        displayName = "C++",
        fileExtensions = listOf("cpp", "cc", "cxx", "c++", "hpp", "hh", "hxx", "h++", "h"),
        textMateScopeName = "source.cpp",
        mimeType = "text/x-c++src"
    ),
    C(
        displayName = "C",
        fileExtensions = listOf("c"),
        textMateScopeName = "source.c",
        mimeType = "text/x-csrc"
    ),
    MAKEFILE(
        displayName = "Makefile",
        fileExtensions = listOf("mk", "makefile", "mak"),
        textMateScopeName = "source.makefile",
        mimeType = "text/x-makefile"
    ),
    LOG(
        displayName = "Log",
        fileExtensions = listOf("log"),
        textMateScopeName = "text.log",
        mimeType = "text/plain"
    ),
    PROPERTIES(
        displayName = "Properties",
        fileExtensions = listOf("properties"),
        textMateScopeName = "source.ini",
        mimeType = "text/x-java-properties"
    ),
    JSON(
        displayName = "JSON",
        fileExtensions = listOf("json"),
        textMateScopeName = "source.json",
        mimeType = "application/json"
    ),
    PLAIN_TEXT(
        displayName = "Plain Text",
        fileExtensions = listOf("txt", "text"),
        textMateScopeName = "text.plain",
        mimeType = "text/plain"
    );

    companion object {
        fun fromFileName(fileName: String): EditorLanguageType {
            val lowerName = fileName.lowercase()
            
            if (lowerName == "makefile" || lowerName == "gnumakefile") {
                return MAKEFILE
            }
            
            if (lowerName.endsWith(".gradle.kts")) {
                return GRADLE_KOTLIN
            }
            
            val extension = lowerName.substringAfterLast('.', "")
            
            return entries.find { langType ->
                langType.fileExtensions.any { ext -> ext == extension }
            } ?: PLAIN_TEXT
        }
        
        fun fromExtension(extension: String): EditorLanguageType {
            val lowerExt = extension.lowercase().removePrefix(".")
            return entries.find { langType ->
                langType.fileExtensions.any { ext -> ext == lowerExt }
            } ?: PLAIN_TEXT
        }
    }
}
