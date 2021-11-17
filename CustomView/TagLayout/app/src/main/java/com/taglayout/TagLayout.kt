package com.taglayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

class TagLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val childRect = ArrayList<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthUse = 0
        var heightUse = 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        var selfWidth = 0
        var selfHeight = 0

        var lineMaxHeight = 0

        for (child in children) {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUse)

            if (child.measuredWidth + widthUse > widthSize) {
                // 另起一行

                heightUse += lineMaxHeight

                val rect = Rect()
                rect.left = 0
                rect.top = heightUse
                rect.right = child.measuredWidth
                rect.bottom = heightUse + child.measuredHeight
                childRect.add(rect)

                widthUse = child.measuredWidth
                lineMaxHeight = child.measuredHeight
            } else {
                val rect = Rect()
                rect.left = widthUse
                rect.top = heightUse
                rect.right = widthUse + child.measuredWidth
                rect.bottom = heightUse + child.measuredHeight
                childRect.add(rect)
                widthUse += child.measuredWidth

                lineMaxHeight = max(lineMaxHeight, child.measuredHeight)
            }

            selfWidth = max(selfWidth, widthUse)
            selfHeight = max(selfHeight, heightUse + lineMaxHeight)
        }

        setMeasuredDimension(selfWidth, selfHeight)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        for ((index, child) in children.withIndex()) {
            val rect = childRect[index]
            println("rect=$rect")
            child.layout(rect.left, rect.top, rect.right, rect.bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

}