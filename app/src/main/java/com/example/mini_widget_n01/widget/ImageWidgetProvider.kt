package com.example.mini_widget_n01.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.mini_widget_n01.R
import com.example.mini_widget_n01.service.ImageWidgetService
import com.example.mini_widget_n01.service.WidgetClickReceiver

class ImageWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val serviceIntent = Intent(context, ImageWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget)

            // Bind the service to the AdapterViewFlipper
            remoteViews.setRemoteAdapter(R.id.adapterViewFlipper, serviceIntent)
            remoteViews.setEmptyView(R.id.adapterViewFlipper, R.id.widgetImageView)

            // Set up the PendingIntent for click events
            val clickIntent = Intent(context, WidgetClickReceiver::class.java).apply {
                action = "com.example.mini_widget_n01.WIDGET_CLICK"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val clickPendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

// Set the PendingIntent template for handling clicks on the AdapterViewFlipper items
            remoteViews.setPendingIntentTemplate(R.id.adapterViewFlipper, clickPendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.adapterViewFlipper)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
