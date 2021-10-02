package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_marks.view.*

class JournalMarksAdapter(
    context: Context,
    journal: Journal,
) : Adapter<Journal.Month>(context, journal.months, R.layout.item_journal_marks) {

    override fun onBindViewHolder(view: View, item: Journal.Month?, position: Int) {
        JournalMonthAdapter(context, item!!.subjects, view.item_journal_marks_layout)
    }
}