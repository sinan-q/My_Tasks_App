package com.sinxn.mytasks.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceRule TEXT DEFAULT NULL")
        database.execSQL("ALTER TABLE events ADD COLUMN recurrenceRule TEXT DEFAULT NULL")
    }
}
