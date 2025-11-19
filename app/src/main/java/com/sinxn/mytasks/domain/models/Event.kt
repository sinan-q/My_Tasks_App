package com.sinxn.mytasks.domain.models

import java.time.LocalDateTime

data class Event(
    val id: Long? = null,
    val folderId: Long = 0,
    val title: String = "",
    val description: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null,
    val recurrenceRule: String? = null,
    val isArchived: Boolean = false,
)
