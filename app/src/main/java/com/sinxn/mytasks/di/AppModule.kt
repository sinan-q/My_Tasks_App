package com.sinxn.mytasks.di

import android.content.Context
import androidx.room.Room
import com.sinxn.mytasks.data.local.dao.AlarmDao
import com.sinxn.mytasks.data.local.dao.EventDao
import com.sinxn.mytasks.data.local.dao.FolderDao
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.database.AppDatabase
import com.sinxn.mytasks.data.local.database.MIGRATION_3_4
import com.sinxn.mytasks.data.local.database.MIGRATION_4_5
import com.sinxn.mytasks.ui.features.alarms.broadcastReceivers.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(appDatabase: AppDatabase) = appDatabase.noteDao()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideFolderDao(database: AppDatabase): FolderDao = database.folderDao()

    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideAlarmDao(database: AppDatabase): AlarmDao = database.alarmDao()

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler = AlarmScheduler(context)

}
