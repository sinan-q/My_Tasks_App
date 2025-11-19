package com.sinxn.mytasks.domain.models

import java.time.LocalDateTime

data class Task(
    val id: Long? = null,
    val folderId: Long = 0,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val due: LocalDateTime? = null,
    val recurrenceRule: String? = null,
    val isArchived: Boolean = false,
)
