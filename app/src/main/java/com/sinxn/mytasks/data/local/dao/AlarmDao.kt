package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sinxn.mytasks.data.local.entities.Alarm
import java.time.LocalDateTime

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm")
    suspend fun getAlarms(): List<Alarm>

    @Insert
    suspend fun insertAlarm(alarm: Alarm): Long

    @Query("UPDATE alarm SET `time` = :time WHERE alarmId = :id")
    suspend fun updateAlarm(id: Long, time: Long)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm WHERE alarmId = :alarmId")
    suspend fun getAlarmById(alarmId: Long): Alarm

    @Query("SELECT * FROM alarm WHERE taskId = :taskId")
    suspend fun getAlarmsByTaskId(taskId: Long): List<Alarm>

    @Query("SELECT * FROM alarm WHERE `time` > :now ORDER BY `time` ASC")
    suspend fun getUpcomingAlarms(now: LocalDateTime): List<Alarm>

}
