package com.ldc.kbp.views.adapters.journal

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import kotlinx.android.synthetic.main.item_journal_cell.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class JournalCellsAdapter(
    val context: Context,
    val journal: Journal,
    currentMonth: Int? = null,
    var onClick: ((Journal.Cell, Int) -> Unit)? = null,
    var onVisibleMonthChanged: ((Int, Int) -> Unit)? = null
) : RecyclerView.Adapter<JournalCellsAdapter.ViewHolder>() {

    var lastShownMonth = 0

    var currentMonth: Int? = currentMonth
        set(value) {
            selectedCard?.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))
            selectedCard = null
            selectedIndex = null

            if (value != field)
                updateMonths()

            field = value
        }


    var items: List<Journal.Cell> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    private fun updateMonths() {
        notifyDataSetChanged()
    }

    init {
        val cells = mutableListOf<Journal.Cell>()

        val subjects = mutableListOf<List<Journal.Cell>>()
        journal.subjects.forEach {
            subjects.add(it.months.flatMap { m -> m.cells })
        }

        for (i in 0 until journal.dates.sumOf { it.dates.size})
            subjects.forEach { cells.add(it[i]) }

        items = cells
    }

    private var selectedIndex: Int? = null
    private var selectedCard: CardView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_journal_cell, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =
        if (currentMonth == null) items.size else journal.dates[currentMonth!!].dates.size * journal.subjects.size

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (currentMonth == null) {
            var endOfPrevMonths = 0
            journal.dates.forEachIndexed { index, date ->
                val cellsInMonth = (date.dates.size * journal.subjects.size)
                if (index == lastShownMonth) {
                    endOfPrevMonths += cellsInMonth
                    return@forEachIndexed
                }
                if ((position - endOfPrevMonths) in 0..cellsInMonth) {
                    onVisibleMonthChanged?.invoke(index, date.month)
                    lastShownMonth = index
                }
                endOfPrevMonths += cellsInMonth
            }
        } else {
            if (lastShownMonth != currentMonth) {
                onVisibleMonthChanged?.invoke(currentMonth!!, journal.dates[currentMonth!!].month)
                lastShownMonth = currentMonth!!
            }
        }

        val item = if (currentMonth == null || currentMonth == 0) items[position]
        else items[journal.dates.subList(0, currentMonth!!)
            .sumOf { it.dates.size } * journal.subjects.size + position]

        MainScope().launch {
            holder.card.setOnClickListener {
                if (position != selectedIndex) {
                    holder.card.setCardBackgroundColor(context.getColor(R.color.orange90))

                    val cardBackground = context.getColor(
                        if (items[selectedIndex ?: 0].marks.isEmpty())
                            R.color.timetable_empty_subject_bg else R.color.timetable_subject_bg
                    )

                    selectedCard?.setCardBackgroundColor(cardBackground)
                }

                selectedCard = holder.card
                selectedIndex = position

                onClick?.invoke(item, position)
            }

            when {
                item.marks.isEmpty() -> {
                    holder.card.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))
                    holder.mark.isVisible = false
                    holder.mark1.isVisible = false
                    holder.mark2.isVisible = false
                    holder.div.isVisible = false
                }
                item.marks.size == 1 -> {
                    holder.card.setCardBackgroundColor(context.getColor(R.color.timetable_subject_bg))

                    holder.mark.text = item.marks.first().mark

                    holder.mark.isVisible = true
                    holder.mark1.isVisible = false
                    holder.mark2.isVisible = false
                    holder.div.isVisible = false
                }
                else -> {
                    holder.card.setCardBackgroundColor(context.getColor(R.color.timetable_subject_bg))

                    holder.mark1.text = item.marks[0].mark
                    holder.mark2.text = item.marks[1].mark

                    holder.mark.isVisible = false
                    holder.mark1.isVisible = true
                    holder.mark2.isVisible = true
                    holder.div.isVisible = true
                }
            }

            if (selectedIndex == position)
                holder.card.setCardBackgroundColor(context.getColor(R.color.orange90))
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.item_journal_cell_card_view

        val mark: TextView = itemView.item_journal_cell_mark
        val mark1: TextView = itemView.item_journal_cell_mark1
        val mark2: TextView = itemView.item_journal_cell_mark2

        val div: ImageView = itemView.item_journal_cell_div
    }
}