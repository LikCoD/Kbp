package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import com.ldc.kbp.models.Timetable
import kotlinx.android.synthetic.main.item_lesson.view.*
import kotlinx.coroutines.*

class TimetableAdapter(val context: Context, timetable: Timetable) :
    RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    var timetable = timetable
        set(value) {
            field = value

            updateInfo()
        }

    var isReplacementShown = true
        set(value) {
            field = value

            replacementLessons.forEachIndexed { i, lesson ->
                if (lesson.subjects != standardLessons[i].subjects)
                    notifyItemChanged(i)
            }
        }


    var shownWeek: Int? = null
        set(value) {
            field = value

            updateInfo()
        }

    private fun updateInfo() {
        if (shownWeek == null) {
            replacementLessons = timetable.weeks.flatMap { it.days }.flatMap { it.replacementLessons }
            standardLessons = timetable.weeks.flatMap { it.days }.flatMap { it.standardLessons }
        } else {
            replacementLessons = timetable.weeks[shownWeek!!].days.flatMap { it.replacementLessons }
            standardLessons = timetable.weeks[shownWeek!!].days.flatMap { it.standardLessons }
        }

        notifyDataSetChanged()
    }

    private var replacementLessons = timetable.weeks.flatMap { it.days }.flatMap { it.replacementLessons }
    private var standardLessons = timetable.weeks.flatMap { it.days }.flatMap { it.standardLessons }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false)
        return ViewHolder(parent, view)
    }

    private fun updateSubject(holder: ViewHolder, subject: Timetable.Subject) {
        val cardColor =
            if (subject.isReplaced) R.color.timetable_replacement_subject_bg else R.color.timetable_subject_bg

        holder.card.setCardBackgroundColor(context.getColor(cardColor))

        holder.subjectTv.text = subject.subject
        holder.teacherTv.text = subject.teacher
        holder.roomTv.text = subject.room
        holder.groupTv.text = subject.group
    }

    private fun checkBounds(subjects: List<Timetable.Subject>, position: Int): Int = when {
        position >= subjects.size -> 0
        position < 0 -> subjects.lastIndex
        else -> position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val lesson = (if (isReplacementShown) replacementLessons[position] else standardLessons[position])
            val subjects = lesson.subjects

            holder.layout.foreground =
                if (lesson.state == Timetable.UpdateState.NOT_UPDATED) ColorDrawable(context.getColor(R.color.not_updated)) else null

            if (subjects == null) {
                holder.layout.post {
                    holder.card.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))

                    holder.expandBtn.isVisible = false

                    holder.subjectTv.text = ""
                    holder.teacherTv.text = ""
                    holder.groupTv.text = ""
                    holder.roomTv.text = ""
                }
                return@launch
            }

            var selectedSubject = 0
            holder.layout.post {
                if (subjects.size > 1) {
                    holder.expandBtn.isVisible = true

                    holder.expandBtn.setOnClickListener {
                        onLessonExpand?.invoke(lesson)
                    }

                    holder.nextBtn.setOnClickListener {
                        selectedSubject = checkBounds(subjects, selectedSubject + 1)
                        updateSubject(holder, subjects[selectedSubject])
                    }
                    holder.prevBtn.setOnClickListener {
                        selectedSubject = checkBounds(subjects, selectedSubject - 1)
                        updateSubject(holder, subjects[selectedSubject])
                    }
                }

                updateSubject(holder, subjects[0])
            }
        }
    }

    override fun getItemCount(): Int {
        val lessonsInWeek = timetable.daysInWeek * timetable.lessonsInDay
        return if (shownWeek == null) timetable.weeksCount * lessonsInWeek else lessonsInWeek
    }

    var onLessonExpand: ((Timetable.Lesson) -> Unit)? = null

    class ViewHolder(val parent: View, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: ConstraintLayout = itemView.item_subject_layout
        val card: CardView = itemView.item_subject_card_view

        val expandBtn: ImageView = itemView.item_subject_expand_image
        val prevBtn: Button = itemView.item_subject_prev_lesson_btn
        val nextBtn: Button = itemView.item_subject_next_lesson_btn

        val subjectTv: TextView = itemView.item_subject_card_tv
        val teacherTv: TextView = itemView.item_teacher_card_tv
        val roomTv: TextView = itemView.item_room_card_tv
        val groupTv: TextView = itemView.item_group_card_tv
    }
}