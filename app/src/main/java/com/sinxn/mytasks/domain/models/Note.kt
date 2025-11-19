package com.sinxn.mytasks.domain.models

import java.time.LocalDateTime

data class Note(
    val id: Long? = null,
    val folderId: Long = 0,
    val title: String = "",
    val content: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isArchived: Boolean = false,
)
