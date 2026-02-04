package com.scto.codelikebastimove.feature.designer.domain.parser

import com.scto.codelikebastimove.feature.designer.data.model.ComposeNode
import com.scto.codelikebastimove.feature.designer.data.model.ComposeNodeType
import com.scto.codelikebastimove.feature.designer.data.model.ComposableFunction
import com.scto.codelikebastimove.feature.designer.data.model.FunctionParameter
import com.scto.codelikebastimove.feature.designer.data.model.ModifierCall
import com.scto.codelikebastimove.feature.designer.data.model.ModifierChain
import com.scto.codelikebastimove.feature.designer.data.model.NodeProperty
import com.scto.codelikebastimove.feature.designer.data.model.ParseError
import com.scto.codelikebastimove.feature.designer.data.model.ParsedComposeFile
import com.scto.codelikebastimove.feature.designer.data.model.SourceRange

class ComposeParser {

    private val composeComponentNames = setOf(
        "Column", "Row", "Box", "Card", "Surface", "Scaffold", "LazyColumn", "LazyRow",
        "Text", "TextField", "OutlinedTextField", "BasicTextField",
        "Button", "OutlinedButton", "TextButton", "IconButton", "FloatingActionButton",
        "ExtendedFloatingActionButton", "FilledTonalButton", "ElevatedButton",
        "Icon", "Image", "AsyncImage",
        "TopAppBar", "CenterAlignedTopAppBar", "MediumTopAppBar", "LargeTopAppBar",
        "BottomAppBar", "NavigationBar", "NavigationRail", "NavigationBarItem", "NavigationRailItem",
        "Switch", "Checkbox", "RadioButton", "Slider", "RangeSlider",
        "Spacer", "Divider", "HorizontalDivider", "VerticalDivider",
        "CircularProgressIndicator", "LinearProgressIndicator",
        "AlertDialog", "Dialog", "ModalBottomSheet", "BottomSheet",
        "TabRow", "Tab", "ScrollableTabRow", "PrimaryTabRow", "SecondaryTabRow",
        "Chip", "AssistChip", "FilterChip", "InputChip", "SuggestionChip",
        "DropdownMenu", "DropdownMenuItem", "ExposedDropdownMenuBox",
        "ListItem", "Badge", "BadgedBox",
    )

    fun parseFile(content: String, filePath: String = ""): ParsedComposeFile {
        val lines = content.lines()
        val errors = mutableListOf<ParseError>()

        val packageName = extractPackageName(content)
        val imports = extractImports(content)

        if (!hasComposeContent(content)) {
            return ParsedComposeFile(
                filePath = filePath,
                packageName = packageName,
                imports = imports,
                composables = emptyList(),
                hasComposeContent = false,
            )
        }

        val composables = extractComposableFunctions(content, lines, errors)

        return ParsedComposeFile(
            filePath = filePath,
            packageName = packageName,
            imports = imports,
            composables = composables,
            hasComposeContent = composables.isNotEmpty(),
            parseErrors = errors,
        )
    }

    fun hasComposeContent(content: String): Boolean {
        return content.contains("@Composable") &&
            composeComponentNames.any { content.contains(it) }
    }

    private fun extractPackageName(content: String): String {
        val packageRegex = Regex("""package\s+([\w.]+)""")
        return packageRegex.find(content)?.groupValues?.get(1) ?: ""
    }

    private fun extractImports(content: String): List<String> {
        val importRegex = Regex("""import\s+([\w.*]+)""")
        return importRegex.findAll(content).map { it.groupValues[1] }.toList()
    }

    private fun extractComposableFunctions(
        content: String,
        lines: List<String>,
        errors: MutableList<ParseError>,
    ): List<ComposableFunction> {
        val composables = mutableListOf<ComposableFunction>()

        val composableRegex = Regex(
            """(@Preview(?:\([^)]*\))?\s*)?@Composable\s+(?:private\s+|internal\s+|public\s+)?fun\s+(\w+)\s*\(([^)]*)\)\s*(?::\s*Unit\s*)?\{""",
            RegexOption.MULTILINE
        )

        composableRegex.findAll(content).forEach { match ->
            val previewAnnotation = match.groupValues[1].trim()
            val functionName = match.groupValues[2]
            val paramsStr = match.groupValues[3]

            val isPreview = previewAnnotation.startsWith("@Preview")
            val previewParams = if (isPreview) extractPreviewParams(previewAnnotation) else emptyMap()

            val parameters = parseParameters(paramsStr)
            val functionStartOffset = match.range.last + 1
            val functionBody = extractFunctionBody(content, functionStartOffset)

            val bodyNode = if (functionBody.isNotEmpty()) {
                parseComposableBody(functionBody, errors)
            } else null

            val startLine = content.substring(0, match.range.first).count { it == '\n' } + 1
            val endLine = content.substring(0, functionStartOffset + functionBody.length)
                .count { it == '\n' } + 1

            composables.add(
                ComposableFunction(
                    name = functionName,
                    parameters = parameters,
                    body = bodyNode,
                    sourceRange = SourceRange(
                        startLine = startLine,
                        startColumn = 0,
                        endLine = endLine,
                        endColumn = 0,
                        startOffset = match.range.first,
                        endOffset = functionStartOffset + functionBody.length,
                    ),
                    annotations = if (isPreview) listOf("Preview", "Composable") else listOf("Composable"),
                    isPreview = isPreview,
                    previewParams = previewParams,
                )
            )
        }

        return composables
    }

    private fun extractPreviewParams(annotation: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        val paramsRegex = Regex("""(\w+)\s*=\s*([^,)]+)""")
        paramsRegex.findAll(annotation).forEach { match ->
            params[match.groupValues[1]] = match.groupValues[2].trim()
        }
        return params
    }

    private fun parseParameters(paramsStr: String): List<FunctionParameter> {
        if (paramsStr.isBlank()) return emptyList()

        val params = mutableListOf<FunctionParameter>()
        val paramRegex = Regex("""(\w+)\s*:\s*([^=,]+)(?:\s*=\s*([^,]+))?""")

        paramRegex.findAll(paramsStr).forEach { match ->
            val name = match.groupValues[1].trim()
            val type = match.groupValues[2].trim()
            val defaultValue = match.groupValues[3].takeIf { it.isNotBlank() }?.trim()

            params.add(FunctionParameter(name, type, defaultValue))
        }

        return params
    }

    private fun extractFunctionBody(content: String, startOffset: Int): String {
        var braceCount = 1
        var index = startOffset

        while (index < content.length && braceCount > 0) {
            when (content[index]) {
                '{' -> braceCount++
                '}' -> braceCount--
            }
            index++
        }

        return if (braceCount == 0) {
            content.substring(startOffset, index - 1)
        } else ""
    }

    fun parseComposableBody(body: String, errors: MutableList<ParseError>): ComposeNode? {
        val trimmed = body.trim()
        if (trimmed.isEmpty()) return null

        val componentRegex = Regex("""(\w+)\s*\(""")
        val firstMatch = componentRegex.find(trimmed)

        return if (firstMatch != null) {
            val componentName = firstMatch.groupValues[1]
            if (componentName in composeComponentNames || componentName.first().isUpperCase()) {
                parseComponent(trimmed, 0, errors).first
            } else null
        } else null
    }

    private fun parseComponent(
        code: String,
        startOffset: Int,
        errors: MutableList<ParseError>,
    ): Pair<ComposeNode?, Int> {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return null to startOffset

        val componentRegex = Regex("""^(\w+)\s*\(""")
        val match = componentRegex.find(trimmed) ?: return null to startOffset

        val componentName = match.groupValues[1]
        val argsStart = match.range.last + 1
        val argsResult = extractArguments(trimmed, argsStart - 1)
        val argsEnd = argsResult.second
        val argsStr = argsResult.first

        val properties = parseComponentProperties(argsStr)

        val afterArgs = trimmed.substring(argsEnd).trim()
        val children = mutableListOf<ComposeNode>()

        if (afterArgs.startsWith("{")) {
            val lambdaBody = extractLambdaBody(afterArgs, 0)
            val childNodes = parseChildComponents(lambdaBody.first, errors)
            children.addAll(childNodes)
        }

        val nodeType = ComposeNodeType.fromName(componentName)
        val node = ComposeNode(
            type = nodeType,
            properties = properties,
            children = children,
            sourceRange = SourceRange(
                startLine = 0, startColumn = 0,
                endLine = 0, endColumn = 0,
                startOffset = startOffset,
                endOffset = startOffset + argsEnd + (afterArgs.indexOf('}').takeIf { it >= 0 } ?: 0),
            ),
        )

        return node to (argsEnd + afterArgs.indexOf('}').coerceAtLeast(0))
    }

    private fun extractArguments(code: String, startIndex: Int): Pair<String, Int> {
        var parenCount = 0
        var started = false
        var startOffset = startIndex
        var endOffset = startIndex

        for (i in startIndex until code.length) {
            when (code[i]) {
                '(' -> {
                    if (!started) {
                        started = true
                        startOffset = i + 1
                    }
                    parenCount++
                }
                ')' -> {
                    parenCount--
                    if (parenCount == 0) {
                        endOffset = i
                        break
                    }
                }
            }
        }

        return code.substring(startOffset, endOffset) to (endOffset + 1)
    }

    private fun extractLambdaBody(code: String, startIndex: Int): Pair<String, Int> {
        var braceCount = 0
        var started = false
        var startOffset = startIndex
        var endOffset = code.length

        for (i in startIndex until code.length) {
            when (code[i]) {
                '{' -> {
                    if (!started) {
                        started = true
                        startOffset = i + 1
                    }
                    braceCount++
                }
                '}' -> {
                    braceCount--
                    if (braceCount == 0) {
                        endOffset = i
                        break
                    }
                }
            }
        }

        return code.substring(startOffset, endOffset) to endOffset
    }

    private fun parseComponentProperties(argsStr: String): Map<String, NodeProperty> {
        val properties = mutableMapOf<String, NodeProperty>()
        if (argsStr.isBlank()) return properties

        var current = argsStr.trim()
        while (current.isNotEmpty()) {
            val namedArgMatch = Regex("""^(\w+)\s*=\s*""").find(current)

            if (namedArgMatch != null) {
                val name = namedArgMatch.groupValues[1]
                current = current.substring(namedArgMatch.range.last + 1).trim()

                val (value, remaining) = extractPropertyValue(current)
                properties[name] = NodeProperty(name, value)
                current = remaining.trim().removePrefix(",").trim()
            } else {
                val (value, remaining) = extractPropertyValue(current)
                if (value != null) {
                    val posIndex = properties.size
                    properties["arg$posIndex"] = NodeProperty("arg$posIndex", value)
                }
                current = remaining.trim().removePrefix(",").trim()
            }
        }

        return properties
    }

    private fun extractPropertyValue(code: String): Pair<Any?, String> {
        val trimmed = code.trim()

        if (trimmed.startsWith("\"")) {
            val endQuote = findStringEnd(trimmed, 0)
            if (endQuote > 0) {
                val stringValue = trimmed.substring(1, endQuote)
                return stringValue to trimmed.substring(endQuote + 1)
            }
        }

        if (trimmed.startsWith("{")) {
            val (lambdaBody, endIndex) = extractLambdaBody(trimmed, 0)
            return "{ $lambdaBody }" to trimmed.substring(endIndex + 1)
        }

        val simpleValueRegex = Regex("""^([^,{}()]+)""")
        val match = simpleValueRegex.find(trimmed)
        if (match != null) {
            val value = match.groupValues[1].trim()
            return parseSimpleValue(value) to trimmed.substring(match.range.last + 1)
        }

        return null to trimmed
    }

    private fun findStringEnd(code: String, startQuote: Int): Int {
        var i = startQuote + 1
        while (i < code.length) {
            if (code[i] == '"' && (i == 0 || code[i - 1] != '\\')) {
                return i
            }
            i++
        }
        return -1
    }

    private fun parseSimpleValue(value: String): Any? {
        return when {
            value == "true" -> true
            value == "false" -> false
            value.toIntOrNull() != null -> value.toInt()
            value.toFloatOrNull() != null -> value.toFloat()
            value.toDoubleOrNull() != null -> value.toDouble()
            value.endsWith(".dp") || value.endsWith(".sp") -> value
            value.startsWith("Color.") -> value
            value.startsWith("Icons.") -> value
            value.startsWith("Modifier") -> parseModifierChain(value)
            else -> value
        }
    }

    private fun parseModifierChain(modifierCode: String): ModifierChain {
        val modifiers = mutableListOf<ModifierCall>()

        val modifierCallRegex = Regex("""\\.(\w+)\(([^)]*)\)""")
        modifierCallRegex.findAll(modifierCode).forEach { match ->
            val name = match.groupValues[1]
            val argsStr = match.groupValues[2]
            val args = parseModifierArgs(argsStr)
            modifiers.add(ModifierCall(name, args))
        }

        return ModifierChain(modifiers)
    }

    private fun parseModifierArgs(argsStr: String): Map<String, Any?> {
        val args = mutableMapOf<String, Any?>()
        if (argsStr.isBlank()) return args

        argsStr.split(",").forEachIndexed { index, arg ->
            val trimmedArg = arg.trim()
            val parts = trimmedArg.split("=", limit = 2)
            if (parts.size == 2) {
                args[parts[0].trim()] = parseSimpleValue(parts[1].trim())
            } else {
                args[""] = parseSimpleValue(trimmedArg)
            }
        }

        return args
    }

    private fun parseChildComponents(
        bodyCode: String,
        errors: MutableList<ParseError>,
    ): List<ComposeNode> {
        val children = mutableListOf<ComposeNode>()
        val lines = bodyCode.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val componentMatch = Regex("""^(\w+)\s*[\({]""").find(trimmed)
            if (componentMatch != null) {
                val componentName = componentMatch.groupValues[1]
                if (componentName in composeComponentNames || componentName.first().isUpperCase()) {
                    val (node, _) = parseComponent(trimmed, 0, errors)
                    node?.let { children.add(it) }
                }
            }
        }

        return children
    }

    fun generateCodeFromNode(node: ComposeNode, indent: Int = 0): String {
        val indentStr = "    ".repeat(indent)
        val sb = StringBuilder()

        sb.append("$indentStr${node.type.name}(")

        val props = node.properties.entries
            .filter { it.key != "modifier" && !it.key.startsWith("arg") }
            .joinToString(",\n$indentStr    ") { (key, prop) ->
                "$key = ${formatPropertyValue(prop.value)}"
            }

        if (props.isNotEmpty()) {
            sb.append("\n$indentStr    $props")
        }

        val modifierProp = node.properties["modifier"]
        if (modifierProp != null && modifierProp.value is ModifierChain) {
            val modifierCode = (modifierProp.value as ModifierChain).toCode()
            if (props.isNotEmpty()) sb.append(",")
            sb.append("\n$indentStr    modifier = $modifierCode")
        }

        sb.append("\n$indentStr)")

        if (node.children.isNotEmpty()) {
            sb.append(" {\n")
            node.children.forEach { child ->
                sb.append(generateCodeFromNode(child, indent + 1))
                sb.append("\n")
            }
            sb.append("$indentStr}")
        }

        return sb.toString()
    }

    private fun formatPropertyValue(value: Any?): String {
        return when (value) {
            is String -> if (value.startsWith("{") || value.contains(".")) value else "\"$value\""
            is ModifierChain -> value.toCode()
            else -> value.toString()
        }
    }
}
