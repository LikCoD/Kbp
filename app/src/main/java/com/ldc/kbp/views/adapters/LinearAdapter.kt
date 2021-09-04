package com.ldc.kbp.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

abstract class LinearAdapter<T>(val context: Context, items: List<T?>?, val layout: Int, private val parent: LinearLayout) {

    var items = items
        set(value) {
            field = value

            updateItems()
        }

    fun updateItems() {
        parent.removeAllViews()

        items?.forEachIndexed { index, t ->
            val view = LayoutInflater.from(context).inflate(layout, parent, false)

            parent.addView(view)

            onBindViewHolder(view, t, index)
        }
    }

    var onItemClickListener: ((Int, T) -> Unit) = { _, _ -> }

    abstract fun onBindViewHolder(view: View, item: T?, position: Int)

    init{ updateItems() }
}