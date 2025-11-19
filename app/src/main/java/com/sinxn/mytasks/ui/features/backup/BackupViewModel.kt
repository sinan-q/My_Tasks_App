package com.sinxn.mytasks.ui.features.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinxn.mytasks.domain.usecase.backup.BackupUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupUseCases: BackupUseCases
) : ViewModel() {

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState

    fun exportDatabase(context: Context, uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                backupUseCases.exportDatabase(context, uri)
                _backupState.value = BackupState.Completed
            } catch (e: Exception) {
                e.printStackTrace()
                _backupState.value = BackupState.Error(e.localizedMessage ?: "Export failed")
            }
        }
    }

    fun importDatabase(context: Context, uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                backupUseCases.importDatabase(context, uri)
                _backupState.value = BackupState.Completed
            } catch (e: Exception) {
                e.printStackTrace()
                _backupState.value = BackupState.Error(e.localizedMessage ?: "Import failed")
            }
        }
    }

    sealed class BackupState {
        object Idle : BackupState()
        object Loading : BackupState()
        object Completed : BackupState()
        data class Error(val message: String) : BackupState()
    }
}

