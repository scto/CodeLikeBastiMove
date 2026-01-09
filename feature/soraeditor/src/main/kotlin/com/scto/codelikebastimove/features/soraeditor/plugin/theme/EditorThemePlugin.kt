package com.scto.codelikebastimove.feature.soraeditor.plugin.theme

import com.scto.codelikebastimove.core.plugin.api.extension.Extension
import com.scto.codelikebastimove.core.plugin.api.extension.ExtensionPointDescriptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface EditorThemePlugin : Extension {
    val themeName: String
    val themeType: EditorThemeType
    val version: String get() = "1.0.0"
    val author: String get() = ""
    val previewColors: EditorThemePreview
    
    fun getThemeDefinition(): EditorThemeDefinition
    
    fun apply(editor: Any)
    
    fun reset()
}

enum class EditorThemeType {
    LIGHT, DARK, HIGH_CONTRAST, HIGH_CONTRAST_LIGHT
}

data class EditorThemePreview(
    val background: String,
    val foreground: String,
    val accent: String,
    val selection: String
)

data class EditorThemeDefinition(
    val id: String,
    val name: String,
    val type: EditorThemeType,
    val colors: EditorColors,
    val tokenColors: List<TokenColorRule>,
    val semanticTokenColors: Map<String, String> = emptyMap()
)

data class EditorColors(
    val editorBackground: String,
    val editorForeground: String,
    val editorLineHighlight: String,
    val editorSelection: String,
    val editorSelectionHighlight: String,
    val editorCursor: String,
    val editorCursorForeground: String,
    val editorWhitespace: String,
    val editorIndentGuide: String,
    val editorLineNumber: String,
    val editorLineNumberActive: String,
    val editorGutterBackground: String,
    val editorGutterBorder: String,
    val editorBracketMatch: String,
    val editorFindMatch: String,
    val editorFindMatchHighlight: String,
    val editorWordHighlight: String,
    val editorErrorForeground: String,
    val editorWarningForeground: String,
    val editorInfoForeground: String,
    val editorHintForeground: String,
    val scrollbarSlider: String,
    val scrollbarSliderHover: String,
    val scrollbarSliderActive: String,
    val customColors: Map<String, String> = emptyMap()
)

data class TokenColorRule(
    val scope: List<String>,
    val settings: TokenColorSettings
)

data class TokenColorSettings(
    val foreground: String? = null,
    val background: String? = null,
    val fontStyle: FontStyle = FontStyle.NORMAL
)

enum class FontStyle {
    NORMAL, BOLD, ITALIC, BOLD_ITALIC, UNDERLINE
}

interface EditorThemeRegistry {
    val themes: StateFlow<List<EditorThemePlugin>>
    val currentTheme: StateFlow<EditorThemePlugin?>
    
    fun registerTheme(theme: EditorThemePlugin): Boolean
    
    fun unregisterTheme(themeId: String): Boolean
    
    fun getTheme(themeId: String): EditorThemePlugin?
    
    fun getThemes(): List<EditorThemePlugin>
    
    fun getThemesByType(type: EditorThemeType): List<EditorThemePlugin>
    
    fun setCurrentTheme(themeId: String): Boolean
    
    fun getCurrentTheme(): EditorThemePlugin?
}

abstract class AbstractEditorThemePlugin : EditorThemePlugin {
    override val description: String get() = "Editor theme: $themeName"
    override val priority: Int get() = 0
    
    abstract val themeDefinition: EditorThemeDefinition
    
    override val previewColors: EditorThemePreview
        get() = EditorThemePreview(
            background = themeDefinition.colors.editorBackground,
            foreground = themeDefinition.colors.editorForeground,
            accent = themeDefinition.colors.editorSelection,
            selection = themeDefinition.colors.editorSelection
        )
    
    override fun getThemeDefinition(): EditorThemeDefinition = themeDefinition
    
    override fun reset() {}
}

object EditorThemeExtensionPoint {
    val DESCRIPTOR = ExtensionPointDescriptor(
        id = "com.scto.clbm.extension.editorThemes",
        name = "Editor Themes",
        extensionClass = EditorThemePlugin::class,
        description = "Custom editor color themes and syntax highlighting schemes",
        allowMultiple = true
    )
}
