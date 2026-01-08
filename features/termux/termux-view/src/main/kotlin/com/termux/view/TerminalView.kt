package com.termux.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.InputType
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.termux.terminal.TerminalBuffer
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TextStyle

class TerminalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    companion object {
        private const val TAG = "TerminalView"
        const val DEFAULT_FONT_SIZE = 14
    }
    
    private var terminalSession: TerminalSession? = null
    private var terminalViewClient: TerminalViewClient? = null
    
    private val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
    }
    
    private val backgroundPaint = Paint()
    private val cursorPaint = Paint()
    
    private var fontWidth: Float = 0f
    private var fontHeight: Float = 0f
    private var fontAscent: Float = 0f
    
    private var fontSize: Int = DEFAULT_FONT_SIZE
    
    private var columns: Int = 80
    private var rows: Int = 24
    
    private val colorScheme: TerminalColorScheme = TerminalColorScheme()
    
    private var selectionX1: Int = -1
    private var selectionY1: Int = -1
    private var selectionX2: Int = -1
    private var selectionY2: Int = -1
    private var isSelecting: Boolean = false
    
    private val gestureDetector: GestureDetector
    
    init {
        isFocusable = true
        isFocusableInTouchMode = true
        
        setTextSize(fontSize)
        
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                requestFocus()
                terminalViewClient?.onSingleTapUp(e)
                return true
            }
            
            override fun onDoubleTap(e: MotionEvent): Boolean {
                return true
            }
            
            override fun onLongPress(e: MotionEvent) {
                startTextSelection(e)
            }
            
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                return true
            }
            
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                return true
            }
        })
    }
    
    fun setTextSize(textSize: Int) {
        fontSize = textSize
        textPaint.textSize = textSize * resources.displayMetrics.density
        
        val metrics = textPaint.fontMetrics
        fontHeight = metrics.descent - metrics.ascent
        fontAscent = -metrics.ascent
        fontWidth = textPaint.measureText("M")
        
        updateTerminalSize()
        invalidate()
    }
    
    fun getTextSize(): Int = fontSize
    
    fun attachSession(session: TerminalSession) {
        terminalSession = session
        updateTerminalSize()
        invalidate()
    }
    
    fun setTerminalViewClient(client: TerminalViewClient) {
        terminalViewClient = client
    }
    
    fun getCurrentSession(): TerminalSession? = terminalSession
    
    private fun updateTerminalSize() {
        if (width > 0 && height > 0 && fontWidth > 0 && fontHeight > 0) {
            val newColumns = (width / fontWidth).toInt().coerceAtLeast(1)
            val newRows = (height / fontHeight).toInt().coerceAtLeast(1)
            
            if (newColumns != columns || newRows != rows) {
                columns = newColumns
                rows = newRows
                terminalSession?.updateSize(columns, rows)
            }
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateTerminalSize()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.drawColor(colorScheme.defaultBackground)
        
        val emulator = terminalSession?.getEmulator() ?: return
        val screen = emulator.getScreen()
        
        for (row in 0 until rows) {
            drawRow(canvas, screen, emulator, row)
        }
        
        if (emulator.isCursorVisible) {
            drawCursor(canvas, emulator)
        }
        
        if (isSelecting) {
            drawSelection(canvas)
        }
    }
    
    private fun drawRow(canvas: Canvas, screen: TerminalBuffer, emulator: TerminalEmulator, row: Int) {
        val y = row * fontHeight + fontAscent
        
        for (col in 0 until columns) {
            val x = col * fontWidth
            
            val char = screen.getChar(col, row)
            val style = screen.getStyle(col, row)
            
            val foreColor = TextStyle.decodeForeColor(style)
            val backColor = TextStyle.decodeBackColor(style)
            val effect = TextStyle.decodeEffect(style)
            
            val bgColor = if (backColor == TextStyle.COLOR_INDEX_BACKGROUND) {
                colorScheme.defaultBackground
            } else {
                colorScheme.getColor(backColor)
            }
            
            if (bgColor != colorScheme.defaultBackground) {
                backgroundPaint.color = bgColor
                canvas.drawRect(x, row * fontHeight, x + fontWidth, (row + 1) * fontHeight, backgroundPaint)
            }
            
            if (char != 0 && char != ' '.code) {
                val fgColor = if (foreColor == TextStyle.COLOR_INDEX_FOREGROUND) {
                    colorScheme.defaultForeground
                } else {
                    colorScheme.getColor(foreColor)
                }
                
                textPaint.color = fgColor
                textPaint.isFakeBoldText = TextStyle.isBold(effect)
                textPaint.textSkewX = if (TextStyle.isItalic(effect)) -0.25f else 0f
                textPaint.isUnderlineText = TextStyle.isUnderline(effect)
                textPaint.isStrikeThruText = TextStyle.isStrikethrough(effect)
                
                canvas.drawText(String(intArrayOf(char), 0, 1), x, y, textPaint)
            }
        }
    }
    
    private fun drawCursor(canvas: Canvas, emulator: TerminalEmulator) {
        val cursorCol = emulator.cursorCol
        val cursorRow = emulator.cursorRow
        
        val x = cursorCol * fontWidth
        val y = cursorRow * fontHeight
        
        cursorPaint.color = colorScheme.cursorColor
        
        when (emulator.cursorStyle) {
            TerminalEmulator.CURSOR_STYLE_BLOCK -> {
                canvas.drawRect(x, y, x + fontWidth, y + fontHeight, cursorPaint)
            }
            TerminalEmulator.CURSOR_STYLE_UNDERLINE -> {
                val underlineHeight = fontHeight * 0.1f
                canvas.drawRect(x, y + fontHeight - underlineHeight, x + fontWidth, y + fontHeight, cursorPaint)
            }
            TerminalEmulator.CURSOR_STYLE_BAR -> {
                val barWidth = fontWidth * 0.1f
                canvas.drawRect(x, y, x + barWidth, y + fontHeight, cursorPaint)
            }
        }
    }
    
    private fun drawSelection(canvas: Canvas) {
        val selectionPaint = Paint().apply {
            color = colorScheme.selectionBackground
        }
        
        for (row in selectionY1..selectionY2) {
            val startCol = if (row == selectionY1) selectionX1 else 0
            val endCol = if (row == selectionY2) selectionX2 else columns
            
            val x1 = startCol * fontWidth
            val x2 = endCol * fontWidth
            val y1 = row * fontHeight
            val y2 = (row + 1) * fontHeight
            
            canvas.drawRect(x1, y1, x2, y2, selectionPaint)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        terminalViewClient?.onKeyDown(keyCode, event, terminalSession)
        return true
    }
    
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        terminalViewClient?.onKeyUp(keyCode, event)
        return true
    }
    
    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        outAttrs.inputType = InputType.TYPE_NULL
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        return TerminalInputConnection(this)
    }
    
    override fun onCheckIsTextEditor(): Boolean = true
    
    private fun startTextSelection(e: MotionEvent) {
        val col = (e.x / fontWidth).toInt().coerceIn(0, columns - 1)
        val row = (e.y / fontHeight).toInt().coerceIn(0, rows - 1)
        
        selectionX1 = col
        selectionY1 = row
        selectionX2 = col
        selectionY2 = row
        isSelecting = true
        
        invalidate()
    }
    
    fun stopTextSelection() {
        isSelecting = false
        selectionX1 = -1
        selectionY1 = -1
        selectionX2 = -1
        selectionY2 = -1
        invalidate()
    }
    
    fun getSelectedText(): String? {
        if (!isSelecting || terminalSession == null) return null
        return terminalSession?.getEmulator()?.getSelectedText(selectionX1, selectionY1, selectionX2, selectionY2)
    }
    
    fun onScreenUpdated() {
        post { invalidate() }
    }
    
    private inner class TerminalInputConnection(view: View) : BaseInputConnection(view, true) {
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            text?.toString()?.let { terminalSession?.write(it) }
            return true
        }
        
        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            repeat(beforeLength) {
                terminalSession?.write(byteArrayOf(0x7F))
            }
            return true
        }
        
        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN) {
                return onKeyDown(event.keyCode, event)
            }
            return super.sendKeyEvent(event)
        }
    }
}

interface TerminalViewClient {
    fun onScale(scale: Float): Float
    fun onSingleTapUp(e: MotionEvent)
    fun shouldBackButtonBeMappedToEscape(): Boolean
    fun shouldEnforceCharBasedInput(): Boolean
    fun shouldUseCtrlSpaceWorkaround(): Boolean
    fun isTerminalViewSelected(): Boolean
    fun copyModeChanged(copyMode: Boolean)
    fun onKeyDown(keyCode: Int, e: KeyEvent, session: TerminalSession?): Boolean
    fun onKeyUp(keyCode: Int, e: KeyEvent): Boolean
    fun onLongPress(e: MotionEvent): Boolean
    fun readControlKey(): Boolean
    fun readAltKey(): Boolean
    fun readShiftKey(): Boolean
    fun readFnKey(): Boolean
    fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession?): Boolean
    fun onEmulatorSet()
}

class TerminalColorScheme {
    var defaultForeground: Int = 0xFFFFFFFF.toInt()
    var defaultBackground: Int = 0xFF000000.toInt()
    var cursorColor: Int = 0xFFFFFFFF.toInt()
    var selectionBackground: Int = 0x80FFFFFF.toInt()
    
    private val colors = IntArray(256) { index ->
        when {
            index < 16 -> getAnsiColor(index)
            index < 232 -> get256Color(index - 16)
            else -> getGrayscaleColor(index - 232)
        }
    }
    
    fun getColor(index: Int): Int {
        return when (index) {
            TextStyle.COLOR_INDEX_FOREGROUND -> defaultForeground
            TextStyle.COLOR_INDEX_BACKGROUND -> defaultBackground
            TextStyle.COLOR_INDEX_CURSOR -> cursorColor
            in 0 until 256 -> colors[index]
            else -> defaultForeground
        }
    }
    
    private fun getAnsiColor(index: Int): Int {
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
    
    private fun get256Color(index: Int): Int {
        val r = ((index / 36) % 6) * 51
        val g = ((index / 6) % 6) * 51
        val b = (index % 6) * 51
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
    
    private fun getGrayscaleColor(index: Int): Int {
        val gray = index * 10 + 8
        return (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
    }
}
