package com.scto.codelikebastimove.feature.designer.domain.sync

import com.scto.codelikebastimove.feature.designer.data.model.ComposeNode
import com.scto.codelikebastimove.feature.designer.data.model.ComposableFunction
import com.scto.codelikebastimove.feature.designer.data.model.ModifierCall
import com.scto.codelikebastimove.feature.designer.data.model.ModifierChain
import com.scto.codelikebastimove.feature.designer.data.model.NodeProperty
import com.scto.codelikebastimove.feature.designer.data.model.SourceRange
import com.scto.codelikebastimove.feature.designer.domain.parser.ComposeParser

class CodeSynchronizer(
    private val parser: ComposeParser = ComposeParser(),
) {

    data class SyncResult(
        val updatedCode: String,
        val success: Boolean,
        val error: String? = null,
        val changeDescription: String? = null,
    )

    fun updateNodeProperty(
        sourceCode: String,
        nodeId: String,
        propertyName: String,
        newValue: Any?,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val node = composableFunction.body?.findById(nodeId)
            ?: return SyncResult(sourceCode, false, "Node not found: $nodeId")

        val sourceRange = node.sourceRange
            ?: return SyncResult(sourceCode, false, "No source range for node")

        val property = node.properties[propertyName]
        val propertyRange = property?.sourceRange

        return if (propertyRange != null) {
            updateExistingProperty(sourceCode, propertyRange, propertyName, newValue)
        } else {
            addPropertyToNode(sourceCode, sourceRange, node, propertyName, newValue)
        }
    }

    private fun updateExistingProperty(
        sourceCode: String,
        range: SourceRange,
        propertyName: String,
        newValue: Any?,
    ): SyncResult {
        val formattedValue = formatValue(newValue)
        val newPropertyCode = "$propertyName = $formattedValue"

        val updatedCode = StringBuilder(sourceCode)
            .replace(range.startOffset, range.endOffset, newPropertyCode)
            .toString()

        return SyncResult(
            updatedCode = updatedCode,
            success = true,
            changeDescription = "Updated $propertyName to $formattedValue",
        )
    }

    private fun addPropertyToNode(
        sourceCode: String,
        nodeRange: SourceRange,
        node: ComposeNode,
        propertyName: String,
        newValue: Any?,
    ): SyncResult {
        val openParenIndex = sourceCode.indexOf('(', nodeRange.startOffset)
        if (openParenIndex == -1) {
            return SyncResult(sourceCode, false, "Could not find opening parenthesis for node")
        }

        val closeParenIndex = findMatchingParen(sourceCode, openParenIndex)
        if (closeParenIndex == -1) {
            return SyncResult(sourceCode, false, "Could not find closing parenthesis for node")
        }

        val existingParams = sourceCode.substring(openParenIndex + 1, closeParenIndex).trim()
        val formattedValue = formatValue(newValue)
        val newParam = "$propertyName = $formattedValue"

        val insertContent = if (existingParams.isEmpty()) {
            newParam
        } else if (existingParams.endsWith(",")) {
            "\n    $newParam"
        } else {
            ",\n    $newParam"
        }

        val updatedCode = StringBuilder(sourceCode)
            .insert(closeParenIndex, insertContent)
            .toString()

        return SyncResult(
            updatedCode = updatedCode,
            success = true,
            changeDescription = "Added $propertyName = $formattedValue",
        )
    }

    fun updateModifier(
        sourceCode: String,
        nodeId: String,
        modifierChain: ModifierChain,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val node = composableFunction.body?.findById(nodeId)
            ?: return SyncResult(sourceCode, false, "Node not found: $nodeId")

        val existingModifier = node.properties["modifier"]
        val modifierCode = modifierChain.toCode()

        return if (existingModifier?.sourceRange != null) {
            updateExistingProperty(sourceCode, existingModifier.sourceRange!!, "modifier", modifierCode)
        } else {
            val sourceRange = node.sourceRange
                ?: return SyncResult(sourceCode, false, "No source range for node")
            addPropertyToNode(sourceCode, sourceRange, node, "modifier", modifierCode)
        }
    }

    fun addModifierCall(
        sourceCode: String,
        nodeId: String,
        modifierCall: ModifierCall,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val node = composableFunction.body?.findById(nodeId)
            ?: return SyncResult(sourceCode, false, "Node not found: $nodeId")

        val existingModifier = node.properties["modifier"]?.value
        val currentChain = when (existingModifier) {
            is ModifierChain -> existingModifier
            is String -> parseModifierString(existingModifier)
            else -> ModifierChain()
        }

        val newChain = ModifierChain(currentChain.modifiers + modifierCall)
        return updateModifier(sourceCode, nodeId, newChain, composableFunction)
    }

    fun removeModifierCall(
        sourceCode: String,
        nodeId: String,
        modifierName: String,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val node = composableFunction.body?.findById(nodeId)
            ?: return SyncResult(sourceCode, false, "Node not found: $nodeId")

        val existingModifier = node.properties["modifier"]?.value
        val currentChain = when (existingModifier) {
            is ModifierChain -> existingModifier
            is String -> parseModifierString(existingModifier)
            else -> return SyncResult(sourceCode, false, "No modifier to remove from")
        }

        val newModifiers = currentChain.modifiers.filter { it.name != modifierName }
        val newChain = ModifierChain(newModifiers)

        return updateModifier(sourceCode, nodeId, newChain, composableFunction)
    }

    fun addChildNode(
        sourceCode: String,
        parentNodeId: String,
        newNodeCode: String,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val parentNode = composableFunction.body?.findById(parentNodeId)
            ?: return SyncResult(sourceCode, false, "Parent node not found: $parentNodeId")

        val sourceRange = parentNode.sourceRange
            ?: return SyncResult(sourceCode, false, "No source range for parent node")

        val closingBraceIndex = findClosingBrace(sourceCode, sourceRange.startOffset)
        if (closingBraceIndex == -1) {
            return SyncResult(sourceCode, false, "Could not find closing brace for parent node")
        }

        val indentLevel = detectIndentLevel(sourceCode, closingBraceIndex)
        val indent = "    ".repeat(indentLevel + 1)
        val formattedNewNode = newNodeCode.lines().joinToString("\n") { "$indent$it" }

        val updatedCode = StringBuilder(sourceCode)
            .insert(closingBraceIndex, "\n$formattedNewNode\n${"    ".repeat(indentLevel)}")
            .toString()

        return SyncResult(
            updatedCode = updatedCode,
            success = true,
            changeDescription = "Added new child node",
        )
    }

    fun removeNode(
        sourceCode: String,
        nodeId: String,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val node = composableFunction.body?.findById(nodeId)
            ?: return SyncResult(sourceCode, false, "Node not found: $nodeId")

        val sourceRange = node.sourceRange
            ?: return SyncResult(sourceCode, false, "No source range for node")

        val startLine = findLineStart(sourceCode, sourceRange.startOffset)
        val endLine = findLineEnd(sourceCode, sourceRange.endOffset)

        val updatedCode = StringBuilder(sourceCode)
            .delete(startLine, endLine + 1)
            .toString()

        return SyncResult(
            updatedCode = updatedCode,
            success = true,
            changeDescription = "Removed node ${node.type.name}",
        )
    }

    fun wrapNodeWithContainer(
        sourceCode: String,
        nodeId: String,
        containerType: String,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val node = composableFunction.body?.findById(nodeId)
            ?: return SyncResult(sourceCode, false, "Node not found: $nodeId")

        val sourceRange = node.sourceRange
            ?: return SyncResult(sourceCode, false, "No source range for node")

        val nodeCode = sourceCode.substring(sourceRange.startOffset, sourceRange.endOffset)
        val indentLevel = detectIndentLevel(sourceCode, sourceRange.startOffset)
        val indent = "    ".repeat(indentLevel)
        val innerIndent = "    ".repeat(indentLevel + 1)

        val wrappedCode = buildString {
            appendLine("$containerType {")
            nodeCode.lines().forEach { line ->
                appendLine("$innerIndent$line")
            }
            append("$indent}")
        }

        val updatedCode = StringBuilder(sourceCode)
            .replace(sourceRange.startOffset, sourceRange.endOffset, wrappedCode)
            .toString()

        return SyncResult(
            updatedCode = updatedCode,
            success = true,
            changeDescription = "Wrapped node with $containerType",
        )
    }

    fun reorderNodes(
        sourceCode: String,
        parentNodeId: String,
        fromIndex: Int,
        toIndex: Int,
        composableFunction: ComposableFunction,
    ): SyncResult {
        val parentNode = composableFunction.body?.findById(parentNodeId)
            ?: return SyncResult(sourceCode, false, "Parent node not found: $parentNodeId")

        if (fromIndex < 0 || fromIndex >= parentNode.children.size ||
            toIndex < 0 || toIndex >= parentNode.children.size) {
            return SyncResult(sourceCode, false, "Invalid indices for reordering")
        }

        val movingNode = parentNode.children[fromIndex]
        val targetNode = parentNode.children[toIndex]

        val movingRange = movingNode.sourceRange
            ?: return SyncResult(sourceCode, false, "No source range for moving node")
        val targetRange = targetNode.sourceRange
            ?: return SyncResult(sourceCode, false, "No source range for target node")

        val movingCode = sourceCode.substring(
            findLineStart(sourceCode, movingRange.startOffset),
            findLineEnd(sourceCode, movingRange.endOffset) + 1
        )
        val targetCode = sourceCode.substring(
            findLineStart(sourceCode, targetRange.startOffset),
            findLineEnd(sourceCode, targetRange.endOffset) + 1
        )

        var updatedCode = sourceCode

        if (fromIndex < toIndex) {
            updatedCode = updatedCode.replaceRange(
                findLineStart(sourceCode, targetRange.startOffset),
                findLineEnd(sourceCode, targetRange.endOffset) + 1,
                movingCode
            )
            updatedCode = updatedCode.replaceRange(
                findLineStart(sourceCode, movingRange.startOffset),
                findLineEnd(sourceCode, movingRange.endOffset) + 1,
                targetCode
            )
        } else {
            updatedCode = updatedCode.replaceRange(
                findLineStart(sourceCode, movingRange.startOffset),
                findLineEnd(sourceCode, movingRange.endOffset) + 1,
                targetCode
            )
            updatedCode = updatedCode.replaceRange(
                findLineStart(sourceCode, targetRange.startOffset),
                findLineEnd(sourceCode, targetRange.endOffset) + 1,
                movingCode
            )
        }

        return SyncResult(
            updatedCode = updatedCode,
            success = true,
            changeDescription = "Reordered nodes from index $fromIndex to $toIndex",
        )
    }

    private fun formatValue(value: Any?): String {
        return when (value) {
            is String -> {
                when {
                    value.startsWith("\"") -> value
                    value.startsWith("{") -> value
                    value.contains(".") && !value.contains(" ") -> value
                    value == "true" || value == "false" -> value
                    value.endsWith(".dp") || value.endsWith(".sp") -> value
                    else -> "\"$value\""
                }
            }
            is Boolean -> value.toString()
            is Number -> value.toString()
            is ModifierChain -> value.toCode()
            else -> value.toString()
        }
    }

    private fun parseModifierString(modifierStr: String): ModifierChain {
        val modifiers = mutableListOf<ModifierCall>()
        val regex = Regex("""\\.(\w+)\(([^)]*)\)""")

        regex.findAll(modifierStr).forEach { match ->
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

        argsStr.split(",").forEach { arg ->
            val trimmed = arg.trim()
            val parts = trimmed.split("=", limit = 2)
            if (parts.size == 2) {
                args[parts[0].trim()] = parts[1].trim()
            } else if (trimmed.isNotEmpty()) {
                args[""] = trimmed
            }
        }

        return args
    }

    private fun findMatchingParen(code: String, openIndex: Int): Int {
        var count = 0
        for (i in openIndex until code.length) {
            when (code[i]) {
                '(' -> count++
                ')' -> {
                    count--
                    if (count == 0) return i
                }
            }
        }
        return -1
    }

    private fun findClosingBrace(code: String, startOffset: Int): Int {
        val openBraceIndex = code.indexOf('{', startOffset)
        if (openBraceIndex == -1) return -1

        var count = 0
        for (i in openBraceIndex until code.length) {
            when (code[i]) {
                '{' -> count++
                '}' -> {
                    count--
                    if (count == 0) return i
                }
            }
        }
        return -1
    }

    private fun detectIndentLevel(code: String, offset: Int): Int {
        val lineStart = findLineStart(code, offset)
        var spaces = 0
        for (i in lineStart until offset) {
            if (code[i] == ' ') spaces++
            else if (code[i] == '\t') spaces += 4
            else break
        }
        return spaces / 4
    }

    private fun findLineStart(code: String, offset: Int): Int {
        var i = offset - 1
        while (i >= 0 && code[i] != '\n') i--
        return i + 1
    }

    private fun findLineEnd(code: String, offset: Int): Int {
        var i = offset
        while (i < code.length && code[i] != '\n') i++
        return i
    }
}
