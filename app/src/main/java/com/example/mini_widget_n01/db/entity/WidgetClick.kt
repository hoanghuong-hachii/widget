package com.example.mini_widget_n01.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_clicks")
data class WidgetClick(
    @PrimaryKey val appWidgetId: Int,
    var clickCount: Int
)
