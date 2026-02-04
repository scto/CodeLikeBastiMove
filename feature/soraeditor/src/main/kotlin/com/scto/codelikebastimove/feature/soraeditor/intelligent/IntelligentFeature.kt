package com.scto.codelikebastimove.feature.soraeditor.intelligent

import androidx.compose.runtime.mutableStateListOf
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import io.github.rosemoe.sora.widget.CodeEditor

object IntelligentFeatureRegistry {
    val builtInFeatures = listOf(AutoCloseTag, BulletContinuation)

    private val mutableFeatures = mutableStateListOf<IntelligentFeature>()
    val extensionFeatures: List<IntelligentFeature>
        get() = mutableFeatures.toList()

    val allFeatures: List<IntelligentFeature>
        get() = builtInFeatures + mutableFeatures

    fun registerFeature(feature: IntelligentFeature) {
        if (!mutableFeatures.contains(feature)) {
            mutableFeatures.add(feature)
        }
    }

    fun unregisterFeature(feature: IntelligentFeature) {
        mutableFeatures.remove(feature)
    }

    fun getFeaturesForExtension(extension: String): List<IntelligentFeature> {
        val normalizedExt = extension.lowercase().removePrefix(".")
        return allFeatures.filter { feature ->
            feature.isEnabled() && feature.supportedExtensions.any { 
                it.equals(normalizedExt, ignoreCase = true) 
            }
        }
    }

    fun getFeaturesForExtensions(extensions: List<String>): List<IntelligentFeature> {
        return allFeatures.filter { feature ->
            feature.isEnabled() && feature.supportedExtensions.any { supported ->
                extensions.any { ext -> ext.equals(supported, ignoreCase = true) }
            }
        }
    }
}

abstract class IntelligentFeature {
    abstract val id: String
    abstract val supportedExtensions: List<String>
    open val triggerCharacters: List<Char> = emptyList()

    open fun handleInsertChar(triggerCharacter: Char, editor: CodeEditor) {}

    open fun handleDeleteChar(triggerCharacter: Char, editor: CodeEditor) {}

    open fun handleInsert(editor: CodeEditor) {}

    open fun handleDelete(editor: CodeEditor) {}

    open fun handleKeyEvent(event: EditorKeyEvent, editor: CodeEditor) {}

    open fun handleKeyBindingEvent(event: KeyBindingEvent, editor: CodeEditor) {}

    open fun isEnabled(): Boolean = true
}
