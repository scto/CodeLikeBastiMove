package com.scto.codelikebastimove.feature.editor.helpers

import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.symbol.SymbolPair
import io.github.rosemoe.sora.widget.component.symbol.SymbolPairMatch

/**
 * Utility to configure auto-bracket and symbol pair behavior for the editor.
 */
object AutoBracketConfiguration {

    fun configure(editor: CodeEditor) {
        editor.symbolPairMatch = SymbolPairMatch().apply {
            addPair(SymbolPair('(', ')'))
            addPair(SymbolPair('[', ']'))
            addPair(SymbolPair('{', '}'))
            addPair(SymbolPair('"', '"'))
            addPair(SymbolPair('\'', '\''))
            addPair(SymbolPair('`', '`'))
            addPair(SymbolPair('<', '>'))
        }
    }
}
