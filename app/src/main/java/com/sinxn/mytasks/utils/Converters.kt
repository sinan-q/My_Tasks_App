package com.sinxn.mytasks.utils

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let {
            if(it==0L) null else Date(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long {
        return date?.time ?: 0
    }
}