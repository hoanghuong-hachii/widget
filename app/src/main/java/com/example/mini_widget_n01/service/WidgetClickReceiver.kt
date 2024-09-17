package com.example.mini_widget_n01.service

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.mini_widget_n01.db.AppDatabase
import com.example.mini_widget_n01.db.entity.WidgetClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

        if (intent.action == "com.example.mini_widget_n01.WIDGET_CLICK") {
            Log.d("WidgetClickReceiver", "AppWidget ID: $appWidgetId")

            if (appWidgetId != -1) {
                Toast.makeText(context, "Widget clicked! ", Toast.LENGTH_SHORT).show()

                // Update click count in the database
                //updateClickCount(context, appWidgetId)
            } else {
                Log.i("CLICK", "Invalid widget click detected")
            }
        } else {
            Log.i("CLICK", "Unexpected action received: ${intent.action}")
        }
    }

    private fun updateClickCount(context: Context, widgetId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val widgetClickDao = db.widgetClickDao()

            // Retrieve the current click count
            val currentRecord = widgetClickDao.getClickCount(widgetId)
            val newCount = (currentRecord?.clickCount ?: 0) + 1
            Toast.makeText(context, "Widget clicked! $newCount", Toast.LENGTH_SHORT).show()
            // Insert or update the click count
            widgetClickDao.insertOrUpdateClickCount(WidgetClick(widgetId, newCount))
            Log.i("CLICK", "Updated click count for Widget ID $widgetId to $newCount")
        }
    }
}