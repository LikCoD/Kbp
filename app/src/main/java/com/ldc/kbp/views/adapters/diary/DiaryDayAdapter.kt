package com.ldc.kbp.views.adapters.diary

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import likco.studyum.R
import com.ldc.kbp.getCurrentWeek
import com.ldc.kbp.getString
import com.ldc.kbp.mainSchedule
import com.ldc.kbp.models.Homeworks
import com.ldc.kbp.models.Schedule
import com.ldc.kbp.views.adapters.Adapter
import com.ldc.kbp.views.adapters.emptyroom.FloorSwitcherAdapter
import kotlinx.android.synthetic.main.item_diary_day.view.*
import org.threeten.bp.LocalDate
import java.io.File

class DiaryDayAdapter(
    private val activity: Activity,
    private val homeworks: Homeworks,
    schedule: Schedule,
    var startWeekDate: LocalDate
) : RecyclerView.Adapter<DiaryDayAdapter.ViewHolder>() {

    private val items = schedule.lessons.chunked(schedule.info.studyPlace.daysCount)

    private var currentWeek = getCurrentWeek()
        set(value) {
            field = when {
                value < 0 -> mainSchedule.info.studyPlace.weeksCount - 1
                value > mainSchedule.info.studyPlace.weeksCount - 1 -> 0
                else -> value
            }

            notifyDataSetChanged()
        }

    fun plusWeek() {
        startWeekDate = startWeekDate.plusWeeks(1)
        currentWeek++
    }

    fun minusWeek() {
        startWeekDate = startWeekDate.minusWeeks(1)
        currentWeek--
    }

    fun setDate(date: LocalDate) {
        startWeekDate = date.minusDays(date.dayOfWeek.value.toLong())
        currentWeek = getCurrentWeek(date = startWeekDate)
    }

    var onHomeworkChanged: ((Homeworks) -> Unit) = { }

    var onImageAddListener: ((Int, Bitmap, File) -> Unit) = { _, _, _ -> }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = startWeekDate.plusDays(position + 1L)

        holder.dateTv.text = date.getString()

        val subjects = items[currentWeek][position]

        holder.lineRecycler.adapter = HomeworkLineAdapter(
            activity,
            homeworks.days[date.toString()] ?: Homeworks.Day(),
            listOf(subjects),
            date,
            onImageAddListener
        ) { subject, homework ->
            if (homeworks.days[date.toString()] == null)
                homeworks.days[date.toString()] = Homeworks.Day()
            homeworks.days[date.toString()]!!.subjects[subject.subject] = homework

            onHomeworkChanged(homeworks)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_diary_day, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mainSchedule.info.studyPlace.daysCount

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv: TextView = itemView.item_diary_day_date_tv
        val lineRecycler: RecyclerView = itemView.item_diary_day_line_recycler
    }
}