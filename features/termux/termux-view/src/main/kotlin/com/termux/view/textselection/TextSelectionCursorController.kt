package com.termux.view.textselection

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import com.termux.view.TerminalView

class TextSelectionCursorController(
    private val terminalView: TerminalView
) {
    
    private var isSelecting = false
    private var selectionStartX = -1
    private var selectionStartY = -1
    private var selectionEndX = -1
    private var selectionEndY = -1
    
    private val handlePaint = Paint().apply {
        isAntiAlias = true
        color = 0xFF2196F3.toInt()
    }
    
    private val handleRadius = 12f
    
    private var isDraggingStart = false
    private var isDraggingEnd = false
    
    fun startSelection(x: Int, y: Int) {
        selectionStartX = x
        selectionStartY = y
        selectionEndX = x
        selectionEndY = y
        isSelecting = true
    }
    
    fun updateSelection(x: Int, y: Int) {
        if (!isSelecting) return
        
        selectionEndX = x
        selectionEndY = y
        
        normalizeSelection()
    }
    
    fun stopSelection() {
        isSelecting = false
        selectionStartX = -1
        selectionStartY = -1
        selectionEndX = -1
        selectionEndY = -1
        isDraggingStart = false
        isDraggingEnd = false
    }
    
    fun isSelecting(): Boolean = isSelecting
    
    fun getSelectionBounds(): SelectionBounds? {
        if (!isSelecting) return null
        
        return SelectionBounds(
            startX = selectionStartX,
            startY = selectionStartY,
            endX = selectionEndX,
            endY = selectionEndY
        )
    }
    
    private fun normalizeSelection() {
        if (selectionStartY > selectionEndY || 
            (selectionStartY == selectionEndY && selectionStartX > selectionEndX)) {
            val tmpX = selectionStartX
            val tmpY = selectionStartY
            selectionStartX = selectionEndX
            selectionStartY = selectionEndY
            selectionEndX = tmpX
            selectionEndY = tmpY
        }
    }
    
    fun onTouchEvent(event: MotionEvent, fontWidth: Float, fontHeight: Float): Boolean {
        if (!isSelecting) return false
        
        val touchX = (event.x / fontWidth).toInt()
        val touchY = (event.y / fontHeight).toInt()
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isNearHandle(touchX, touchY, selectionStartX, selectionStartY)) {
                    isDraggingStart = true
                    return true
                } else if (isNearHandle(touchX, touchY, selectionEndX, selectionEndY)) {
                    isDraggingEnd = true
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDraggingStart) {
                    selectionStartX = touchX
                    selectionStartY = touchY
                    normalizeSelection()
                    return true
                } else if (isDraggingEnd) {
                    selectionEndX = touchX
                    selectionEndY = touchY
                    normalizeSelection()
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDraggingStart = false
                isDraggingEnd = false
            }
        }
        
        return false
    }
    
    private fun isNearHandle(touchX: Int, touchY: Int, handleX: Int, handleY: Int): Boolean {
        val dx = touchX - handleX
        val dy = touchY - handleY
        return dx * dx + dy * dy <= 4
    }
    
    fun drawHandles(canvas: Canvas, fontWidth: Float, fontHeight: Float) {
        if (!isSelecting) return
        
        val startHandleX = selectionStartX * fontWidth
        val startHandleY = (selectionStartY + 1) * fontHeight
        canvas.drawCircle(startHandleX, startHandleY, handleRadius, handlePaint)
        
        val endHandleX = (selectionEndX + 1) * fontWidth
        val endHandleY = (selectionEndY + 1) * fontHeight
        canvas.drawCircle(endHandleX, endHandleY, handleRadius, handlePaint)
    }
}

data class SelectionBounds(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int
)
