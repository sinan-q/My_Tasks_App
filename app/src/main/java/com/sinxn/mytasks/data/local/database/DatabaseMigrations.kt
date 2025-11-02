package com.sinxn.mytasks.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE folders ADD COLUMN isLocked INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceRule TEXT DEFAULT NULL")
        database.execSQL("ALTER TABLE events ADD COLUMN recurrenceRule TEXT DEFAULT NULL")
    }
}
