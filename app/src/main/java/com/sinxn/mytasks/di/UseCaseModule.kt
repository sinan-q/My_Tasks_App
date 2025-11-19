package com.sinxn.mytasks.di

import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.ExpiredTaskRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.event.AddEvent
import com.sinxn.mytasks.domain.usecase.event.DeleteEvent
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.event.GetArchivedEvents
import com.sinxn.mytasks.domain.usecase.event.GetEvent
import com.sinxn.mytasks.domain.usecase.event.GetEvents
import com.sinxn.mytasks.domain.usecase.event.ToggleEventArchive
import com.sinxn.mytasks.domain.usecase.event.ToggleEventsArchive
import com.sinxn.mytasks.domain.usecase.expired_task.DeleteExpiredTask
import com.sinxn.mytasks.domain.usecase.expired_task.ExpiredTaskUseCases
import com.sinxn.mytasks.domain.usecase.expired_task.GetExpiredTask
import com.sinxn.mytasks.domain.usecase.expired_task.GetExpiredTasks
import com.sinxn.mytasks.domain.usecase.expired_task.InsertExpiredTask
import com.sinxn.mytasks.domain.usecase.folder.AddFolder
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolder
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.folder.GetArchivedFolders
import com.sinxn.mytasks.domain.usecase.folder.GetFolder
import com.sinxn.mytasks.domain.usecase.folder.GetFolders
import com.sinxn.mytasks.domain.usecase.folder.ToggleFolderArchive
import com.sinxn.mytasks.domain.usecase.folder.ToggleFoldersArchive
import com.sinxn.mytasks.domain.usecase.note.AddNote
import com.sinxn.mytasks.domain.usecase.note.DeleteNote
import com.sinxn.mytasks.domain.usecase.note.GetArchivedNotes
import com.sinxn.mytasks.domain.usecase.note.GetNote
import com.sinxn.mytasks.domain.usecase.note.GetNotes
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.note.ToggleNoteArchive
import com.sinxn.mytasks.domain.usecase.note.ToggleNotesArchive
import com.sinxn.mytasks.domain.usecase.pinned.DeletePinned
import com.sinxn.mytasks.domain.usecase.pinned.DeletePinnedItems
import com.sinxn.mytasks.domain.usecase.pinned.GetPinnedItems
import com.sinxn.mytasks.domain.usecase.pinned.InsertPinned
import com.sinxn.mytasks.domain.usecase.pinned.InsertPinnedItems
import com.sinxn.mytasks.domain.usecase.pinned.IsPinned
import com.sinxn.mytasks.domain.usecase.pinned.PinnedUseCases
import com.sinxn.mytasks.domain.usecase.task.AddTask
import com.sinxn.mytasks.domain.usecase.task.DeleteTask
import com.sinxn.mytasks.domain.usecase.task.GetArchivedTasks
import com.sinxn.mytasks.domain.usecase.task.GetTask
import com.sinxn.mytasks.domain.usecase.task.GetTasks
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.domain.usecase.task.ToggleTaskArchive
import com.sinxn.mytasks.domain.usecase.task.ToggleTasksArchive
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepositoryInterface): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            getArchivedNotes = GetArchivedNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            toggleArchive = ToggleNoteArchive(repository),
            toggleArchives = ToggleNotesArchive(repository),
        )
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(repository: TaskRepositoryInterface): TaskUseCases {
        return TaskUseCases(
            getTasks = GetTasks(repository),
            getArchivedTasks = GetArchivedTasks(repository),
            deleteTask = DeleteTask(repository),
            addTask = AddTask(repository),
            getTask = GetTask(repository),
            toggleArchive = ToggleTaskArchive(repository),
            toggleArchives = ToggleTasksArchive(repository),
        )
    }

    @Provides
    @Singleton
    fun provideEventUseCases(repository: EventRepositoryInterface): EventUseCases {
        return EventUseCases(
            getEvents = GetEvents(repository),
            getArchivedEvents = GetArchivedEvents(repository),
            deleteEvent = DeleteEvent(repository),
            addEvent = AddEvent(repository),
            getEvent = GetEvent(repository),
            toggleArchive = ToggleEventArchive(repository),
            toggleArchives = ToggleEventsArchive(repository),
        )
    }

    @Provides
    @Singleton
    fun provideFolderUseCases(repository: FolderRepositoryInterface): FolderUseCases {
        return FolderUseCases(
            getFolders = GetFolders(repository),
            getArchivedFolders = GetArchivedFolders(repository),
            deleteFolder = DeleteFolder(repository),
            addFolder = AddFolder(repository),
            getFolder = GetFolder(repository),
            toggleArchive = ToggleFolderArchive(repository),
            toggleArchives = ToggleFoldersArchive(repository),
        )
    }

    @Provides
    @Singleton
    fun providePinnedUseCases(repository: PinnedRepositoryInterface): PinnedUseCases {
        return PinnedUseCases(
            getPinnedItems = GetPinnedItems(repository),
            isPinned = IsPinned(repository),
            insertPinned = InsertPinned(repository),
            deletePinned = DeletePinned(repository),
            insertPinnedItems = InsertPinnedItems(repository),
            deletePinnedItems = DeletePinnedItems(repository)
        )
    }

    @Provides
    @Singleton
    fun provideExpiredTaskUseCases(repository: ExpiredTaskRepositoryInterface): ExpiredTaskUseCases {
        return ExpiredTaskUseCases(
            getExpiredTasks = GetExpiredTasks(repository),
            getExpiredTask = GetExpiredTask(repository),
            insertExpiredTask = InsertExpiredTask(repository),
            deleteExpiredTask = DeleteExpiredTask(repository)
        )
    }
}
