package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_week_index.view.*

class WeekIndexAdapter(
    context: Context,
    val weeksCount: Int,
    val daysInWeek: Int,
    child: LinearLayout,
) : LinearAdapter<Int>(
    context,
    (0 until weeksCount).toList(),
    R.layout.item_week_index,
    child
) {
    override fun onBindViewHolder(view: View, item: Int?, position: Int) {
        DayOfWeekIndexAdapter(context, daysInWeek, item!!, view.item_day_of_week_index_layout)
    }

    fun changeWeekMode(weekIndex: Int? = null) {
        items = if (weekIndex == null) (0 until weeksCount).toList() else listOf(weekIndex)
    }
}