package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import com.ldc.kbp.models.Timetable
import kotlinx.android.synthetic.main.item_lesson.view.*
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter

class TimetableExpandAdapter(
    context: Context,
    items: List<Timetable.Subject>? = null
) : Adapter<Timetable.Subject>(context, items, R.layout.item_lesson) {
    override fun onBindViewHolder(view: View, item: Timetable.Subject?, position: Int) {
        if (items != null) {
            view.item_subject_card_view.setCardBackgroundColor(
                context.getColor(
                    if (item!!.isReplaced) R.color.timetable_replacement_subject_bg else R.color.timetable_subject_bg
                )
            )

            view.item_subject_card_tv.text = item.subject
            view.item_teacher_card_tv.text = item.teacher
            view.item_room_card_tv.text = item.room
            view.item_group_card_tv.text = item.group
        }
    }
}