package com.sinxn.mytasks.di

import android.content.Context
import androidx.room.Room
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.database.AppDatabase
import com.sinxn.mytasks.data.repository.NoteRepository
import com.sinxn.mytasks.data.repository.TaskRepository
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(appDatabase: AppDatabase) = appDatabase.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(appDatabase: AppDatabase): NoteRepository {
        return NoteRepository(appDatabase.noteDao())
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(appDatabase: AppDatabase): TaskRepository {
        return TaskRepository(appDatabase.taskDao())
    }
}