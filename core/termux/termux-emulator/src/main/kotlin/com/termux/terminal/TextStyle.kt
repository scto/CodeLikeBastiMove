package com.termux.terminal

object TextStyle {
    const val NORMAL: Long = 0L
    
    const val CHARACTER_ATTRIBUTE_BOLD = 1
    const val CHARACTER_ATTRIBUTE_ITALIC = 1 shl 1
    const val CHARACTER_ATTRIBUTE_UNDERLINE = 1 shl 2
    const val CHARACTER_ATTRIBUTE_BLINK = 1 shl 3
    const val CHARACTER_ATTRIBUTE_INVERSE = 1 shl 4
    const val CHARACTER_ATTRIBUTE_INVISIBLE = 1 shl 5
    const val CHARACTER_ATTRIBUTE_STRIKETHROUGH = 1 shl 6
    const val CHARACTER_ATTRIBUTE_DIM = 1 shl 7
    
    const val COLOR_INDEX_FOREGROUND = 256
    const val COLOR_INDEX_BACKGROUND = 257
    const val COLOR_INDEX_CURSOR = 258
    
    const val NUM_INDEXED_COLORS = 256
    
    fun encode(foreColor: Int, backColor: Int, effect: Int): Long {
        return (foreColor.toLong() and 0x1FF) or
               ((backColor.toLong() and 0x1FF) shl 9) or
               ((effect.toLong() and 0xFF) shl 18)
    }
    
    fun decodeForeColor(style: Long): Int = (style and 0x1FF).toInt()
    
    fun decodeBackColor(style: Long): Int = ((style shr 9) and 0x1FF).toInt()
    
    fun decodeEffect(style: Long): Int = ((style shr 18) and 0xFF).toInt()
    
    fun isBold(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_BOLD) != 0
    fun isItalic(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_ITALIC) != 0
    fun isUnderline(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_UNDERLINE) != 0
    fun isBlink(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_BLINK) != 0
    fun isInverse(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_INVERSE) != 0
    fun isInvisible(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_INVISIBLE) != 0
    fun isStrikethrough(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_STRIKETHROUGH) != 0
    fun isDim(effect: Int): Boolean = (effect and CHARACTER_ATTRIBUTE_DIM) != 0
}
