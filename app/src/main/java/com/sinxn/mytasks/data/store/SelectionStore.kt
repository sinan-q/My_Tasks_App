package com.sinxn.mytasks.data.store

import com.sinxn.mytasks.data.local.entities.Task
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
class SelectionStore @Inject constructor() {
    private val _selectedTasks = MutableStateFlow<Set<Task>>(emptySet())
    val selectedTasks: StateFlow<Set<Task>> = _selectedTasks

    private val _action = MutableStateFlow<SelectionActions>(SelectionActions.NONE)
    val action: StateFlow<SelectionActions> = _action

    fun toggleTask(task: Task) = _selectedTasks.update { current ->
        if (task in current) current - task else current + task
    }
    fun clear() {
        _selectedTasks.update { emptySet() }
        _action.update { SelectionActions.NONE }
    }

    fun setAction(action: SelectionActions) {
        _action.update { action }
    }
}

enum class SelectionActions {
    CUT, COPY, DELETE, NONE
}
