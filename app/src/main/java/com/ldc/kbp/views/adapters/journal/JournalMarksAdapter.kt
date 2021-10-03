package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_marks.view.*

class JournalMarksAdapter(
    context: Context,
    items: List<Journal.Subject>,
    val onClick: (Journal.Subject, Journal.Cell) -> Unit = { _, _ -> }
) : Adapter<Journal.Subject>(context, items, R.layout.item_journal_month) {

    override fun onBindViewHolder(view: View, item: Journal.Subject?, position: Int) {
        JournalCellsAdapter(context, item, view.item_journal_marks_layout, onClick)
    }
}