package com.scto.codelikebastimove.feature.designer.data.model

import androidx.compose.ui.geometry.Offset
import java.util.UUID

enum class BlockCategory {
    LAYOUT,
    CONTAINER,
    TEXT,
    INPUT,
    BUTTON,
    IMAGE,
    NAVIGATION,
    CUSTOM
}

enum class BlockType(
    val displayName: String,
    val category: BlockCategory,
    val defaultWidth: Float = 200f,
    val defaultHeight: Float = 48f
) {
    COLUMN("Column", BlockCategory.LAYOUT, 200f, 300f),
    ROW("Row", BlockCategory.LAYOUT, 300f, 100f),
    BOX("Box", BlockCategory.CONTAINER, 200f, 200f),
    CARD("Card", BlockCategory.CONTAINER, 200f, 150f),
    SURFACE("Surface", BlockCategory.CONTAINER, 200f, 150f),
    SCAFFOLD("Scaffold", BlockCategory.LAYOUT, 360f, 640f),
    
    TEXT("Text", BlockCategory.TEXT, 150f, 40f),
    TEXT_FIELD("TextField", BlockCategory.INPUT, 200f, 56f),
    OUTLINED_TEXT_FIELD("OutlinedTextField", BlockCategory.INPUT, 200f, 56f),
    
    BUTTON("Button", BlockCategory.BUTTON, 120f, 48f),
    OUTLINED_BUTTON("OutlinedButton", BlockCategory.BUTTON, 120f, 48f),
    TEXT_BUTTON("TextButton", BlockCategory.BUTTON, 100f, 48f),
    ICON_BUTTON("IconButton", BlockCategory.BUTTON, 48f, 48f),
    FAB("FloatingActionButton", BlockCategory.BUTTON, 56f, 56f),
    EXTENDED_FAB("ExtendedFAB", BlockCategory.BUTTON, 140f, 56f),
    
    ICON("Icon", BlockCategory.IMAGE, 24f, 24f),
    IMAGE("Image", BlockCategory.IMAGE, 150f, 150f),
    
    TOP_APP_BAR("TopAppBar", BlockCategory.NAVIGATION, 360f, 64f),
    BOTTOM_APP_BAR("BottomAppBar", BlockCategory.NAVIGATION, 360f, 80f),
    NAVIGATION_BAR("NavigationBar", BlockCategory.NAVIGATION, 360f, 80f),
    NAVIGATION_RAIL("NavigationRail", BlockCategory.NAVIGATION, 80f, 400f),
    BOTTOM_SHEET("BottomSheet", BlockCategory.NAVIGATION, 360f, 300f),
    
    SWITCH("Switch", BlockCategory.INPUT, 52f, 32f),
    CHECKBOX("Checkbox", BlockCategory.INPUT, 24f, 24f),
    RADIO_BUTTON("RadioButton", BlockCategory.INPUT, 24f, 24f),
    SLIDER("Slider", BlockCategory.INPUT, 200f, 48f),
    
    SPACER("Spacer", BlockCategory.LAYOUT, 16f, 16f),
    DIVIDER("Divider", BlockCategory.LAYOUT, 200f, 1f),
    
    CUSTOM_COMPONENT("Custom", BlockCategory.CUSTOM, 200f, 100f)
}

data class BlockProperty(
    val key: String,
    val displayName: String,
    val type: PropertyType,
    val value: Any?,
    val options: List<String>? = null
)

enum class PropertyType {
    STRING,
    INT,
    FLOAT,
    BOOLEAN,
    COLOR,
    ENUM,
    DIMENSION,
    MODIFIER,
    ICON,
    FONT_WEIGHT,
    TEXT_ALIGN
}

data class Block(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    val position: Offset = Offset.Zero,
    val width: Float = type.defaultWidth,
    val height: Float = type.defaultHeight,
    val properties: Map<String, BlockProperty> = getDefaultProperties(type),
    val children: List<Block> = emptyList(),
    val parentId: String? = null,
    val isExpanded: Boolean = true,
    val isSelected: Boolean = false,
    val customComponentId: String? = null
) {
    companion object {
        fun getDefaultProperties(type: BlockType): Map<String, BlockProperty> {
            val common = mutableMapOf(
                "modifier" to BlockProperty("modifier", "Modifier", PropertyType.MODIFIER, "Modifier"),
                "contentDescription" to BlockProperty("contentDescription", "Content Description", PropertyType.STRING, null)
            )
            
            when (type) {
                BlockType.TEXT -> {
                    common["text"] = BlockProperty("text", "Text", PropertyType.STRING, "Text")
                    common["fontSize"] = BlockProperty("fontSize", "Font Size", PropertyType.DIMENSION, "16.sp")
                    common["fontWeight"] = BlockProperty("fontWeight", "Font Weight", PropertyType.FONT_WEIGHT, "Normal")
                    common["color"] = BlockProperty("color", "Color", PropertyType.COLOR, null)
                    common["textAlign"] = BlockProperty("textAlign", "Text Align", PropertyType.TEXT_ALIGN, "Start")
                }
                BlockType.BUTTON, BlockType.OUTLINED_BUTTON, BlockType.TEXT_BUTTON -> {
                    common["text"] = BlockProperty("text", "Label", PropertyType.STRING, "Button")
                    common["enabled"] = BlockProperty("enabled", "Enabled", PropertyType.BOOLEAN, true)
                }
                BlockType.TEXT_FIELD, BlockType.OUTLINED_TEXT_FIELD -> {
                    common["value"] = BlockProperty("value", "Value", PropertyType.STRING, "")
                    common["label"] = BlockProperty("label", "Label", PropertyType.STRING, "Label")
                    common["placeholder"] = BlockProperty("placeholder", "Placeholder", PropertyType.STRING, "")
                    common["singleLine"] = BlockProperty("singleLine", "Single Line", PropertyType.BOOLEAN, true)
                }
                BlockType.COLUMN -> {
                    common["verticalArrangement"] = BlockProperty(
                        "verticalArrangement", "Vertical Arrangement", PropertyType.ENUM, "Top",
                        listOf("Top", "Bottom", "Center", "SpaceBetween", "SpaceAround", "SpaceEvenly")
                    )
                    common["horizontalAlignment"] = BlockProperty(
                        "horizontalAlignment", "Horizontal Alignment", PropertyType.ENUM, "Start",
                        listOf("Start", "CenterHorizontally", "End")
                    )
                }
                BlockType.ROW -> {
                    common["horizontalArrangement"] = BlockProperty(
                        "horizontalArrangement", "Horizontal Arrangement", PropertyType.ENUM, "Start",
                        listOf("Start", "End", "Center", "SpaceBetween", "SpaceAround", "SpaceEvenly")
                    )
                    common["verticalAlignment"] = BlockProperty(
                        "verticalAlignment", "Vertical Alignment", PropertyType.ENUM, "Top",
                        listOf("Top", "CenterVertically", "Bottom")
                    )
                }
                BlockType.SPACER -> {
                    common["width"] = BlockProperty("width", "Width", PropertyType.DIMENSION, "16.dp")
                    common["height"] = BlockProperty("height", "Height", PropertyType.DIMENSION, "16.dp")
                }
                BlockType.ICON -> {
                    common["icon"] = BlockProperty("icon", "Icon", PropertyType.ICON, "Icons.Default.Star")
                    common["tint"] = BlockProperty("tint", "Tint", PropertyType.COLOR, null)
                }
                BlockType.SWITCH -> {
                    common["checked"] = BlockProperty("checked", "Checked", PropertyType.BOOLEAN, false)
                    common["enabled"] = BlockProperty("enabled", "Enabled", PropertyType.BOOLEAN, true)
                }
                BlockType.CHECKBOX, BlockType.RADIO_BUTTON -> {
                    common["checked"] = BlockProperty("checked", "Checked", PropertyType.BOOLEAN, false)
                    common["enabled"] = BlockProperty("enabled", "Enabled", PropertyType.BOOLEAN, true)
                }
                BlockType.TOP_APP_BAR -> {
                    common["title"] = BlockProperty("title", "Title", PropertyType.STRING, "Title")
                    common["navigationIcon"] = BlockProperty("navigationIcon", "Navigation Icon", PropertyType.BOOLEAN, true)
                }
                else -> {}
            }
            
            return common
        }
    }
}

data class BlockTree(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Untitled Layout",
    val rootBlocks: List<Block> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)
