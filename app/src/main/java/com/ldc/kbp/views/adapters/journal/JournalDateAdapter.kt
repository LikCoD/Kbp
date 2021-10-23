package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_date_num.view.*

class JournalDateAdapter(
    context: Context,
    items: List<String>,
) : Adapter<String>(
    context,
    items,
    R.layout.item_journal_date_num
) {
    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_journal_date_num_tv.text = item!!
    }
}