package com.sinxn.mytasks.di

import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.ExpiredTaskRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.PinnedRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.domain.usecase.event.AddEvent
import com.sinxn.mytasks.domain.usecase.event.ArchiveEvent
import com.sinxn.mytasks.domain.usecase.event.ArchiveEvents
import com.sinxn.mytasks.domain.usecase.event.DeleteEvent
import com.sinxn.mytasks.domain.usecase.event.EventUseCases
import com.sinxn.mytasks.domain.usecase.event.GetArchivedEvents
import com.sinxn.mytasks.domain.usecase.event.GetEvent
import com.sinxn.mytasks.domain.usecase.event.GetEvents
import com.sinxn.mytasks.domain.usecase.event.UnarchiveEvent
import com.sinxn.mytasks.domain.usecase.event.UnarchiveEvents
import com.sinxn.mytasks.domain.usecase.expired_task.DeleteExpiredTask
import com.sinxn.mytasks.domain.usecase.expired_task.ExpiredTaskUseCases
import com.sinxn.mytasks.domain.usecase.expired_task.GetExpiredTask
import com.sinxn.mytasks.domain.usecase.expired_task.GetExpiredTasks
import com.sinxn.mytasks.domain.usecase.expired_task.InsertExpiredTask
import com.sinxn.mytasks.domain.usecase.folder.AddFolder
import com.sinxn.mytasks.domain.usecase.folder.ArchiveFolder
import com.sinxn.mytasks.domain.usecase.folder.ArchiveFolders
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolder
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.folder.GetArchivedFolders
import com.sinxn.mytasks.domain.usecase.folder.GetFolder
import com.sinxn.mytasks.domain.usecase.folder.GetFolders
import com.sinxn.mytasks.domain.usecase.folder.UnarchiveFolder
import com.sinxn.mytasks.domain.usecase.folder.UnarchiveFolders
import com.sinxn.mytasks.domain.usecase.note.AddNote
import com.sinxn.mytasks.domain.usecase.note.ArchiveNote
import com.sinxn.mytasks.domain.usecase.note.ArchiveNotes
import com.sinxn.mytasks.domain.usecase.note.DeleteNote
import com.sinxn.mytasks.domain.usecase.note.GetArchivedNotes
import com.sinxn.mytasks.domain.usecase.note.GetNote
import com.sinxn.mytasks.domain.usecase.note.GetNotes
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.note.UnarchiveNote
import com.sinxn.mytasks.domain.usecase.note.UnarchiveNotes
import com.sinxn.mytasks.domain.usecase.pinned.DeletePinned
import com.sinxn.mytasks.domain.usecase.pinned.DeletePinnedItems
import com.sinxn.mytasks.domain.usecase.pinned.GetPinnedItems
import com.sinxn.mytasks.domain.usecase.pinned.InsertPinned
import com.sinxn.mytasks.domain.usecase.pinned.InsertPinnedItems
import com.sinxn.mytasks.domain.usecase.pinned.IsPinned
import com.sinxn.mytasks.domain.usecase.pinned.PinnedUseCases
import com.sinxn.mytasks.domain.usecase.task.AddTask
import com.sinxn.mytasks.domain.usecase.task.ArchiveTask
import com.sinxn.mytasks.domain.usecase.task.ArchiveTasks
import com.sinxn.mytasks.domain.usecase.task.DeleteTask
import com.sinxn.mytasks.domain.usecase.task.GetArchivedTasks
import com.sinxn.mytasks.domain.usecase.task.GetTask
import com.sinxn.mytasks.domain.usecase.task.GetTasks
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.domain.usecase.task.UnarchiveTask
import com.sinxn.mytasks.domain.usecase.task.UnarchiveTasks
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
            archiveNote = ArchiveNote(repository),
            unarchiveNote = UnarchiveNote(repository),
            archiveNotes = ArchiveNotes(repository),
            unarchiveNotes = UnarchiveNotes(repository)
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
            archiveTask = ArchiveTask(repository),
            unarchiveTask = UnarchiveTask(repository),
            archiveTasks = ArchiveTasks(repository),
            unarchiveTasks = UnarchiveTasks(repository)
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
            archiveEvent = ArchiveEvent(repository),
            unarchiveEvent = UnarchiveEvent(repository),
            archiveEvents = ArchiveEvents(repository),
            unarchiveEvents = UnarchiveEvents(repository)
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
            archiveFolder = ArchiveFolder(repository),
            unarchiveFolder = UnarchiveFolder(repository),
            archiveFolders = ArchiveFolders(repository),
            unarchiveFolders = UnarchiveFolders(repository)
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
