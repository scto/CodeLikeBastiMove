package com.scto.codelikebastimove.feature.soraeditor.util

import com.scto.codelikebastimove.core.datastore.EditorSettings as DatastoreEditorSettings
import com.scto.codelikebastimove.core.datastore.CursorAnimationType as DatastoreCursorAnimationType
import com.scto.codelikebastimove.core.datastore.RenderWhitespaceMode as DatastoreRenderWhitespaceMode
import com.scto.codelikebastimove.core.datastore.LineEndingType as DatastoreLineEndingType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.CursorAnimationType
import com.scto.codelikebastimove.feature.soraeditor.model.RenderWhitespaceMode
import com.scto.codelikebastimove.feature.soraeditor.model.LineEndingType

object EditorSettingsMapper {

    fun DatastoreEditorSettings.toEditorConfig(): EditorConfig {
        return EditorConfig(
            textSize = fontSize,
            tabSize = tabSize,
            pinLineNumber = pinLineNumber,
            stickyScroll = stickyScroll,
            fastDelete = fastDelete,
            showLineNumber = showLineNumbers,
            cursorAnimation = cursorAnimation.toSoraEditorCursorAnimation(),
            wordWrap = wordWrap,
            keyboardSuggestion = keyboardSuggestion,
            lineSpacing = lineSpacing,
            renderWhitespace = renderWhitespace.toSoraEditorRenderWhitespace(),
            hideSoftKbd = hideSoftKbd,
            lineEndingSetting = lineEndingSetting.toSoraEditorLineEnding(),
            finalNewline = finalNewline,
            autoIndent = autoIndent,
            autoComplete = autoCloseBrackets,
            highlightCurrentLine = highlightCurrentLine,
            highlightBrackets = bracketMatching,
            useTabCharacter = !useSoftTabs,
            fontFamily = fontFamily,
        )
    }

    fun EditorConfig.toDatastoreEditorSettings(): DatastoreEditorSettings {
        return DatastoreEditorSettings(
            fontSize = textSize,
            fontFamily = fontFamily,
            tabSize = tabSize,
            useSoftTabs = !useTabCharacter,
            showLineNumbers = showLineNumber,
            pinLineNumber = pinLineNumber,
            wordWrap = wordWrap,
            highlightCurrentLine = highlightCurrentLine,
            autoIndent = autoIndent,
            showWhitespace = renderWhitespace != RenderWhitespaceMode.NONE,
            bracketMatching = highlightBrackets,
            autoCloseBrackets = autoComplete,
            autoCloseQuotes = autoComplete,
            stickyScroll = stickyScroll,
            fastDelete = fastDelete,
            cursorAnimation = cursorAnimation.toDatastoreCursorAnimation(),
            keyboardSuggestion = keyboardSuggestion,
            lineSpacing = lineSpacing,
            renderWhitespace = renderWhitespace.toDatastoreRenderWhitespace(),
            hideSoftKbd = hideSoftKbd,
            lineEndingSetting = lineEndingSetting.toDatastoreLineEnding(),
            finalNewline = finalNewline,
        )
    }

    private fun DatastoreCursorAnimationType.toSoraEditorCursorAnimation(): CursorAnimationType {
        return when (this) {
            DatastoreCursorAnimationType.NONE -> CursorAnimationType.NONE
            DatastoreCursorAnimationType.FADE -> CursorAnimationType.FADE
            DatastoreCursorAnimationType.BLINK -> CursorAnimationType.BLINK
            DatastoreCursorAnimationType.SCALE -> CursorAnimationType.SCALE
        }
    }

    private fun CursorAnimationType.toDatastoreCursorAnimation(): DatastoreCursorAnimationType {
        return when (this) {
            CursorAnimationType.NONE -> DatastoreCursorAnimationType.NONE
            CursorAnimationType.FADE -> DatastoreCursorAnimationType.FADE
            CursorAnimationType.BLINK -> DatastoreCursorAnimationType.BLINK
            CursorAnimationType.SCALE -> DatastoreCursorAnimationType.SCALE
        }
    }

    private fun DatastoreRenderWhitespaceMode.toSoraEditorRenderWhitespace(): RenderWhitespaceMode {
        return when (this) {
            DatastoreRenderWhitespaceMode.NONE -> RenderWhitespaceMode.NONE
            DatastoreRenderWhitespaceMode.SELECTION -> RenderWhitespaceMode.SELECTION
            DatastoreRenderWhitespaceMode.BOUNDARY -> RenderWhitespaceMode.BOUNDARY
            DatastoreRenderWhitespaceMode.TRAILING -> RenderWhitespaceMode.TRAILING
            DatastoreRenderWhitespaceMode.ALL -> RenderWhitespaceMode.ALL
        }
    }

    private fun RenderWhitespaceMode.toDatastoreRenderWhitespace(): DatastoreRenderWhitespaceMode {
        return when (this) {
            RenderWhitespaceMode.NONE -> DatastoreRenderWhitespaceMode.NONE
            RenderWhitespaceMode.SELECTION -> DatastoreRenderWhitespaceMode.SELECTION
            RenderWhitespaceMode.BOUNDARY -> DatastoreRenderWhitespaceMode.BOUNDARY
            RenderWhitespaceMode.TRAILING -> DatastoreRenderWhitespaceMode.TRAILING
            RenderWhitespaceMode.ALL -> DatastoreRenderWhitespaceMode.ALL
        }
    }

    private fun DatastoreLineEndingType.toSoraEditorLineEnding(): LineEndingType {
        return when (this) {
            DatastoreLineEndingType.LF -> LineEndingType.LF
            DatastoreLineEndingType.CRLF -> LineEndingType.CRLF
            DatastoreLineEndingType.CR -> LineEndingType.CR
        }
    }

    private fun LineEndingType.toDatastoreLineEnding(): DatastoreLineEndingType {
        return when (this) {
            LineEndingType.LF -> DatastoreLineEndingType.LF
            LineEndingType.CRLF -> DatastoreLineEndingType.CRLF
            LineEndingType.CR -> DatastoreLineEndingType.CR
        }
    }
}
