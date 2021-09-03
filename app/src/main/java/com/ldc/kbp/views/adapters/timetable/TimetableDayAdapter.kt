package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.ldc.kbp.R
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_lesson.view.*

class TimetableDayAdapter(
    context: Context,
    items: List<Timetable.Lesson?>,
    private val onLessonExpand: (Timetable.Lesson, Int) -> Unit
) : Adapter<Timetable.Lesson>(context, items, R.layout.item_lesson) {

    override fun onBindViewHolder(view: View, item: Timetable.Lesson?, position: Int) {
        if (item != null) {
            var selectedSubject = 0
            fun updateSubject() {
                when {
                    selectedSubject >= item.subjects.size -> selectedSubject = 0
                    selectedSubject < 0 -> selectedSubject = item.subjects.size - 1
                }

                view.item_subject_card_view.setCardBackgroundColor(
                    context.getColor(
                        if (item.subjects[selectedSubject].isReplaced) R.color.timetable_replacement_subject_bg else R.color.timetable_subject_bg
                    )
                )

                view.item_subject_card_tv.text = item.subjects[selectedSubject].subject
                view.item_teacher_card_tv.text = item.subjects[selectedSubject].teacher
                view.item_room_card_tv.text = item.subjects[selectedSubject].room
                view.item_group_card_tv.text = item.subjects[selectedSubject].group
            }

            if (item.subjects.size > 1) {
                view.item_subject_expand_image.isVisible = true
                view.item_subject_expand_image.setOnClickListener {
                    onLessonExpand(item, selectedSubject)
                }

                view.item_subject_next_lesson_btn.setOnClickListener {
                    selectedSubject++
                    updateSubject()
                }
                view.item_subject_prev_lesson_btn.setOnClickListener {
                    selectedSubject--
                    updateSubject()
                }
            }

            updateSubject()
        }
    }
}