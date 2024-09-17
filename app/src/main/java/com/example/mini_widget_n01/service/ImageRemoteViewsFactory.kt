package com.example.mini_widget_n01.service

import android.appwidget.AppWidgetManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.mini_widget_n01.R
import java.io.IOException

class ImageRemoteViewsFactory(
    private val context: Context,
    intent: Intent,
    private var appWidgetId: Int
) : RemoteViewsService.RemoteViewsFactory {

    private val imageUris: MutableList<Uri> = ArrayList()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        // Fetch images from MediaStore
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

        imageUris.clear()
        cursor?.use {
            val columnIndexId = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val imageUri = ContentUris.withAppendedId(uri, it.getLong(columnIndexId))
                imageUris.add(imageUri)
                Log.d("ImageRemoteViewsFactory", "Loaded image URI: $imageUri")
            }
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = imageUris.size

    override fun getViewAt(position: Int): RemoteViews {
        Log.i("ImageRemoteViewsFactory getViewAt: ", "getViewAt: $position" )
        val rv = RemoteViews(context.packageName, R.layout.widget_image_item)

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUris[position])
            rv.setImageViewBitmap(R.id.widgetImageView, bitmap)

            // Create an intent that will trigger the BroadcastReceiver
            val clickIntent = Intent().apply {
                action = "com.example.mini_widget_n01.WIDGET_CLICK"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra("item_position", position)  // Pass the position here
            }

            // Set the fill-in intent for the widget view
            rv.setOnClickFillInIntent(R.id.widgetImageView, clickIntent)
        } catch (e: IOException) {
            Log.e("ImageRemoteViewsFactory", "Error loading bitmap for URI: ${imageUris[position]}", e)
        }

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
