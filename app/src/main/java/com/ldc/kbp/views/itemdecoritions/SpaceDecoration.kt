package com.ldc.kbp.views.itemdecoritions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpaceDecoration(private val space: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.layoutManager !is LinearLayoutManager || parent.adapter == null) return

        val position = parent.getChildLayoutPosition(view)

        if ((parent.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.HORIZONTAL) {
            when (position) {
                0 -> outRect.right = space
                parent.adapter!!.itemCount - 1 -> outRect.left = space
                else -> {
                    outRect.right = space
                    outRect.left = space
                }
            }
        } else {
            when (position) {
                0 -> outRect.bottom = space
                parent.childCount -> outRect.top = space
                else -> {
                    outRect.bottom = space
                    outRect.top = space
                }
            }
        }
    }
}