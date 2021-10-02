package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_journal_marks.view.*

class JournalMonthAdapter(
    context: Context,
    items: List<Journal.Subject>,
    child: LinearLayout
) : LinearAdapter<Journal.Subject>(context, items, R.layout.item_journal_month, child) {

    override fun onBindViewHolder(view: View, item: Journal.Subject?, position: Int) {
        JournalCellsAdapter(context, item!!.cells, view.item_journal_marks_layout)
    }
}