package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_cell.view.*
import java.text.DecimalFormat

class JournalAverageAdapter(
    context: Context,
    items: List<Journal.Subject>? = null,
) : Adapter<Journal.Subject?>(context, items?.plus(null), R.layout.item_journal_cell) {

    private val marksAverage = mutableListOf<Double>()

    private fun getAverage(average: Double): String = DecimalFormat("##.00").format(average).take(4)

    override fun onBindViewHolder(view: View, item: Journal.Subject?, position: Int) {
        if (item == null) {
            val average = marksAverage.sum() / marksAverage.size
            view.item_journal_cell_average.text = getAverage(average)
            return
        }

        val marks = item.cells.flatMap { it.marks.map { c -> c.mark } }.filter { it.toIntOrNull() != null }
        if (marks.isEmpty())
            view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))
        else {
            val average = marks.sumOf { it.toDouble() } / marks.size
            marksAverage.add(average)

            view.item_journal_cell_average.text = getAverage(average)
        }
    }
}