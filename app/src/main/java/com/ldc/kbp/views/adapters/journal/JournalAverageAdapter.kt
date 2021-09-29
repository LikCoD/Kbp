package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_journal_cell.view.*
import java.text.DecimalFormat

class JournalAverageAdapter(
    context: Context,
    items: List<Journal.Subject>? = null,
    child: LinearLayout,
) : LinearAdapter<Journal.Subject?>(context, items?.plus(null), R.layout.item_journal_cell, child, false) {

    private val marksAverage = mutableListOf<Double>()

    override fun onBindViewHolder(view: View, item: Journal.Subject?, position: Int) {
        if (item == null) {
            val average = marksAverage.sum() / marksAverage.size
            view.item_journal_cell_average.text = DecimalFormat("##.00").format(average)
            return
        }

        val marks = item.cells.flatMap { it.marks.map { c -> c.mark } }.filter { it.toIntOrNull() != null }
        if (marks.isEmpty())
            view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))
        else {
            val average = marks.sumOf { it.toDouble() } / marks.size
            marksAverage.add(average)

            view.item_journal_cell_average.text = DecimalFormat("#.00").format(average)
        }
    }

    init { updateItems() }
}