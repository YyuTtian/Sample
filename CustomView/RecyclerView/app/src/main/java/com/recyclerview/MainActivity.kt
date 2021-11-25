package com.recyclerview

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recyclerview.databinding.ActivityMainBinding
import com.recyclerview.databinding.ItemBinding
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    data class ItemData(val groupName: String, val txt: String)

    private val headHeight = dp2px(50)
    private val lineHeight = dp2px(2)
    private val groupLeft = dp2px(20).toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = ArrayList<ItemData>()
        var groupName = "group0"
        for (i in 0..100) {
            if (i % 10 == 0) {
                groupName = "group${i / 10}"
            }
            data.add(ItemData(groupName, "$i"))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = Adapter(data)
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {

            private val headPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            private val txtPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            private var txtRect = Rect()

            init {
                headPaint.color = Color.RED

                txtPaint.color = Color.BLACK
                txtPaint.textSize = dp2px(20).toFloat()
            }

            // 绘制在item的下层
            override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
                super.onDraw(canvas, recyclerView, state)
                if (recyclerView.adapter is Adapter) {
                    val adapter = recyclerView.adapter as Adapter
                    val left = recyclerView.paddingLeft
                    val right = recyclerView.width - recyclerView.paddingRight
                    for (i in 0 until recyclerView.childCount) {
                        val child = recyclerView.getChildAt(i)
                        val position = recyclerView.getChildLayoutPosition(child)
                        val canDraw = child.top - headHeight - recyclerView.paddingTop >= 0

                        if (canDraw) {
                            if (adapter.isGroupHead(position)) {
                                val groupName = adapter.getGroupName(position)
                                canvas.drawRect(left.toFloat(), (child.top - headHeight).toFloat(), right.toFloat(), child.top.toFloat(), headPaint)
                                txtPaint.getTextBounds(groupName, 0, groupName.length - 1, txtRect)
                                val y = child.top - headHeight / 2 + txtRect.height() / 2
                                canvas.drawText(groupName, groupLeft, y.toFloat(), txtPaint)
                            } else {
                                canvas.drawRect(left.toFloat(), (child.top - lineHeight).toFloat(), right.toFloat(), child.top.toFloat(), headPaint)
                            }
                        }
                    }
                }
            }

            // 绘制在item的上层
            override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
                super.onDrawOver(canvas, recyclerView, state)
                if (recyclerView.adapter is Adapter) {
                    val firstVisiblePosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val firstItemView = recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition)!!.itemView

                    val left = recyclerView.paddingLeft
                    val right = recyclerView.width - recyclerView.paddingRight
                    val top = recyclerView.paddingTop

                    val adapter = recyclerView.adapter as Adapter
                    if (adapter.isGroupHead(firstVisiblePosition + 1)) {
                        val bottom = min(headHeight, firstItemView.bottom - recyclerView.paddingTop)

                        canvas.clipRect(left.toFloat(), top.toFloat(), right.toFloat(), (top + bottom).toFloat())

                        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), (top + bottom).toFloat(), headPaint)

                        val groupName = adapter.getGroupName(firstVisiblePosition)
                        txtPaint.getTextBounds(groupName, 0, groupName.length - 1, txtRect)
                        val y = top + bottom - headHeight / 2 + txtRect.height() / 2
                        canvas.drawText(groupName, groupLeft, y.toFloat(), txtPaint)

                    } else {
                        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), (top + headHeight).toFloat(), headPaint)

                        val groupName = adapter.getGroupName(firstVisiblePosition)
                        txtPaint.getTextBounds(groupName, 0, groupName.length - 1, txtRect)
                        val y = top + headHeight / 2 + txtRect.height() / 2
                        canvas.drawText(groupName, groupLeft, y.toFloat(), txtPaint)
                    }
                }
            }

            override fun getItemOffsets(outRect: Rect, view: View, recyclerView: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, recyclerView, state)
                if (recyclerView.adapter is Adapter) {
                    val position = recyclerView.getChildLayoutPosition(view)
                    val adapter = recyclerView.adapter as Adapter
                    if (adapter.isGroupHead(position)) {
                        outRect.set(0/*左边的宽度*/, headHeight/*上边的宽度*/, 0/*右边的宽度*/, 0/*下边的宽度*/)
                    } else {
                        outRect.set(0/*左边的宽度*/, lineHeight/*上边的宽度*/, 0/*右边的宽度*/, 0/*下边的宽度*/)
                    }
                }
            }
        })
    }

    inner class Adapter(private val data: List<ItemData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemBinding.inflate(layoutInflater, parent, false)
            return ItemHolder(binding, binding.root)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemHolder = holder as ItemHolder
            itemHolder.binding.tv.text = data[position].txt
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun isGroupHead(position: Int): Boolean {
            if (position == 0) {
                return true
            }
            return data[position].groupName != data[position - 1].groupName
        }

        fun getGroupName(position: Int): String {
            return data[position].groupName
        }
    }

    class ItemHolder(val binding: ItemBinding, itemView: View) : RecyclerView.ViewHolder(itemView)

    fun dp2px(dpValue: Int): Int {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
    }
}