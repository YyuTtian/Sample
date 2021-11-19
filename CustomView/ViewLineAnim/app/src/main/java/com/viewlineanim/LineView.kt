package com.viewlineanim

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class LineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path.reset()
        path.moveTo(0f, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()

        paint.strokeWidth = Util.dp2pxFloat(6f)
        paint.color = Color.parseColor("#FBD14E")
        paint.style = Paint.Style.STROKE

        start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isStart) {
            paint.pathEffect = DashPathEffect(floatArrayOf(width + height.toFloat(), width + height.toFloat()), animValue)
            canvas.drawPath(path, paint)
        }
    }

    private var animValue = 0f
    private var isStart = false
    private lateinit var anim: ValueAnimator

    private fun start() {
        stop()
        isStart = true
        anim = ValueAnimator.ofFloat(0f, width * 2f + height * 2f).setDuration(2 * 1000)
        anim.addUpdateListener { animation ->
            animValue = animation.animatedValue as Float
            invalidate()
        }
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.start()
    }


    fun stop() {
        if (::anim.isInitialized) {
            anim.cancel()
        }
    }
}