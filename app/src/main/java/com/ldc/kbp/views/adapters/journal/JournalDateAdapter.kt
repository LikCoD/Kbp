package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_journal_date.view.*

class JournalDateAdapter(
    context: Context,
    items: List<Journal.Month>,
    child: LinearLayout,
) : LinearAdapter<Journal.Month>(
    context,
    items,
    R.layout.item_journal_date,
    child
) {
    override fun onBindViewHolder(view: View, item: Journal.Month?, position: Int) {
        view.item_journal_date_month.text = item!!.name
        JournalDateNumAdapter(context, item, view.item_journal_date_nums)
    }

    init {
        updateItems()
    }
}