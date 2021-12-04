package com.ldc.kbp.views.layoutmanagers

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class RtlGridLayoutManager @JvmOverloads constructor(
    context: Context, spanCount: Int = 0, orientation: Int = 0, reverseLayout: Boolean
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {
    override fun isLayoutRTL(): Boolean = true
}