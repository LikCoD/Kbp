package com.ldc.kbp.views.adapters.journal

import android.content.Context
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_journal_cell.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class JournalCellsAdapter(
    context: Context,
    val journal: Journal? = null,
    var onClick: ((Journal.Cell, Int) -> Unit)? = null
) : Adapter<Journal.Cell>(context, journal?.subjects?.flatMap { it.cells }, R.layout.item_journal_cell) {

    private var selectedIndex: Int? = null
    private var selectedCard: CardView? = null

    override fun onBindViewHolder(view: View, item: Journal.Cell?, position: Int) {
        if (item == null) return

        MainScope().launch {
            view.item_journal_cell_card_view.setOnClickListener {
                if (position != selectedIndex) {
                    view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.orange90))

                    val cardBackground = context.getColor(
                        if (items!![selectedIndex ?: 0]!!.marks.isEmpty()) R.color.timetable_empty_subject_bg
                        else R.color.timetable_subject_bg
                    )

                    selectedCard?.setCardBackgroundColor(cardBackground)
                }

                selectedCard = view.item_journal_cell_card_view
                selectedIndex = position

                onClick?.invoke(item, position)
            }

            when {
                item.marks.isEmpty() -> {
                    view.item_journal_cell_mark.isVisible = false
                    view.item_journal_cell_mark1.isVisible = false
                    view.item_journal_cell_mark2.isVisible = false
                    view.item_journal_cell_div.isVisible = false
                }
                item.marks.size == 1 -> {
                    view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.timetable_subject_bg))

                    view.item_journal_cell_mark.text = item.marks.first().mark

                    view.item_journal_cell_mark.isVisible = true
                    view.item_journal_cell_mark1.isVisible = false
                    view.item_journal_cell_mark2.isVisible = false
                    view.item_journal_cell_div.isVisible = false
                }
                else -> {
                    view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.timetable_subject_bg))

                    view.item_journal_cell_mark1.text = item.marks[0].mark
                    view.item_journal_cell_mark2.text = item.marks[1].mark

                    view.item_journal_cell_mark.isVisible = false
                    view.item_journal_cell_mark1.isVisible = true
                    view.item_journal_cell_mark2.isVisible = true
                    view.item_journal_cell_div.isVisible = true
                }
            }

            if (selectedIndex == position) view.item_journal_cell_card_view.setCardBackgroundColor(context.getColor(R.color.orange90))
        }
    }
}