package com.scto.codelikebastimove.feature.designer.domain.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNode
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNodeType
import com.scto.codelikebastimove.feature.designer.data.model.ModifierChain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderComposeNode(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = node.id == selectedNodeId
    val selectionModifier = if (isSelected) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
    } else {
        Modifier
    }

    val clickModifier = Modifier.clickable { onNodeSelected(node.id) }

    when (node.type) {
        is ComposeNodeType.Column -> RenderColumn(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Row -> RenderRow(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Box -> RenderBox(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Card -> RenderCard(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Surface -> RenderSurface(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Scaffold -> RenderScaffold(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.LazyColumn -> RenderLazyColumn(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.LazyRow -> RenderLazyRow(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Text -> RenderText(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.TextField -> RenderTextField(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.OutlinedTextField -> RenderOutlinedTextField(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Button -> RenderButton(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.OutlinedButton -> RenderOutlinedButton(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.TextButton -> RenderTextButton(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.IconButton -> RenderIconButton(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.FloatingActionButton -> RenderFAB(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.ExtendedFloatingActionButton -> RenderExtendedFAB(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Icon -> RenderIcon(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Image, is ComposeNodeType.AsyncImage -> RenderImagePlaceholder(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.TopAppBar, is ComposeNodeType.CenterAlignedTopAppBar -> RenderTopAppBar(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.BottomAppBar -> RenderBottomAppBar(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.NavigationBar -> RenderNavigationBar(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.NavigationRail -> RenderNavigationRail(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Switch -> RenderSwitch(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Checkbox -> RenderCheckbox(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.RadioButton -> RenderRadioButton(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Slider -> RenderSlider(node, selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.Spacer -> RenderSpacer(node, selectionModifier.then(modifier))
        is ComposeNodeType.Divider, is ComposeNodeType.HorizontalDivider -> HorizontalDivider(modifier = selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.VerticalDivider -> VerticalDivider(modifier = selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.CircularProgressIndicator -> CircularProgressIndicator(modifier = selectionModifier.then(clickModifier).then(modifier))
        is ComposeNodeType.LinearProgressIndicator -> LinearProgressIndicator(modifier = selectionModifier.then(clickModifier).then(modifier).fillMaxWidth())
        is ComposeNodeType.Custom -> RenderCustomComponent(node, selectedNodeId, onNodeSelected, selectionModifier.then(clickModifier).then(modifier))
    }
}

@Composable
private fun RenderColumn(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    val arrangement = when (node.properties["verticalArrangement"]?.value as? String) {
        "Center" -> Arrangement.Center
        "Bottom" -> Arrangement.Bottom
        "SpaceBetween" -> Arrangement.SpaceBetween
        "SpaceAround" -> Arrangement.SpaceAround
        "SpaceEvenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.Top
    }

    val alignment = when (node.properties["horizontalAlignment"]?.value as? String) {
        "CenterHorizontally" -> Alignment.CenterHorizontally
        "End" -> Alignment.End
        else -> Alignment.Start
    }

    Column(
        modifier = modifier,
        verticalArrangement = arrangement,
        horizontalAlignment = alignment,
    ) {
        node.children.forEach { child ->
            RenderComposeNode(child, selectedNodeId, onNodeSelected)
        }
    }
}

@Composable
private fun RenderRow(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    val arrangement = when (node.properties["horizontalArrangement"]?.value as? String) {
        "Center" -> Arrangement.Center
        "End" -> Arrangement.End
        "SpaceBetween" -> Arrangement.SpaceBetween
        "SpaceAround" -> Arrangement.SpaceAround
        "SpaceEvenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.Start
    }

    val alignment = when (node.properties["verticalAlignment"]?.value as? String) {
        "CenterVertically" -> Alignment.CenterVertically
        "Bottom" -> Alignment.Bottom
        else -> Alignment.Top
    }

    Row(
        modifier = modifier,
        horizontalArrangement = arrangement,
        verticalAlignment = alignment,
    ) {
        node.children.forEach { child ->
            RenderComposeNode(child, selectedNodeId, onNodeSelected)
        }
    }
}

@Composable
private fun RenderBox(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    val contentAlignment = when (node.properties["contentAlignment"]?.value as? String) {
        "Center" -> Alignment.Center
        "TopStart" -> Alignment.TopStart
        "TopCenter" -> Alignment.TopCenter
        "TopEnd" -> Alignment.TopEnd
        "CenterStart" -> Alignment.CenterStart
        "CenterEnd" -> Alignment.CenterEnd
        "BottomStart" -> Alignment.BottomStart
        "BottomCenter" -> Alignment.BottomCenter
        "BottomEnd" -> Alignment.BottomEnd
        else -> Alignment.TopStart
    }

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        node.children.forEach { child ->
            RenderComposeNode(child, selectedNodeId, onNodeSelected)
        }
    }
}

@Composable
private fun RenderCard(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            node.children.forEach { child ->
                RenderComposeNode(child, selectedNodeId, onNodeSelected)
            }
        }
    }
}

@Composable
private fun RenderSurface(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    Surface(modifier = modifier) {
        Column {
            node.children.forEach { child ->
                RenderComposeNode(child, selectedNodeId, onNodeSelected)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenderScaffold(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Preview") })
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            node.children.forEach { child ->
                RenderComposeNode(child, selectedNodeId, onNodeSelected)
            }
        }
    }
}

@Composable
private fun RenderLazyColumn(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(node.children.size) { index ->
            RenderComposeNode(node.children[index], selectedNodeId, onNodeSelected)
        }
    }
}

@Composable
private fun RenderLazyRow(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    LazyRow(modifier = modifier) {
        items(node.children.size) { index ->
            RenderComposeNode(node.children[index], selectedNodeId, onNodeSelected)
        }
    }
}

@Composable
private fun RenderText(node: ComposeNode, modifier: Modifier) {
    val text = node.properties["text"]?.value as? String
        ?: node.properties["arg0"]?.value as? String
        ?: "Text"

    val fontSize = parseDimension(node.properties["fontSize"]?.value)?.sp ?: 16.sp
    val fontWeight = when (node.properties["fontWeight"]?.value as? String) {
        "Bold", "FontWeight.Bold" -> FontWeight.Bold
        "SemiBold", "FontWeight.SemiBold" -> FontWeight.SemiBold
        "Medium", "FontWeight.Medium" -> FontWeight.Medium
        "Light", "FontWeight.Light" -> FontWeight.Light
        else -> FontWeight.Normal
    }

    val textAlign = when (node.properties["textAlign"]?.value as? String) {
        "Center", "TextAlign.Center" -> TextAlign.Center
        "End", "TextAlign.End" -> TextAlign.End
        "Justify", "TextAlign.Justify" -> TextAlign.Justify
        else -> TextAlign.Start
    }

    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
    )
}

@Composable
private fun RenderTextField(node: ComposeNode, modifier: Modifier) {
    var value by remember { mutableStateOf(node.properties["value"]?.value as? String ?: "") }
    val label = node.properties["label"]?.value as? String ?: "Label"

    TextField(
        value = value,
        onValueChange = { value = it },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun RenderOutlinedTextField(node: ComposeNode, modifier: Modifier) {
    var value by remember { mutableStateOf(node.properties["value"]?.value as? String ?: "") }
    val label = node.properties["label"]?.value as? String ?: "Label"

    OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun RenderButton(node: ComposeNode, modifier: Modifier) {
    val text = node.properties["text"]?.value as? String
        ?: node.properties["arg0"]?.value as? String
        ?: "Button"

    Button(onClick = {}, modifier = modifier) {
        Text(text)
    }
}

@Composable
private fun RenderOutlinedButton(node: ComposeNode, modifier: Modifier) {
    val text = node.properties["text"]?.value as? String ?: "Button"

    OutlinedButton(onClick = {}, modifier = modifier) {
        Text(text)
    }
}

@Composable
private fun RenderTextButton(node: ComposeNode, modifier: Modifier) {
    val text = node.properties["text"]?.value as? String ?: "Button"

    TextButton(onClick = {}, modifier = modifier) {
        Text(text)
    }
}

@Composable
private fun RenderIconButton(node: ComposeNode, modifier: Modifier) {
    val iconName = node.properties["icon"]?.value as? String ?: "Icons.Default.Star"
    val icon = iconFromName(iconName)

    IconButton(onClick = {}, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = null)
    }
}

@Composable
private fun RenderFAB(node: ComposeNode, modifier: Modifier) {
    FloatingActionButton(onClick = {}, modifier = modifier) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}

@Composable
private fun RenderExtendedFAB(node: ComposeNode, modifier: Modifier) {
    val text = node.properties["text"]?.value as? String ?: "Action"

    ExtendedFloatingActionButton(
        onClick = {},
        modifier = modifier,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text(text) },
    )
}

@Composable
private fun RenderIcon(node: ComposeNode, modifier: Modifier) {
    val iconName = node.properties["imageVector"]?.value as? String
        ?: node.properties["icon"]?.value as? String
        ?: "Icons.Default.Star"
    val icon = iconFromName(iconName)

    Icon(imageVector = icon, contentDescription = null, modifier = modifier)
}

@Composable
private fun RenderImagePlaceholder(node: ComposeNode, modifier: Modifier) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text("Image", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenderTopAppBar(node: ComposeNode, modifier: Modifier) {
    val title = node.properties["title"]?.value as? String ?: "Title"

    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        },
    )
}

@Composable
private fun RenderBottomAppBar(node: ComposeNode, modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {}) { Icon(Icons.Default.Home, contentDescription = null) }
        IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = null) }
        IconButton(onClick = {}) { Icon(Icons.Default.Settings, contentDescription = null) }
    }
}

@Composable
private fun RenderNavigationBar(node: ComposeNode, modifier: Modifier) {
    var selected by remember { mutableStateOf(0) }

    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = { selected = 0 },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = { selected = 1 },
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") },
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = { selected = 2 },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
        )
    }
}

@Composable
private fun RenderNavigationRail(node: ComposeNode, modifier: Modifier) {
    var selected by remember { mutableStateOf(0) }

    NavigationRail(modifier = modifier) {
        NavigationRailItem(
            selected = selected == 0,
            onClick = { selected = 0 },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
        )
        NavigationRailItem(
            selected = selected == 1,
            onClick = { selected = 1 },
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") },
        )
        NavigationRailItem(
            selected = selected == 2,
            onClick = { selected = 2 },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
        )
    }
}

@Composable
private fun RenderSwitch(node: ComposeNode, modifier: Modifier) {
    var checked by remember { mutableStateOf(node.properties["checked"]?.value as? Boolean ?: false) }

    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = modifier,
    )
}

@Composable
private fun RenderCheckbox(node: ComposeNode, modifier: Modifier) {
    var checked by remember { mutableStateOf(node.properties["checked"]?.value as? Boolean ?: false) }

    Checkbox(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = modifier,
    )
}

@Composable
private fun RenderRadioButton(node: ComposeNode, modifier: Modifier) {
    var selected by remember { mutableStateOf(node.properties["selected"]?.value as? Boolean ?: false) }

    RadioButton(
        selected = selected,
        onClick = { selected = !selected },
        modifier = modifier,
    )
}

@Composable
private fun RenderSlider(node: ComposeNode, modifier: Modifier) {
    var value by remember { mutableFloatStateOf(node.properties["value"]?.value as? Float ?: 0.5f) }

    Slider(
        value = value,
        onValueChange = { value = it },
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun RenderSpacer(node: ComposeNode, modifier: Modifier) {
    val width = parseDimension(node.properties["width"]?.value)?.dp ?: 16.dp
    val height = parseDimension(node.properties["height"]?.value)?.dp ?: 16.dp

    Spacer(modifier = modifier.size(width, height))
}

@Composable
private fun RenderCustomComponent(
    node: ComposeNode,
    selectedNodeId: String?,
    onNodeSelected: (String) -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = node.type.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            node.children.forEach { child ->
                RenderComposeNode(child, selectedNodeId, onNodeSelected)
            }
        }
    }
}

private fun parseDimension(value: Any?): Float? {
    return when (value) {
        is Number -> value.toFloat()
        is String -> {
            val numStr = value.replace(Regex("""[^\d.]"""), "")
            numStr.toFloatOrNull()
        }
        else -> null
    }
}

private fun iconFromName(name: String): ImageVector {
    return when {
        name.contains("Add") -> Icons.Default.Add
        name.contains("Check") -> Icons.Default.Check
        name.contains("Close") -> Icons.Default.Close
        name.contains("Home") -> Icons.Default.Home
        name.contains("Menu") -> Icons.Default.Menu
        name.contains("Search") -> Icons.Default.Search
        name.contains("Settings") -> Icons.Default.Settings
        name.contains("Star") -> Icons.Default.Star
        name.contains("MoreVert") -> Icons.Default.MoreVert
        else -> Icons.Default.Star
    }
}
