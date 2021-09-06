package com.ldc.kbp.views.adapters.timetable

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ldc.kbp.R
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_week_timetable.view.*

class TimetableAdapter(
    context: Context,
    var timetable: Timetable? = null
) : Adapter<Timetable.Week>(
    context,
    timetable?.weeks,
    R.layout.item_week_timetable
) {
    private var recyclers: MutableList<RecyclerView> = mutableListOf()
    private var adapters: MutableList<TimetableWeekAdapter> = mutableListOf()
    private var isReplacementShown = true

    private var weekIndex: Int? = null

    override fun onBindViewHolder(view: View, item: Timetable.Week?, position: Int) {
        recyclers.add(view.item_week_timetable_recycler)

        val weekAdapter = TimetableWeekAdapter(
            context,
            item
        ) { lesson, i -> onLessonExpand(lesson, i) }

        adapters.add(weekAdapter)
        view.item_week_timetable_recycler.adapter = weekAdapter
    }

    var onLessonExpand: (Timetable.Lesson, Int) -> Unit = { _, _ -> }

    fun changeReplacementMode(): Boolean {
        isReplacementShown = !isReplacementShown

        items?.forEachIndexed { index, _ -> adapters[index].changeMode() }

        return isReplacementShown
    }

    fun changeWeekMode(weekIndex: Int? = null) {
        this.weekIndex = weekIndex
        items = if (weekIndex == null) timetable?.weeks else listOf(timetable?.weeks?.get(weekIndex))
    }

    fun changeData(timetable: Timetable) {
        this.timetable = timetable

        changeWeekMode(weekIndex)
    }
}