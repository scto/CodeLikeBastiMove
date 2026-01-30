package com.scto.codelikebastimove.feature.designer.domain.codegen

import com.scto.codelikebastimove.feature.designer.data.model.Block
import com.scto.codelikebastimove.feature.designer.data.model.BlockTree
import com.scto.codelikebastimove.feature.designer.data.model.BlockType
import com.scto.codelikebastimove.feature.designer.data.model.ExportConfig
import com.scto.codelikebastimove.feature.designer.data.model.ThemeDescriptor
import com.scto.codelikebastimove.feature.designer.data.model.ValidationError
import com.scto.codelikebastimove.feature.designer.data.model.ValidationResult
import com.scto.codelikebastimove.feature.designer.data.model.ValidationWarning

class CodeEmitter {

  private val requiredImports = mutableSetOf<String>()

  fun generateCode(
    blockTree: BlockTree,
    config: ExportConfig,
    themeDescriptor: ThemeDescriptor? = null,
  ): GeneratedCode {
    requiredImports.clear()

    addBaseImports()

    val composableBody =
      blockTree.rootBlocks.joinToString("\n") { block -> generateBlockCode(block, indent = 2) }

    val functionName = blockTree.name.replace(" ", "").replaceFirstChar { it.uppercase() }

    val composableFunction = buildString {
      appendLine("@Composable")
      appendLine("fun $functionName(")
      appendLine("    modifier: Modifier = Modifier")
      appendLine(") {")
      append(composableBody)
      appendLine("}")
    }

    val fullCode =
      if (config.includeImports) {
        buildString {
          requiredImports.sorted().forEach { import -> appendLine("import $import") }
          appendLine()
          append(composableFunction)
        }
      } else {
        composableFunction
      }

    return GeneratedCode(
      code = fullCode,
      imports = requiredImports.toList(),
      functionName = functionName,
    )
  }

  private fun addBaseImports() {
    requiredImports.addAll(
      listOf(
        "androidx.compose.runtime.Composable",
        "androidx.compose.ui.Modifier",
        "androidx.compose.foundation.layout.*",
        "androidx.compose.material3.*",
        "androidx.compose.ui.unit.dp",
        "androidx.compose.ui.unit.sp",
      )
    )
  }

  private fun generateBlockCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    return when (block.type) {
      BlockType.COLUMN -> generateColumnCode(block, indent)
      BlockType.ROW -> generateRowCode(block, indent)
      BlockType.BOX -> generateBoxCode(block, indent)
      BlockType.CARD -> generateCardCode(block, indent)
      BlockType.SURFACE -> generateSurfaceCode(block, indent)
      BlockType.SCAFFOLD -> generateScaffoldCode(block, indent)
      BlockType.TEXT -> generateTextCode(block, indent)
      BlockType.BUTTON -> generateButtonCode(block, indent)
      BlockType.OUTLINED_BUTTON -> generateOutlinedButtonCode(block, indent)
      BlockType.TEXT_BUTTON -> generateTextButtonCode(block, indent)
      BlockType.ICON_BUTTON -> generateIconButtonCode(block, indent)
      BlockType.FAB,
      BlockType.EXTENDED_FAB -> generateFabCode(block, indent)
      BlockType.TEXT_FIELD -> generateTextFieldCode(block, indent)
      BlockType.OUTLINED_TEXT_FIELD -> generateOutlinedTextFieldCode(block, indent)
      BlockType.ICON -> generateIconCode(block, indent)
      BlockType.IMAGE -> generateImageCode(block, indent)
      BlockType.TOP_APP_BAR -> generateTopAppBarCode(block, indent)
      BlockType.BOTTOM_APP_BAR -> generateBottomAppBarCode(block, indent)
      BlockType.NAVIGATION_BAR -> generateNavigationBarCode(block, indent)
      BlockType.NAVIGATION_RAIL -> generateNavigationRailCode(block, indent)
      BlockType.BOTTOM_SHEET -> generateBottomSheetCode(block, indent)
      BlockType.SWITCH -> generateSwitchCode(block, indent)
      BlockType.CHECKBOX -> generateCheckboxCode(block, indent)
      BlockType.RADIO_BUTTON -> generateRadioButtonCode(block, indent)
      BlockType.SLIDER -> generateSliderCode(block, indent)
      BlockType.SPACER -> generateSpacerCode(block, indent)
      BlockType.DIVIDER -> generateDividerCode(block, indent)
      BlockType.CUSTOM_COMPONENT -> generateCustomComponentCode(block, indent)
    }
  }

  private fun generateColumnCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val verticalArrangement = block.properties["verticalArrangement"]?.value as? String ?: "Top"
    val horizontalAlignment = block.properties["horizontalAlignment"]?.value as? String ?: "Start"

    requiredImports.add("androidx.compose.foundation.layout.Arrangement")
    requiredImports.add("androidx.compose.ui.Alignment")

    val childrenCode =
      block.children.joinToString("\n") { child -> generateBlockCode(child, indent + 1) }

    return buildString {
      appendLine("${indentStr}Column(")
      appendLine("${indentStr}    modifier = Modifier.fillMaxWidth(),")
      appendLine("${indentStr}    verticalArrangement = Arrangement.${verticalArrangement},")
      appendLine("${indentStr}    horizontalAlignment = Alignment.${horizontalAlignment}")
      appendLine("${indentStr}) {")
      if (childrenCode.isNotEmpty()) {
        append(childrenCode)
      }
      appendLine("${indentStr}}")
    }
  }

  private fun generateRowCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val horizontalArrangement =
      block.properties["horizontalArrangement"]?.value as? String ?: "Start"
    val verticalAlignment = block.properties["verticalAlignment"]?.value as? String ?: "Top"

    requiredImports.add("androidx.compose.foundation.layout.Arrangement")
    requiredImports.add("androidx.compose.ui.Alignment")

    val childrenCode =
      block.children.joinToString("\n") { child -> generateBlockCode(child, indent + 1) }

    return buildString {
      appendLine("${indentStr}Row(")
      appendLine("${indentStr}    modifier = Modifier.fillMaxWidth(),")
      appendLine("${indentStr}    horizontalArrangement = Arrangement.${horizontalArrangement},")
      appendLine("${indentStr}    verticalAlignment = Alignment.${verticalAlignment}")
      appendLine("${indentStr}) {")
      if (childrenCode.isNotEmpty()) {
        append(childrenCode)
      }
      appendLine("${indentStr}}")
    }
  }

  private fun generateBoxCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val childrenCode =
      block.children.joinToString("\n") { child -> generateBlockCode(child, indent + 1) }

    return buildString {
      appendLine("${indentStr}Box(")
      appendLine(
        "${indentStr}    modifier = Modifier.size(${block.width.toInt()}.dp, ${block.height.toInt()}.dp)"
      )
      appendLine("${indentStr}) {")
      if (childrenCode.isNotEmpty()) {
        append(childrenCode)
      }
      appendLine("${indentStr}}")
    }
  }

  private fun generateCardCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val childrenCode =
      block.children.joinToString("\n") { child -> generateBlockCode(child, indent + 1) }

    requiredImports.add("androidx.compose.material3.Card")

    return buildString {
      appendLine("${indentStr}Card(")
      appendLine("${indentStr}    modifier = Modifier.fillMaxWidth()")
      appendLine("${indentStr}) {")
      if (childrenCode.isNotEmpty()) {
        append(childrenCode)
      }
      appendLine("${indentStr}}")
    }
  }

  private fun generateSurfaceCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val childrenCode =
      block.children.joinToString("\n") { child -> generateBlockCode(child, indent + 1) }

    requiredImports.add("androidx.compose.material3.Surface")

    return buildString {
      appendLine("${indentStr}Surface(")
      appendLine("${indentStr}    modifier = Modifier.fillMaxWidth()")
      appendLine("${indentStr}) {")
      if (childrenCode.isNotEmpty()) {
        append(childrenCode)
      }
      appendLine("${indentStr}}")
    }
  }

  private fun generateScaffoldCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val childrenCode =
      block.children.joinToString("\n") { child -> generateBlockCode(child, indent + 1) }

    requiredImports.add("androidx.compose.material3.Scaffold")

    return buildString {
      appendLine("${indentStr}Scaffold(")
      appendLine("${indentStr}    modifier = Modifier.fillMaxSize()")
      appendLine("${indentStr}) { paddingValues ->")
      appendLine("${indentStr}    Column(")
      appendLine("${indentStr}        modifier = Modifier.padding(paddingValues)")
      appendLine("${indentStr}    ) {")
      if (childrenCode.isNotEmpty()) {
        append(childrenCode)
      }
      appendLine("${indentStr}    }")
      appendLine("${indentStr}}")
    }
  }

  private fun generateTextCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val text = (block.properties["text"]?.value as? String)?.let { "\"$it\"" } ?: "\"Text\""
    val fontSize = block.properties["fontSize"]?.value as? String ?: "16.sp"

    requiredImports.add("androidx.compose.material3.Text")

    return buildString {
      appendLine("${indentStr}Text(")
      appendLine("${indentStr}    text = $text,")
      appendLine("${indentStr}    fontSize = $fontSize")
      appendLine("${indentStr})")
    }
  }

  private fun generateButtonCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val text = (block.properties["text"]?.value as? String) ?: "Button"
    val enabled = block.properties["enabled"]?.value as? Boolean ?: true

    requiredImports.add("androidx.compose.material3.Button")

    return buildString {
      appendLine("${indentStr}Button(")
      appendLine("${indentStr}    onClick = { /* TODO */ },")
      appendLine("${indentStr}    enabled = $enabled")
      appendLine("${indentStr}) {")
      appendLine("${indentStr}    Text(\"$text\")")
      appendLine("${indentStr}}")
    }
  }

  private fun generateOutlinedButtonCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val text = (block.properties["text"]?.value as? String) ?: "Button"

    requiredImports.add("androidx.compose.material3.OutlinedButton")

    return buildString {
      appendLine("${indentStr}OutlinedButton(")
      appendLine("${indentStr}    onClick = { /* TODO */ }")
      appendLine("${indentStr}) {")
      appendLine("${indentStr}    Text(\"$text\")")
      appendLine("${indentStr}}")
    }
  }

  private fun generateTextButtonCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val text = (block.properties["text"]?.value as? String) ?: "Button"

    requiredImports.add("androidx.compose.material3.TextButton")

    return buildString {
      appendLine("${indentStr}TextButton(")
      appendLine("${indentStr}    onClick = { /* TODO */ }")
      appendLine("${indentStr}) {")
      appendLine("${indentStr}    Text(\"$text\")")
      appendLine("${indentStr}}")
    }
  }

  private fun generateIconButtonCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val icon = block.properties["icon"]?.value as? String ?: "Icons.Default.Star"

    requiredImports.add("androidx.compose.material3.IconButton")
    requiredImports.add("androidx.compose.material.icons.Icons")
    requiredImports.add("androidx.compose.material.icons.filled.*")

    return buildString {
      appendLine("${indentStr}IconButton(")
      appendLine("${indentStr}    onClick = { /* TODO */ }")
      appendLine("${indentStr}) {")
      appendLine("${indentStr}    Icon(")
      appendLine("${indentStr}        imageVector = $icon,")
      appendLine("${indentStr}        contentDescription = null")
      appendLine("${indentStr}    )")
      appendLine("${indentStr}}")
    }
  }

  private fun generateFabCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.FloatingActionButton")
    requiredImports.add("androidx.compose.material.icons.Icons")
    requiredImports.add("androidx.compose.material.icons.filled.Add")

    return if (block.type == BlockType.EXTENDED_FAB) {
      buildString {
        appendLine("${indentStr}ExtendedFloatingActionButton(")
        appendLine("${indentStr}    onClick = { /* TODO */ },")
        appendLine("${indentStr}    icon = { Icon(Icons.Default.Add, contentDescription = null) },")
        appendLine("${indentStr}    text = { Text(\"Action\") }")
        appendLine("${indentStr})")
      }
    } else {
      buildString {
        appendLine("${indentStr}FloatingActionButton(")
        appendLine("${indentStr}    onClick = { /* TODO */ }")
        appendLine("${indentStr}) {")
        appendLine("${indentStr}    Icon(Icons.Default.Add, contentDescription = null)")
        appendLine("${indentStr}}")
      }
    }
  }

  private fun generateTextFieldCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val label = (block.properties["label"]?.value as? String) ?: "Label"
    val singleLine = block.properties["singleLine"]?.value as? Boolean ?: true

    requiredImports.add("androidx.compose.material3.TextField")
    requiredImports.add("androidx.compose.runtime.remember")
    requiredImports.add("androidx.compose.runtime.mutableStateOf")
    requiredImports.add("androidx.compose.runtime.getValue")
    requiredImports.add("androidx.compose.runtime.setValue")

    return buildString {
      appendLine("${indentStr}var textValue by remember { mutableStateOf(\"\") }")
      appendLine("${indentStr}TextField(")
      appendLine("${indentStr}    value = textValue,")
      appendLine("${indentStr}    onValueChange = { textValue = it },")
      appendLine("${indentStr}    label = { Text(\"$label\") },")
      appendLine("${indentStr}    singleLine = $singleLine")
      appendLine("${indentStr})")
    }
  }

  private fun generateOutlinedTextFieldCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val label = (block.properties["label"]?.value as? String) ?: "Label"
    val singleLine = block.properties["singleLine"]?.value as? Boolean ?: true

    requiredImports.add("androidx.compose.material3.OutlinedTextField")
    requiredImports.add("androidx.compose.runtime.remember")
    requiredImports.add("androidx.compose.runtime.mutableStateOf")
    requiredImports.add("androidx.compose.runtime.getValue")
    requiredImports.add("androidx.compose.runtime.setValue")

    return buildString {
      appendLine("${indentStr}var textValue by remember { mutableStateOf(\"\") }")
      appendLine("${indentStr}OutlinedTextField(")
      appendLine("${indentStr}    value = textValue,")
      appendLine("${indentStr}    onValueChange = { textValue = it },")
      appendLine("${indentStr}    label = { Text(\"$label\") },")
      appendLine("${indentStr}    singleLine = $singleLine")
      appendLine("${indentStr})")
    }
  }

  private fun generateIconCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val icon = block.properties["icon"]?.value as? String ?: "Icons.Default.Star"

    requiredImports.add("androidx.compose.material3.Icon")
    requiredImports.add("androidx.compose.material.icons.Icons")
    requiredImports.add("androidx.compose.material.icons.filled.*")

    return buildString {
      appendLine("${indentStr}Icon(")
      appendLine("${indentStr}    imageVector = $icon,")
      appendLine("${indentStr}    contentDescription = null")
      appendLine("${indentStr})")
    }
  }

  private fun generateImageCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.foundation.Image")
    requiredImports.add("androidx.compose.ui.res.painterResource")

    return buildString {
      appendLine("${indentStr}// TODO: Replace with actual image resource")
      appendLine("${indentStr}Box(")
      appendLine("${indentStr}    modifier = Modifier")
      appendLine("${indentStr}        .size(${block.width.toInt()}.dp, ${block.height.toInt()}.dp)")
      appendLine("${indentStr}        .background(Color.Gray)")
      appendLine("${indentStr})")
    }
  }

  private fun generateTopAppBarCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val title = (block.properties["title"]?.value as? String) ?: "Title"

    requiredImports.add("androidx.compose.material3.TopAppBar")
    requiredImports.add("androidx.compose.material3.ExperimentalMaterial3Api")

    return buildString {
      appendLine("${indentStr}TopAppBar(")
      appendLine("${indentStr}    title = { Text(\"$title\") }")
      appendLine("${indentStr})")
    }
  }

  private fun generateBottomAppBarCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.BottomAppBar")

    return buildString {
      appendLine("${indentStr}BottomAppBar {")
      appendLine("${indentStr}    // TODO: Add content")
      appendLine("${indentStr}}")
    }
  }

  private fun generateNavigationBarCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.NavigationBar")
    requiredImports.add("androidx.compose.material3.NavigationBarItem")

    return buildString {
      appendLine("${indentStr}NavigationBar {")
      appendLine("${indentStr}    NavigationBarItem(")
      appendLine("${indentStr}        selected = true,")
      appendLine("${indentStr}        onClick = { /* TODO */ },")
      appendLine(
        "${indentStr}        icon = { Icon(Icons.Default.Home, contentDescription = null) },"
      )
      appendLine("${indentStr}        label = { Text(\"Home\") }")
      appendLine("${indentStr}    )")
      appendLine("${indentStr}}")
    }
  }

  private fun generateNavigationRailCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.NavigationRail")
    requiredImports.add("androidx.compose.material3.NavigationRailItem")

    return buildString {
      appendLine("${indentStr}NavigationRail {")
      appendLine("${indentStr}    NavigationRailItem(")
      appendLine("${indentStr}        selected = true,")
      appendLine("${indentStr}        onClick = { /* TODO */ },")
      appendLine(
        "${indentStr}        icon = { Icon(Icons.Default.Home, contentDescription = null) },"
      )
      appendLine("${indentStr}        label = { Text(\"Home\") }")
      appendLine("${indentStr}    )")
      appendLine("${indentStr}}")
    }
  }

  private fun generateBottomSheetCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.ModalBottomSheet")
    requiredImports.add("androidx.compose.material3.rememberModalBottomSheetState")

    return buildString {
      appendLine("${indentStr}// ModalBottomSheet requires state management")
      appendLine("${indentStr}// val sheetState = rememberModalBottomSheetState()")
      appendLine("${indentStr}// ModalBottomSheet(")
      appendLine("${indentStr}//     onDismissRequest = { /* TODO */ },")
      appendLine("${indentStr}//     sheetState = sheetState")
      appendLine("${indentStr}// ) {")
      appendLine("${indentStr}//     // Sheet content")
      appendLine("${indentStr}// }")
    }
  }

  private fun generateSwitchCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val checked = block.properties["checked"]?.value as? Boolean ?: false

    requiredImports.add("androidx.compose.material3.Switch")
    requiredImports.add("androidx.compose.runtime.remember")
    requiredImports.add("androidx.compose.runtime.mutableStateOf")
    requiredImports.add("androidx.compose.runtime.getValue")
    requiredImports.add("androidx.compose.runtime.setValue")

    return buildString {
      appendLine("${indentStr}var switchChecked by remember { mutableStateOf($checked) }")
      appendLine("${indentStr}Switch(")
      appendLine("${indentStr}    checked = switchChecked,")
      appendLine("${indentStr}    onCheckedChange = { switchChecked = it }")
      appendLine("${indentStr})")
    }
  }

  private fun generateCheckboxCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val checked = block.properties["checked"]?.value as? Boolean ?: false

    requiredImports.add("androidx.compose.material3.Checkbox")
    requiredImports.add("androidx.compose.runtime.remember")
    requiredImports.add("androidx.compose.runtime.mutableStateOf")
    requiredImports.add("androidx.compose.runtime.getValue")
    requiredImports.add("androidx.compose.runtime.setValue")

    return buildString {
      appendLine("${indentStr}var checked by remember { mutableStateOf($checked) }")
      appendLine("${indentStr}Checkbox(")
      appendLine("${indentStr}    checked = checked,")
      appendLine("${indentStr}    onCheckedChange = { checked = it }")
      appendLine("${indentStr})")
    }
  }

  private fun generateRadioButtonCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val selected = block.properties["checked"]?.value as? Boolean ?: false

    requiredImports.add("androidx.compose.material3.RadioButton")

    return buildString {
      appendLine("${indentStr}RadioButton(")
      appendLine("${indentStr}    selected = $selected,")
      appendLine("${indentStr}    onClick = { /* TODO */ }")
      appendLine("${indentStr})")
    }
  }

  private fun generateSliderCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.Slider")
    requiredImports.add("androidx.compose.runtime.remember")
    requiredImports.add("androidx.compose.runtime.mutableFloatStateOf")
    requiredImports.add("androidx.compose.runtime.getValue")
    requiredImports.add("androidx.compose.runtime.setValue")

    return buildString {
      appendLine("${indentStr}var sliderValue by remember { mutableFloatStateOf(0f) }")
      appendLine("${indentStr}Slider(")
      appendLine("${indentStr}    value = sliderValue,")
      appendLine("${indentStr}    onValueChange = { sliderValue = it }")
      appendLine("${indentStr})")
    }
  }

  private fun generateSpacerCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val width = block.properties["width"]?.value as? String ?: "16.dp"
    val height = block.properties["height"]?.value as? String ?: "16.dp"

    requiredImports.add("androidx.compose.foundation.layout.Spacer")

    return "${indentStr}Spacer(modifier = Modifier.size($width, $height))\n"
  }

  private fun generateDividerCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)

    requiredImports.add("androidx.compose.material3.HorizontalDivider")

    return "${indentStr}HorizontalDivider()\n"
  }

  private fun generateCustomComponentCode(block: Block, indent: Int): String {
    val indentStr = "    ".repeat(indent)
    val componentName = block.customComponentId ?: "CustomComponent"

    return buildString { appendLine("${indentStr}$componentName()") }
  }

  fun validateCode(code: String): ValidationResult {
    val errors = mutableListOf<ValidationError>()
    val warnings = mutableListOf<ValidationWarning>()

    var braceCount = 0
    var parenCount = 0
    val lines = code.lines()

    lines.forEachIndexed { lineIndex, line ->
      braceCount += line.count { it == '{' } - line.count { it == '}' }
      parenCount += line.count { it == '(' } - line.count { it == ')' }

      if (line.contains("TODO")) {
        warnings.add(
          ValidationWarning(
            line = lineIndex + 1,
            message = "TODO comment found - implementation may be incomplete",
          )
        )
      }
    }

    if (braceCount != 0) {
      errors.add(
        ValidationError(
          line = null,
          column = null,
          message =
            "Mismatched curly braces: ${if (braceCount > 0) "missing $braceCount closing" else "extra ${-braceCount} closing"}",
        )
      )
    }

    if (parenCount != 0) {
      errors.add(
        ValidationError(
          line = null,
          column = null,
          message =
            "Mismatched parentheses: ${if (parenCount > 0) "missing $parenCount closing" else "extra ${-parenCount} closing"}",
        )
      )
    }

    if (!code.contains("@Composable")) {
      errors.add(
        ValidationError(line = 1, column = null, message = "Missing @Composable annotation")
      )
    }

    return ValidationResult(isValid = errors.isEmpty(), errors = errors, warnings = warnings)
  }
}

data class GeneratedCode(val code: String, val imports: List<String>, val functionName: String)
