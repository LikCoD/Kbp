package com.ldc.kbp.views.widgets.factories

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import likco.studyum.R

class TimetableWidgetLessonFactory(val context: Context, val items: ArrayList<String>?) :
    RemoteViewsService.RemoteViewsFactory {
    override fun onCreate() {

    }

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount(): Int = items?.size ?: 0

    override fun getViewAt(i: Int): RemoteViews {
        val view = RemoteViews(context.packageName, R.layout.item_widget_timetable_lesson)

        view.setTextViewText(R.id.item_widget_timetable_lesson_text, "${i + 1}: ${items?.get(i) ?: ""}")

        return view
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(pos: Int): Long = pos.toLong()

    override fun hasStableIds(): Boolean = true
}