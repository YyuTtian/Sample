package com.objectdetect

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TipView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init {
        paint.color = Color.RED
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        path.reset()
        path.moveTo(rect.left.toFloat(), rect.top.toFloat())
        path.lineTo(rect.right.toFloat(), rect.top.toFloat())
        path.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
        path.lineTo(rect.left.toFloat(), rect.bottom.toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private var rect = Rect()

    fun setRect(rect: Rect) {
        this.rect = rect
        invalidate()
    }
}