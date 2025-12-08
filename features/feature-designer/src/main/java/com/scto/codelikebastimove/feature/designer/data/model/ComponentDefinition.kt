package com.scto.codelikebastimove.feature.designer.data.model

import java.util.UUID

data class ComponentDefinition(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val displayName: String,
    val description: String = "",
    val category: ComponentCategory,
    val template: String,
    val previewCode: String = "",
    val properties: List<ComponentProperty> = emptyList(),
    val isBuiltIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)

enum class ComponentCategory {
    TOP_APP_BAR,
    BOTTOM_BAR,
    BUTTON,
    CARD,
    DIALOG,
    BOTTOM_SHEET,
    INPUT,
    NAVIGATION,
    LIST_ITEM,
    CHIP,
    OTHER
}

data class ComponentProperty(
    val name: String,
    val type: String,
    val defaultValue: String?,
    val isRequired: Boolean = false,
    val description: String = ""
)

data class ComponentLibrary(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val components: List<ComponentDefinition> = emptyList(),
    val version: String = "1.0.0",
    val author: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

object BuiltInComponents {
    
    val gradientTopAppBar = ComponentDefinition(
        id = "builtin_gradient_topappbar",
        name = "GradientTopAppBar",
        displayName = "Gradient Top App Bar",
        description = "A TopAppBar with gradient background",
        category = ComponentCategory.TOP_APP_BAR,
        isBuiltIn = true,
        template = """
@Composable
fun {{PREFIX}}GradientTopAppBar(
    title: String,
    onNavigationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}
        """.trimIndent(),
        properties = listOf(
            ComponentProperty("title", "String", null, true),
            ComponentProperty("onNavigationClick", "() -> Unit", "{}", false),
            ComponentProperty("modifier", "Modifier", "Modifier", false)
        )
    )
    
    val outlinedCard = ComponentDefinition(
        id = "builtin_outlined_card",
        name = "OutlinedCard",
        displayName = "Outlined Card",
        description = "A card with outlined border",
        category = ComponentCategory.CARD,
        isBuiltIn = true,
        template = """
@Composable
fun {{PREFIX}}OutlinedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
        """.trimIndent(),
        properties = listOf(
            ComponentProperty("modifier", "Modifier", "Modifier", false),
            ComponentProperty("content", "@Composable ColumnScope.() -> Unit", null, true)
        )
    )
    
    val primaryButton = ComponentDefinition(
        id = "builtin_primary_button",
        name = "PrimaryButton",
        displayName = "Primary Button",
        description = "A styled primary action button",
        category = ComponentCategory.BUTTON,
        isBuiltIn = true,
        template = """
@Composable
fun {{PREFIX}}PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
        """.trimIndent(),
        properties = listOf(
            ComponentProperty("text", "String", null, true),
            ComponentProperty("onClick", "() -> Unit", null, true),
            ComponentProperty("modifier", "Modifier", "Modifier", false),
            ComponentProperty("enabled", "Boolean", "true", false)
        )
    )
    
    val all = listOf(gradientTopAppBar, outlinedCard, primaryButton)
}
