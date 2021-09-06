package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_day_of_week_index.view.*

class DayOfWeekIndexAdapter(
    context: Context,
    daysInWeek: Int,
    private val weekIndex: Int,
    child: LinearLayout,
) : LinearAdapter<Int>(
    context,
    (0 until daysInWeek).toList(),
    R.layout.item_day_of_week_index,
    child
) {
    override fun onBindViewHolder(view: View, item: Int?, position: Int) {
        view.item_week_index_number_tv.text = (weekIndex + 1).toString()
        view.item_week_index_tv.text = context.resources.getStringArray(R.array.days_of_weeks)[position]
    }

    init { updateItems() }
}