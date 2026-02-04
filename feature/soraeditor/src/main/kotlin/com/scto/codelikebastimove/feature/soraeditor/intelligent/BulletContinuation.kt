package com.scto.codelikebastimove.feature.soraeditor.intelligent

import android.view.KeyEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.widget.CodeEditor

object BulletContinuation : IntelligentFeature() {
    override val id: String = "md.bullet_continuation"

    override val supportedExtensions: List<String> = listOf("md", "markdown", "mkd", "mdx")

    private val QUOTE_REGEX = Regex("^> ")
    private val LIST_WHITESPACE_REGEX = Regex("^\\s*([-+*]|[0-9]+[.)]) +(\\[[ x]] +)?")
    private val LIST_REGEX = Regex("^([-+*]|[0-9]+[.)])( +\\[[ x]])?\$")
    private val UL_LIST_REGEX = Regex("^((\\s*[-+*] +)(\\[[ x]] +)?)")
    private val OL_LIST_REGEX = Regex("^(\\s*)([0-9]+)([.)])( +)((\\[[ x]] +)?)")

    private var enabled = true

    override fun handleKeyEvent(event: EditorKeyEvent, editor: CodeEditor) {
        if (event.action != KeyEvent.ACTION_DOWN) return

        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.modifiers == 0) {
            onEnter(editor) { 
                runCatching { event.result = true }
            }
        } else if (event.keyCode == KeyEvent.KEYCODE_TAB && !event.isCtrlPressed && !event.isAltPressed) {
            onTab(editor, event.isShiftPressed) { 
                runCatching { event.result = true }
            }
        }
    }

    private fun onTab(editor: CodeEditor, shiftPressed: Boolean, consumeEvent: () -> Unit) {
        if (editor.cursor.leftLine != editor.cursor.rightLine) return
        val lineIndexBefore = editor.cursor.leftLine
        val columnIndexBefore = editor.cursor.leftColumn

        val line = editor.text.getLine(lineIndexBefore)
        val lineToCursor = line.substring(0, minOf(columnIndexBefore, line.length))

        val listMatch = LIST_WHITESPACE_REGEX.find(line.toString())
        if (listMatch != null && (lineToCursor.endsWith(listMatch.value) || editor.cursor.isSelected)) {
            if (!shiftPressed) {
                runCatching { editor.indentSelection() }
            } else {
                runCatching { editor.unindentSelection() }
            }
            consumeEvent()
            return
        }
    }

    private fun onEnter(editor: CodeEditor, consumeEvent: () -> Unit) {
        if (editor.cursor.isSelected) return
        val lineIndexBefore = editor.cursor.leftLine
        val columnIndexBefore = editor.cursor.leftColumn

        val line = editor.text.getLine(lineIndexBefore)
        val lineStr = line.toString()
        val lineToCursor = lineStr.substring(0, minOf(columnIndexBefore, lineStr.length))

        val quoteMatch = QUOTE_REGEX.find(lineStr)
        if (quoteMatch != null) {
            if (lineStr.trim() == ">") {
                editor.text.delete(lineIndexBefore, 0, lineIndexBefore, line.length)
            } else {
                editor.text.insert(lineIndexBefore, columnIndexBefore, "\n> ")
            }
            consumeEvent()
            return
        }

        val liMatch = LIST_REGEX.matchEntire(lineStr.trim())
        if (liMatch != null) {
            editor.text.delete(lineIndexBefore, 0, lineIndexBefore, line.length)
            consumeEvent()
            return
        }

        val ulLiMatch = UL_LIST_REGEX.find(lineToCursor)
        if (ulLiMatch != null) {
            val listPrefix = ulLiMatch.groupValues[1]
            val appendedListItem = '\n' + listPrefix.replace("[x]", "[ ]")
            editor.text.insert(lineIndexBefore, columnIndexBefore, appendedListItem)
            consumeEvent()
            return
        }

        val olLiMatch = OL_LIST_REGEX.find(lineToCursor)
        if (olLiMatch != null) {
            val leadingSpace = olLiMatch.groupValues[1]
            val previousMarker = olLiMatch.groupValues[2]
            val delimiter = olLiMatch.groupValues[3]
            var trailingSpace = olLiMatch.groupValues[4]
            val checkbox = olLiMatch.groupValues[5].replace("[x]", "[ ]")

            val marker = (previousMarker.toInt() + 1).toString()

            val markerDiff = previousMarker.length - marker.length
            val newTrailingSpaceLength = (trailingSpace.length + markerDiff).coerceAtLeast(1)
            trailingSpace = " ".repeat(newTrailingSpaceLength)

            val appendedListItem = '\n' + leadingSpace + marker + delimiter + trailingSpace + checkbox
            editor.text.insert(lineIndexBefore, columnIndexBefore, appendedListItem)
            consumeEvent()
            return
        }
    }

    override fun isEnabled(): Boolean = enabled

    fun setEnabled(value: Boolean) {
        enabled = value
    }
}
