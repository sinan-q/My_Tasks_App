package com.sinxn.mytasks.di

import android.content.Context
import androidx.room.Room
import com.sinxn.mytasks.core.SelectionStore
import com.sinxn.mytasks.data.local.dao.AlarmDao
import com.sinxn.mytasks.data.local.dao.EventDao
import com.sinxn.mytasks.data.local.dao.FolderDao
import com.sinxn.mytasks.data.local.dao.TaskDao
import com.sinxn.mytasks.data.local.database.AppDatabase
import com.sinxn.mytasks.data.repository.AlarmRepository
import com.sinxn.mytasks.data.repository.EventRepository
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.NoteRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import com.sinxn.mytasks.data.usecase.folder.CopyFolderAndItsContentsUseCase
import com.sinxn.mytasks.data.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.ui.screens.alarmScreen.AlarmScheduler
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
    fun provideNoteRepository(appDatabase: AppDatabase): NoteRepository = NoteRepository(appDatabase.noteDao())

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()


    @Provides
    @Singleton
    fun provideTaskRepository(appDatabase: AppDatabase): TaskRepository = TaskRepository(appDatabase.taskDao())

    @Provides
    @Singleton
    fun provideFolderDao(database: AppDatabase): FolderDao = database.folderDao()

    @Provides
    @Singleton
    fun provideFolderRepository(appDatabase: AppDatabase): FolderRepository = FolderRepository(appDatabase.folderDao())

    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): EventDao = database.eventDao()

    @Provides
    @Singleton
    fun provideEventRepository(appDatabase: AppDatabase): EventRepository = EventRepository(appDatabase.eventDao())

    @Provides
    @Singleton
    fun provideAlarmDao(database: AppDatabase): AlarmDao = database.alarmDao()

    @Provides
    @Singleton
    fun provideAlarmRepository(@ApplicationContext context: Context,appDatabase: AppDatabase): AlarmRepository =
        AlarmRepository(
            appDatabase.alarmDao(),
            AlarmScheduler(context)
        )

    @Provides
    @Singleton
    fun provideSelectionStore(
        taskRepository: TaskRepository,
        noteRepository: NoteRepository,
        folderRepository: FolderRepository,
        copyFolderAndItsContentsUseCase: CopyFolderAndItsContentsUseCase,
        deleteFolderAndItsContentsUseCase: DeleteFolderAndItsContentsUseCase
    ): SelectionStore {
        return SelectionStore(
            taskRepository, noteRepository, folderRepository, copyFolderAndItsContentsUseCase, deleteFolderAndItsContentsUseCase
        )
    }

}