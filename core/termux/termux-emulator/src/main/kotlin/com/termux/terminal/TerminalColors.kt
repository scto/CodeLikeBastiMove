package com.termux.terminal

class TerminalColors {
    
    companion object {
        const val NUM_INDEXED_COLORS = 256
        const val COLOR_INDEX_FOREGROUND = 256
        const val COLOR_INDEX_BACKGROUND = 257
        const val COLOR_INDEX_CURSOR = 258
    }
    
    private val currentColors = IntArray(NUM_INDEXED_COLORS + 3)
    
    init {
        reset()
    }
    
    fun reset() {
        for (i in 0 until 8) {
            currentColors[i] = getDefaultColor(i)
            currentColors[i + 8] = getDefaultColor(i + 8)
        }
        
        for (i in 16 until 232) {
            val index = i - 16
            val red = ((index / 36) % 6) * 51
            val green = ((index / 6) % 6) * 51
            val blue = (index % 6) * 51
            currentColors[i] = (0xFF shl 24) or (red shl 16) or (green shl 8) or blue
        }
        
        for (i in 232 until 256) {
            val gray = ((i - 232) * 10) + 8
            currentColors[i] = (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
        }
        
        currentColors[COLOR_INDEX_FOREGROUND] = 0xFFFFFFFF.toInt()
        currentColors[COLOR_INDEX_BACKGROUND] = 0xFF000000.toInt()
        currentColors[COLOR_INDEX_CURSOR] = 0xFFCCCCCC.toInt()
    }
    
    private fun getDefaultColor(index: Int): Int {
        return when (index) {
            0 -> 0xFF000000.toInt()
            1 -> 0xFFCD0000.toInt()
            2 -> 0xFF00CD00.toInt()
            3 -> 0xFFCDCD00.toInt()
            4 -> 0xFF0000EE.toInt()
            5 -> 0xFFCD00CD.toInt()
            6 -> 0xFF00CDCD.toInt()
            7 -> 0xFFE5E5E5.toInt()
            8 -> 0xFF7F7F7F.toInt()
            9 -> 0xFFFF0000.toInt()
            10 -> 0xFF00FF00.toInt()
            11 -> 0xFFFFFF00.toInt()
            12 -> 0xFF5C5CFF.toInt()
            13 -> 0xFFFF00FF.toInt()
            14 -> 0xFF00FFFF.toInt()
            15 -> 0xFFFFFFFF.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
    }
    
    fun getColor(colorIndex: Int): Int {
        return when {
            colorIndex < 0 -> currentColors[COLOR_INDEX_FOREGROUND]
            colorIndex < NUM_INDEXED_COLORS + 3 -> currentColors[colorIndex]
            else -> currentColors[COLOR_INDEX_FOREGROUND]
        }
    }
    
    fun setColor(colorIndex: Int, color: Int) {
        if (colorIndex in 0 until NUM_INDEXED_COLORS + 3) {
            currentColors[colorIndex] = color
        }
    }
    
    fun tryParseColor(colorSpec: String): Int? {
        if (colorSpec.startsWith("#")) {
            return try {
                val colorValue = colorSpec.substring(1).toLong(16)
                when (colorSpec.length) {
                    4 -> {
                        val r = ((colorValue shr 8) and 0xF) * 17
                        val g = ((colorValue shr 4) and 0xF) * 17
                        val b = (colorValue and 0xF) * 17
                        (0xFF shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
                    }
                    7 -> {
                        (0xFF shl 24) or colorValue.toInt()
                    }
                    else -> null
                }
            } catch (e: NumberFormatException) {
                null
            }
        }
        
        if (colorSpec.startsWith("rgb:")) {
            return try {
                val parts = colorSpec.substring(4).split("/")
                if (parts.size == 3) {
                    val r = (parts[0].toLong(16) * 255 / ((1L shl (parts[0].length * 4)) - 1)).toInt()
                    val g = (parts[1].toLong(16) * 255 / ((1L shl (parts[1].length * 4)) - 1)).toInt()
                    val b = (parts[2].toLong(16) * 255 / ((1L shl (parts[2].length * 4)) - 1)).toInt()
                    (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                } else null
            } catch (e: Exception) {
                null
            }
        }
        
        return null
    }
    
    fun getDefaultForeground(): Int = currentColors[COLOR_INDEX_FOREGROUND]
    fun getDefaultBackground(): Int = currentColors[COLOR_INDEX_BACKGROUND]
    fun getCursorColor(): Int = currentColors[COLOR_INDEX_CURSOR]
}

object ColorSchemes {
    
    val Default = ColorScheme(
        name = "Default",
        foreground = 0xFFFFFFFF.toInt(),
        background = 0xFF000000.toInt(),
        cursor = 0xFFCCCCCC.toInt()
    )
    
    val Monokai = ColorScheme(
        name = "Monokai",
        foreground = 0xFFF8F8F2.toInt(),
        background = 0xFF272822.toInt(),
        cursor = 0xFFF8F8F0.toInt(),
        colors = intArrayOf(
            0xFF272822.toInt(), 0xFFF92672.toInt(), 0xFFA6E22E.toInt(), 0xFFF4BF75.toInt(),
            0xFF66D9EF.toInt(), 0xFFAE81FF.toInt(), 0xFFA1EFE4.toInt(), 0xFFF8F8F2.toInt(),
            0xFF75715E.toInt(), 0xFFF92672.toInt(), 0xFFA6E22E.toInt(), 0xFFF4BF75.toInt(),
            0xFF66D9EF.toInt(), 0xFFAE81FF.toInt(), 0xFFA1EFE4.toInt(), 0xFFF9F8F5.toInt()
        )
    )
    
    val Dracula = ColorScheme(
        name = "Dracula",
        foreground = 0xFFF8F8F2.toInt(),
        background = 0xFF282A36.toInt(),
        cursor = 0xFFF8F8F2.toInt(),
        colors = intArrayOf(
            0xFF21222C.toInt(), 0xFFFF5555.toInt(), 0xFF50FA7B.toInt(), 0xFFF1FA8C.toInt(),
            0xFFBD93F9.toInt(), 0xFFFF79C6.toInt(), 0xFF8BE9FD.toInt(), 0xFFF8F8F2.toInt(),
            0xFF6272A4.toInt(), 0xFFFF6E6E.toInt(), 0xFF69FF94.toInt(), 0xFFFFFFA5.toInt(),
            0xFFD6ACFF.toInt(), 0xFFFF92DF.toInt(), 0xFFA4FFFF.toInt(), 0xFFFFFFFF.toInt()
        )
    )
    
    val SolarizedDark = ColorScheme(
        name = "Solarized Dark",
        foreground = 0xFF839496.toInt(),
        background = 0xFF002B36.toInt(),
        cursor = 0xFF93A1A1.toInt(),
        colors = intArrayOf(
            0xFF073642.toInt(), 0xFFDC322F.toInt(), 0xFF859900.toInt(), 0xFFB58900.toInt(),
            0xFF268BD2.toInt(), 0xFFD33682.toInt(), 0xFF2AA198.toInt(), 0xFFEEE8D5.toInt(),
            0xFF002B36.toInt(), 0xFFCB4B16.toInt(), 0xFF586E75.toInt(), 0xFF657B83.toInt(),
            0xFF839496.toInt(), 0xFF6C71C4.toInt(), 0xFF93A1A1.toInt(), 0xFFFDF6E3.toInt()
        )
    )
    
    val all = listOf(Default, Monokai, Dracula, SolarizedDark)
}

data class ColorScheme(
    val name: String,
    val foreground: Int,
    val background: Int,
    val cursor: Int,
    val colors: IntArray? = null
) {
    fun applyTo(terminalColors: TerminalColors) {
        terminalColors.setColor(TerminalColors.COLOR_INDEX_FOREGROUND, foreground)
        terminalColors.setColor(TerminalColors.COLOR_INDEX_BACKGROUND, background)
        terminalColors.setColor(TerminalColors.COLOR_INDEX_CURSOR, cursor)
        
        colors?.forEachIndexed { index, color ->
            if (index < 16) {
                terminalColors.setColor(index, color)
            }
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ColorScheme
        if (name != other.name) return false
        return true
    }
    
    override fun hashCode(): Int = name.hashCode()
}
