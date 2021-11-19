package com.viewlineanim

import android.content.res.Resources

object Util {

    @JvmStatic
    fun dp2pxFloat(dpValue: Float): Float {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density)
    }
}