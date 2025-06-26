package com.schoolbridge.v2.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.academic.TodayCourse

// widget/TodayScheduleWidget.kt
class TodayScheduleWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_today_schedule)

            // TODO: Replace with real logic or shared prefs
            val todayCourses = listOf(
                TodayCourse("Math", "08:00", "09:40", "Mr. Kamali", "Room A1"),
                TodayCourse("Chemistry", "10:00", "11:40", "Ms. Uwase", "Lab 3")
            )

            val text = todayCourses.joinToString("\n") {
                "${it.startTime} - ${it.subject} (${it.location})"
            }
            views.setTextViewText(R.id.widget_schedule_lines, text)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}