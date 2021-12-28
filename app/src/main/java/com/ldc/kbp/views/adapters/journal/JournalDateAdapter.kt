package com.ldc.kbp.views.adapters.journal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import com.ldc.kbp.models.Journal
import kotlinx.android.synthetic.main.item_journal_date.view.*

class JournalDateAdapter(
    val context: Context,
    val dates: List<Journal.Date>,
) : RecyclerView.Adapter<JournalDateAdapter.ViewHolder>() {

    var currentMonth: Int? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            items = if (value == null)
                dates.flatMap { it.dates }
            else
                dates[currentMonth!!].dates

            notifyDataSetChanged()
        }

    private var selectedDateIndex: Int? = null
    var onSelectionChanged: (Int?) -> Unit = {}

    var items = dates.flatMap { it.dates }
    
    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder,  position: Int) {
        if (selectedDateIndex == position)
            holder.numberTv.setTextColor(context.getColor(R.color.orange90))
        else
            holder.numberTv.setTextColor(context.getColor(android.R.color.white))

        val item = if (currentMonth != null) {
            dates[currentMonth!!].dates[position]
        } else dates.flatMap { it.dates }[position]
        holder.numberTv.text = item

        holder.numberTv.setOnClickListener {
            holder.numberTv.setTextColor(context.getColor(R.color.orange90))
            if (selectedDateIndex != null)
                notifyItemChanged(selectedDateIndex!!)
            selectedDateIndex = position
            onSelectionChanged(selectedDateIndex)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_journal_date, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberTv: TextView = itemView.item_journal_date_num_tv
    }
}