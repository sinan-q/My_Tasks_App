package com.sinxn.mytasks.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// Extension function for formatting Date
fun LocalDateTime.formatDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
    return this.format(formatter)
}

fun fromMillis(millis :Long): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
}

fun LocalDateTime.toMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun LocalDateTime.differenceSeconds(localDateTime: LocalDateTime): Long {
    return (this.toMillis() - localDateTime.toMillis())/1000
}

@OptIn(ExperimentalMaterial3Api::class)
fun LocalDateTime.addTimerPickerState(timePickerState: TimePickerState): LocalDateTime {
    return this.withHour(timePickerState.hour).withMinute(timePickerState.minute)
}
enum class DurationUnit(val label: String, val unit: ChronoUnit) {
    SECOND("Seconds", ChronoUnit.SECONDS),
    MINUTE("Minutes", ChronoUnit.MINUTES),
    HOUR("Hours", ChronoUnit.HOURS),
    DAY("Days", ChronoUnit.DAYS)
}