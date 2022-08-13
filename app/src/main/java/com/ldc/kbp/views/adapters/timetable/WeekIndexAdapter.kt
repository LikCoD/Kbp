package com.ldc.kbp.views.adapters.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import likco.studyum.R
import kotlinx.android.synthetic.main.item_day_of_week_index.view.*

class WeekIndexAdapter(
    val context: Context,
    private val weeksCount: Int,
    private val daysInWeek: Int,
) : RecyclerView.Adapter<WeekIndexAdapter.ViewHolder>() {

    var shownWeek: Int? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weekIndex = shownWeek ?: (position / daysInWeek)

        holder.weekNumber.text = (weekIndex + 1).toString()
        holder.dayOfWeek.text =
            context.resources.getStringArray(R.array.days_of_weeks)[position % daysInWeek]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_day_of_week_index, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =
        if (shownWeek == null) weeksCount * daysInWeek else daysInWeek

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weekNumber: TextView = itemView.item_week_index_number_tv
        val dayOfWeek: TextView = itemView.item_week_index_tv
    }
}