package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_subject.view.*

class JournalSubjectAdapter(
    context: Context,
    items: List<JournalTeacherSelector.Subject>?,
    val index: Int,
    val onClick: (Int, Int) -> Unit
) : Adapter<JournalTeacherSelector.Subject>(context, items, R.layout.item_journal_subject) {

    override fun onBindViewHolder(view: View, item: JournalTeacherSelector.Subject?, position: Int) {
        view.item_journal_subject_name.text = item!!.name

        view.item_journal_subject_name.setOnClickListener {
            onClick(index, position)
        }
    }
}