package com.sinxn.mytasks.ui.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sinxn.mytasks.domain.repository.AlarmRepositoryInterface
import com.sinxn.mytasks.domain.repository.EventRepositoryInterface
import com.sinxn.mytasks.domain.repository.FolderRepositoryInterface
import com.sinxn.mytasks.domain.repository.NoteRepositoryInterface
import com.sinxn.mytasks.domain.repository.TaskRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Alarm
import com.sinxn.mytasks.data.local.entities.Event
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import com.sinxn.mytasks.utils.LocalDateTimeAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val eventRepository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface,
    private val noteRepository: NoteRepositoryInterface,
    private val alarmRepository: AlarmRepositoryInterface,
    private val folderRepository: FolderRepositoryInterface,
) : ViewModel() {

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState

    fun exportDatabase(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _backupState.value = BackupState.Loading // Indicate loading
            try {
                val events = eventRepository.getAllEvents().first()
                val tasks = taskRepository.getAllTasks().first()
                val notes = noteRepository.getAllNotes().first()
                // Assuming getAlarms() returns a List<Alarm> or similar directly, not a Flow
                // If it's a Flow, use .first() like the others.
                val alarms = alarmRepository.getAlarms()
                val folders = folderRepository.getAllFolders().first()

                val gson = GsonBuilder()
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create()

                // It's often cleaner to create a single wrapper object for your backup
                val backupData = mapOf(
                    "events" to events,
                    "tasks" to tasks,
                    "notes" to notes,
                    "alarms" to alarms,
                    "folders" to folders
                )
                val jsonString = gson.toJson(backupData)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.writer().use { it.write(jsonString) } // Use writer for strings
                }
                _backupState.value = BackupState.Completed
            } catch (e: Exception) {
                e.printStackTrace()
                _backupState.value = BackupState.Error(e.localizedMessage ?: "Export failed")
            }
        }
    }

    // Data class to represent the structure of your JSON backup file
    data class BackupData(
        val events: List<Event>,
        val tasks: List<Task>,
        val notes: List<Note>,
        val alarms: List<Alarm>,
        val folders: List<Folder>
    )

    fun importDatabase(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _backupState.value = BackupState.Loading // Indicate loading
            try {
                val gson = GsonBuilder()
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                    .create() // No need for pretty printing when reading

                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        reader.readText()
                    }
                }

                if (jsonString.isNullOrBlank()) {
                    _backupState.value = BackupState.Error("Backup file is empty or could not be read.")
                    return@launch
                }

                // Define the type for Gson to deserialize into.
                // This uses a helper data class BackupData.
                val backupDataType = object : TypeToken<BackupData>() {}.type
                val backupData: BackupData = gson.fromJson(jsonString, backupDataType)

                // Clear existing data (optional, depends on your desired import behavior)
                // Be very careful with these operations!
                 eventRepository.clearAllEvents()
                 taskRepository.clearAllTasks()
                 noteRepository.clearAllNotes()
                 alarmRepository.clearAllAlarms()
                 folderRepository.clearAllFolders()

                // Insert the imported data
                // Ensure your repositories have methods like these (e.g., insertAll, addEvents)
                eventRepository.insertEvents(backupData.events)
                taskRepository.insertTasks(backupData.tasks)
                noteRepository.insertNotes(backupData.notes)
                alarmRepository.insertAlarms(backupData.alarms.filter {
                    it.time > System.currentTimeMillis()
                })
                folderRepository.insertFolders(backupData.folders)

                _backupState.value = BackupState.Completed
            } catch (e: Exception) {
                e.printStackTrace()
                _backupState.value = BackupState.Error(e.localizedMessage ?: "Import failed")
            }
        }
    }

    // Updated BackupState to include a loading state and error message
    sealed class BackupState {
        object Idle : BackupState()
        object Loading : BackupState() // Added for better UX
        object Completed : BackupState()
        data class Error(val message: String) : BackupState() // Can hold an error message
    }
}

