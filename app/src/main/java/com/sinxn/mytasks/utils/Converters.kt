package com.sinxn.mytasks.utils

import androidx.room.TypeConverter
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun epochMilliToLocalDate(value: Long?): LocalDateTime? {
        return value?.let {
            if(it==0L) null else fromMillis(it)
        }
    }

    @TypeConverter
    fun localDateToEpochMilli(date: LocalDateTime?): Long {
        return date?.toMillis()?:0L
    }
}