package com.termux.terminal

import android.view.KeyEvent

object KeyHandler {
    
    private const val ESC = '\u001b'
    private const val DEL = '\u007f'
    
    fun getCode(keyCode: Int, keyMod: Int, cursorKeysApplicationMode: Boolean, keypadApplicationMode: Boolean): String? {
        val ctrl = (keyMod and KEYMOD_CTRL) != 0
        val shift = (keyMod and KEYMOD_SHIFT) != 0
        val alt = (keyMod and KEYMOD_ALT) != 0
        val meta = (keyMod and KEYMOD_META) != 0
        
        return when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> "\r"
            KeyEvent.KEYCODE_DEL -> if (ctrl) "\u001f" else DEL.toString()
            KeyEvent.KEYCODE_FORWARD_DEL -> "${ESC}[3~"
            KeyEvent.KEYCODE_TAB -> if (shift) "${ESC}[Z" else "\t"
            KeyEvent.KEYCODE_ESCAPE -> ESC.toString()
            
            KeyEvent.KEYCODE_DPAD_UP -> getCursorCode(cursorKeysApplicationMode, 'A', keyMod)
            KeyEvent.KEYCODE_DPAD_DOWN -> getCursorCode(cursorKeysApplicationMode, 'B', keyMod)
            KeyEvent.KEYCODE_DPAD_RIGHT -> getCursorCode(cursorKeysApplicationMode, 'C', keyMod)
            KeyEvent.KEYCODE_DPAD_LEFT -> getCursorCode(cursorKeysApplicationMode, 'D', keyMod)
            
            KeyEvent.KEYCODE_MOVE_HOME -> "${ESC}[H"
            KeyEvent.KEYCODE_MOVE_END -> "${ESC}[F"
            
            KeyEvent.KEYCODE_PAGE_UP -> "${ESC}[5~"
            KeyEvent.KEYCODE_PAGE_DOWN -> "${ESC}[6~"
            
            KeyEvent.KEYCODE_INSERT -> "${ESC}[2~"
            
            KeyEvent.KEYCODE_F1 -> getFunctionCode(1, keyMod)
            KeyEvent.KEYCODE_F2 -> getFunctionCode(2, keyMod)
            KeyEvent.KEYCODE_F3 -> getFunctionCode(3, keyMod)
            KeyEvent.KEYCODE_F4 -> getFunctionCode(4, keyMod)
            KeyEvent.KEYCODE_F5 -> getFunctionCode(5, keyMod)
            KeyEvent.KEYCODE_F6 -> getFunctionCode(6, keyMod)
            KeyEvent.KEYCODE_F7 -> getFunctionCode(7, keyMod)
            KeyEvent.KEYCODE_F8 -> getFunctionCode(8, keyMod)
            KeyEvent.KEYCODE_F9 -> getFunctionCode(9, keyMod)
            KeyEvent.KEYCODE_F10 -> getFunctionCode(10, keyMod)
            KeyEvent.KEYCODE_F11 -> getFunctionCode(11, keyMod)
            KeyEvent.KEYCODE_F12 -> getFunctionCode(12, keyMod)
            
            KeyEvent.KEYCODE_NUMPAD_0 -> if (keypadApplicationMode) "${ESC}Op" else "0"
            KeyEvent.KEYCODE_NUMPAD_1 -> if (keypadApplicationMode) "${ESC}Oq" else "1"
            KeyEvent.KEYCODE_NUMPAD_2 -> if (keypadApplicationMode) "${ESC}Or" else "2"
            KeyEvent.KEYCODE_NUMPAD_3 -> if (keypadApplicationMode) "${ESC}Os" else "3"
            KeyEvent.KEYCODE_NUMPAD_4 -> if (keypadApplicationMode) "${ESC}Ot" else "4"
            KeyEvent.KEYCODE_NUMPAD_5 -> if (keypadApplicationMode) "${ESC}Ou" else "5"
            KeyEvent.KEYCODE_NUMPAD_6 -> if (keypadApplicationMode) "${ESC}Ov" else "6"
            KeyEvent.KEYCODE_NUMPAD_7 -> if (keypadApplicationMode) "${ESC}Ow" else "7"
            KeyEvent.KEYCODE_NUMPAD_8 -> if (keypadApplicationMode) "${ESC}Ox" else "8"
            KeyEvent.KEYCODE_NUMPAD_9 -> if (keypadApplicationMode) "${ESC}Oy" else "9"
            
            KeyEvent.KEYCODE_NUMPAD_DOT, KeyEvent.KEYCODE_NUMPAD_COMMA -> 
                if (keypadApplicationMode) "${ESC}On" else "."
            KeyEvent.KEYCODE_NUMPAD_DIVIDE -> if (keypadApplicationMode) "${ESC}Oo" else "/"
            KeyEvent.KEYCODE_NUMPAD_MULTIPLY -> if (keypadApplicationMode) "${ESC}Oj" else "*"
            KeyEvent.KEYCODE_NUMPAD_SUBTRACT -> if (keypadApplicationMode) "${ESC}Om" else "-"
            KeyEvent.KEYCODE_NUMPAD_ADD -> if (keypadApplicationMode) "${ESC}Ok" else "+"
            KeyEvent.KEYCODE_NUMPAD_EQUALS -> if (keypadApplicationMode) "${ESC}OX" else "="
            
            else -> null
        }
    }
    
    private fun getCursorCode(applicationMode: Boolean, code: Char, keyMod: Int): String {
        val modifiers = getModifiers(keyMod)
        return if (modifiers.isEmpty()) {
            if (applicationMode) "${ESC}O$code" else "${ESC}[$code"
        } else {
            "${ESC}[1;${modifiers}$code"
        }
    }
    
    private fun getFunctionCode(number: Int, keyMod: Int): String {
        val modifiers = getModifiers(keyMod)
        val code = when (number) {
            1 -> "11"
            2 -> "12"
            3 -> "13"
            4 -> "14"
            5 -> "15"
            6 -> "17"
            7 -> "18"
            8 -> "19"
            9 -> "20"
            10 -> "21"
            11 -> "23"
            12 -> "24"
            else -> return ""
        }
        
        return if (modifiers.isEmpty()) {
            "${ESC}[$code~"
        } else {
            "${ESC}[$code;${modifiers}~"
        }
    }
    
    private fun getModifiers(keyMod: Int): String {
        if (keyMod == 0) return ""
        
        var modifier = 1
        if ((keyMod and KEYMOD_SHIFT) != 0) modifier += 1
        if ((keyMod and KEYMOD_ALT) != 0) modifier += 2
        if ((keyMod and KEYMOD_CTRL) != 0) modifier += 4
        if ((keyMod and KEYMOD_META) != 0) modifier += 8
        
        return modifier.toString()
    }
    
    const val KEYMOD_SHIFT = 1
    const val KEYMOD_ALT = 2
    const val KEYMOD_CTRL = 4
    const val KEYMOD_META = 8
    const val KEYMOD_NUM_LOCK = 16
    
    fun getKeyModifiers(shiftDown: Boolean, altDown: Boolean, ctrlDown: Boolean, metaDown: Boolean, numLockOn: Boolean): Int {
        var result = 0
        if (shiftDown) result = result or KEYMOD_SHIFT
        if (altDown) result = result or KEYMOD_ALT
        if (ctrlDown) result = result or KEYMOD_CTRL
        if (metaDown) result = result or KEYMOD_META
        if (numLockOn) result = result or KEYMOD_NUM_LOCK
        return result
    }
    
    fun getCodeFromTermcap(termcapName: String, cursorKeysApplicationMode: Boolean, keypadApplicationMode: Boolean): String? {
        return when (termcapName) {
            "ku" -> getCursorCode(cursorKeysApplicationMode, 'A', 0)
            "kd" -> getCursorCode(cursorKeysApplicationMode, 'B', 0)
            "kr" -> getCursorCode(cursorKeysApplicationMode, 'C', 0)
            "kl" -> getCursorCode(cursorKeysApplicationMode, 'D', 0)
            "kh" -> "${ESC}[H"
            "@7" -> "${ESC}[F"
            "kD" -> "${ESC}[3~"
            "kI" -> "${ESC}[2~"
            "kN" -> "${ESC}[6~"
            "kP" -> "${ESC}[5~"
            else -> null
        }
    }
}
