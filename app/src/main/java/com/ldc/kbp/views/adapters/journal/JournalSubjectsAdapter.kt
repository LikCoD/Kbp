package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_subject_selector.view.*

class JournalSubjectsAdapter(
    context: Context,
    journal: JournalTeacherSelector,
) : Adapter<JournalTeacherSelector.Subjects>(
    context,
    journal.groups.values.toList(),
    R.layout.item_journal_subject_selector
) {

    override fun onBindViewHolder(view: View, item: JournalTeacherSelector.Subjects?, position: Int) {
        view.item_journal_subject_selector_layout.adapter = JournalSubjectAdapter(context, item!!, onClick)
    }

    var onClick: (JournalTeacherSelector.Subjects?, JournalTeacherSelector.Subject?) -> Unit = { _, _ -> }
}