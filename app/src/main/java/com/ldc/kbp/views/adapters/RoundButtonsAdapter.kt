package com.ldc.kbp.views.adapters

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import kotlinx.android.synthetic.main.item_round_button.view.*

class RoundButtonsAdapter(
    context: Context,
    var updateItemOnClick: Boolean = true,
    items: List<String>? = null,
    firstSelectionIndex: Int = 0
) : Adapter<String>(context, items, R.layout.item_round_button) {

    val list = mutableListOf<View>()

    var selectionIndex = firstSelectionIndex
        set(value) {
            notifyItemChanged(selectionIndex)
            field = value

            notifyItemChanged(value)
        }

    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_round_button_card.setCardBackgroundColor(
            context.getColor(if (position == selectionIndex) R.color.orange90 else R.color.white60)
        )

        view.item_round_button_text.text = item
        view.item_round_button_card.setOnClickListener {
            onItemClickListener(position, item!!)

            if (updateItemOnClick)
                selectionIndex = position
        }

        list.add(view)
    }
}