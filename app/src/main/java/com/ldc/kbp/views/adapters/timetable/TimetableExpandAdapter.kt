package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.models.Schedule
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_lesson.view.*

class TimetableExpandAdapter(
    context: Context,
    val subjects: Schedule.Subjects? = null
) : Adapter<Schedule.Subject>(context, subjects?.subjects, R.layout.item_lesson) {
    override fun onBindViewHolder(view: View, item: Schedule.Subject?, position: Int) {
        if (items == null) return
        val colorRes =
            if (item!!.type == Schedule.Type.ADDED) R.color.timetable_replacement_subject_bg else R.color.timetable_subject_bg

        view.item_subject_card_view.setCardBackgroundColor(context.getColor(colorRes))

        view.item_subject_card_tv.text = item.subject
        view.item_teacher_card_tv.text = item.teacher
        view.item_room_card_tv.text = item.room
        view.item_group_card_tv.text = item.group
    }
}