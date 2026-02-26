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
import com.sinxn.mytasks.domain.usecase.event.UpdateEvent
import com.sinxn.mytasks.domain.usecase.expired_task.DeleteExpiredTask
import com.sinxn.mytasks.domain.usecase.expired_task.ExpiredTaskUseCases
import com.sinxn.mytasks.domain.usecase.expired_task.GetExpiredTask
import com.sinxn.mytasks.domain.usecase.expired_task.GetExpiredTasks
import com.sinxn.mytasks.domain.usecase.expired_task.InsertExpiredTask
import com.sinxn.mytasks.domain.usecase.folder.AddFolderUseCase
import com.sinxn.mytasks.domain.usecase.folder.DeleteFolderAndItsContentsUseCase
import com.sinxn.mytasks.domain.usecase.folder.FolderUseCases
import com.sinxn.mytasks.domain.usecase.folder.GetArchivedFolders
import com.sinxn.mytasks.domain.usecase.folder.GetFolder
import com.sinxn.mytasks.domain.usecase.folder.GetFolders
import com.sinxn.mytasks.domain.usecase.folder.GetPathUseCase
import com.sinxn.mytasks.domain.usecase.folder.GetSubFolders
import com.sinxn.mytasks.domain.usecase.folder.LockFolderUseCase
import com.sinxn.mytasks.domain.usecase.folder.ToggleFolderArchive
import com.sinxn.mytasks.domain.usecase.folder.ToggleFoldersArchive
import com.sinxn.mytasks.domain.usecase.folder.UpdateFolderName
import com.sinxn.mytasks.domain.usecase.note.AddNote
import com.sinxn.mytasks.domain.usecase.note.DeleteNote
import com.sinxn.mytasks.domain.usecase.note.GetArchivedNotes
import com.sinxn.mytasks.domain.usecase.note.GetNote
import com.sinxn.mytasks.domain.usecase.note.GetNotes
import com.sinxn.mytasks.domain.usecase.note.GetNotesByFolderId
import com.sinxn.mytasks.domain.usecase.note.NoteUseCases
import com.sinxn.mytasks.domain.usecase.note.ToggleNoteArchive
import com.sinxn.mytasks.domain.usecase.note.ToggleNotesArchive
import com.sinxn.mytasks.domain.usecase.note.UpdateNote
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
import com.sinxn.mytasks.domain.usecase.task.GetTasksByFolderId
import com.sinxn.mytasks.domain.usecase.task.TaskUseCases
import com.sinxn.mytasks.domain.usecase.task.ToggleTaskArchive
import com.sinxn.mytasks.domain.usecase.task.ToggleTasksArchive
import com.sinxn.mytasks.domain.usecase.task.UpdateStatusTask
import com.sinxn.mytasks.domain.usecase.task.UpdateTask
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.usecase.alarm.AlarmUseCases
import com.sinxn.mytasks.domain.usecase.alarm.CancelAlarmUseCase
import com.sinxn.mytasks.domain.usecase.alarm.CancelAlarmsByTaskIdUseCase
import com.sinxn.mytasks.domain.usecase.alarm.ClearAllAlarmsUseCase
import com.sinxn.mytasks.domain.usecase.alarm.DeleteAlarmUseCase
import com.sinxn.mytasks.domain.usecase.alarm.GetAlarmByIdUseCase
import com.sinxn.mytasks.domain.usecase.alarm.GetAlarmsByTaskIdUseCase
import com.sinxn.mytasks.domain.usecase.alarm.GetAlarmsUseCase
import com.sinxn.mytasks.domain.usecase.alarm.GetUpcomingAlarmsUseCase
import com.sinxn.mytasks.domain.usecase.alarm.InsertAlarmUseCase
import com.sinxn.mytasks.domain.usecase.alarm.InsertAlarmsUseCase
import com.sinxn.mytasks.domain.usecase.alarm.SnoozeAlarmUseCase
import com.sinxn.mytasks.domain.usecase.backup.BackupUseCases
import com.sinxn.mytasks.domain.usecase.backup.ExportDatabaseUseCase
import com.sinxn.mytasks.domain.usecase.backup.ImportDatabaseUseCase
import com.sinxn.mytasks.domain.usecase.home.GetDashboardDataUseCase
import com.sinxn.mytasks.domain.usecase.home.HomeUseCases
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
    fun provideNoteUseCases(
        repository: NoteRepositoryInterface,
        itemRelationUseCases: com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases
    ): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            getArchivedNotes = GetArchivedNotes(repository),
            deleteNote = DeleteNote(repository, itemRelationUseCases.removeRelationsForItem),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            toggleArchive = ToggleNoteArchive(repository),
            toggleArchives = ToggleNotesArchive(repository),
            updateNote = UpdateNote(repository),
            getNotesByFolderId = GetNotesByFolderId(repository),
        )
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(
        repository: TaskRepositoryInterface,
        itemRelationUseCases: com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases
    ): TaskUseCases {
        return TaskUseCases(
            getTasks = GetTasks(repository),
            getArchivedTasks = GetArchivedTasks(repository),
            deleteTask = DeleteTask(repository, itemRelationUseCases.removeRelationsForItem),
            addTask = AddTask(repository),
            getTask = GetTask(repository),
            toggleArchive = ToggleTaskArchive(repository),
            toggleArchives = ToggleTasksArchive(repository),
            updateStatusTask = UpdateStatusTask(repository),
            updateTask = UpdateTask(repository),
            getTasksByFolderId = GetTasksByFolderId(repository),
        )
    }

    @Provides
    @Singleton
    fun provideEventUseCases(
        repository: EventRepositoryInterface,
        itemRelationUseCases: com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases
    ): EventUseCases {
        return EventUseCases(
            getEvents = GetEvents(repository),
            getArchivedEvents = GetArchivedEvents(repository),
            deleteEvent = DeleteEvent(repository, itemRelationUseCases.removeRelationsForItem),
            addEvent = AddEvent(repository),
            getEvent = GetEvent(repository),
            toggleArchive = ToggleEventArchive(repository),
            toggleArchives = ToggleEventsArchive(repository),
            updateEvent = UpdateEvent(repository),
        )
    }

    @Provides
    @Singleton
    fun provideFolderUseCases(repository: FolderRepositoryInterface, noteRepositoryInterface: NoteRepositoryInterface, taskRepositoryInterface: TaskRepositoryInterface): FolderUseCases {
        return FolderUseCases(
            getFolders = GetFolders(repository),
            getArchivedFolders = GetArchivedFolders(repository),
            deleteFolder = DeleteFolderAndItsContentsUseCase(repository, noteRepository = noteRepositoryInterface, taskRepositoryInterface),
            addFolder = AddFolderUseCase(repository),
            getFolder = GetFolder(repository),
            toggleArchive = ToggleFolderArchive(repository),
            toggleArchives = ToggleFoldersArchive(repository),
            lockFolder = LockFolderUseCase(repository),
            getPath = GetPathUseCase(repository),
            updateFolderName = UpdateFolderName(repository),
            getSubFolders = GetSubFolders(repository),
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
    @Provides
    @Singleton
    fun provideAlarmUseCases(
        repository: AlarmRepositoryInterface,
        scheduler: com.sinxn.mytasks.domain.repository.AlarmSchedulerInterface
    ): AlarmUseCases {
        return AlarmUseCases(
            getAlarms = GetAlarmsUseCase(repository),
            getAlarmById = GetAlarmByIdUseCase(repository),
            snoozeAlarm = SnoozeAlarmUseCase(repository, scheduler),
            cancelAlarm = CancelAlarmUseCase(repository, scheduler),
            deleteAlarm = DeleteAlarmUseCase(repository),
            cancelAlarmsByTaskId = CancelAlarmsByTaskIdUseCase(repository),
            insertAlarm = InsertAlarmUseCase(repository),
            insertAlarms = InsertAlarmsUseCase(repository),
            clearAllAlarms = ClearAllAlarmsUseCase(repository),
            getAlarmsByTaskId = GetAlarmsByTaskIdUseCase(repository),
            getUpcomingAlarms = GetUpcomingAlarmsUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideBackupUseCases(
        eventRepository: EventRepositoryInterface,
        taskRepository: TaskRepositoryInterface,
        noteRepository: NoteRepositoryInterface,
        alarmRepository: AlarmRepositoryInterface,
        folderRepository: FolderRepositoryInterface
    ): BackupUseCases {
        return BackupUseCases(
            exportDatabase = ExportDatabaseUseCase(
                eventRepository,
                taskRepository,
                noteRepository,
                alarmRepository,
                folderRepository
            ),
            importDatabase = ImportDatabaseUseCase(
                eventRepository,
                taskRepository,
                noteRepository,
                alarmRepository,
                folderRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideHomeUseCases(): HomeUseCases {
        return HomeUseCases(
            getDashboardData = GetDashboardDataUseCase()
        )
    }

    @Provides
    @Singleton
    fun provideItemRelationUseCases(repository: com.sinxn.mytasks.domain.repository.ItemRelationRepository): com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases {
        return com.sinxn.mytasks.domain.usecase.relation.ItemRelationUseCases(
            addRelation = com.sinxn.mytasks.domain.usecase.relation.AddRelation(repository),
            removeRelation = com.sinxn.mytasks.domain.usecase.relation.RemoveRelation(repository),
            removeRelationsForItem = com.sinxn.mytasks.domain.usecase.relation.RemoveRelationsForItem(repository),
            getParent = com.sinxn.mytasks.domain.usecase.relation.GetParent(repository),
            getChildren = com.sinxn.mytasks.domain.usecase.relation.GetChildren(repository)
        )
    }
}
