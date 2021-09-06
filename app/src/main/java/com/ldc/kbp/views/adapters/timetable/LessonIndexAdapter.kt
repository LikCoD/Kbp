package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.ldc.kbp.R
import com.ldc.kbp.dimen
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.views.adapters.LinearAdapter
import kotlinx.android.synthetic.main.item_bell.view.*
import java.time.LocalDate

class LessonIndexAdapter(
    context: Context,
    itemsCount: Int,
    child: LinearLayout,
) : LinearAdapter<Int>(
    context,
    (0 until itemsCount).toList(),
    R.layout.item_bell,
    child
) {
    var isBellShown: Boolean = false
        set(value) {
            field = value

            updateItems()
        }

    override fun onBindViewHolder(view: View, item: Int?, position: Int) {
        view.item_bell_index_tv.text = (position + 1).toString()

        view.item_bell_workdays_time_tv.text = context.resources.getStringArray(R.array.bell_workdays)[position]
        view.item_bell_saturday_time_tv.text = context.resources.getStringArray(R.array.bell_saturday)[position]

        if (LocalDate.now().dayOfWeek.ordinal == 5){
            Deprecates.setTextAppearance(view.item_bell_saturday, R.style.head_text)
            Deprecates.setTextAppearance(view.item_bell_saturday_time_tv, R.style.head_text)
        }else{
            Deprecates.setTextAppearance(view.item_bell_workdays, R.style.head_text)
            Deprecates.setTextAppearance(view.item_bell_workdays_time_tv, R.style.head_text)
        }

        val itemSubjectHeight = dimen(context.resources, R.dimen.item_subject_height).toInt()

        view.item_bell_layout.layoutParams = if (isBellShown)
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, itemSubjectHeight)
        else LinearLayout.LayoutParams(0, itemSubjectHeight)
    }

    init { updateItems() }
}