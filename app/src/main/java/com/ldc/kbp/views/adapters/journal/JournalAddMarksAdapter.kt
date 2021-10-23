package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_cell.view.*

class JournalAddMarksAdapter(
    context: Context,
    items: List<String>,
    private val onClick: ((String, Int) -> Unit)? = null
) : Adapter<String>(context, items, R.layout.item_journal_cell) {

    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_journal_cell_mark.text = item!!

        view.item_journal_cell_card_view.setOnClickListener { onClick?.invoke(item, position) }
    }
}