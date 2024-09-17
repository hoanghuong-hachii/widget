package com.example.mini_widget_n01.service

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViewsService

class ImageWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)

        return ImageRemoteViewsFactory(applicationContext, intent, appWidgetId)
    }
}

