package com.termux.terminal

class TerminalEmulator(
    private val session: TerminalSession,
    columns: Int,
    rows: Int,
    transcriptRows: Int
) {
    companion object {
        const val MOUSE_LEFT_BUTTON = 0
        const val MOUSE_MIDDLE_BUTTON = 1
        const val MOUSE_RIGHT_BUTTON = 2
        const val MOUSE_WHEEL_UP = 64
        const val MOUSE_WHEEL_DOWN = 65
        
        const val CURSOR_STYLE_BLOCK = 0
        const val CURSOR_STYLE_UNDERLINE = 1
        const val CURSOR_STYLE_BAR = 2
        
        const val DEFAULT_TERMINAL_TRANSCRIPT_ROWS = 2000
    }
    
    private val screen: TerminalBuffer = TerminalBuffer(columns, rows, transcriptRows)
    private var altBuffer: TerminalBuffer? = null
    
    var cursorRow: Int = 0
        private set
    var cursorCol: Int = 0
        private set
    
    var cursorStyle: Int = CURSOR_STYLE_BLOCK
        private set
    var cursorBlink: Boolean = false
        private set
    
    var foreColor: Int = TextStyle.COLOR_INDEX_FOREGROUND
        private set
    var backColor: Int = TextStyle.COLOR_INDEX_BACKGROUND
        private set
    
    var effect: Int = 0
        private set
    
    var isAlternateBufferActive: Boolean = false
        private set
    
    var savedCursorRow: Int = 0
        private set
    var savedCursorCol: Int = 0
        private set
    
    var rows: Int = rows
        private set
    var columns: Int = columns
        private set
    
    private var topMargin: Int = 0
    private var bottomMargin: Int = rows
    
    private var leftMargin: Int = 0
    private var rightMargin: Int = columns
    
    var title: String = ""
        private set
    
    var isCursorVisible: Boolean = true
        private set
    
    var isAutoWrap: Boolean = true
        private set
    
    private var isDecsetInternalBitSet: Boolean = false
    private var tabStop: BooleanArray = BooleanArray(columns)
    
    init {
        reset()
    }
    
    fun getScreen(): TerminalBuffer = if (isAlternateBufferActive && altBuffer != null) altBuffer!! else screen
    
    fun reset() {
        cursorRow = 0
        cursorCol = 0
        cursorStyle = CURSOR_STYLE_BLOCK
        cursorBlink = false
        
        foreColor = TextStyle.COLOR_INDEX_FOREGROUND
        backColor = TextStyle.COLOR_INDEX_BACKGROUND
        effect = 0
        
        topMargin = 0
        bottomMargin = rows
        leftMargin = 0
        rightMargin = columns
        
        title = ""
        isCursorVisible = true
        isAutoWrap = true
        
        tabStop = BooleanArray(columns) { i -> i > 0 && i % 8 == 0 }
        
        screen.clearTranscript()
        screen.blockClear(0, 0, columns, rows)
    }
    
    fun resize(newColumns: Int, newRows: Int) {
        if (newColumns == columns && newRows == rows) return
        
        val oldColumns = columns
        val oldRows = rows
        
        columns = newColumns
        rows = newRows
        
        screen.resize(newColumns, newRows, cursorRow, cursorCol, foreColor, backColor)
        altBuffer?.resize(newColumns, newRows, cursorRow, cursorCol, foreColor, backColor)
        
        tabStop = BooleanArray(newColumns) { i -> i > 0 && i % 8 == 0 }
        
        topMargin = 0
        bottomMargin = newRows
        leftMargin = 0
        rightMargin = newColumns
        
        cursorRow = cursorRow.coerceIn(0, newRows - 1)
        cursorCol = cursorCol.coerceIn(0, newColumns - 1)
    }
    
    fun setCursor(row: Int, col: Int) {
        cursorRow = row.coerceIn(0, rows - 1)
        cursorCol = col.coerceIn(0, columns - 1)
    }
    
    fun setCursorStyle(style: Int, blink: Boolean) {
        cursorStyle = style
        cursorBlink = blink
    }
    
    fun setCursorVisible(visible: Boolean) {
        isCursorVisible = visible
    }
    
    fun setAutoWrap(enabled: Boolean) {
        isAutoWrap = enabled
    }
    
    fun setTitle(newTitle: String) {
        title = newTitle
        session.notifyTitleChanged()
    }
    
    fun useAlternateBuffer(use: Boolean) {
        if (use && !isAlternateBufferActive) {
            altBuffer = TerminalBuffer(columns, rows, 0)
            isAlternateBufferActive = true
        } else if (!use && isAlternateBufferActive) {
            altBuffer = null
            isAlternateBufferActive = false
        }
    }
    
    fun saveCursor() {
        savedCursorRow = cursorRow
        savedCursorCol = cursorCol
    }
    
    fun restoreCursor() {
        cursorRow = savedCursorRow.coerceIn(0, rows - 1)
        cursorCol = savedCursorCol.coerceIn(0, columns - 1)
    }
    
    fun process(bytes: ByteArray, length: Int) {
        for (i in 0 until length) {
            processByte(bytes[i])
        }
    }
    
    private fun processByte(byte: Byte) {
        val char = byte.toInt() and 0xFF
        
        when (char) {
            0x07 -> session.notifyBell()
            0x08 -> if (cursorCol > 0) cursorCol--
            0x09 -> cursorCol = findNextTabStop(cursorCol)
            0x0A, 0x0B, 0x0C -> doLineFeed()
            0x0D -> cursorCol = 0
            0x1B -> {}
            else -> {
                if (char >= 0x20) {
                    emitChar(char.toChar())
                }
            }
        }
    }
    
    private fun emitChar(char: Char) {
        if (cursorCol >= columns) {
            if (isAutoWrap) {
                cursorCol = 0
                doLineFeed()
            } else {
                cursorCol = columns - 1
            }
        }
        
        getScreen().setChar(cursorCol, cursorRow, char.code, TextStyle.encode(foreColor, backColor, effect))
        cursorCol++
    }
    
    private fun doLineFeed() {
        val belowScrollingRegion = cursorRow >= bottomMargin
        val newCursorRow = cursorRow + 1
        
        if (belowScrollingRegion) {
            cursorRow = newCursorRow.coerceAtMost(rows - 1)
        } else if (newCursorRow == bottomMargin) {
            getScreen().scrollDownOneLine(topMargin, bottomMargin, leftMargin, rightMargin, 
                TextStyle.encode(foreColor, backColor, 0))
        } else {
            cursorRow = newCursorRow
        }
    }
    
    private fun findNextTabStop(col: Int): Int {
        for (i in col + 1 until columns) {
            if (tabStop[i]) return i
        }
        return columns - 1
    }
    
    fun getSelectedText(selX1: Int, selY1: Int, selX2: Int, selY2: Int): String {
        return getScreen().getSelectedText(selX1, selY1, selX2, selY2)
    }
}
