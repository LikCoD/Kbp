package com.ldc.kbp.views.layoutmanagers

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FixedGridLayoutManager(context: Context, spanCount: Int, orientation: Int, reverse: Boolean) :
    GridLayoutManager(context, spanCount, orientation, reverse) {

    override fun canScrollHorizontally(): Boolean = true

    override fun canScrollVertically(): Boolean = true
}