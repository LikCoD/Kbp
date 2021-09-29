package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_journal_subject_name.view.*

class JournalSubjectsNameAdapter(
    context: Context,
    items: List<String>? = null,
    child: LinearLayout
) : LinearAdapter<String>(context, items, R.layout.item_journal_subject_name, child) {

    override fun onBindViewHolder(view: View, item: String?, position: Int) {
        view.item_journal_subject_name.text = item!!
    }
}