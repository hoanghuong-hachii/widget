package com.example.mini_widget_n01.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mini_widget_n01.db.entity.WidgetClick

@Dao
interface WidgetClickDao {

    @Query("SELECT * FROM widget_clicks WHERE appWidgetId = :widgetId")
    suspend fun getClickCount(widgetId: Int): WidgetClick?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateClickCount(widgetClick: WidgetClick)
}
