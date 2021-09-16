package com.ldc.kbp.views.widgets.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.ldc.kbp.views.widgets.factories.TimetableWidgetLessonFactory

class TimetableWidgetsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val data = intent.getStringArrayListExtra("data")

        return TimetableWidgetLessonFactory(applicationContext, data)
    }
}