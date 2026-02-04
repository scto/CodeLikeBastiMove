package com.scto.codelikebastimove.feature.designer.data.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

sealed class ComposeNodeType(val name: String) {
    data object Column : ComposeNodeType("Column")
    data object Row : ComposeNodeType("Row")
    data object Box : ComposeNodeType("Box")
    data object Card : ComposeNodeType("Card")
    data object Surface : ComposeNodeType("Surface")
    data object Scaffold : ComposeNodeType("Scaffold")
    data object LazyColumn : ComposeNodeType("LazyColumn")
    data object LazyRow : ComposeNodeType("LazyRow")
    data object Text : ComposeNodeType("Text")
    data object TextField : ComposeNodeType("TextField")
    data object OutlinedTextField : ComposeNodeType("OutlinedTextField")
    data object Button : ComposeNodeType("Button")
    data object OutlinedButton : ComposeNodeType("OutlinedButton")
    data object TextButton : ComposeNodeType("TextButton")
    data object IconButton : ComposeNodeType("IconButton")
    data object FloatingActionButton : ComposeNodeType("FloatingActionButton")
    data object ExtendedFloatingActionButton : ComposeNodeType("ExtendedFloatingActionButton")
    data object Icon : ComposeNodeType("Icon")
    data object Image : ComposeNodeType("Image")
    data object AsyncImage : ComposeNodeType("AsyncImage")
    data object TopAppBar : ComposeNodeType("TopAppBar")
    data object CenterAlignedTopAppBar : ComposeNodeType("CenterAlignedTopAppBar")
    data object BottomAppBar : ComposeNodeType("BottomAppBar")
    data object NavigationBar : ComposeNodeType("NavigationBar")
    data object NavigationRail : ComposeNodeType("NavigationRail")
    data object Switch : ComposeNodeType("Switch")
    data object Checkbox : ComposeNodeType("Checkbox")
    data object RadioButton : ComposeNodeType("RadioButton")
    data object Slider : ComposeNodeType("Slider")
    data object Spacer : ComposeNodeType("Spacer")
    data object Divider : ComposeNodeType("Divider")
    data object HorizontalDivider : ComposeNodeType("HorizontalDivider")
    data object VerticalDivider : ComposeNodeType("VerticalDivider")
    data object CircularProgressIndicator : ComposeNodeType("CircularProgressIndicator")
    data object LinearProgressIndicator : ComposeNodeType("LinearProgressIndicator")
    data class Custom(val customName: String) : ComposeNodeType(customName)

    companion object {
        fun fromName(name: String): ComposeNodeType {
            return when (name) {
                "Column" -> Column
                "Row" -> Row
                "Box" -> Box
                "Card" -> Card
                "Surface" -> Surface
                "Scaffold" -> Scaffold
                "LazyColumn" -> LazyColumn
                "LazyRow" -> LazyRow
                "Text" -> Text
                "TextField" -> TextField
                "OutlinedTextField" -> OutlinedTextField
                "Button" -> Button
                "OutlinedButton" -> OutlinedButton
                "TextButton" -> TextButton
                "IconButton" -> IconButton
                "FloatingActionButton" -> FloatingActionButton
                "ExtendedFloatingActionButton" -> ExtendedFloatingActionButton
                "Icon" -> Icon
                "Image" -> Image
                "AsyncImage" -> AsyncImage
                "TopAppBar" -> TopAppBar
                "CenterAlignedTopAppBar" -> CenterAlignedTopAppBar
                "BottomAppBar" -> BottomAppBar
                "NavigationBar" -> NavigationBar
                "NavigationRail" -> NavigationRail
                "Switch" -> Switch
                "Checkbox" -> Checkbox
                "RadioButton" -> RadioButton
                "Slider" -> Slider
                "Spacer" -> Spacer
                "Divider", "HorizontalDivider" -> HorizontalDivider
                "VerticalDivider" -> VerticalDivider
                "CircularProgressIndicator" -> CircularProgressIndicator
                "LinearProgressIndicator" -> LinearProgressIndicator
                else -> Custom(name)
            }
        }
    }
}

data class ComposeNode(
    val id: String = UUID.randomUUID().toString(),
    val type: ComposeNodeType,
    val properties: Map<String, NodeProperty> = emptyMap(),
    val children: List<ComposeNode> = emptyList(),
    val sourceRange: SourceRange? = null,
    val isSelected: Boolean = false,
    val isHovered: Boolean = false,
) {
    fun findById(nodeId: String): ComposeNode? {
        if (id == nodeId) return this
        return children.firstNotNullOfOrNull { it.findById(nodeId) }
    }

    fun updateNode(nodeId: String, updater: (ComposeNode) -> ComposeNode): ComposeNode {
        return if (id == nodeId) {
            updater(this)
        } else {
            copy(children = children.map { it.updateNode(nodeId, updater) })
        }
    }

    fun updateProperty(propertyName: String, value: Any?): ComposeNode {
        val currentProperty = properties[propertyName]
        return if (currentProperty != null) {
            copy(properties = properties + (propertyName to currentProperty.copy(value = value)))
        } else {
            copy(properties = properties + (propertyName to NodeProperty(propertyName, value)))
        }
    }
}

data class NodeProperty(
    val name: String,
    val value: Any?,
    val type: PropertyValueType = PropertyValueType.inferType(value),
    val sourceRange: SourceRange? = null,
)

enum class PropertyValueType {
    STRING,
    INT,
    FLOAT,
    BOOLEAN,
    COLOR,
    DIMENSION,
    ENUM,
    LAMBDA,
    MODIFIER,
    COMPOSABLE,
    UNKNOWN;

    companion object {
        fun inferType(value: Any?): PropertyValueType {
            return when (value) {
                is String -> when {
                    value.endsWith(".dp") || value.endsWith(".sp") -> DIMENSION
                    value.startsWith("Color.") || value.startsWith("#") -> COLOR
                    value.contains("{}") || value.contains("->") -> LAMBDA
                    else -> STRING
                }
                is Int -> INT
                is Float, is Double -> FLOAT
                is Boolean -> BOOLEAN
                else -> UNKNOWN
            }
        }
    }
}

data class SourceRange(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
    val startOffset: Int = 0,
    val endOffset: Int = 0,
)

data class ComposableFunction(
    val name: String,
    val parameters: List<FunctionParameter> = emptyList(),
    val body: ComposeNode?,
    val sourceRange: SourceRange?,
    val annotations: List<String> = emptyList(),
    val isPreview: Boolean = false,
    val previewParams: Map<String, String> = emptyMap(),
)

data class FunctionParameter(
    val name: String,
    val type: String,
    val defaultValue: String? = null,
    val isRequired: Boolean = defaultValue == null,
)

data class ParsedComposeFile(
    val filePath: String,
    val packageName: String = "",
    val imports: List<String> = emptyList(),
    val composables: List<ComposableFunction> = emptyList(),
    val hasComposeContent: Boolean = composables.isNotEmpty(),
    val parseErrors: List<ParseError> = emptyList(),
)

data class ParseError(
    val message: String,
    val line: Int,
    val column: Int,
    val severity: ErrorSeverity = ErrorSeverity.ERROR,
)

enum class ErrorSeverity {
    WARNING,
    ERROR,
}

data class ModifierChain(
    val modifiers: List<ModifierCall> = emptyList(),
) {
    fun toCode(): String {
        if (modifiers.isEmpty()) return "Modifier"
        return "Modifier" + modifiers.joinToString("") { ".${it.toCode()}" }
    }
}

data class ModifierCall(
    val name: String,
    val arguments: Map<String, Any?> = emptyMap(),
) {
    fun toCode(): String {
        if (arguments.isEmpty()) return "$name()"
        val args = arguments.entries.joinToString(", ") { (key, value) ->
            if (key.isBlank()) "$value" else "$key = $value"
        }
        return "$name($args)"
    }
}
