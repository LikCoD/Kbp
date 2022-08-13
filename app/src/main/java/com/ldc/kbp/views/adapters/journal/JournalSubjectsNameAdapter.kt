package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import likco.studyum.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_group_name.view.*

class JournalSubjectsNameAdapter(
    context: Context,
    items: List<String>? = null
) : Adapter<String>(context, items, R.layout.item_journal_group_name) {

    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_journal_group_name.text = item!!
    }
}