package com.sinxn.mytasks.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sinxn.mytasks.data.local.dao.AlarmDao
import com.sinxn.mytasks.data.local.dao.EventDao
import com.sinxn.mytasks.data.local.dao.FolderDao
import com.sinxn.mytasks.data.local.dao.NoteDao
import com.sinxn.mytasks.data.local.dao.PinnedDao
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Pinned
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.utils.Converters

const val DB_VERSION = 6
@Database(entities = [Note::class, Task::class, Folder::class, Event::class, Alarm::class, Pinned::class],
    version = DB_VERSION,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun folderDao(): FolderDao
    abstract fun eventDao(): EventDao
    abstract fun alarmDao(): AlarmDao
    abstract fun pinnedDao(): PinnedDao
}