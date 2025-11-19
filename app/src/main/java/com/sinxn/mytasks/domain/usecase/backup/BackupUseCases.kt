package com.sinxn.mytasks.domain.usecase.backup

import android.content.Context
import android.net.Uri
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sinxn.mytasks.domain.models.Alarm
import com.sinxn.mytasks.domain.models.Event
import com.sinxn.mytasks.domain.models.Folder
import com.sinxn.mytasks.domain.models.Note
import com.sinxn.mytasks.domain.models.Task
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.utils.LocalDateTimeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.time.LocalDateTime

data class BackupUseCases(
    val exportDatabase: ExportDatabaseUseCase,
    val importDatabase: ImportDatabaseUseCase
)

data class BackupData(
    val events: List<Event>,
    val tasks: List<Task>,
    val notes: List<Note>,
    val alarms: List<Alarm>,
    val folders: List<Folder>
)

class ExportDatabaseUseCase(
    private val eventRepository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val alarmRepository: AlarmRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface
) {
    suspend operator fun invoke(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val events = eventRepository.getAllEvents().first()
        val tasks = taskRepository.getAllTasks().first()
        val notes = noteRepository.getAllNotes().first()
        val alarms = alarmRepository.getAlarms()
        val folders = folderRepository.getAllFolders().first()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create()

        val backupData = BackupData(
            events = events,
            tasks = tasks,
            notes = notes,
            alarms = alarms,
            folders = folders
        )
        val jsonString = gson.toJson(backupData)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.writer().use { it.write(jsonString) }
        }
    }
}

class ImportDatabaseUseCase(
    private val eventRepository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val alarmRepository: AlarmRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface
) {
    suspend operator fun invoke(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                reader.readText()
            }
        }

        if (jsonString.isNullOrBlank()) {
            throw Exception("Backup file is empty or could not be read.")
        }

        val backupDataType = object : TypeToken<BackupData>() {}.type
        val backupData: BackupData = gson.fromJson(jsonString, backupDataType)

        eventRepository.clearAllEvents()
        taskRepository.clearAllTasks()
        noteRepository.clearAllNotes()
        alarmRepository.clearAllAlarms()
        folderRepository.clearAllFolders()

        eventRepository.insertEvents(backupData.events)
        taskRepository.insertTasks(backupData.tasks)
        noteRepository.insertNotes(backupData.notes)
        alarmRepository.insertAlarms(backupData.alarms.filter {
            it.time > System.currentTimeMillis()
        })
        folderRepository.insertFolders(backupData.folders)
    }
}
