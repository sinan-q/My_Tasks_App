package com.sinxn.mytasks.utils

import androidx.room.TypeConverter
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
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

    @TypeConverter
    fun fromReminderTrigger(trigger: ReminderTrigger): String {
        return trigger.name
    }

    @TypeConverter
    fun toReminderTrigger(value: String): ReminderTrigger {
        return try {
            ReminderTrigger.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ReminderTrigger.FROM_END // Default fallback
        }
    }
}

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toMillis())
        }
    }

    @Throws(IOException::class)
    override fun read(input: JsonReader): LocalDateTime? {
        return when (input.peek()) {
            JsonToken.NULL -> {
                input.nextNull()
                null
            }
            else -> {
                val stringValue = input.nextLong()
                fromMillis(stringValue)
            }
        }
    }
}