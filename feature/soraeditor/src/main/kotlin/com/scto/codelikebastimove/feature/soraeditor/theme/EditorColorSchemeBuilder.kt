package com.scto.codelikebastimove.feature.soraeditor.theme

import androidx.core.graphics.toColorInt
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import java.lang.reflect.Modifier

data class EditorColor(val key: Int, val color: Int)

private val EDITOR_COLOR_MAPPING: Map<String, Int> by lazy { getEditorColorSchemeMapping() }

private fun String.toColorIntSafe(): Int = runCatching {
    if (this == "0") return 0
    toColorInt()
}.getOrElse { -1 }

fun mapEditorColorScheme(rawScheme: Map<String, String>?): List<EditorColor> {
    if (rawScheme.isNullOrEmpty()) return emptyList()

    val result = mutableListOf<EditorColor>()

    rawScheme.forEach { (rawKey, hexColor) ->
        val normalizedKey = rawKey.lowercase().trim()
        val editorKey = EDITOR_COLOR_MAPPING[normalizedKey]

        val colorInt = hexColor.toColorIntSafe()
        if (editorKey != null && colorInt != -1) {
            result.add(EditorColor(editorKey, colorInt))
        }
    }

    return result
}

fun getEditorColorSchemeMapping(): Map<String, Int> {
    return EditorColorScheme::class.java.declaredFields
        .filter { field ->
            Modifier.isPublic(field.modifiers) &&
                Modifier.isStatic(field.modifiers) &&
                Modifier.isFinal(field.modifiers) &&
                field.type == Int::class.javaPrimitiveType
        }
        .onEach { it.isAccessible = true }
        .associate { field -> field.name.lowercase() to (field.get(null) as Int) }
}

fun EditorColorScheme.applyEditorColors(colors: List<EditorColor>) {
    colors.forEach { (key, color) ->
        setColor(key, color)
    }
}

fun createColorSchemeFromMap(baseScheme: EditorColorScheme, colors: Map<String, String>): EditorColorScheme {
    val editorColors = mapEditorColorScheme(colors)
    editorColors.forEach { (key, color) ->
        baseScheme.setColor(key, color)
    }
    return baseScheme
}
