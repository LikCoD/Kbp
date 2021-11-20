package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.JournalTeacherSelector
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_subject_selector.view.*

class JournalSubjectsSelectorAdapter(
    context: Context,
    journal: JournalTeacherSelector,
) : Adapter<Pair<JournalTeacherSelector.Group, JournalTeacherSelector.Subjects>>(
    context,
    journal.groups.map { it.key to it.value },
    R.layout.item_journal_subject_selector
) {

    override fun onBindViewHolder(
        view: View,
        item: Pair<JournalTeacherSelector.Group, JournalTeacherSelector.Subjects>?,
        position: Int
    ) {
        view.item_journal_subject_selector_group.text = item!!.first.name.replaceFirstChar { it.uppercase() }
        view.item_journal_subject_selector_layout.adapter = JournalSubjectAdapter(context, item.second, onClick)
    }

    var onClick: (Int, JournalTeacherSelector.Subject) -> Unit = {_, _->  }
}