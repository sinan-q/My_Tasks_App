package com.sinxn.mytasks.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Using SharedFlow for events like toasts that should be consumed once.
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

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