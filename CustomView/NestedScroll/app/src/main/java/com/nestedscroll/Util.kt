package com.nestedscroll

import android.content.res.Resources

object Util {

    @JvmStatic
    fun dp2px(dpValue: Int): Int {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
    }
}