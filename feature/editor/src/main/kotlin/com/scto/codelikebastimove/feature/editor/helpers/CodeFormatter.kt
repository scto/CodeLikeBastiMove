package com.scto.codelikebastimove.feature.editor.helpers

import io.github.rosemoe.sora.widget.CodeEditor

/**
 * Interface for code formatting logic.
 * Implementations will define how different languages are formatted.
 */
interface CodeFormatter {
    fun format(editor: CodeEditor)
}
