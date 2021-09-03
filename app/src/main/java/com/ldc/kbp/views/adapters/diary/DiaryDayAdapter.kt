package com.ldc.kbp.views.adapters.diary

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import com.ldc.kbp.*
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.models.Homeworks
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_diary_day.view.*
import java.io.File
import java.time.LocalDate

class DiaryDayAdapter(
    private val activity: Activity,
    private val homeworks: Homeworks,
    items: List<Timetable.Day?>? = null,
    startWeekDate: LocalDate
) : Adapter<Timetable.Day?>(activity, items, R.layout.item_diary_day) {
    var startWeekDate = startWeekDate
        set(value) {
            dataSetChanged()

            field = value
        }

    override fun onBindViewHolder(view: View, item: Timetable.Day?, position: Int) {
        val date = startWeekDate.plusDays(position.toLong())

        val screenSize = Deprecates.getScreenSize(activity)

        view.item_diary_day_layout.layoutParams = ViewGroup.LayoutParams(
            screenSize.x,
            (screenSize.y - dimen(context.resources, R.dimen.toolbar_height)
                    - dimen(context.resources, R.dimen.diary_days_of_week_selection_height)
                    - dimen(context.resources, R.dimen.diary_days_of_week_selection_margin_v)
                    - dimen(context.resources, R.dimen.diary_recycler_margin_v) * 2).toInt()
        )

        view.item_diary_day_date_tv.text = date.plusDays(1L).getString()

        view.item_diary_day_line_recycler.adapter = HomeworkLineAdapter(
            activity,
            homeworks.days[date.toString()] ?: Homeworks.Day(),
            item!!.replacementLessons,
            date.plusDays(1L),
            { i, bitmap, file -> onImageAddListener(i, bitmap, file) },
            { subject, homework ->
                if (homeworks.days[date.toString()] == null)
                    homeworks.days[date.toString()] = Homeworks.Day()
                homeworks.days[date.toString()]!!.subjects[subject.subject] = homework

                onHomeworkChanged(homeworks)
            }
        )
    }

    var onHomeworkChanged: ((Homeworks) -> Unit) = { }

    var onImageAddListener: ((Int, Bitmap, File) -> Unit) = { _, _, _ -> }
}