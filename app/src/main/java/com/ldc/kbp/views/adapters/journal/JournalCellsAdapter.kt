package com.ldc.kbp.views.adapters.journal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import kotlinx.android.synthetic.main.item_journal_cell.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class JournalCellsAdapter(
    val context: Context,
    val journal: Journal,
    var onClick: ((Journal.Cell, Int) -> Unit)? = null
) : RecyclerView.Adapter<JournalCellsAdapter.ViewHolder>() {

    private lateinit var manager: GridLayoutManager

    private fun updateSpan() {
        manager.spanCount = journal.dates[currentMonth].dates.size
    }

    var currentMonth: Int = 0
        set(value) {
            items = journal.subjects.flatMap { it.months[value].cells }

            selectedCard?.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))
            selectedCard = null
            selectedIndex = null

            field = value
            updateSpan()
        }

    var items: List<Journal.Cell> = journal.subjects.flatMap { it.months.last().cells }
        set(value) {
            val temp = field
            field = value

            fun check(lower: List<Journal.Cell>, bigger: List<Journal.Cell>, remove: Boolean) {
                if (remove) notifyItemRangeRemoved(lower.size, bigger.size - lower.size)
                else notifyItemRangeInserted(lower.size, bigger.size - lower.size)
                lower.forEachIndexed { i, cell ->
                    if (bigger[i].marks.isNotEmpty() || cell.marks.isNotEmpty())
                        notifyItemChanged(i)
                }
            }

            if (field.size > temp.size) check(temp, field, false)
            else check(field, temp, true)

        }

    private var selectedIndex: Int? = null
    private var selectedCard: CardView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_journal_cell, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        MainScope().launch {
            holder.card.setOnClickListener {
                if (position != selectedIndex) {
                    holder.card.setCardBackgroundColor(context.getColor(R.color.orange90))

                    val cardBackground = context.getColor(
                        if (items[selectedIndex ?: 0].marks.isEmpty()) R.color.timetable_empty_subject_bg
                        else R.color.timetable_subject_bg
                    )

                    selectedCard?.setCardBackgroundColor(cardBackground)
                }

                selectedCard = holder.card
                selectedIndex = position

                onClick?.invoke(item, position)
            }

            when {
                item.marks.isEmpty() -> {
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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        manager = GridLayoutManager(
            context,
            journal.dates.last().dates.size,
            GridLayoutManager.VERTICAL,
            false
        )

        recyclerView.layoutManager = manager
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.item_journal_cell_card_view

        val mark: TextView = itemView.item_journal_cell_mark
        val mark1: TextView = itemView.item_journal_cell_mark1
        val mark2: TextView = itemView.item_journal_cell_mark2

        val div: ImageView = itemView.item_journal_cell_div
    }
}