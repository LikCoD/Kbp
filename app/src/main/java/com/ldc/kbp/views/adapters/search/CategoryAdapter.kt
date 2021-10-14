package com.ldc.kbp.views.adapters.search

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(
    context: Context,
    items: List<String>? = null,
    var emptySelection: Boolean = true
) : Adapter<String>(context, items, R.layout.item_category) {

    var selectionIndex: Int? = null
        set(value) {
            if (selectionIndex != null)
                notifyItemChanged(selectionIndex!!)
            field = value

            if (value != null)
                notifyItemChanged(value)
        }

    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_category_card.setCardBackgroundColor(
            context.getColor(if (position == selectionIndex) R.color.orange90 else R.color.white60)
        )

        view.item_category_tv.text = item
        view.item_category_card.setOnClickListener {
            selectionIndex = if (selectionIndex == position && emptySelection) null else position
            onItemClickListener(position, item!!)
        }
    }
}