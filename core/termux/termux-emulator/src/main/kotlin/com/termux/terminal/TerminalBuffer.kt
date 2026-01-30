package com.termux.terminal

class TerminalBuffer(
  private var columns: Int,
  private var totalRows: Int,
  private var transcriptRows: Int,
) {
  private var screenBuffer: Array<TerminalRow> =
    Array(totalRows) { TerminalRow(columns, TextStyle.NORMAL) }
  private var activeTranscriptRows: Int = 0
  private var screenFirstRow: Int = 0

  fun resize(
    newColumns: Int,
    newRows: Int,
    cursorRow: Int,
    cursorCol: Int,
    foreColor: Int,
    backColor: Int,
  ) {
    if (newColumns == columns && newRows == totalRows) return

    val newBuffer =
      Array(newRows) { TerminalRow(newColumns, TextStyle.encode(foreColor, backColor, 0)) }

    val copyRows = minOf(totalRows, newRows)
    for (i in 0 until copyRows) {
      val srcRowIndex = externalToInternalRow(i)
      if (srcRowIndex >= 0 && srcRowIndex < screenBuffer.size) {
        val srcRow = screenBuffer[srcRowIndex]
        val copyColumns = minOf(columns, newColumns)
        for (j in 0 until copyColumns) {
          newBuffer[i].setChar(j, srcRow.getChar(j), srcRow.getStyle(j))
        }
      }
    }

    screenBuffer = newBuffer
    columns = newColumns
    totalRows = newRows
    screenFirstRow = 0
    activeTranscriptRows = 0
  }

  fun setChar(column: Int, row: Int, codePoint: Int, style: Long) {
    if (row < 0 || row >= totalRows || column < 0 || column >= columns) return
    val internalRow = externalToInternalRow(row)
    screenBuffer[internalRow].setChar(column, codePoint, style)
  }

  fun getChar(column: Int, row: Int): Int {
    if (row < 0 || row >= totalRows || column < 0 || column >= columns) return ' '.code
    val internalRow = externalToInternalRow(row)
    return screenBuffer[internalRow].getChar(column)
  }

  fun getStyle(column: Int, row: Int): Long {
    if (row < 0 || row >= totalRows || column < 0 || column >= columns) return TextStyle.NORMAL
    val internalRow = externalToInternalRow(row)
    return screenBuffer[internalRow].getStyle(column)
  }

  fun getLineText(row: Int): String {
    if (row < 0 || row >= totalRows) return ""
    val internalRow = externalToInternalRow(row)
    return screenBuffer[internalRow].getText()
  }

  fun scrollDownOneLine(
    topMargin: Int,
    bottomMargin: Int,
    leftMargin: Int,
    rightMargin: Int,
    style: Long,
  ) {
    if (leftMargin == 0 && rightMargin == columns) {
      val internalTop = externalToInternalRow(topMargin)
      screenBuffer[internalTop] = TerminalRow(columns, style)
      screenFirstRow = (screenFirstRow + 1) % totalRows
    } else {
      for (row in topMargin until bottomMargin - 1) {
        val destInternalRow = externalToInternalRow(row)
        val srcInternalRow = externalToInternalRow(row + 1)
        for (col in leftMargin until rightMargin) {
          screenBuffer[destInternalRow].setChar(
            col,
            screenBuffer[srcInternalRow].getChar(col),
            screenBuffer[srcInternalRow].getStyle(col),
          )
        }
      }
      val lastRow = externalToInternalRow(bottomMargin - 1)
      for (col in leftMargin until rightMargin) {
        screenBuffer[lastRow].setChar(col, ' '.code, style)
      }
    }
  }

  fun scrollUpOneLine(
    topMargin: Int,
    bottomMargin: Int,
    leftMargin: Int,
    rightMargin: Int,
    style: Long,
  ) {
    for (row in bottomMargin - 1 downTo topMargin + 1) {
      val destInternalRow = externalToInternalRow(row)
      val srcInternalRow = externalToInternalRow(row - 1)
      for (col in leftMargin until rightMargin) {
        screenBuffer[destInternalRow].setChar(
          col,
          screenBuffer[srcInternalRow].getChar(col),
          screenBuffer[srcInternalRow].getStyle(col),
        )
      }
    }
    val firstRow = externalToInternalRow(topMargin)
    for (col in leftMargin until rightMargin) {
      screenBuffer[firstRow].setChar(col, ' '.code, style)
    }
  }

  fun blockClear(
    startCol: Int,
    startRow: Int,
    endCol: Int,
    endRow: Int,
    style: Long = TextStyle.NORMAL,
  ) {
    for (row in startRow until endRow) {
      val internalRow = externalToInternalRow(row)
      for (col in startCol until endCol) {
        screenBuffer[internalRow].setChar(col, ' '.code, style)
      }
    }
  }

  fun blockClear(startCol: Int, startRow: Int, endCol: Int, endRow: Int) {
    blockClear(startCol, startRow, endCol, endRow, TextStyle.NORMAL)
  }

  fun clearTranscript() {
    activeTranscriptRows = 0
  }

  fun getActiveTranscriptRows(): Int = activeTranscriptRows

  fun getActiveRows(): Int = totalRows + activeTranscriptRows

  private fun externalToInternalRow(externalRow: Int): Int {
    return (screenFirstRow + externalRow) % totalRows
  }

  fun getSelectedText(selX1: Int, selY1: Int, selX2: Int, selY2: Int): String {
    val sb = StringBuilder()

    for (row in selY1..selY2) {
      val startCol = if (row == selY1) selX1 else 0
      val endCol = if (row == selY2) selX2 else columns

      for (col in startCol until endCol) {
        val char = getChar(col, row)
        if (char != 0) {
          sb.appendCodePoint(char)
        }
      }

      if (row < selY2) {
        sb.append('\n')
      }
    }

    return sb.toString()
  }
}

class TerminalRow(private val columns: Int, style: Long) {
  private val chars: IntArray = IntArray(columns) { ' '.code }
  private val styles: LongArray = LongArray(columns) { style }

  fun setChar(column: Int, codePoint: Int, style: Long) {
    if (column in 0 until columns) {
      chars[column] = codePoint
      styles[column] = style
    }
  }

  fun getChar(column: Int): Int = if (column in 0 until columns) chars[column] else ' '.code

  fun getStyle(column: Int): Long =
    if (column in 0 until columns) styles[column] else TextStyle.NORMAL

  fun getText(): String {
    val sb = StringBuilder()
    for (i in 0 until columns) {
      if (chars[i] != 0 && chars[i] != ' '.code) {
        sb.appendCodePoint(chars[i])
      } else if (sb.isNotEmpty() || chars[i] == ' '.code) {
        sb.append(' ')
      }
    }
    return sb.toString().trimEnd()
  }

  fun clear(style: Long) {
    for (i in 0 until columns) {
      chars[i] = ' '.code
      styles[i] = style
    }
  }
}
