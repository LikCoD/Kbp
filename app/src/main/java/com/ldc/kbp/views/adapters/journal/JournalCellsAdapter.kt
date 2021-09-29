package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_journal_cell.view.*

class JournalCellsAdapter(
    context: Context,
    items: List<Journal.Cell?>? = null,
    child: LinearLayout,
) : LinearAdapter<Journal.Cell>(context, items, R.layout.item_journal_cell, child) {

    override fun onBindViewHolder(view: View, item: Journal.Cell?, position: Int) {
        if (item == null) return

        when {
            item.marks.isEmpty() -> view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))
            item.marks.size == 1 -> view.item_journal_cell_mark.text = item.marks.first().mark
            else -> {
                view.item_journal_cell_mark1.text = item.marks[0].mark
                view.item_journal_cell_mark2.text = item.marks[1].mark
                view.item_journal_cell_mark.isVisible = false
                view.item_journal_cell_mark1.isVisible = true
                view.item_journal_cell_mark2.isVisible = true
                view.item_journal_cell_div.isVisible = true
            }
        }
    }
}