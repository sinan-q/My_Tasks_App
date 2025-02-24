package com.sinxn.mytasks.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {

    @TypeConverter
    fun epochMilliToLocalDate(value: Long?): LocalDateTime? {
        return value?.let {
            if(it==0L) null else LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun localDateToEpochMilli(date: LocalDateTime?): Long {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()?:0L
    }
}