package com.sinxn.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val alarmId: Long = 0L,
    val isTask: Boolean,
    val taskId: Long,
    val time: Long,
)
