package com.ldc.kbp.views.adapters.search

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_group_selector.view.*

class SearchAdapter<T>(context: Context, items: List<T>? = null, val buildString: (T) -> String) :
    Adapter<T>(context, items, R.layout.item_group_selector) {

    override fun onBindViewHolder(view: View, item: T?, position: Int) {
        item ?: return

        view.item_selector_index.text = (position + 1).toString()
        view.item_selector_link.text = buildString(item)

        view.item_selector_line.setOnClickListener {
            onItemClickListener(position, item)
        }
    }
}