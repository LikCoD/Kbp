package com.ldc.kbp.views.itemdecoritions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class BottomOffsetDecoration(private val mBottomOffset: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        
        if (parent.getChildAdapterPosition(view) == state.itemCount - 1)
            outRect.set(0, 0, 0, mBottomOffset)
        else
            outRect.set(0, 0, 0, 0)

    }
}