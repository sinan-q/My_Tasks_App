package com.sinxn.mytasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.data.interfaces.FolderRepositoryInterface
import com.sinxn.mytasks.data.local.entities.Folder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class BaseViewModel(
    protected val folderRepository: FolderRepositoryInterface
) : ViewModel() {

    protected val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    protected val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    private val allFolders = folderRepository.getAllFolders().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    // Using SharedFlow for events like toasts that should be consumed once.
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()



    protected fun fetchFolderById(folderId: Long, action: (Long) -> Unit) {
        viewModelScope.launch {
            val fetchedFolder = folderRepository.getFolderById(folderId)
            val subFolders = folderRepository.getSubFolders(folderId).first()
            _folders.value = subFolders
            _folder.value = fetchedFolder
            action(fetchedFolder.folderId)

        }
    }
    fun getPath(folderId: Long, hideLocked: Boolean): String? {
        val path = StringBuilder()
        var curr = folderId
        while (curr != 0L) {
            val folder = allFolders.value.find { it.folderId == curr }
            path.insert(0, "/")
            path.insert(0, folder?.name)
            curr = folder?.parentFolderId ?: 0L
            if (folder?.isLocked == true && hideLocked) return null
        }
        return path.toString()
    }
    /**
     * Emits a message to be shown as a toast.
     * This should be called from a coroutine scope, typically viewModelScope.
     */
    protected fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

    /**
     * A convenience function to show a toast if a condition is met.
     */
    protected fun showToastIf(condition: Boolean, message: String) {
        if (condition) {
            showToast(message)
        }
    }

    /**
     * A convenience function to show a toast if a value is null.
     */
    protected fun showToastIfNull(value: Any?, messageIfNull: String) {
        if (value == null) {
            showToast(messageIfNull)
        }
    }
}