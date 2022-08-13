package com.ldc.kbp.views.adapters.journal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import likco.studyum.R
import com.ldc.kbp.models.Journal
import kotlinx.android.synthetic.main.item_journal_cell.view.*

class JournalViewMarkAdapter(
    val context: Context,
    items: List<Journal.Mark?> = listOf(),
    val onClick: (Journal.Mark?) -> Unit
) : RecyclerView.Adapter<JournalViewMarkAdapter.ViewHolder>() {

    var items = items
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            selectedCard = null
            selectedIndex = value.size
            notifyDataSetChanged()

            field = value
        }

    private var selectedIndex: Int = items.size + 1
    private var selectedCard: CardView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_journal_cell, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size + 1

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (selectedCard == null && selectedIndex == position){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.orange90))

            selectedCard = holder.cardView
        }else{
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.timetable_subject_bg))
        }

        holder.cardView.setOnClickListener {
            if (position != selectedIndex) {
                holder.cardView.setCardBackgroundColor(context.getColor(R.color.orange90))
                selectedCard?.setCardBackgroundColor(context.getColor(R.color.timetable_subject_bg))
            }

            selectedCard = holder.cardView
            selectedIndex = position

            onClick(items.getOrNull(position))
        }

        holder.mark.text = items.getOrNull(position)?.mark
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.item_journal_cell_card_view
        val mark: TextView = itemView.item_journal_cell_mark
    }
}