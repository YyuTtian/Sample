package com.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat

class NestedLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent2 {

    private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)

    private val TAG = "NestedLayout"

    private var consumeSize = 0
    private var totalSize = 0
    fun initParams(consumeSize: Int, totalSize: Int = 0) {
        this.consumeSize = consumeSize
        this.totalSize = totalSize
        if (this.totalSize == 0) {
            this.totalSize = consumeSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val child = getChildAt(1)
        val params = child.layoutParams
        params.height = measuredHeight - (totalSize - consumeSize)
        child.layoutParams = params
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 对NestedScrollingChild发起嵌套滑动作出应答
     * @param child 布局中包含下面target的直接父View
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param axes 滑动方向  ViewCompat.SCROLL_AXIS_NONE ViewCompat.SCROLL_AXIS_HORIZONTAL ViewCompat.SCROLL_AXIS_VERTICAL
     * @param type 触摸类型  ViewCompat.TYPE_TOUCH ViewCompat.TYPE_NON_TOUCH
     * @return 返回NestedScrollingParent是否配合处理嵌套滑动
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    /**
     * NestedScrollingChild滑动完成后将滑动值分发给NestedScrollingParent回调此方法
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param dxConsumed 水平方向消费的距离
     * @param dyConsumed 垂直方向消费的距离
     * @param dxUnconsumed 水平方向剩余的距离
     * @param dyUnconsumed 垂直方向剩余的距离
     * @param type 触摸类型  ViewCompat.TYPE_TOUCH ViewCompat.TYPE_NON_TOUCH
     */
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        println("$TAG onNestedScroll dxConsumed=$dxConsumed dyConsumed=$dyConsumed dxUnconsumed=$dxUnconsumed dyUnconsumed=$dyUnconsumed type=$type")
    }

    /**
     * NestedScrollingChild滑动完之前将滑动值分发给NestedScrollingParent回调此方法
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param dx 水平方向的距离
     * @param dy 水平方向的距离
     * @param consumed 返回NestedScrollingParent是否消费部分或全部滑动值 consumed[0]是水平方向 consumed[1]是竖直方向
     * @param type 触摸类型  ViewCompat.TYPE_TOUCH ViewCompat.TYPE_NON_TOUCH
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val hideTop = dy > 0 && scrollY < consumeSize
        val showTop = dy < 0 && scrollY >= 0 && !target.canScrollVertically(-1) // 向下不能再滑动了
        if (hideTop || showTop) {
            scrollBy(0, dy)
            consumed[1] = dy
        }

        println("$TAG onNestedPreScroll dx=$dx dy=$dy consumed[0]=${consumed[0]} consumed[1]=${consumed[1]} type=$type scrollY=$scrollY consumeSize=$consumeSize")
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        println("$TAG onNestedFling")
        // 处理惯性滑动
        return true
    }

    override fun scrollTo(x: Int, y: Int) {
        var lastY = y
        if (lastY < 0) {
            lastY = 0
        }
        if (lastY > consumeSize) {
            lastY = consumeSize
        }
        super.scrollTo(x, lastY)
    }


    /**
     * NestedScrollingParent配合处理嵌套滑动回调此方法
     * @param child 布局中包含下面target的直接父View
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param axes 滑动方向  ViewCompat.SCROLL_AXIS_NONE ViewCompat.SCROLL_AXIS_HORIZONTAL ViewCompat.SCROLL_AXIS_VERTICAL
     * @param type 触摸类型  ViewCompat.TYPE_TOUCH ViewCompat.TYPE_NON_TOUCH
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    /**
     * 嵌套滑动结束
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param type 触摸类型  ViewCompat.TYPE_TOUCH ViewCompat.TYPE_NON_TOUCH
     */
    override fun onStopNestedScroll(target: View, type: Int) {
        nestedScrollingParentHelper.onStopNestedScroll(target, type)
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollingParentHelper.nestedScrollAxes
    }
}