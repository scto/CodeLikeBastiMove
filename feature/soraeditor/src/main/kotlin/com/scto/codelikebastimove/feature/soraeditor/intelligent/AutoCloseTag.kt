package com.scto.codelikebastimove.feature.soraeditor.intelligent

import io.github.rosemoe.sora.widget.CodeEditor

object AutoCloseTag : IntelligentFeature() {
    override val id: String = "html.auto_close_tag"

    override val supportedExtensions: List<String> = listOf(
        "html", "htm", "xhtml", "xml", "vue", "svelte", "jsx", "tsx", "aidl"
    )

    override val triggerCharacters: List<Char> = listOf('>', '/')

    private val OPEN_TAG_REGEX = Regex("<([_a-zA-Z][a-zA-Z0-9:\\-_.]*)(?:\\s+[^<>]*?[^\\s/<>=]+?)*?\\s?(/|>)$")

    private val selfClosingTags = listOf(
        "area", "base", "br", "col", "command", "embed", "hr", "img",
        "input", "keygen", "link", "meta", "param", "source", "track", "wbr"
    )

    private var enabled = true

    override fun handleInsertChar(triggerCharacter: Char, editor: CodeEditor) {
        if (editor.cursor.isSelected) return
        val lineIndexBefore = editor.cursor.leftLine
        val columnIndexBefore = editor.cursor.leftColumn

        val line = editor.text.getLine(lineIndexBefore)
        val lineToCursor = line.substring(0, minOf(columnIndexBefore, line.length))

        val result = OPEN_TAG_REGEX.find(lineToCursor) ?: return
        val tagName = result.groupValues[1].lowercase()
        val endingChar = result.groupValues[2]

        val evenSingleQuotes = lineToCursor.count { it == '\'' } % 2 == 0
        val evenDoubleQuotes = lineToCursor.count { it == '\"' } % 2 == 0
        val evenBackticks = lineToCursor.count { it == '`' } % 2 == 0
        if (!evenSingleQuotes && !evenDoubleQuotes && !evenBackticks) return

        if (endingChar == ">") {
            if (selfClosingTags.contains(tagName)) return
            editor.text.insert(lineIndexBefore, columnIndexBefore, "</$tagName>")
            editor.setSelection(lineIndexBefore, columnIndexBefore)
        } else {
            if (lineToCursor.length < line.length) return
            if (columnIndexBefore >= 2 && lineToCursor[columnIndexBefore - 2] != ' ') {
                editor.text.insert(lineIndexBefore, columnIndexBefore - 1, " ")
            }
            editor.text.insert(editor.cursor.leftLine, editor.cursor.leftColumn, ">")
        }
    }

    override fun isEnabled(): Boolean = enabled

    fun setEnabled(value: Boolean) {
        enabled = value
    }
}
