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
import likco.studyum.R
import com.ldc.kbp.models.Schedule
import kotlinx.android.synthetic.main.item_lesson.view.*

/*
class TimetableAdapter(
    val context: Context,
    schedule: Schedule,
    shownWeek: Int? = null,
    var onVisibleWeekChanged: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    var schedule = schedule
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    var isReplacementShown = true
        set(value) {
            field = value

            schedule.subjects.forEachIndexed { i, subjects ->
                if (subjects?.isStay == false)
                    notifyItemChanged(i)
            }
        }

    var lastShownWeek = 0

    var shownWeek: Int? = shownWeek
        set(value) {
            if (value != field)
                updateWeeks()

            field = value
        }

    private fun updateWeeks() {
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false)
        return ViewHolder(parent, view)
    }

    private fun updateSubject(holder: ViewHolder, subject: Schedule.Subject) {
        val cardColor =
            if (subject.type == Schedule.Type.ADDED) R.color.timetable_replacement_subject_bg else R.color.timetable_subject_bg

        holder.card.setCardBackgroundColor(context.getColor(cardColor))

        holder.subjectTv.text = subject.subject
        holder.teacherTv.text = subject.teacher
        holder.roomTv.text = subject.room
        holder.groupTv.text = subject.group
    }

    private fun checkBounds(subjects: List<Schedule.Subject>, position: Int): Int = when {
        position >= subjects.size -> 0
        position < 0 -> subjects.lastIndex
        else -> position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentDay = position / schedule.info.subjectsCount
        if (shownWeek != null)
            currentDay += shownWeek!! * schedule.info.daysCount

        val subject =
            if (shownWeek == null) schedule.subjects[position] else schedule.subjects[position + shownWeek!! * schedule.info.daysCount * schedule.info.subjectsCount]

        if (subject != null && subject.weekIndex != lastShownWeek) {
            lastShownWeek = subject.weekIndex
            onVisibleWeekChanged?.invoke(lastShownWeek)
        }

        holder.layout.foreground =
            if (schedule.status[currentDay].status == Schedule.StatusInfo.NOT_UPDATED)
                ColorDrawable(context.getColor(R.color.not_updated)) else null

        val subjects = subject?.subjects?.filter {
            it.type == Schedule.Type.STAY
                    || (isReplacementShown && it.type == Schedule.Type.ADDED)
                    || (!isReplacementShown && it.type == Schedule.Type.REMOVED)
        }


        holder.expandBtn.isVisible = subject != null && subjects!!.size > 1

        if (subjects == null || subjects.isEmpty()) {
            holder.card.setCardBackgroundColor(context.getColor(R.color.timetable_empty_subject_bg))

            holder.subjectTv.text = ""
            holder.teacherTv.text = ""
            holder.groupTv.text = ""
            holder.roomTv.text = ""

            holder.nextBtn.setOnClickListener {}
            holder.prevBtn.setOnClickListener {}

            return
        }

        var selectedSubject = 0

        if (subjects.size > 1) {
            holder.expandBtn.setOnClickListener {
                onExpand?.invoke(subjects)
            }

            holder.nextBtn.setOnClickListener {
                selectedSubject = checkBounds(subjects, selectedSubject + 1)
                updateSubject(holder, subjects[selectedSubject])
            }
            holder.prevBtn.setOnClickListener {
                selectedSubject = checkBounds(subjects, selectedSubject - 1)
                updateSubject(holder, subjects[selectedSubject])
            }
        } else {
            holder.nextBtn.setOnClickListener {}
            holder.prevBtn.setOnClickListener {}
        }

        updateSubject(holder, subjects[0])

    }

    override fun getItemCount(): Int =
        if (shownWeek == null) schedule.subjects.size else schedule.subjects.size / schedule.info.weeksCount

    var onExpand: ((List<Schedule.Subject>) -> Unit)? = null

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
}*/
