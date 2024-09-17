package com.example.mini_widget_n01.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mini_widget_n01.db.dao.WidgetClickDao
import com.example.mini_widget_n01.db.entity.WidgetClick

@Database(entities = [WidgetClick::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun widgetClickDao(): WidgetClickDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
