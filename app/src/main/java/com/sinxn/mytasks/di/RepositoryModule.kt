package com.sinxn.mytasks.di

import com.sinxn.mytasks.data.interfaces.AlarmRepositoryInterface
import com.sinxn.mytasks.data.interfaces.EventRepositoryInterface
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.interfaces.NoteRepositoryInterface
import com.sinxn.mytasks.data.interfaces.TaskRepositoryInterface
import com.sinxn.mytasks.data.repository.AlarmRepository
import com.sinxn.mytasks.data.repository.EventRepository
import com.sinxn.mytasks.data.repository.FolderRepository
import com.sinxn.mytasks.data.repository.NoteRepository
import com.sinxn.mytasks.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAlarmRepository(
        alarmRepository: AlarmRepository
    ): AlarmRepositoryInterface


    @Binds
    @Singleton
    abstract fun bindFolderRepository(
        folderRepository: FolderRepository
    ): FolderRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepository: TaskRepository
    ): TaskRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepository: EventRepository
    ): EventRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepository: NoteRepository
    ): NoteRepositoryInterface


}