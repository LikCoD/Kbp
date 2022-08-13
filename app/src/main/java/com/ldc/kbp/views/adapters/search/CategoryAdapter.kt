package com.ldc.kbp.views.adapters.search

import android.content.Context
import android.view.View
import likco.studyum.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(
    context: Context,
    items: List<String>? = null,
    private var emptySelection: Boolean = true
) : Adapter<String>(context, items, R.layout.item_category) {

    var selectionIndex: Int? = null
        set(value) {
            if (selectionIndex != null) notifyItemChanged(selectionIndex!!)

            if (value != null) notifyItemChanged(value)

            field = value
        }

    var selectedItem: String? = null

    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_category_card.setCardBackgroundColor(
            context.getColor(if (position == selectionIndex) R.color.orange90 else R.color.white60)
        )

        view.item_category_tv.text = item
        view.item_category_card.setOnClickListener {
            if (selectionIndex == position && emptySelection){
                selectedItem = null
                selectionIndex = null
            }else{
                selectedItem = item
                selectionIndex = position
            }

            onItemClickListener(position, item!!)
        }
    }
}