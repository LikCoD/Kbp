package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_subject.view.*

class JournalSubjectsAdapter(
    context: Context,
    journal: Journal,
) : Adapter<Journal.Subject>(context, journal.months.map { it.subjects }.flatten(), R.layout.item_journal_subject) {

    override fun onBindViewHolder(view: View, item: Journal.Subject?, position: Int) {
        JournalCellsAdapter(context, item!!.cells, view.item_journal_subject_layout)
    }
}