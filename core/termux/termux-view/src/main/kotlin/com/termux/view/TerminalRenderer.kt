package com.termux.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.termux.terminal.TerminalBuffer
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TextStyle

class TerminalRenderer(private var textSize: Float, typeface: Typeface = Typeface.MONOSPACE) {

  private val textPaint =
    Paint().apply {
      isAntiAlias = true
      this.typeface = typeface
      this.textSize = this@TerminalRenderer.textSize
    }

  private val backgroundPaint = Paint()
  private val cursorPaint = Paint()

  var fontWidth: Float = 0f
    private set

  var fontHeight: Float = 0f
    private set

  var fontAscent: Float = 0f
    private set

  var fontDescent: Float = 0f
    private set

  var fontLineSpacing: Float = 0f
    private set

  private val colorScheme = TerminalColorScheme()

  init {
    updateFontMetrics()
  }

  fun setTextSize(size: Float) {
    textSize = size
    textPaint.textSize = size
    updateFontMetrics()
  }

  fun setTypeface(typeface: Typeface) {
    textPaint.typeface = typeface
    updateFontMetrics()
  }

  private fun updateFontMetrics() {
    val metrics = textPaint.fontMetrics
    fontHeight = metrics.descent - metrics.ascent
    fontAscent = -metrics.ascent
    fontDescent = metrics.descent
    fontLineSpacing = fontHeight + metrics.leading
    fontWidth = textPaint.measureText("M")
  }

  fun getColorScheme(): TerminalColorScheme = colorScheme

  fun render(
    canvas: Canvas,
    emulator: TerminalEmulator,
    topRow: Int,
    leftColumn: Int,
    columns: Int,
    rows: Int,
  ) {
    canvas.drawColor(colorScheme.defaultBackground)

    val screen = emulator.getScreen()

    for (row in 0 until rows) {
      renderRow(canvas, screen, emulator, row, topRow + row, leftColumn, columns)
    }

    if (emulator.isCursorVisible) {
      renderCursor(canvas, emulator)
    }
  }

  private fun renderRow(
    canvas: Canvas,
    screen: TerminalBuffer,
    emulator: TerminalEmulator,
    displayRow: Int,
    bufferRow: Int,
    leftColumn: Int,
    columns: Int,
  ) {
    val y = displayRow * fontHeight + fontAscent

    for (col in 0 until columns) {
      val bufferCol = leftColumn + col
      val x = col * fontWidth

      val char = screen.getChar(bufferCol, bufferRow)
      val style = screen.getStyle(bufferCol, bufferRow)

      renderCell(canvas, char, style, x, y, displayRow)
    }
  }

  private fun renderCell(
    canvas: Canvas,
    codePoint: Int,
    style: Long,
    x: Float,
    y: Float,
    row: Int,
  ) {
    val foreColor = TextStyle.decodeForeColor(style)
    val backColor = TextStyle.decodeBackColor(style)
    val effect = TextStyle.decodeEffect(style)

    val isInverse = TextStyle.isInverse(effect)

    var fgColor =
      if (foreColor == TextStyle.COLOR_INDEX_FOREGROUND) {
        colorScheme.defaultForeground
      } else {
        colorScheme.getColor(foreColor)
      }

    var bgColor =
      if (backColor == TextStyle.COLOR_INDEX_BACKGROUND) {
        colorScheme.defaultBackground
      } else {
        colorScheme.getColor(backColor)
      }

    if (isInverse) {
      val tmp = fgColor
      fgColor = bgColor
      bgColor = tmp
    }

    if (TextStyle.isInvisible(effect)) {
      fgColor = bgColor
    }

    if (bgColor != colorScheme.defaultBackground) {
      backgroundPaint.color = bgColor
      canvas.drawRect(x, row * fontHeight, x + fontWidth, (row + 1) * fontHeight, backgroundPaint)
    }

    if (codePoint != 0 && codePoint != ' '.code) {
      textPaint.color = fgColor
      textPaint.isFakeBoldText = TextStyle.isBold(effect)
      textPaint.textSkewX = if (TextStyle.isItalic(effect)) -0.25f else 0f
      textPaint.isUnderlineText = TextStyle.isUnderline(effect)
      textPaint.isStrikeThruText = TextStyle.isStrikethrough(effect)

      if (TextStyle.isDim(effect)) {
        textPaint.alpha = 128
      } else {
        textPaint.alpha = 255
      }

      val text = String(intArrayOf(codePoint), 0, 1)
      canvas.drawText(text, x, y, textPaint)
    }
  }

  private fun renderCursor(canvas: Canvas, emulator: TerminalEmulator) {
    val cursorCol = emulator.cursorCol
    val cursorRow = emulator.cursorRow

    val x = cursorCol * fontWidth
    val y = cursorRow * fontHeight

    cursorPaint.color = colorScheme.cursorColor

    when (emulator.cursorStyle) {
      TerminalEmulator.CURSOR_STYLE_BLOCK -> {
        cursorPaint.style = Paint.Style.FILL
        canvas.drawRect(x, y, x + fontWidth, y + fontHeight, cursorPaint)
      }
      TerminalEmulator.CURSOR_STYLE_UNDERLINE -> {
        val underlineHeight = fontHeight * 0.15f
        cursorPaint.style = Paint.Style.FILL
        canvas.drawRect(
          x,
          y + fontHeight - underlineHeight,
          x + fontWidth,
          y + fontHeight,
          cursorPaint,
        )
      }
      TerminalEmulator.CURSOR_STYLE_BAR -> {
        val barWidth = fontWidth * 0.15f
        cursorPaint.style = Paint.Style.FILL
        canvas.drawRect(x, y, x + barWidth, y + fontHeight, cursorPaint)
      }
    }
  }

  fun renderSelection(canvas: Canvas, startX: Int, startY: Int, endX: Int, endY: Int) {
    val selectionPaint = Paint().apply { color = colorScheme.selectionBackground }

    for (row in startY..endY) {
      val colStart = if (row == startY) startX else 0
      val colEnd = if (row == endY) endX else Int.MAX_VALUE

      val x1 = colStart * fontWidth
      val x2 = colEnd * fontWidth
      val y1 = row * fontHeight
      val y2 = (row + 1) * fontHeight

      canvas.drawRect(x1, y1, x2, y2, selectionPaint)
    }
  }
}
