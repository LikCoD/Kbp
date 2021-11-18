package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_date_num.view.*

class JournalDateAdapter(
    context: Context,
    items: Journal.Date,
) : Adapter<Int>(
    context,
    items.dates,
    R.layout.item_journal_date_num
) {
    override fun onBindViewHolder(view: View, item: Int?, position: Int) {
        view.item_journal_date_num_tv.text = item!!.toString()
    }
}