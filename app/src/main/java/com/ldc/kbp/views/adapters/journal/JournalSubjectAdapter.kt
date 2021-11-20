package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_subject.view.*

class JournalSubjectAdapter(
    context: Context,
    line: JournalTeacherSelector.Subjects,
    onItemClickListener: (Int, JournalTeacherSelector.Subject) -> Unit
) : Adapter<JournalTeacherSelector.Subject>(context, line.subjects, R.layout.item_journal_subject, onItemClickListener) {

    override fun onBindViewHolder(view: View, item: JournalTeacherSelector.Subject?, position: Int) {
        view.item_journal_subject_name.text = item!!.name

        view.item_journal_cell_card_view.setOnClickListener {
            onItemClickListener(position, item)
        }
    }
}