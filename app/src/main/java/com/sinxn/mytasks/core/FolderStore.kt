package com.sinxn.mytasks.core

import android.content.ContentValues.TAG
import android.util.Log
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import com.sinxn.mytasks.data.local.entities.Note
import com.sinxn.mytasks.data.local.entities.Task
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

@Singleton
class FolderStore @Inject constructor(
    val folderRepository: FolderRepositoryInterface
) {
    val allFolders = mutableListOf<Folder>()

    private val _parentFolder = MutableStateFlow<Folder?>(null)
    val parentFolder: StateFlow<Folder?> = _parentFolder

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    suspend fun fetchFolderById(folderId: Long): Folder {
        val fetchedFolder = folderRepository.getFolderById(folderId)
        val subFolders = folderRepository.getSubFolders(folderId).first()
        _folders.value = subFolders
        _parentFolder.value = fetchedFolder
        subFolders.plus(fetchedFolder).forEach { if (!allFolders.contains(it)) allFolders.add(it) }
        return fetchedFolder
    }
    suspend fun getPath(folderId: Long, hideLocked: Boolean): String? {
        val path = StringBuilder("/")
        var curr = folderId
        while (curr != 0L) {
            if (allFolders.find { it.folderId == curr } == null) Log.d(TAG, "getPath: ")
            val folder = allFolders.find { it.folderId == curr } ?: fetchFolderById(curr)
            path.insert(0, folder.name)
            path.insert(0, "/")
            curr = folder.parentFolderId ?: 0L
            if (folder.isLocked == true && hideLocked) return null
        }
        return path.toString()
    }
}