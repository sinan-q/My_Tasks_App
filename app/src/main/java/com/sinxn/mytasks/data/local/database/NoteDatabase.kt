package com.sinxn.mytasks.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sinxn.mytasks.data.local.dao.NoteDao
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.utils.Converters

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}