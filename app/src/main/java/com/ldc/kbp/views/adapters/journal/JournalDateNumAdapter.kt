package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_journal_date_num.view.*

class JournalDateNumAdapter(
    context: Context,
    month: Journal.Month,
    child: LinearLayout,
) : LinearAdapter<String>(
    context,
    month.subjects[0].cells.map { it!!.date },
    R.layout.item_journal_date_num,
    child
) {
    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_journal_date_num_tv.text = item
    }

    init {
        updateItems()
    }
}