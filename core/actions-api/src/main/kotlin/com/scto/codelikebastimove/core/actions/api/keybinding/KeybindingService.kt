package com.scto.codelikebastimove.core.actions.api.keybinding

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionWhen
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import kotlinx.coroutines.flow.StateFlow

interface KeybindingService {
    val keybindings: StateFlow<List<ResolvedKeybinding>>
    
    fun registerKeybinding(actionId: String, keybinding: Keybinding, when: ActionWhen? = null): Boolean
    
    fun unregisterKeybinding(actionId: String, keybinding: Keybinding): Boolean
    
    fun unregisterAllKeybindings(actionId: String)
    
    fun getKeybindingsForAction(actionId: String): List<Keybinding>
    
    fun getActionForKeybinding(keybinding: Keybinding, context: ActionContext): String?
    
    fun resolveKeybinding(keyEvent: KeyEvent, context: ActionContext): String?
    
    fun getConflicts(keybinding: Keybinding): List<String>
    
    fun setEnabled(enabled: Boolean)
    
    fun isEnabled(): Boolean
}

data class ResolvedKeybinding(
    val actionId: String,
    val keybinding: Keybinding,
    val when: ActionWhen? = null,
    val priority: Int = 0,
    val source: KeybindingSource = KeybindingSource.DEFAULT
)

enum class KeybindingSource {
    DEFAULT,
    USER,
    PLUGIN
}

data class KeyEvent(
    val keyCode: Int,
    val key: String,
    val modifiers: Set<KeyModifier>,
    val isRepeat: Boolean = false
) {
    fun toKeybinding(): Keybinding = Keybinding(key, modifiers)
    
    fun matches(keybinding: Keybinding): Boolean {
        return key.equals(keybinding.key, ignoreCase = true) && modifiers == keybinding.modifiers
    }
}

interface KeybindingResolver {
    fun resolve(event: KeyEvent, context: ActionContext): ResolvedKeybinding?
    
    fun findMatches(event: KeyEvent): List<ResolvedKeybinding>
    
    fun evaluateWhen(when: ActionWhen?, context: ActionContext): Boolean
}

interface KeybindingHandler {
    suspend fun handleKeyEvent(event: KeyEvent, context: ActionContext): Boolean
}

data class KeybindingConflict(
    val keybinding: Keybinding,
    val actionIds: List<String>,
    val severity: ConflictSeverity
)

enum class ConflictSeverity {
    INFO,
    WARNING,
    ERROR
}

object DefaultKeybindings {
    val UNDO = Keybinding("Z", setOf(KeyModifier.CTRL))
    val REDO = Keybinding("Y", setOf(KeyModifier.CTRL))
    val CUT = Keybinding("X", setOf(KeyModifier.CTRL))
    val COPY = Keybinding("C", setOf(KeyModifier.CTRL))
    val PASTE = Keybinding("V", setOf(KeyModifier.CTRL))
    val SELECT_ALL = Keybinding("A", setOf(KeyModifier.CTRL))
    val SAVE = Keybinding("S", setOf(KeyModifier.CTRL))
    val SAVE_ALL = Keybinding("S", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))
    val FIND = Keybinding("F", setOf(KeyModifier.CTRL))
    val REPLACE = Keybinding("H", setOf(KeyModifier.CTRL))
    val COMMAND_PALETTE = Keybinding("P", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))
    val QUICK_OPEN = Keybinding("P", setOf(KeyModifier.CTRL))
    val GO_TO_LINE = Keybinding("G", setOf(KeyModifier.CTRL))
    val COMMENT_LINE = Keybinding("/", setOf(KeyModifier.CTRL))
    val DUPLICATE_LINE = Keybinding("D", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))
    val DELETE_LINE = Keybinding("K", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))
    val MOVE_LINE_UP = Keybinding("Up", setOf(KeyModifier.ALT))
    val MOVE_LINE_DOWN = Keybinding("Down", setOf(KeyModifier.ALT))
    val FORMAT_DOCUMENT = Keybinding("F", setOf(KeyModifier.CTRL, KeyModifier.SHIFT))
    val TOGGLE_TERMINAL = Keybinding("`", setOf(KeyModifier.CTRL))
    val TOGGLE_SIDEBAR = Keybinding("B", setOf(KeyModifier.CTRL))
}
