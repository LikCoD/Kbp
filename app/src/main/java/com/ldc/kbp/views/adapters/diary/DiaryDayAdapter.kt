package com.ldc.kbp.views.adapters.diary

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.ldc.kbp.R
import com.ldc.kbp.getString
import com.ldc.kbp.models.Homeworks
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_diary_day.view.*
import org.threeten.bp.LocalDate
import java.io.File

class DiaryDayAdapter(
    private val activity: Activity,
    private val homeworks: Homeworks,
    items: List<Timetable.Day>? = null,
    startWeekDate: LocalDate
) : Adapter<Timetable.Day?>(activity, items, R.layout.item_diary_day) {
    var startWeekDate = startWeekDate
        set(value) {
            field = value

            dataSetChanged()
        }

    override fun onBindViewHolder(view: View, item: Timetable.Day?, position: Int) {
        val date = startWeekDate.plusDays(position + 1L)

        view.item_diary_day_date_tv.text = date.getString()

        view.item_diary_day_line_recycler.adapter = HomeworkLineAdapter(
            activity,
            homeworks.days[date.toString()] ?: Homeworks.Day(),
            item!!.replacementLessons.filter { it.subjects != null },
            date,
            onImageAddListener
        ) { subject, homework ->
            if (homeworks.days[date.toString()] == null)
                homeworks.days[date.toString()] = Homeworks.Day()
            homeworks.days[date.toString()]!!.subjects[subject.subject] = homework

            onHomeworkChanged(homeworks)
        }
    }

    var onHomeworkChanged: ((Homeworks) -> Unit) = { }

    var onImageAddListener: ((Int, Bitmap, File) -> Unit) = { _, _, _ -> }
}