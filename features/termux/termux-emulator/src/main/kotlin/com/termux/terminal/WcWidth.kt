package com.termux.terminal

object WcWidth {
    
    fun width(codePoint: Int): Int {
        if (codePoint < 0x20) return -1
        if (codePoint < 0x7F) return 1
        if (codePoint in 0x7F..0x9F) return -1
        
        if (isZeroWidth(codePoint)) return 0
        
        if (isWide(codePoint)) return 2
        
        return 1
    }
    
    fun width(charSequence: CharSequence): Int {
        var width = 0
        var i = 0
        while (i < charSequence.length) {
            val codePoint = Character.codePointAt(charSequence, i)
            val charWidth = width(codePoint)
            if (charWidth >= 0) {
                width += charWidth
            }
            i += Character.charCount(codePoint)
        }
        return width
    }
    
    private fun isZeroWidth(codePoint: Int): Boolean {
        if (codePoint == 0x200B) return true
        if (codePoint == 0x200C) return true
        if (codePoint == 0x200D) return true
        if (codePoint == 0xFEFF) return true
        
        if (codePoint in 0x0300..0x036F) return true
        if (codePoint in 0x0483..0x0489) return true
        if (codePoint in 0x0591..0x05BD) return true
        if (codePoint in 0x05BF..0x05C7) return true
        if (codePoint in 0x0610..0x061A) return true
        if (codePoint in 0x064B..0x065F) return true
        if (codePoint in 0x0670..0x0670) return true
        if (codePoint in 0x06D6..0x06DC) return true
        if (codePoint in 0x06DF..0x06E4) return true
        if (codePoint in 0x06E7..0x06E8) return true
        if (codePoint in 0x06EA..0x06ED) return true
        
        if (codePoint in 0x1160..0x11FF) return true
        
        if (codePoint in 0xFE20..0xFE2F) return true
        
        if (codePoint in 0xE0100..0xE01EF) return true
        
        return false
    }
    
    private fun isWide(codePoint: Int): Boolean {
        if (codePoint in 0x1100..0x115F) return true
        if (codePoint in 0x231A..0x231B) return true
        if (codePoint in 0x2329..0x232A) return true
        if (codePoint in 0x23E9..0x23F3) return true
        if (codePoint in 0x23F8..0x23FA) return true
        
        if (codePoint in 0x2600..0x26FF) return true
        if (codePoint in 0x2700..0x27BF) return true
        if (codePoint in 0x2934..0x2935) return true
        if (codePoint in 0x25AA..0x25AB) return true
        if (codePoint in 0x25B6..0x25B6) return true
        if (codePoint in 0x25C0..0x25C0) return true
        if (codePoint in 0x25FB..0x25FE) return true
        
        if (codePoint in 0x2E80..0x2EFF) return true
        if (codePoint in 0x2F00..0x2FDF) return true
        if (codePoint in 0x2FF0..0x2FFF) return true
        if (codePoint in 0x3000..0x303E) return true
        if (codePoint in 0x3041..0x3096) return true
        if (codePoint in 0x3099..0x30FF) return true
        if (codePoint in 0x3105..0x312F) return true
        if (codePoint in 0x3131..0x318E) return true
        if (codePoint in 0x3190..0x31FF) return true
        if (codePoint in 0x3200..0x33FF) return true
        if (codePoint in 0x3400..0x4DBF) return true
        if (codePoint in 0x4E00..0x9FFF) return true
        if (codePoint in 0xA000..0xA4CF) return true
        
        if (codePoint in 0xAC00..0xD7A3) return true
        
        if (codePoint in 0xF900..0xFAFF) return true
        if (codePoint in 0xFE10..0xFE1F) return true
        if (codePoint in 0xFE30..0xFE6F) return true
        if (codePoint in 0xFF00..0xFF60) return true
        if (codePoint in 0xFFE0..0xFFE6) return true
        
        if (codePoint in 0x1F000..0x1FFFF) return true
        if (codePoint in 0x20000..0x2FFFF) return true
        if (codePoint in 0x30000..0x3FFFF) return true
        
        return false
    }
}
