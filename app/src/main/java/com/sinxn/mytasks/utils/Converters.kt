package com.sinxn.mytasks.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
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

    @TypeConverter
    fun fromDb(value: Long?): LocalDateTime? {
        return value?.let {
            if(it==0L) null else LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDateTime?): Long {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()?:0L
    }
}