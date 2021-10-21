package com.ldc.kbp.views.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.ldc.kbp.R
import com.ldc.kbp.config
import com.ldc.kbp.getCurrentWeek
import com.ldc.kbp.getString
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import com.ldc.kbp.views.widgets.services.TimetableWidgetsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*

class TimetableWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        runBlocking {
            withContext(Dispatchers.IO) {
                launch { Groups.loadTimetable() }.join()
                val timetable = Timetable.loadTimetable(
                    Groups.timetable.find { it.link == config.link } ?: Groups.timetable[0]
                )

                for (i in appWidgetIds) {
                    val rv = RemoteViews(context.packageName, R.layout.widget_timetable)
                    val data = arrayListOf<String?>()

                    val nowDate = LocalDateTime.now()
                    rv.setTextViewText(
                        R.id.widget_timetable_last_update,
                        "${LocalDate.now().getString()} ${nowDate.hour}:${nowDate.minute}"
                    )
                    if (LocalDate.now().dayOfWeek.ordinal <= timetable.daysInWeek) {
                       /* rv.setTextViewText(
                            R.id.widget_timetable_status,
                            timetable.weeks[getCurrentWeek(timetable.weeks.size)].days[LocalDate.now().dayOfWeek.ordinal].state.value
                        )

                        timetable.weeks[getCurrentWeek(timetable.weeks.size)].days[LocalDate.now().dayOfWeek.ordinal].replacementLessons.forEach {
                            data.add(it?.subjects?.get(0)?.subject)
                        }*/
                    }


                    val adapterIntent = Intent(context, TimetableWidgetsService::class.java)
                    adapterIntent.putStringArrayListExtra("data", data)
                    rv.setRemoteAdapter(R.id.widget_timetable_list, adapterIntent)

                    appWidgetManager.updateAppWidget(i, rv)
                }
            }
        }
    }
}