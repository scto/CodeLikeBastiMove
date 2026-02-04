package com.scto.codelikebastimove.feature.themebuilder

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.feature.themebuilder.export.ThemeFileExporter
import com.scto.codelikebastimove.feature.themebuilder.generator.GeneratedTheme
import com.scto.codelikebastimove.feature.themebuilder.generator.ThemeCodeGenerator
import com.scto.codelikebastimove.feature.themebuilder.generator.MaterialColorGenerator
import com.scto.codelikebastimove.feature.themebuilder.generator.SchemeStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ThemeBuilderUiState(
    val themeName: String = "AppTheme",
    val packageName: String = "com.example.app.ui.theme",
    val seedColor: Color = Color(0xFF6750A4),
    val schemeStyle: SchemeStyle = SchemeStyle.TONAL_SPOT,
    val generatedTheme: GeneratedTheme? = null,
    val isDarkPreview: Boolean = false,
    val dynamicColorEnabled: Boolean = false,
    val extractedColors: List<Color> = emptyList(),
    val selectedTab: ThemeBuilderTab = ThemeBuilderTab.COLORS,
    val showColorPicker: Boolean = false,
    val showExportDialog: Boolean = false,
    val exportFormat: ExportFormat = ExportFormat.COMPOSE,
    val projectUri: Uri? = null,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean? = null,
    val exportMessage: String = "",
    val generatedCode: String = "",
    val showCodePreview: Boolean = false,
    val codePreviewType: CodePreviewType = CodePreviewType.COMPOSE_COLOR,
)

enum class ThemeBuilderTab(val title: String) {
    COLORS("Colors"),
    PALETTES("Palettes"),
    PREVIEW("Preview"),
    EXPORT("Export"),
}

enum class ExportFormat(val displayName: String) {
    COMPOSE("Compose Theme"),
    ANDROID_XML("Android XML"),
    BOTH("Both"),
}

enum class CodePreviewType(val displayName: String) {
    COMPOSE_COLOR("Color.kt"),
    COMPOSE_THEME("Theme.kt"),
    XML_COLORS("colors.xml"),
    XML_THEMES("themes.xml"),
    XML_THEMES_NIGHT("themes.xml (night)"),
}

class ThemeBuilderViewModel : ViewModel() {

    private val colorGenerator = MaterialColorGenerator()

    private val _uiState = MutableStateFlow(ThemeBuilderUiState())
    val uiState: StateFlow<ThemeBuilderUiState> = _uiState.asStateFlow()

    init {
        generateTheme()
    }

    fun setThemeName(name: String) {
        _uiState.update { it.copy(themeName = name) }
    }

    fun setPackageName(packageName: String) {
        _uiState.update { it.copy(packageName = packageName) }
    }

    fun setSeedColor(color: Color) {
        _uiState.update { it.copy(seedColor = color) }
        generateTheme()
    }

    fun setSchemeStyle(style: SchemeStyle) {
        _uiState.update { it.copy(schemeStyle = style) }
        generateTheme()
    }

    fun toggleDarkPreview() {
        _uiState.update { it.copy(isDarkPreview = !it.isDarkPreview) }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        _uiState.update { it.copy(dynamicColorEnabled = enabled) }
    }

    fun setSelectedTab(tab: ThemeBuilderTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun showColorPicker() {
        _uiState.update { it.copy(showColorPicker = true) }
    }

    fun hideColorPicker() {
        _uiState.update { it.copy(showColorPicker = false) }
    }

    fun showExportDialog() {
        _uiState.update { it.copy(showExportDialog = true) }
    }

    fun hideExportDialog() {
        _uiState.update { it.copy(showExportDialog = false, exportSuccess = null) }
    }

    fun setExportFormat(format: ExportFormat) {
        _uiState.update { it.copy(exportFormat = format) }
    }

    fun setProjectUri(uri: Uri) {
        _uiState.update { it.copy(projectUri = uri) }
    }

    fun showCodePreview(type: CodePreviewType) {
        val theme = _uiState.value.generatedTheme ?: return
        val packageName = _uiState.value.packageName
        val themeName = _uiState.value.themeName

        viewModelScope.launch {
            val code = withContext(Dispatchers.Default) {
                when (type) {
                    CodePreviewType.COMPOSE_COLOR -> ThemeCodeGenerator.generateColorKt(theme, packageName)
                    CodePreviewType.COMPOSE_THEME -> ThemeCodeGenerator.generateThemeKt(packageName)
                    CodePreviewType.XML_COLORS -> ThemeCodeGenerator.generateColorsXml(theme)
                    CodePreviewType.XML_THEMES -> ThemeCodeGenerator.generateThemesXml(theme, themeName)
                    CodePreviewType.XML_THEMES_NIGHT -> ThemeCodeGenerator.generateNightThemesXml(theme, themeName)
                }
            }

            _uiState.update {
                it.copy(
                    generatedCode = code,
                    showCodePreview = true,
                    codePreviewType = type,
                )
            }
        }
    }

    fun hideCodePreview() {
        _uiState.update { it.copy(showCodePreview = false) }
    }

    fun exportTheme(context: Context) {
        val state = _uiState.value
        val theme = state.generatedTheme ?: return
        val projectUri = state.projectUri

        if (projectUri == null) {
            _uiState.update {
                it.copy(
                    exportSuccess = false,
                    exportMessage = "Please select a project directory",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }

            val exporter = ThemeFileExporter(context)
            val errors = mutableListOf<String>()
            val exportedFiles = mutableListOf<String>()

            when (state.exportFormat) {
                ExportFormat.COMPOSE -> {
                    val result = exporter.exportComposeThemeFiles(
                        theme = theme,
                        projectUri = projectUri,
                        packageName = state.packageName,
                    )
                    exportedFiles.addAll(result.exportedFiles)
                    errors.addAll(result.errors)
                }
                ExportFormat.ANDROID_XML -> {
                    val result = exporter.exportToAndroidProject(
                        theme = theme,
                        projectUri = projectUri,
                        themeName = state.themeName,
                        packageName = state.packageName,
                    )
                    exportedFiles.addAll(result.exportedFiles)
                    errors.addAll(result.errors)
                }
                ExportFormat.BOTH -> {
                    val composeResult = exporter.exportComposeThemeFiles(
                        theme = theme,
                        projectUri = projectUri,
                        packageName = state.packageName,
                    )
                    val xmlResult = exporter.exportToAndroidProject(
                        theme = theme,
                        projectUri = projectUri,
                        themeName = state.themeName,
                        packageName = state.packageName,
                    )
                    exportedFiles.addAll(composeResult.exportedFiles)
                    exportedFiles.addAll(xmlResult.exportedFiles)
                    errors.addAll(composeResult.errors)
                    errors.addAll(xmlResult.errors)
                }
            }

            _uiState.update {
                it.copy(
                    isExporting = false,
                    exportSuccess = errors.isEmpty(),
                    exportMessage = if (errors.isEmpty()) {
                        "Exported ${exportedFiles.size} files successfully"
                    } else {
                        "Errors: ${errors.joinToString(", ")}"
                    },
                )
            }
        }
    }

    fun extractColorsFromImage(bitmap: Bitmap) {
        viewModelScope.launch {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

            val colors = withContext(Dispatchers.Default) {
                colorGenerator.extractColorsFromImage(pixels, bitmap.width, bitmap.height)
            }

            _uiState.update { it.copy(extractedColors = colors) }

            if (colors.isNotEmpty()) {
                setSeedColor(colors.first())
            }
        }
    }

    fun selectPresetColor(color: Color) {
        setSeedColor(color)
    }

    private fun generateTheme() {
        viewModelScope.launch {
            val state = _uiState.value
            val theme = withContext(Dispatchers.Default) {
                colorGenerator.generateTheme(state.seedColor, state.schemeStyle)
            }
            _uiState.update { it.copy(generatedTheme = theme) }
        }
    }

    fun clearExportResult() {
        _uiState.update { it.copy(exportSuccess = null, exportMessage = "") }
    }

    companion object {
        val presetColors = listOf(
            Color(0xFF6750A4),
            Color(0xFFD32F2F),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFF673AB7),
            Color(0xFF3F51B5),
            Color(0xFF2196F3),
            Color(0xFF03A9F4),
            Color(0xFF00BCD4),
            Color(0xFF009688),
            Color(0xFF4CAF50),
            Color(0xFF8BC34A),
            Color(0xFFCDDC39),
            Color(0xFFFFEB3B),
            Color(0xFFFFC107),
            Color(0xFFFF9800),
            Color(0xFFFF5722),
            Color(0xFF795548),
            Color(0xFF607D8B),
        )
    }
}
